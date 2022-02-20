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

import com.github.jinzhaosn.common.model.ResultVo;
import com.github.jinzhaosn.common.enums.CommonEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;

/**
 * 全局异常拦截处理
 *
 * @auther 961374431@qq.com
 * @date 2022年01月03日
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * MethodArgumentNotValidException 异常处理
     *
     * @param exception 异常
     * @return 通用结果
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResultVo<?> validationBodyException(MethodArgumentNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();
        return ResultVo.of(CommonEnum.BAD_REQUEST.getCode(), getBindingResult(bindingResult), null);
    }

    /**
     * BindException异常
     *
     * @param exception 异常
     * @return 通用结果
     */
    @ExceptionHandler(value = BindException.class)
    public ResultVo<?> validationBindException(BindException exception) {
        BindingResult bindingResult = exception.getBindingResult();
        return ResultVo.of(CommonEnum.BAD_REQUEST.getCode(), getBindingResult(bindingResult), null);
    }

    /**
     * ConstraintViolationException异常
     *
     * @param exception 异常
     * @return 异常结果
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResultVo<?> ConstraintViolationExceptionHandler(ConstraintViolationException exception) {
        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
        StringBuilder builder = new StringBuilder();
        for (ConstraintViolation<?> item : violations) {
            builder.append(item.getMessage());
            builder.append("<br/>");
        }
        return ResultVo.fail(builder.toString(), null);
    }

    /**
     * HttpMessageConversionException异常
     *
     * @param exception 异常
     * @return 通用结果
     */
    @ExceptionHandler(value = HttpMessageConversionException.class)
    public ResultVo<?> parameterTypeException(HttpMessageConversionException exception) {
        return ResultVo.from(CommonEnum.BAD_REQUEST);
    }

    /**
     * Exception异常
     *
     * @param exception 异常
     * @return 通用结果
     */
    @ExceptionHandler(value = Exception.class)
    public ResultVo<?> handle(Exception exception) {
        logger.error("system exception: [{}]", exception.getMessage());
        return ResultVo.from(CommonEnum.INTERNAL_SERVER_ERROR);
    }

    private String getBindingResult(BindingResult bindingResult) {
        List<ObjectError> errorList = bindingResult.getAllErrors();

        StringBuilder builder = new StringBuilder();
        for (ObjectError error : errorList) {
            builder.append(error.getDefaultMessage()).append("<br/>");
        }
        return builder.toString();
    }
}
