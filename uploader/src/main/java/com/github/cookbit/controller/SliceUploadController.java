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

package com.github.cookbit.controller;

import com.github.jinzhaosn.common.model.ResultVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文件切片上传
 *
 * @auther 961374431@qq.com
 * @date 2022年02月27日
 */
@RestController
@RequestMapping("/v1/sliceUpload")
public class SliceUploadController {
    private static final Logger logger = LoggerFactory.getLogger(SliceUploadController.class);

    @PostMapping("/do")
    public ResultVo<?> singleUpload() {
        // TODO
        return ResultVo.success();
    }
}
