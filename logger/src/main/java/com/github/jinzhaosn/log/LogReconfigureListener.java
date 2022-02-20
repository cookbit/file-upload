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

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.logging.LoggingApplicationListener;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.ConfigurableEnvironment;


/**
 * log配置
 *
 * @auther 961374431@qq.com
 * @date 2022年01月02日
 */
public class LogReconfigureListener implements GenericApplicationListener {
    private static final Class<?>[] EVENT_TYPES = {ApplicationEnvironmentPreparedEvent.class};

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationEnvironmentPreparedEvent) {
            // 设置环境
            ConfigurableEnvironment environment = ((ApplicationEnvironmentPreparedEvent) event).getEnvironment();
            SpringContextHolder.setEnvironment(environment);
        }
    }

    @Override
    public boolean supportsEventType(ResolvableType resolvableType) {
        return this.isAssignableFrom(resolvableType.getRawClass(), EVENT_TYPES);
    }

    private boolean isAssignableFrom(Class<?> type, Class<?>... supportedTypes) {
        if (type != null) {
            int size = supportedTypes.length;
            for (int index = 0; index < size; ++index) {
                Class<?> supportedType = supportedTypes[index];
                if (supportedType.isAssignableFrom(type)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return LoggingApplicationListener.DEFAULT_ORDER - 1;
    }
}