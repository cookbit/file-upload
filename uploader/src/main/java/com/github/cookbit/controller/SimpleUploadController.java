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

import com.github.cookbit.service.SimpleUploadService;
import com.github.jinzhaosn.common.model.ResultVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * upload controller
 *
 * @auther 961374431@qq.com
 * @date 2022年02月21日
 */
@RestController
@RequestMapping("/v1/simpleUpload/")
public class SimpleUploadController {
    private static final Logger logger = LoggerFactory.getLogger(SimpleUploadController.class);
    @Autowired SimpleUploadService uploadService;

    /**
     * 上传单文件
     *
     * @param file 文件
     * @return 上传结果
     */
    @PostMapping("/single")
    public ResultVo<?> uploadFile(@RequestParam("file") MultipartFile file) {
        logger.info("upload single file");
        List<String> successFiles = uploadService.uploadFiles(Collections.singletonList(file));
        logger.info("success files: [{}]", successFiles);
        return ResultVo.success("success", successFiles);
    }

    /**
     * 上传多文件
     *
     * @param files 文件数组
     * @return 上传结果
     */
    @PostMapping("/multi")
    public ResultVo<?> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        logger.info("upload files");
        List<String> successFiles = uploadService.uploadFiles(Arrays.asList(files));
        logger.info("success files: [{}]", successFiles);
        return ResultVo.success("success", successFiles);
    }
}
