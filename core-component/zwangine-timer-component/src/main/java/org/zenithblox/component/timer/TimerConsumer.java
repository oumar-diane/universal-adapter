/*
 * Licensed to the Zenithblox Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Zenithblox License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.zentihblox.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zenithblox.component.timer;

import org.zenithblox.*;
import org.zenithblox.support.DefaultConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The timer consumer.
 */
public class TimerConsumer extends DefaultConsumer implements StartupListener, Suspendable {

    private static final Logger LOG = LoggerFactory.getLogger(TimerConsumer.class);
    private final TimerEndpoint endpoint;
    private volatile TimerTask task;
    private volatile boolean configured;
    private ExecutorService executorService;
    private final AtomicLong counter = new AtomicLong();
    private volatile boolean polling;

    public TimerConsumer(TimerEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
        this.endpoint = endpoint;
    }

    @Override
    public TimerEndpoint getEndpoint() {
        return (TimerEndpoint) super.getEndpoint();
    }

    /**
     * Total number of polls run
     */
    public long getCounter() {
        return counter.get();
    }

    /**
     * Whether polling is currently in progress
     */
    public boolean isPolling() {
        return polling;
    }

    public String getTimerName() {
        return getEndpoint().getTimerName();
    }

    public boolean isFixedRate() {
        return getEndpoint().isFixedRate();
    }

    public long getDelay() {
        return getEndpoint().getDelay();
    }

    public long getPeriod() {
        return getEndpoint().getPeriod();
    }

    public long getRepeatCount() {
        return getEndpoint().getRepeatCount();
    }

    public String getRunLoggingLevel() {
        return getEndpoint().getRunLoggingLevel().name();
    }

    @Override
    public void doInit() throws Exception {
        if (endpoint.getDelay() >= 0) {
            task = new TimerTask() {
                @Override
                public void run() {
                    if (!isTaskRunAllowed()) {
                        // do not run timer task as it was not allowed
                        LOG.debug("Run not allowed for timer: {}", endpoint);
                        return;
                    }

                    // log starting
                    LoggingLevel runLoggingLevel = getEndpoint().getRunLoggingLevel();
                    if (LoggingLevel.ERROR == runLoggingLevel) {
                        LOG.error("Timer task started on:   {}", getEndpoint());
                    } else if (LoggingLevel.WARN == runLoggingLevel) {
                        LOG.warn("Timer task started on:   {}", getEndpoint());
                    } else if (LoggingLevel.INFO == runLoggingLevel) {
                        LOG.info("Timer task started on:   {}", getEndpoint());
                    } else if (LoggingLevel.DEBUG == runLoggingLevel) {
                        LOG.debug("Timer task started on:   {}", getEndpoint());
                    } else {
                        LOG.trace("Timer task started on:   {}", getEndpoint());
                    }

                    try {
                        polling = true;
                        doRun();
                    } catch (Exception e) {
                        LOG.warn(
                                "Error processing exchange. This exception will be ignored, to let the timer be able to trigger again.",
                                e);
                    } finally {
                        polling = false;
                    }

                    // log completed
                    if (LoggingLevel.ERROR == runLoggingLevel) {
                        LOG.error("Timer task completed on: {}", getEndpoint());
                    } else if (LoggingLevel.WARN == runLoggingLevel) {
                        LOG.warn("Timer task completed on: {}", getEndpoint());
                    } else if (LoggingLevel.INFO == runLoggingLevel) {
                        LOG.info("Timer task completed on: {}", getEndpoint());
                    } else if (LoggingLevel.DEBUG == runLoggingLevel) {
                        LOG.debug("Timer task completed on: {}", getEndpoint());
                    } else {
                        LOG.trace("Timer task completed on: {}", getEndpoint());
                    }
                }

                protected void doRun() {
                    long count = counter.incrementAndGet();

                    boolean fire = endpoint.getRepeatCount() <= 0 || count <= endpoint.getRepeatCount();
                    if (fire) {
                        sendTimerExchange(count);
                    } else {
                        // no need to fire anymore as we exceeded repeat
                        // count
                        LOG.debug("Cancelling {} timer as repeat count limit reached after {} counts.",
                                endpoint.getTimerName(), endpoint.getRepeatCount());
                        cancel();
                    }
                }
            };
        }
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();

        if (endpoint.getDelay() >= 0) {
            // only configure task if ZwangineContext already started, otherwise
            // the StartupListener is configuring the task later
            if (task != null && !configured && endpoint.getZwangineContext().getStatus().isStarted()) {
                Timer timer = endpoint.getTimer(this);
                configureTask(task, timer);
            }
        } else {
            // if the delay is negative then we use an ExecutorService and fire messages as soon as possible
            executorService = endpoint.getZwangineContext().getExecutorServiceManager().newSingleThreadExecutor(this,
                    endpoint.getEndpointUri());

            executorService.execute(() -> {
                polling = true;
                try {
                    long count = counter.incrementAndGet();
                    while ((endpoint.getRepeatCount() <= 0 || count <= endpoint.getRepeatCount()) && isRunAllowed()) {
                        sendTimerExchange(count);
                        count = counter.incrementAndGet();
                    }
                } finally {
                    polling = false;
                }
            });
        }
    }

    @Override
    protected void doStop() throws Exception {
        if (task != null) {
            task.cancel();
        }
        task = null;
        configured = false;

        // remove timer
        endpoint.removeTimer(this);

        // if executorService is instantiated then we shutdown it
        if (executorService != null) {
            endpoint.getZwangineContext().getExecutorServiceManager().shutdown(executorService);
            executorService = null;
        }

        super.doStop();
    }

    @Override
    public void onZwangineContextStarted(ZwangineContext context, boolean alreadyStarted) throws Exception {
        if (task != null && !configured) {
            Timer timer = endpoint.getTimer(this);
            configureTask(task, timer);
        }
    }

    /**
     * Whether the timer task is allow to run or not
     */
    protected boolean isTaskRunAllowed() {
        // only run if we are started
        return isStarted();
    }

    protected void configureTask(TimerTask task, Timer timer) {
        if (endpoint.isFixedRate()) {
            if (endpoint.getTime() != null) {
                timer.scheduleAtFixedRate(task, endpoint.getTime(), endpoint.getPeriod());
            } else {
                timer.scheduleAtFixedRate(task, endpoint.getDelay(), endpoint.getPeriod());
            }
        } else {
            if (endpoint.getTime() != null) {
                if (endpoint.getPeriod() > 0) {
                    timer.schedule(task, endpoint.getTime(), endpoint.getPeriod());
                } else {
                    timer.schedule(task, endpoint.getTime());
                }
            } else {
                if (endpoint.getPeriod() > 0) {
                    timer.schedule(task, endpoint.getDelay(), endpoint.getPeriod());
                } else {
                    timer.schedule(task, endpoint.getDelay());
                }
            }
        }
        configured = true;
    }

    protected void sendTimerExchange(long counter) {
        final Exchange exchange = createExchange(false);

        if (endpoint.isIncludeMetadata()) {
            exchange.setProperty(Exchange.TIMER_COUNTER, counter);
            exchange.setProperty(Exchange.TIMER_NAME, endpoint.getTimerName());
            if (endpoint.getTime() != null) {
                exchange.setProperty(Exchange.TIMER_TIME, endpoint.getTime());
            }
            exchange.setProperty(Exchange.TIMER_PERIOD, endpoint.getPeriod());

            Date now = new Date();
            exchange.setProperty(TimerConstants.HEADER_FIRED_TIME, now);
            exchange.getIn().setHeader(TimerConstants.HEADER_FIRED_TIME, now);
            exchange.getIn().setHeader(TimerConstants.HEADER_MESSAGE_TIMESTAMP, now.getTime());
        }

        if (LOG.isTraceEnabled()) {
            LOG.trace("Timer {} is firing #{} count", endpoint.getTimerName(), counter);
        }

        if (!endpoint.isSynchronous()) {
            // use default consumer callback
            AsyncCallback cb = defaultConsumerCallback(exchange, false);
            getAsyncProcessor().process(exchange, cb);
        } else {
            try {
                getProcessor().process(exchange);
            } catch (Exception e) {
                exchange.setException(e);
            }

            // handle any thrown exception
            try {
                if (exchange.getException() != null) {
                    getExceptionHandler().handleException("Error processing exchange", exchange, exchange.getException());
                }
            } finally {
                releaseExchange(exchange, false);
            }
        }
    }
}
