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

package com.github.jinzhaosn.common.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * 调度任务
 *
 * @auther 961374431@qq.com
 * @date 2022年02月12日
 */
public abstract class AbstractScheduleJob implements SchedulingConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(AbstractScheduleJob.class);

    /**
     * 注册任务
     *
     * @param taskRegistrar 任务注册器
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addTriggerTask(this::schedule, getTrigger());
    }

    public void schedule() {
        long startTimestamp = System.currentTimeMillis();
        logger.info("schedule start: [{}]", startTimestamp);
        doSchedule();
        logger.info("schedule cost: [{}] ms", System.currentTimeMillis() - startTimestamp);
    }

    /**
     * 调度任务
     */
    public abstract void doSchedule();

    /**
     * 触发器
     *
     * @return 触发器
     */
    public abstract Trigger getTrigger();
}
