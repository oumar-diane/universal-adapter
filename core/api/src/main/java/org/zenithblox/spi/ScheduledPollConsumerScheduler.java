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

import org.zenithblox.ZwangineContextAware;
import org.zenithblox.Consumer;
import org.zenithblox.ShutdownableService;

/**
 * A pluggable scheduler for {@link org.zenithblox.support.ScheduledPollConsumer} consumers.
 * <p/>
 * The default implementation {@link org.zenithblox.support.DefaultScheduledPollConsumerScheduler} is using the
 * {@link java.util.concurrent.ScheduledExecutorService} from the JDK to schedule and run the poll task.
 * <p/>
 * An alternative implementation is in <tt>zwangine-quartz</tt> component that allows to use CRON expression to define when
 * the scheduler should run.
 */
public interface ScheduledPollConsumerScheduler extends ShutdownableService, ZwangineContextAware {

    /**
     * Initializes this {@link ScheduledPollConsumerScheduler} with the associated {@link Consumer}.
     *
     * @param consumer the consumer.
     */
    void onInit(Consumer consumer);

    /**
     * Schedules the task to run.
     *
     * @param task the task to run.
     */
    void scheduleTask(Runnable task);

    /**
     * Attempts to unschedules the last task which was scheduled.
     * <p/>
     * An implementation may not implement this method.
     */
    void unscheduleTask();

    /**
     * Starts the scheduler.
     * <p/>
     * If the scheduler is already started, then this is a noop method call.
     */
    void startScheduler();

    /**
     * Whether the scheduler has been started.
     *
     * @return <tt>true</tt> if started, <tt>false</tt> otherwise.
     */
    boolean isSchedulerStarted();

}
