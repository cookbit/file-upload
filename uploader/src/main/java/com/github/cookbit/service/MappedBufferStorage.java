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

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.UUID;

/**
 * 采用IO映射进行文件写入
 *
 * @auther 961374431@qq.com
 * @date 2022年02月24日
 */
public class MappedBufferStorage {
    private MappedBufferStorage() {
    }

    private RandomAccessFile randomAccess;
    private FileChannel channel;

    /**
     * 创建映射存储实例
     *
     * @param targetFile 目标存储文件
     * @return 映射存储实例
     * @throws FileNotFoundException 文件找不到异常
     */
    public static MappedBufferStorage target(String targetFile) throws FileNotFoundException {
        File file = new File(targetFile);
        if (file.isDirectory() || !file.canRead()) {
            file = FileUtils.getFile(file, UUID.randomUUID().toString());
        }
        File parentFile = file.getParentFile();
        if (!parentFile.exists() && !parentFile.mkdirs()) {
            throw new FileNotFoundException(String.format("path"));
        }
        MappedBufferStorage storage = new MappedBufferStorage();
        storage.randomAccess = new RandomAccessFile(file, "rw");
        storage.channel = storage.randomAccess.getChannel();
        return storage;
    }
}
