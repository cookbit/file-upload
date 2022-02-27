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

package com.github.cookbit.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

import static com.github.cookbit.util.CheckUtil.checkArgument;

/**
 * 随机访问文件存储写入
 *
 * @auther 961374431@qq.com
 * @date 2022年02月23日
 */
public class RandomAccessStorage implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(RandomAccessStorage.class);
    private RandomAccessFile targetFile;
    private int writeBytesTotal;
    private int preWriteCount;

    private RandomAccessStorage() {
    }

    /**
     * 目标文件
     *
     * @param target     文件路径
     * @param seekOffset 写入偏移，当偏移小于0时，清空目标文件
     * @return this
     * @throws IOException 目标文件校验失败
     */
    public static RandomAccessStorage target(File target, int seekOffset) throws IOException {
        // 检查文件有效性
        checkFileValidity(target);

        // 初始化
        RandomAccessStorage storage = new RandomAccessStorage();
        storage.targetFile = new RandomAccessFile(target, "rw");
        if (seekOffset >= 0) {
            storage.targetFile.seek(seekOffset);
        } else {
            // 清空目标文件
            storage.targetFile.setLength(0);
        }
        storage.writeBytesTotal = 0;
        storage.preWriteCount = 0;
        return storage;
    }

    /**
     * 目标文件
     *
     * @param path       文件路径
     * @param seekOffset 写入偏移，当偏移小于0时，清空目标文件
     * @return this
     * @throws IOException 目标文件校验失败
     */
    public static RandomAccessStorage target(String path, int seekOffset) throws IOException {
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
    public static RandomAccessStorage target(File target, boolean append) throws IOException {
        // 检查文件有效性
        checkFileValidity(target);
        int offset = append ? (int) target.length() : -1;
        return target(target, offset);
    }

    /**
     * 设置目标文件
     *
     * @param target 目标文件，默认覆盖写入模式
     * @return this
     * @throws IOException 校验目标文件失败
     */
    public static RandomAccessStorage target(File target) throws IOException {
        return target(target, false);
    }

    /**
     * 设置目标文件
     *
     * @param path   目标文件路径
     * @param append 是否APPEND模式
     * @return this
     * @throws IOException 目标文件校验失败
     */
    public static RandomAccessStorage target(String path, boolean append) throws IOException {
        return target(new File(path), append);
    }

    /**
     * 设置目标文件
     *
     * @param path 目标文件，默认覆盖写入模式
     * @return this
     * @throws IOException 目标文件校验失败
     */
    public static RandomAccessStorage target(String path) throws IOException {
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
        targetFile.seek(position);
    }

    /**
     * 设置文件的长度
     *
     * @param newLength 新文件长度
     * @throws IOException 设置长度IO异常
     */
    public void setLength(int newLength) throws IOException {
        checkArgument(newLength >= 0, "length must >= 0");
        targetFile.setLength(newLength);
    }

    /**
     * 获取文件的长度大小
     *
     * @throws IOException 获取长度IO异常
     */
    public int getLength() throws IOException {
        return (int) targetFile.length();
    }

    /**
     * 写入buffer中的数据
     *
     * @param buffer 数据
     * @return this
     * @throws IOException 写入时发生IO异常
     */
    public RandomAccessStorage write(byte[] buffer) throws IOException {
        targetFile.write(buffer);

        // 记录统计写入的大小
        record(buffer.length);
        return this;
    }

    /**
     * 写入buffer中的数据
     *
     * @param sourceStr 字符串数据
     * @return this
     * @throws IOException 写入时发生IO异常
     */
    public RandomAccessStorage write(String sourceStr) throws IOException {
        checkArgument(sourceStr != null, "source string can't be null");
        byte[] bytes = sourceStr.getBytes(StandardCharsets.UTF_8);
        targetFile.write(bytes);

        // 记录统计写入的大小
        record(bytes.length);
        return this;
    }

    /**
     * 拷贝从buffer偏移offset的len大小数据写入
     *
     * @param buffer    源数据
     * @param bufOffset 源数据偏移
     * @param len       拷贝的长度
     * @return this
     * @throws IOException 缓存获取数据或者写入拷贝的时候发生异常
     */
    public RandomAccessStorage write(byte[] buffer, int bufOffset, int len) throws IOException {
        targetFile.write(buffer, bufOffset, len);

        // 记录统计写入的大小
        record(len);
        return this;
    }

    /**
     * 从输入流获取数据写入
     *
     * @param inputStream 输入数据流
     * @return this
     * @throws IOException 输入流获取数据或者写入拷贝的时候发生异常，关闭源输入流异常
     */
    public RandomAccessStorage copyFrom(InputStream inputStream) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        byte[] buffer = new byte[1024];
        int count = 0;
        int len = 0;
        while ((len = bis.read(buffer, 0, buffer.length)) != -1) {
            write(buffer, 0, len);
            count += len;
        }
        // 记录统计写入的大小
        record(count);
        // 关闭源输入流
        inputStream.close();
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
    public RandomAccessStorage copyFrom(InputStream inputStream, int insOffset, int len) throws IOException {
        checkArgument(inputStream != null, "input stream can't be null");
        checkArgument(insOffset >= 0, "input stream offset must >= 0");
        checkArgument(len > 0, "len must > 0");

        BufferedInputStream bis = new BufferedInputStream(inputStream);
        int tempOffset = insOffset;
        int count = 0;
        int buffer = 0;
        while ((buffer = bis.read()) != -1) {
            // 输入流丢弃前偏移量大小数据
            if (tempOffset > 0) {
                tempOffset--;
                continue;
            }
            // 写入数据
            targetFile.write(buffer);
            count++;
            // 长度等于len时跳出
            if (count >= len) {
                break;
            }
        }

        // 记录统计写入的大小
        record(count);
        // 关闭输入流
        inputStream.close();
        return this;
    }

    /**
     * 从源文件拷贝所有数据写入
     *
     * @param source 源文件
     * @return this
     * @throws IOException 从源文件获取数据或者写入IO异常，关闭源输入流异常
     */
    public RandomAccessStorage copyFrom(File source) throws IOException {
        return copyFrom(new FileInputStream(source));
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
    public RandomAccessStorage copyFrom(File sourceFile, int sfOffset, int len) throws IOException {
        checkArgument(sourceFile != null, "source file can't be null");
        checkArgument(sfOffset >= 0, "offset must >= 0");
        checkArgument(len > 0, "len must > 0");

        RandomAccessFile source = new RandomAccessFile(sourceFile, "r");
        source.seek(sfOffset);
        int read = 0; // 读取的大小
        int remaining = len; // 剩余需要写入的大小
        byte[] buffer = new byte[1024];
        while ((read = source.read(buffer, 0, buffer.length)) != -1) {
            int writeNum = Math.min(read, remaining);
            targetFile.write(buffer, 0, writeNum);
            remaining -= writeNum;
            if (remaining <= 0) {
                break;
            }
        }

        // 记录统计写入的大小
        record(len - remaining);
        // 关闭输入文件
        source.close();
        return this;
    }

    /**
     * 拷贝path指定的文件写入
     *
     * @param path 输入文件path
     * @return this
     * @throws IOException 从Path获取文件或者写入拷贝数据IO异常，关闭源输入异常
     */
    public RandomAccessStorage copyFrom(String path) throws IOException {
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
    public RandomAccessStorage copyFrom(String path, int sfOffset, int len) throws IOException {
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
        if (targetFile != null) {
            try {
                targetFile.close();
            } catch (Exception exp) {
                logger.error("file close failed: [{}]", exp.getMessage());
            }
        }
    }
}
