/*
 * Copyright 2021-2022 COOKBIT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.cookbit.storage.local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.nio.ch.FileChannelImpl;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

import static com.github.cookbit.util.CheckUtil.checkArgument;

/**
 * 采用IO映射进行文件写入
 *
 * @auther 961374431@qq.com
 * @date 2022年02月24日
 */
public class MappedBufferStorage implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(MappedBufferStorage.class);

    private MappedBufferStorage() {
    }

    private File targetFile;
    private FileChannel channel;
    private int writeBytesTotal;
    private int preWriteCount;

    /**
     * 创建映射存储实例
     *
     * @param target     文件路径
     * @param seekOffset 写入偏移，当偏移小于0时，清空目标文件
     * @return this
     * @throws IOException 目标文件校验失败
     */
    public static MappedBufferStorage target(File target, int seekOffset) throws IOException {
        // 检查文件有效性
        checkFileValidity(target);

        // 初始化
        MappedBufferStorage storage = new MappedBufferStorage();
        RandomAccessFile randomAccessFile = new RandomAccessFile(target, "rw");
        FileChannel channel = randomAccessFile.getChannel();
        if (seekOffset >= 0) {
            channel.position(seekOffset);
        } else {
            channel.truncate(0);
        }
        storage.targetFile = target;
        storage.channel = channel;
        storage.writeBytesTotal = 0;
        storage.preWriteCount = 0;
        return storage;
    }

    /**
     * 创建映射存储实例
     *
     * @param path       文件路径
     * @param seekOffset 写入偏移，当偏移小于0时，清空目标文件
     * @return this
     * @throws IOException 目标文件校验失败
     */
    public static MappedBufferStorage target(String path, int seekOffset) throws IOException {
        File file = new File(path);
        return target(file, seekOffset);
    }

    /**
     * 设置目标文件
     *
     * @param target 目标文件
     * @param append 是否是APPEND模式
     * @return this
     * @throws IOException 目标文件校验失败
     */
    public static MappedBufferStorage target(File target, boolean append) throws IOException {
        // 检查文件有效性
        checkFileValidity(target);
        int offset = append ? (int) target.length() : -1;
        return target(target, offset);
    }

    /**
     * 创建映射存储实例
     *
     * @param target 目标文件，默认覆盖写入模式
     * @return this
     * @throws IOException 校验目标文件失败
     */
    public static MappedBufferStorage target(File target) throws IOException {
        return target(target, false);
    }

    /**
     * 创建映射存储实例
     *
     * @param path   目标文件路径
     * @param append 是否APPEND模式
     * @return this
     * @throws IOException 目标文件校验失败
     */
    public static MappedBufferStorage target(String path, boolean append) throws IOException {
        return target(new File(path), append);
    }

    /**
     * 创建映射存储实例
     *
     * @param path 目标文件，默认覆盖写入模式
     * @return this
     * @throws IOException 目标文件校验失败
     */
    public static MappedBufferStorage target(String path) throws IOException {
        return target(path, false);
    }

    /**
     * 检查文件的有效性
     *
     * @param file File
     * @throws IOException 文件或文件夹创建IO异常
     */
    private static void checkFileValidity(File file) throws IOException {
        if (file.isDirectory()) {
            throw new IOException(String.format("file path %s is directory", file.getCanonicalPath()));
        }

        File parentFile = file.getParentFile();
        if (!parentFile.exists() && !parentFile.mkdirs()) {
            throw new IOException(String.format("path %s parent create fail", parentFile.getCanonicalPath()));
        }

        if (file.exists() && !file.canWrite()) {
            throw new IOException(String.format("file %s write access denied", file.getCanonicalPath()));
        }

        if (!file.exists() && !file.createNewFile()) {
            throw new IOException(String.format("file %s create failed", file.getCanonicalPath()));
        }
    }

    /**
     * 从新定位到指定位置
     *
     * @param position 新访问位置
     * @throws IOException 定位位置IO异常
     */
    public void seek(int position) throws IOException {
        checkArgument(position >= 0, "position must >= 0");
        channel.position(position);
    }

    /**
     * 设置文件的长度
     *
     * @param newLength 新文件长度
     * @throws IOException 设置长度IO异常
     */
    public void setLength(int newLength) throws IOException {
        checkArgument(newLength >= 0, "length must >= 0");
        channel.truncate(newLength);
    }

    /**
     * 获取文件的长度大小
     *
     * @throws IOException 获取长度IO异常
     */
    public int getLength() throws IOException {
        return (int) channel.size();
    }

    /**
     * 拷贝从buffer偏移offset的len大小数据写入
     *
     * @param buffer 源数据
     * @param offset 源数据偏移
     * @param len    拷贝的长度
     * @return this
     * @throws IOException 缓存获取数据或者写入拷贝的时候发生异常
     */
    public MappedBufferStorage write(byte[] buffer, int offset, int len) throws Exception {
        int write = writeInternal(buffer, offset, len);
        record(write);
        return this;
    }

    /**
     * 拷贝从buffer偏移offset的len大小数据写入
     *
     * @param buffer 源数据
     * @param offset 源数据偏移
     * @param len    拷贝的长度
     * @return 写入的大小
     * @throws IOException 缓存获取数据或者写入拷贝的时候发生异常
     */
    private int writeInternal(byte[] buffer, int offset, int len) throws Exception {
        int remaining = len - offset;
        int position = (int) channel.position();
        int bufOffset = offset;
        while (remaining > 0) {
            int mapSize = Math.min(remaining, 8192);
            MappedByteBuffer mappedBF = channel.map(FileChannel.MapMode.READ_WRITE, position, mapSize);
            mappedBF.put(buffer, bufOffset, mapSize);
            bufOffset += mapSize;
            position += mapSize;
            remaining -= mapSize;

            channel.position(position);
            // unmap
            unmapMappedByteBuffer(mappedBF);
        }

        return bufOffset - offset;
    }

    /**
     * 写入buffer中的数据
     *
     * @param buffer 数据
     * @return this
     * @throws Exception 写入时发生IO异常，或者文件映射和反映射发生异常
     */
    public MappedBufferStorage write(byte[] buffer) throws Exception {
        checkArgument(buffer != null, "buffer can't be null");
        int write = writeInternal(buffer, 0, buffer.length);
        record(write);
        return this;
    }

    /**
     * 写入buffer中的数据
     *
     * @param sourceStr 字符串数据
     * @return this
     * @throws IOException 写入时发生IO异常
     */
    public MappedBufferStorage write(String sourceStr) throws Exception {
        checkArgument(sourceStr != null, "source string can't be null");
        byte[] bytes = sourceStr.getBytes(StandardCharsets.UTF_8);
        return write(bytes);
    }

    /**
     * 从输入流获取数据写入
     *
     * @param inputStream 输入数据流
     * @return this
     * @throws IOException 输入流获取数据或者写入拷贝的时候发生异常，关闭源输入流异常
     */
    public MappedBufferStorage copyFrom(InputStream inputStream) throws Exception {
        checkArgument(inputStream != null, "input stream can't be null");
        // 文件输入流的处理
        if (inputStream instanceof FileInputStream) {
            FileInputStream fis = (FileInputStream) inputStream;
            int transferCount = copyFromFileStream(fis, 0, fis.available());
            record(transferCount);
            return this;
        }

        int count = copyFromStream(inputStream);
        // 记录统计写入的大小
        record(count);
        return this;
    }

    /**
     * 从输入流的偏移offset处拷贝数据写入
     *
     * @param inputStream 输入流
     * @param insOffset   输入流偏移
     * @param len         拷贝大小
     * @return this
     * @throws IOException 从源输入流获取或者写入IO异常，关闭源输入流异常
     */
    public MappedBufferStorage copyFrom(InputStream inputStream, int insOffset, int len) throws Exception {
        checkArgument(inputStream != null, "input stream can't be null");
        checkArgument(insOffset >= 0, "input stream offset must >= 0");
        checkArgument(len > 0, "len must > 0");

        // 文件输入流的处理
        if (inputStream instanceof FileInputStream) {
            FileInputStream fis = (FileInputStream) inputStream;
            int transferCount = copyFromFileStream(fis, insOffset, len);
            record(transferCount);
            return this;
        }

        BufferedInputStream bis = new BufferedInputStream(inputStream);
        int count = 0;
        int read = 0;
        while ((read = bis.read()) != -1) {
            count++;
            if (count >= insOffset) {
                break;
            }
        }
        if (read != -1) {
            // 从流获取
            count += copyFromStream(bis);
        }
        // 记录统计写入的大小
        record(count);
        return this;
    }

    private int copyFromStream(InputStream inputStream) throws Exception {
        checkArgument(inputStream != null, "input stream can't be null");
        BufferedInputStream bis = inputStream instanceof BufferedInputStream
                ? (BufferedInputStream) inputStream : new BufferedInputStream(inputStream);
        byte[] buffer = new byte[8192];
        int count = 0;
        int len = 0;
        while ((len = bis.read(buffer, 0, buffer.length)) != -1) {
            int write = writeInternal(buffer, 0, len);
            count += write;
        }
        return count;
    }

    private int copyFromFileStream(FileInputStream inputStream, int position, int len) throws Exception {
        checkArgument(position >= 0, "position must >= 0");
        checkArgument(len >= 0, "len must > 0");
        if (len == 0) {
            return 0;
        }

        FileChannel insChannel = inputStream.getChannel();
        insChannel.position(position);
        int insPosition = position;
        int chPosition = (int) channel.position();
        int remaining = len;
        while (remaining > 0) {
            int mapSize = Math.min(remaining, 8192);
            MappedByteBuffer inMappedBF = insChannel.map(FileChannel.MapMode.READ_ONLY, insPosition, mapSize);
            MappedByteBuffer chMappedBF = channel.map(FileChannel.MapMode.READ_WRITE, chPosition, mapSize);
            chMappedBF.put(inMappedBF);

            insPosition += mapSize;
            chPosition += mapSize;
            remaining -= mapSize;

            // unmap
            unmapMappedByteBuffer(inMappedBF);
            unmapMappedByteBuffer(chMappedBF);
        }
        return len - remaining;
    }

    /**
     * 从源文件拷贝所有数据写入
     *
     * @param source 源文件
     * @return this
     * @throws Exception 从源文件获取数据或者写入IO异常，关闭源输入流异常
     */
    public MappedBufferStorage copyFrom(File source) throws Exception {
        FileInputStream fis = new FileInputStream(source);
        copyFrom(fis);
        // 关闭文件
        fis.close();
        return this;
    }

    /**
     * 从输入文件File中偏移offset获取len大小的数据写入
     *
     * @param sourceFile 源数据文件
     * @param sfOffset   输入文件偏移
     * @param len        拷贝数据大小
     * @return this
     * @throws IOException 从源数据获取文件或者写入文件IO异常，关闭源输入异常
     */
    public MappedBufferStorage copyFrom(File sourceFile, int sfOffset, int len) throws Exception {
        checkArgument(sourceFile != null, "source file can't be null");
        checkArgument(sfOffset >= 0, "offset must >= 0");
        checkArgument(len > 0, "len must > 0");
        FileInputStream fis = new FileInputStream(sourceFile);
        copyFrom(fis, sfOffset, len);
        // 关闭文件
        fis.close();
        return this;
    }

    /**
     * 拷贝path指定的文件写入
     *
     * @param path 输入文件path
     * @return this
     * @throws IOException 从Path获取文件或者写入拷贝数据IO异常，关闭源输入异常
     */
    public MappedBufferStorage copyFrom(String path) throws Exception {
        File file = new File(path);
        if (!file.exists() || file.isDirectory() || !file.canRead()) {
            throw new IOException("invalid file path");
        }
        return copyFrom(file);
    }

    /**
     * 从path指定的文件的offset偏移处获取len大小数据写入
     *
     * @param path     数据源，来=来自于path的文件
     * @param sfOffset 源文件offset偏移
     * @param len      拷贝的大小
     * @return this
     * @throws IOException 在访问源文件或者写入数据时，发生IO异常，关闭源输入异常
     */
    public MappedBufferStorage copyFrom(String path, int sfOffset, int len) throws Exception {
        File file = new File(path);
        if (!file.exists() || file.isDirectory() || !file.canRead()) {
            throw new IOException("invalid file path");
        }
        return copyFrom(file, sfOffset, len);
    }

    private void record(int len) {
        this.writeBytesTotal += len;
        this.preWriteCount = len;
    }

    /**
     * 写入的数据总大小
     *
     * @return 总大小
     */
    public int getWriteBytesTotal() {
        return writeBytesTotal;
    }

    /**
     * 获取上次写入的大小
     *
     * @return 字节大小
     */
    public int getPreWriteCount() {
        return preWriteCount;
    }

    @Override
    public void close() throws IOException {
        if (channel != null) {
            try {
                channel.close();
            } catch (Exception exp) {
                logger.error("file channel close failed: [{}]", exp.getMessage());
            }
        }
    }

    private void unmapMappedByteBuffer(MappedByteBuffer mappedBF)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method unmap = FileChannelImpl.class.getDeclaredMethod("unmap", MappedByteBuffer.class);
        unmap.setAccessible(true);
        unmap.invoke(null, mappedBF);
    }
}
