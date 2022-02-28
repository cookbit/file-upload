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

import com.github.cookbit.storage.local.RandomAccessStorage;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * RandomAccess 存储写入测试
 *
 * @auther 961374431@qq.com
 * @date 2022年02月25日
 */
public class RandomAccessStorageTest {
    private static final Logger logger = LoggerFactory.getLogger(RandomAccessStorageTest.class);

    /**
     * 测试目标是文件path，并且是APPEND模式
     */
    @Test
    public void targetFileAppendTest() {
        final String sourcePath = "C:/Users/96137/desktop/test/source.txt";
        final String destPath = "C:/Users/96137/desktop/test/1/2/3.txt";

        logger.info("target path append test start");
        File targetFile = new File(destPath);
        try (RandomAccessStorage storage = RandomAccessStorage.target(targetFile, true)) {
            storage.copyFrom(sourcePath);
            storage.write("\nhello世界\n");
        } catch (Exception exp) {
            logger.error("target path append test exception: [{}]", exp.getMessage());
        }
        logger.info("target path append test end");
    }

    /**
     * 从文件拷贝写入测试
     */
    @Test
    public void copyFromPathTest() {
        final String sourcePath = "C:/Users/96137/desktop/test/source.txt";
        final String destPath = "C:/Users/96137/desktop/test/dest.txt";

        logger.info("copy from path test");
        long start = System.currentTimeMillis();
        try (RandomAccessStorage storage = RandomAccessStorage.target(destPath, false)) {
            storage.copyFrom(sourcePath);
        } catch (Exception exp) {
            logger.error("storage exception: [{}]", exp.getMessage());
        }
        logger.info("copy from path test cost: [{}]ms", System.currentTimeMillis() - start);
    }

    /**
     * 目标文件偏移测试
     */
    @Test
    public void targetFileOffsetTest() {
        final String sourcePath = "C:/Users/96137/desktop/test/source.txt";
        final String destPath = "C:/Users/96137/desktop/test/dest.txt";

        logger.info("target file offset test start");
        try (RandomAccessStorage storage = RandomAccessStorage.target(destPath, 10)) {
            storage.write("我是大帅哥\n");
            storage.copyFrom(sourcePath);
        } catch (Exception exp) {
            logger.error("storage exception: [{}]", exp.getMessage());
        }
        logger.info("target file offset test end");
    }

    /**
     * 写入字符串和bytes测试
     */
    @Test
    public void writeStringAndBytesTest() {
        final String sourcePath = "C:/Users/96137/desktop/test/source.txt";
        final String destPath = "C:/Users/96137/desktop/test/dest.txt";

        logger.info("write string and bytes start");
        try (RandomAccessStorage storage = RandomAccessStorage.target(destPath, false)){
            storage.write("这是一个测试");
            storage.write(new byte[]{'1', '2', '3'});
        } catch (Exception exp) {
            logger.error("storage exception: [{}]", exp.getMessage());
        }
        logger.info("write string and bytes end");
    }
}
