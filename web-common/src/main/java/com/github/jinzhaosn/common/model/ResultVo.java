/*
 *    Copyright 2021-2022 jinzhaosn
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.github.jinzhaosn.common.model;

import com.github.jinzhaosn.common.enums.IBaseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 结果Vo
 *
 * @auther 961374431@qq.com
 * @date 2022年01月03日
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultVo<T> {
    /**
     * 成功
     */
    public static final Integer SUCCESS = 200;
    /**
     * 失败
     */
    public static final Integer FAIL = 500;

    private Integer code;
    private String message;
    private T data;

    /**
     * 构建结果
     *
     * @param code    返回状态
     * @param message 消息
     * @param data    数据
     * @param <U>     类型
     * @return 结果
     */
    public static <U> ResultVo<U> of(Integer code, String message, U data) {
        return new ResultVo<>(code, message, data);
    }

    /**
     * 成功
     *
     * @return 成功结果
     */
    public static ResultVo<?> success() {
        return of(SUCCESS, "success", null);
    }

    /**
     * 成功
     *
     * @param message 成功消息
     * @param data    成功数据
     * @param <U>     数据类型
     * @return 成功结果
     */
    public static <U> ResultVo<U> success(String message, U data) {
        return of(SUCCESS, message, data);
    }

    /**
     * 失败
     *
     * @return 失败结果
     */
    public static ResultVo<?> fail() {
        return of(FAIL, "fail", null);
    }

    /**
     * 失败结果
     *
     * @param message 失败消息
     * @param data    失败数据
     * @param <U>     失败数据类型
     * @return 失败结果
     */
    public static <U> ResultVo<U> fail(String message, U data) {
        return of(FAIL, message, data);
    }

    /**
     * 从状态枚举构建结果
     *
     * @param statusEnum 状态枚举
     * @return 结果
     */
    public static ResultVo<?> from(IBaseStatus statusEnum) {
        return of(statusEnum.getCode(), statusEnum.getMessage(), null);
    }

    /**
     * 从状态枚举构建结果
     *
     * @param statusEnum 状态枚举
     * @param data       数据
     * @param <U>        数据类型
     * @return 结果
     */
    public static <U> ResultVo<U> from(IBaseStatus statusEnum, U data) {
        return of(statusEnum.getCode(), statusEnum.getMessage(), data);
    }
}
