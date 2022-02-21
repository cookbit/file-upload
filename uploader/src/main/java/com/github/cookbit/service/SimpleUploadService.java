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

import com.github.cookbit.config.FileStorageConfig;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 简单上传服务
 *
 * @auther 961374431@qq.com
 * @date 2022年02月21日
 */
@Service
public class SimpleUploadService {
    private static final Logger logger = LoggerFactory.getLogger(SimpleUploadService.class);
    @Autowired
    FileStorageConfig config;

    /**
     * 上传多个文件
     *
     * @param files 文件列表
     * @return 上传文件的获取路径
     */
    public List<String> uploadFiles(List<MultipartFile> files) {
        List<String> successFiles = new ArrayList<>();
        if (CollectionUtils.isEmpty(files)) {
            return successFiles;
        }

        // 创建watch用于监控上传事件花费
        StopWatch watch = new StopWatch("upload files");
        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();
            watch.start(String.format(Locale.ROOT, "upload  file: [%s]", originalFilename));
            String url = uploadFile(file);
            logger.info("upload file: [{}] return file url: [{}]", originalFilename, url);
            successFiles.add(url);
            watch.stop();
        }
        logger.info(watch.prettyPrint());
        return successFiles;
    }

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 上传文件的获取路径
     */
    public String uploadFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        try {

        } catch (Exception exp) {
            logger.error("upload file [{}] failed: [{}]", originalFilename, exp.getMessage());
        }
        // TODO
        return "";
    }

    private String getStoragePath(String originName) {
        // TODO
        return "";
    }
}
