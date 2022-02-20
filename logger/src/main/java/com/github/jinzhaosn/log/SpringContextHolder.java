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

package com.github.jinzhaosn.log;

import org.springframework.core.env.Environment;

/**
 * spring环境
 *
 * @auther 961374431@qq.com
 * @date 2022年01月03日
 */
public class SpringContextHolder {
    private SpringContextHolder() {

    }

    /**
     * 环境
     */
    private static Environment environment;

    /**
     * 获取环境
     *
     * @return 环境对象
     */
    public static Environment getEnvironment() {
        return SpringContextHolder.environment;
    }

    /**
     * 设置环境
     *
     * @param environment 环境对象
     */
    public static void setEnvironment(Environment environment) {
        SpringContextHolder.environment = environment;
    }
}
