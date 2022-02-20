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

package com.github.jinzhaosn.common.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 基础线程工厂
 *
 * @auther 961374431@qq.com
 * @date 2022年01月19日
 */
public class DefaultThreadFactory implements ThreadFactory {
    private static final Logger logger = LoggerFactory.getLogger(DefaultThreadFactory.class);
    private final ThreadGroup threadGroup;
    private final String threadNamePrefix;
    private final AtomicLong threadIndex = new AtomicLong(0);

    public DefaultThreadFactory(String groupName, String namePrefix) {
        this.threadGroup = validGroupName(groupName) ? new ThreadGroup(groupName) : null;
        this.threadNamePrefix = namePrefix;
    }

    @Override
    public Thread newThread(Runnable run) {
        return new Thread(threadGroup, run, threadNamePrefix);
    }

    private boolean validGroupName(String groupName) {
        return groupName != null && groupName.length() > 0;
    }

    public ThreadGroup getThreadGroup() {
        return threadGroup;
    }

    public String getThreadNamePrefix() {
        return threadNamePrefix;
    }

    /**
     * 线程工厂builder
     *
     * @auther 961374431@qq.com
     * @date 2022年01月19日
     */
    public static class ThreadFactoryBuilder {
        private String groupName;
        private String threadNamePrefix = "Thread" + System.currentTimeMillis();

        public ThreadFactoryBuilder threadGroup(String threadGroupName) {
            this.groupName = threadGroupName;
            return this;
        }

        public ThreadFactoryBuilder threadNamePrefix(String namePrefix) {
            this.threadNamePrefix = namePrefix;
            return this;
        }

        public DefaultThreadFactory build() {
            return new DefaultThreadFactory(groupName, threadNamePrefix);
        }
    }
}
