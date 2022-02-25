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

package com.github.cookbit;

import com.github.cookbit.service.RandomAccessStorage;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RandomAccess 存储写入测试
 *
 * @auther 961374431@qq.com
 * @date 2022年02月25日
 */
public class RandomAccessStorageTest {
    private static final Logger logger = LoggerFactory.getLogger(RandomAccessStorageTest.class);

    private static final String sourcePath = "C:/Users/96137/desktop/test/source.txt";
    private static final String destPath = "C:/Users/96137/desktop/test/dest.txt";

    /**
     * 写入测试
     */
    @Test
    public void copyFromPathTest() {
        logger.info("copy from path test");
        long start = System.currentTimeMillis();
        try (RandomAccessStorage storage = RandomAccessStorage.target(destPath, true)
                .copyFrom(sourcePath)) {
        } catch (Exception exp) {
            logger.error("storage exception: [{}]", exp.getMessage());
        }
        logger.info("copy from path test cost: [{}]ms", System.currentTimeMillis() - start);
    }
}
