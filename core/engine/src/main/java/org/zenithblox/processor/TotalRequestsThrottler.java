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
package org.zenithblox.processor;

import org.zenithblox.*;
import org.zenithblox.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A <a href="http://zwangine.zwangine.org/throttler.html">Throttler</a> will set a limit on the maximum number of message
 * exchanges which can be sent to a processor within a specific time period.
 * <p/>
 * This pattern can be extremely useful if you have some external system which meters access; such as only allowing 100
 * requests per second; or if huge load can cause a particular system to malfunction or to reduce its throughput you
 * might want to introduce some throttling.
 *
 * This throttle implementation is thread-safe and is therefore safe to be used by multiple concurrent threads in a
 * single workflow.
 *
 * The throttling mechanism is a DelayQueue with maxRequestsPerPeriod permits on it. Each permit is set to be delayed by
 * timePeriodMillis (except when the throttler is initialized or the throttle rate increased, then there is no delay for
 * those permits). Callers trying to acquire a permit from the DelayQueue will block if necessary. The end result is a
 * rolling window of time. Where from the callers point of view in the last timePeriodMillis no more than
 * maxRequestsPerPeriod have been allowed to be acquired.
 */
public class TotalRequestsThrottler extends AbstractThrottler {

    private static final Logger LOG = LoggerFactory.getLogger(TotalRequestsThrottler.class);

    private long timePeriodMillis;
    private final long cleanPeriodMillis;
    private final Expression correlationExpression;
    private final Map<String, ThrottlingState> states = new ConcurrentHashMap<>();

    public TotalRequestsThrottler(final ZwangineContext zwangineContext, final Expression maxRequestsExpression,
                                  final long timePeriodMillis,
                                  final ScheduledExecutorService asyncExecutor, final boolean shutdownAsyncExecutor,
                                  final boolean rejectExecution, Expression correlation) {
        super(asyncExecutor, shutdownAsyncExecutor, zwangineContext, rejectExecution, correlation, maxRequestsExpression);

        if (timePeriodMillis <= 0) {
            throw new IllegalArgumentException("TimePeriodMillis should be a positive number, was: " + timePeriodMillis);
        }
        this.timePeriodMillis = timePeriodMillis;
        this.cleanPeriodMillis = timePeriodMillis * 10;
        this.correlationExpression = correlation;
    }

    @Override
    public boolean process(final Exchange exchange, final AsyncCallback callback) {
        long queuedStart = 0;
        if (LOG.isTraceEnabled()) {
            queuedStart = exchange.getProperty(PROPERTY_EXCHANGE_QUEUED_TIMESTAMP, 0L, Long.class);
            exchange.removeProperty(PROPERTY_EXCHANGE_QUEUED_TIMESTAMP);
        }
        State state = exchange.getProperty(PROPERTY_EXCHANGE_STATE, State.SYNC, State.class);
        exchange.removeProperty(PROPERTY_EXCHANGE_STATE);
        boolean doneSync = state == State.SYNC || state == State.ASYNC_REJECTED;

        try {
            if (!isRunAllowed()) {
                throw new RejectedExecutionException("Run is not allowed");
            }

            String key = DEFAULT_KEY;
            if (correlationExpression != null) {
                key = correlationExpression.evaluate(exchange, String.class);
            }
            ThrottlingState throttlingState = states.computeIfAbsent(key, ThrottlingState::new);
            throttlingState.calculateAndSetMaxRequestsPerPeriod(exchange);

            ThrottlePermit permit = throttlingState.poll();

            if (permit == null) {
                if (isRejectExecution()) {
                    throw new ThrottlerRejectedExecutionException(
                            "Exceeded the max throttle rate of "
                                                                  + throttlingState.getThrottleRate() + " within "
                                                                  + timePeriodMillis + "ms");
                } else {
                    // delegate to async pool
                    if (isAsyncDelayed() && !exchange.isTransacted() && state == State.SYNC) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(
                                    "Throttle rate exceeded but AsyncDelayed enabled, so queueing for async processing, exchangeId: {}",
                                    exchange.getExchangeId());
                        }
                        return processAsynchronously(exchange, callback, throttlingState);
                    }

                    // block waiting for a permit
                    long start = 0;
                    long elapsed = 0;
                    if (LOG.isTraceEnabled()) {
                        start = System.currentTimeMillis();
                    }
                    permit = throttlingState.take();
                    if (LOG.isTraceEnabled()) {
                        elapsed = System.currentTimeMillis() - start;
                    }
                    throttlingState.enqueue(permit, exchange);

                    if (state == State.ASYNC) {
                        if (LOG.isTraceEnabled()) {
                            long queuedTime = start - queuedStart;
                            if (LOG.isTraceEnabled()) {
                                LOG.trace("Queued for {}ms, Throttled for {}ms, exchangeId: {}", queuedTime, elapsed,
                                        exchange.getExchangeId());
                            }
                        }
                    } else {
                        if (LOG.isTraceEnabled()) {
                            LOG.trace("Throttled for {}ms, exchangeId: {}", elapsed, exchange.getExchangeId());
                        }
                    }
                }
            } else {
                throttlingState.enqueue(permit, exchange);

                if (state == State.ASYNC) {
                    if (LOG.isTraceEnabled()) {
                        long queuedTime = System.currentTimeMillis() - queuedStart;
                        LOG.trace("Queued for {}ms, No throttling applied (throttle cleared while queued), for exchangeId: {}",
                                queuedTime, exchange.getExchangeId());
                    }
                } else {
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("No throttling applied to exchangeId: {}", exchange.getExchangeId());
                    }
                }
            }

            callback.done(doneSync);
            return doneSync;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return handleInterrupt(exchange, callback, e, doneSync);
        } catch (Exception e) {
            return handleException(exchange, callback, e, doneSync);
        }
    }

    /**
     * Delegate blocking on the DelayQueue to an asyncExecutor. Except if the executor rejects the submission and
     * isCallerRunsWhenRejected() is enabled, then this method will delegate back to process(), but not before changing
     * the exchange state to stop any recursion.
     */
    protected boolean processAsynchronously(
            final Exchange exchange, final AsyncCallback callback, ThrottlingState throttlingState) {
        try {
            if (LOG.isTraceEnabled()) {
                exchange.setProperty(PROPERTY_EXCHANGE_QUEUED_TIMESTAMP, System.nanoTime());
            }
            exchange.setProperty(PROPERTY_EXCHANGE_STATE, State.ASYNC);
            long delay = throttlingState.peek().getDelay(TimeUnit.NANOSECONDS);
            asyncExecutor.schedule(() -> process(exchange, callback), delay, TimeUnit.NANOSECONDS);
            return false;
        } catch (final RejectedExecutionException e) {
            if (isCallerRunsWhenRejected()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("AsyncExecutor is full, rejected exchange will run in the current thread, exchangeId: {}",
                            exchange.getExchangeId());
                }
                exchange.setProperty(PROPERTY_EXCHANGE_STATE, State.ASYNC_REJECTED);
                return process(exchange, callback);
            }
            throw e;
        }
    }

    @Override
    protected void doStart() throws Exception {
        if (isAsyncDelayed()) {
            ObjectHelper.notNull(asyncExecutor, "executorService", this);
        }
    }

    @Override
    protected void doShutdown() throws Exception {
        if (shutdownAsyncExecutor && asyncExecutor != null) {
            zwangineContext.getExecutorServiceManager().shutdownNow(asyncExecutor);
        }
        states.clear();
        super.doShutdown();
    }

    protected class ThrottlingState {
        private final String key;
        private final Lock lock = new ReentrantLock();
        private final DelayQueue<ThrottlePermit> delayQueue = new DelayQueue<>();
        private final AtomicReference<ScheduledFuture<?>> cleanFuture = new AtomicReference<>();
        private volatile int throttleRate;

        ThrottlingState(String key) {
            this.key = key;
        }

        public int getThrottleRate() {
            return throttleRate;
        }

        public ThrottlePermit poll() {
            return delayQueue.poll();
        }

        public ThrottlePermit peek() {
            return delayQueue.peek();
        }

        public ThrottlePermit take() throws InterruptedException {
            return delayQueue.take();
        }

        public void clean() {
            states.remove(key);
        }

        /**
         * Returns a permit to the DelayQueue, first resetting it's delay to be relative to now.
         */
        public void enqueue(final ThrottlePermit permit, final Exchange exchange) {
            permit.setDelayMs(getTimePeriodMillis());
            delayQueue.put(permit);
            try {
                ScheduledFuture<?> next = asyncExecutor.schedule(this::clean, cleanPeriodMillis, TimeUnit.MILLISECONDS);
                ScheduledFuture<?> prev = cleanFuture.getAndSet(next);
                if (prev != null) {
                    prev.cancel(false);
                }
                // try and incur the least amount of overhead while releasing permits back to the queue
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Permit released, for exchangeId: {}", exchange.getExchangeId());
                }
            } catch (RejectedExecutionException e) {
                LOG.debug("Throttling queue cleaning rejected", e);
            }
        }

        /**
         * Evaluates the maxRequestsPerPeriodExpression and adjusts the throttle rate up or down.
         */
        public void calculateAndSetMaxRequestsPerPeriod(final Exchange exchange) throws Exception {
            lock.lock();
            try {
                Integer newThrottle
                        = TotalRequestsThrottler.this.getMaximumRequestsExpression().evaluate(exchange, Integer.class);

                if (newThrottle != null && newThrottle < 0) {
                    throw new IllegalStateException(
                            "The maximumRequestsPerPeriod must be a positive number, was: " + newThrottle);
                }

                if (newThrottle == null && throttleRate == 0) {
                    throw new RuntimeExchangeException(
                            "The maxRequestsPerPeriodExpression was evaluated as null: "
                                                       + TotalRequestsThrottler.this.getMaximumRequestsExpression(),
                            exchange);
                }

                if (newThrottle != null) {
                    if (newThrottle != throttleRate) {
                        // decrease
                        if (throttleRate > newThrottle) {
                            int delta = throttleRate - newThrottle;

                            // discard any permits that are needed to decrease throttling
                            while (delta > 0) {
                                delayQueue.take();
                                delta--;
                                if (LOG.isTraceEnabled()) {
                                    LOG.trace("Permit discarded due to throttling rate decrease, triggered by ExchangeId: {}",
                                            exchange.getExchangeId());
                                }
                            }
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Throttle rate decreased from {} to {}, triggered by ExchangeId: {}", throttleRate,
                                        newThrottle, exchange.getExchangeId());
                            }

                            // increase
                        } else if (newThrottle > throttleRate) {
                            int delta = newThrottle - throttleRate;
                            for (int i = 0; i < delta; i++) {
                                delayQueue.put(new ThrottlePermit(-1));
                            }
                            if (throttleRate == 0) {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("Initial throttle rate set to {}, triggered by ExchangeId: {}", newThrottle,
                                            exchange.getExchangeId());
                                }
                            } else {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("Throttle rate increase from {} to {}, triggered by ExchangeId: {}", throttleRate,
                                            newThrottle, exchange.getExchangeId());
                                }
                            }
                        }
                        throttleRate = newThrottle;
                    }
                }
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * Permit that implements the Delayed interface needed by DelayQueue.
     */
    private static class ThrottlePermit implements Delayed {
        private volatile long scheduledTime;

        ThrottlePermit(final long delayMs) {
            setDelayMs(delayMs);
        }

        public void setDelayMs(final long delayMs) {
            this.scheduledTime = System.currentTimeMillis() + delayMs;
        }

        @Override
        public long getDelay(final TimeUnit unit) {
            return unit.convert(scheduledTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(final Delayed o) {
            return Long.compare(getDelay(TimeUnit.MILLISECONDS), o.getDelay(TimeUnit.MILLISECONDS));
        }
    }

    @Override
    public String getMode() {
        return "TotalRequests";
    }

    /**
     * Gets the current maximum request per period value. If it is grouped throttling applied with correlationExpression
     * than the max per period within the group will return
     */
    @Override
    public int getCurrentMaximumRequests() {
        return states.values().stream().mapToInt(ThrottlingState::getThrottleRate).max().orElse(0);
    }

    /**
     * Sets the time period during which the maximum number of requests apply
     */
    public void setTimePeriodMillis(final long timePeriodMillis) {
        this.timePeriodMillis = timePeriodMillis;
    }

    public long getTimePeriodMillis() {
        return timePeriodMillis;
    }

    @Override
    public String getTraceLabel() {
        return "throttle[" + this.getMaximumRequestsExpression() + " per: " + timePeriodMillis + "]";
    }

    @Override
    public String toString() {
        return id;
    }
}
