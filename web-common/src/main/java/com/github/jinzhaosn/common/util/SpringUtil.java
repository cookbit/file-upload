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

package com.github.jinzhaosn.common.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * spring工具类
 *
 * @auther 961374431@qq.com
 * @date 2022年02月13日
 */
@Component
public class SpringUtil implements ApplicationContextAware {
    private SpringUtil() {
    }

    private static ApplicationContext context;

    /**
     * 通过类型获取Bean
     *
     * @param clazz 类
     * @param <T>   类泛型
     * @return Bean
     */
    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    /**
     * 通过类型以及Bean名称获取Bean
     *
     * @param beanName Bean名称
     * @param clazz    类
     * @param <T>      类泛型
     * @return Bean
     */
    public static <T> T getBean(String beanName, Class<T> clazz) {
        return context.getBean(beanName, clazz);
    }

    /**
     * 获取指定类别所有的Bean
     *
     * @param clazz 类
     * @param <T>   泛型
     * @return Bean列表，找不到返回空列表
     */
    public static <T> List<T> getBeansOfType(Class<T> clazz) {
        Map<String, T> beanMaps = context.getBeansOfType(clazz);
        return Optional.of(beanMaps).map(Map::values)
                .map(ArrayList::new).map(Collections::unmodifiableList)
                .orElseGet(Collections::emptyList);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
