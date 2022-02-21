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

package com.github.cookbit.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 文件存储路径
 *
 * @auther 961374431@qq.com
 * @date 2022年02月21日
 */
@Configuration
@ConfigurationProperties(prefix = "simple.storage.config")
@Data
public class FileStorageConfig {
    /** 服务器基本存储目录 **/
    private String basicPath;

    /** 文件存储访问基础URL **/
    private String accessUrl;
}
