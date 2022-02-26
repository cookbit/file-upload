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

package com.github.cookbit.util;

import com.github.cookbit.exception.VerifyException;

import java.util.function.Predicate;

/**
 * 校验工具类
 *
 * @auther 961374431@qq.com
 * @date 2022年02月26日
 */
public class CheckUtil {
    private CheckUtil() {
    }


    /**
     * 检查是否满足条件，在满足条件时返回自身，否则抛出异常
     *
     * @param data      被检查对象
     * @param predicate 检查条件
     * @param message   异常消息
     * @param <T>       类型
     * @return 在条件满足时返回数据
     */
    public static <T> T checkThrow(T data, Predicate<T> predicate, String message) {
        if (!predicate.test(data)) {
            throw new VerifyException(message);
        }
        return data;
    }

    /**
     * 检查是否为真
     *
     * @param checked 是否为真
     * @param message 不为真异常消息
     */
    public static void checkTrue(boolean checked, String message) {
        if (!checked) {
            throw new VerifyException(message);
        }
    }

    /**
     * 检查参数
     *
     * @param checked 检查结果
     * @param message 抛出异常信息
     */
    public static void checkArgument(boolean checked, String message) {
        if (!checked) {
            throw new IllegalArgumentException(message);
        }
    }
}
