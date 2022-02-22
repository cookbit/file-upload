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

package com.github.cookbit.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * 文件记录
 *
 * @auther 961374431@qq.com
 * @date 2022年02月22日
 */
@Data
@Builder
@TableName("t_storage_file_record")
public class FileRecordEntity {
    @TableId(value = "id")
    private Long id; // 主键

    @TableField(value = "file_id")
    private String fileId; // 文件ID

    @TableField(value = "storage_path")
    private String storagePath; // 存储路径

    @TableField(value = "create_time")
    private Date createTime; // 创建时间

    @TableField(value = "create_user")
    private String createUser; // 创建人

    @TableField(value = "modify_time")
    private Date modifyTime; // 修改时间

    @TableField(value = "modify_user")
    private String modifyUser; // 修改人
}
