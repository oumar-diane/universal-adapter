/*
 * Licensed to the  Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the  License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.zwangine.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zenithblox.spi;

/**
 * A shared scheduler to run small period tasks, such as updating internal statistics, or for custom components to have
 * a background task.
 *
 * For example the AWS vault is using this to periodically check for secrets refreshed in AWS to trigger Zwangine to reload
 * to use the updated secrets.
 */
public interface PeriodTaskScheduler {

    /**
     * Schedules the period task.
     *
     * @param task   the period task (the task can extend {@link org.zenithblox.support.service.ServiceSupport} to
     *               have lifecycle)
     * @param period the interval (approximate) in millis, between running the task
     */
    void schedulePeriodTask(Runnable task, long period);

    /**
     * Schedules the task once so the task keeps running always. This is to be used for tasks that is not triggered in
     * periods, but need to run forever.
     *
     * @param task the task (the task can extend {@link org.zenithblox.support.service.ServiceSupport} to have
     *             lifecycle)
     */
    void scheduledTask(Runnable task);

    /**
     * Gets an existing task by a given type, assuming there is only one task of the given type.
     *
     * @param  type the type of the task
     * @return      the task, or <tt>null</tt> if no tasks exists
     */
    <T> T getTaskByType(Class<T> type);

}
