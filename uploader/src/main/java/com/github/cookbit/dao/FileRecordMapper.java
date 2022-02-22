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

package com.github.cookbit.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.cookbit.model.FileRecordEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件记录DAO
 *
 * @auther 961374431@qq.com
 * @date 2022年02月22日
 */
@Mapper
public interface FileRecordMapper extends BaseMapper<FileRecordEntity> {
}
