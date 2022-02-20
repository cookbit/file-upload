/**
 * Copyright 2021-2022 jinzhaosn
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.jinzhaosn.common.exception;

import com.github.jinzhaosn.common.enums.IBaseStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 通用服务层异常
 *
 * @auther 961374431@qq.com
 * @date 2022年01月03日
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@ToString(callSuper = true)
public class BizException extends RuntimeException implements IBaseStatus {
    private final Integer code;
    private final String message;

    public BizException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BizException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public BizException(IBaseStatus statusEnum) {
        super(statusEnum.getMessage());
        this.code = statusEnum.getCode();
        this.message = statusEnum.getMessage();
    }

    public BizException(IBaseStatus statusEnum, Throwable cause) {
        super(statusEnum.getMessage(), cause);
        this.code = statusEnum.getCode();
        this.message = statusEnum.getMessage();
    }
}
