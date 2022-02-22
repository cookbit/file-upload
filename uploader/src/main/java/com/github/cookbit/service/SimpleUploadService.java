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
import com.github.cookbit.dao.FileRecordMapper;
import com.github.cookbit.model.FileRecordEntity;
import com.github.jinzhaosn.common.util.SpringUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

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
    @Autowired
    FileRecordMapper recordMapper;

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
            Optional.ofNullable(url).ifPresent(successFiles::add);
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
            // 目前策略存储到本地
            File storageFile = getStorageFile(originalFilename);
            requireNonNull(storageFile, String.format(Locale.ROOT, "storage file for [%s] null", originalFilename));
            FileUtils.copyToFile(file.getInputStream(), storageFile);

            // 生成文件ID并存储在数据库中
            String fileId = generateFileId();

            SimpleUploadService thisService = SpringUtil.getBean(SimpleUploadService.class);
            FileRecordEntity fileRecord = FileRecordEntity.builder().fileId(fileId)
                    .storagePath(storageFile.getCanonicalPath()).build();
            logger.info("file record: [{}]", fileRecord);
            int add = thisService.insertStorageFileRecord(fileRecord);
            logger.info("insert storage file record: [{}], add: [{}]", fileId, add);
            return add > 0 ? getUrl(fileId) : null;
        } catch (Exception exp) {
            logger.error("upload file [{}] failed: [{}]", originalFilename, exp.getMessage());
        }
        return null;
    }

    private File getStorageFile(String originName) throws IOException {
        String basicPath = config.getBasicPath();
        File file = new File(basicPath);
        File parentFile = file.getCanonicalFile();
        logger.info("file path: [{}]", file.getCanonicalPath());
        if (!parentFile.exists() && !parentFile.mkdirs()) {
            return null;
        }
        return FileUtils.getFile(parentFile, originName);
    }

    private String generateFileId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private String getUrl(String fileId) {
        return String.format(Locale.ROOT, config.getAccessUrl(), fileId);
    }

    /**
     * 记录文件
     *
     * @param record 文件记录
     * @return 更新条目数
     */
    @Transactional(rollbackFor = Exception.class)
    public int insertStorageFileRecord(FileRecordEntity record) {
        return recordMapper.insert(record);
    }
}
