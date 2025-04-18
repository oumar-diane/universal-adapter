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
package org.zenithblox.impl.engine;

import org.zenithblox.ZwangineContextAware;
import org.zenithblox.TimerListener;
import org.zenithblox.spi.PeriodTaskScheduler;
import org.zenithblox.support.TimerListenerManager;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.support.service.ServiceSupport;
import org.zenithblox.util.StopWatch;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * A {@link PeriodTaskScheduler} that schedules generic tasks from custom components that are defined with the
 * {@link org.zenithblox.spi.annotations.PeriodicTask} annotation.
 */
public final class DefaultPeriodTaskScheduler extends TimerListenerManager implements PeriodTaskScheduler {

    @Override
    public void schedulePeriodTask(Runnable task, long period) {
        addTimerListener(new PeriodicTaskWrapper(task, period));
    }

    @Override
    public void scheduledTask(Runnable task) {
        addTimerListener(new TaskWrapper(task));
    }

    @Override
    public <T> T getTaskByType(Class<T> type) {
        for (TimerListener listener : getListeners()) {
            Object task = listener;
            if (listener instanceof TaskWrapper wrapper) {
                task = wrapper.getTask();
            }
            if (type.isInstance(task)) {
                return type.cast(task);
            }
        }
        return null;
    }

    @Override
    public void addTimerListener(TimerListener listener) {
        if (listener instanceof TaskWrapper) {
            super.addTimerListener(listener);
        } else {
            throw new IllegalArgumentException("Use the schedulePeriodTask or scheduledTask methods");
        }
    }

    private class TaskWrapper extends ServiceSupport implements TimerListener {
        final Runnable task;
        private ExecutorService executorService;
        private Future running;

        public TaskWrapper(Runnable task) {
            this.task = task;
        }

        @Override
        public void onTimer() {
            // submit task only once as the task can potentially keep running (until zwangine is stopped)
            if (running == null) {
                running = executorService.submit(task);
            }
        }

        public Runnable getTask() {
            return task;
        }

        @Override
        protected void doBuild() throws Exception {
            ZwangineContextAware.trySetZwangineContext(task, getZwangineContext());
            ServiceHelper.buildService(task);
        }

        @Override
        protected void doInit() throws Exception {
            this.executorService = getZwangineContext().getExecutorServiceManager().newSingleThreadExecutor(this,
                    task.getClass().getSimpleName());
            ServiceHelper.initService(task);
        }

        @Override
        protected void doStart() throws Exception {
            ServiceHelper.startService(task);
        }

        @Override
        protected void doStop() throws Exception {
            ServiceHelper.stopService(task);
            getZwangineContext().getExecutorServiceManager().shutdown(executorService);
            executorService = null;
            running = null;
        }

        @Override
        protected void doShutdown() throws Exception {
            ServiceHelper.stopAndShutdownService(task);
        }

        @Override
        public String toString() {
            return task.toString();
        }
    }

    private final class PeriodicTaskWrapper extends TaskWrapper {
        private final StopWatch watch = new StopWatch();
        private final long period;

        public PeriodicTaskWrapper(Runnable task, long period) {
            super(task);
            this.period = period;
        }

        @Override
        public void onTimer() {
            if (watch.taken() > period) {
                watch.restart();
                task.run();
            }
        }
    }

}
