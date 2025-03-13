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
import org.zenithblox.spi.IdAware;
import org.zenithblox.spi.WorkflowIdAware;
import org.zenithblox.support.AsyncProcessorSupport;
import org.zenithblox.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;

public abstract class AbstractThrottler extends AsyncProcessorSupport implements Traceable, IdAware, WorkflowIdAware, Throttler {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractThrottler.class);

    protected static final String DEFAULT_KEY = "ZwangineThrottlerDefaultKey";
    protected static final String PROPERTY_EXCHANGE_QUEUED_TIMESTAMP = "ZwangineThrottlerExchangeQueuedTimestamp";
    protected static final String PROPERTY_EXCHANGE_STATE = "ZwangineThrottlerExchangeState";
    protected final ScheduledExecutorService asyncExecutor;
    protected final boolean shutdownAsyncExecutor;
    protected final ZwangineContext zwangineContext;
    protected final Expression correlationExpression;
    protected String id;
    protected String workflowId;
    protected boolean rejectExecution;
    protected boolean asyncDelayed;
    protected boolean callerRunsWhenRejected = true;
    protected Expression maxRequestsExpression;

    AbstractThrottler(final ScheduledExecutorService asyncExecutor, final boolean shutdownAsyncExecutor,
                      final ZwangineContext zwangineContext, final boolean rejectExecution, Expression correlation,
                      final Expression maxRequestsExpression) {
        this.asyncExecutor = asyncExecutor;
        this.shutdownAsyncExecutor = shutdownAsyncExecutor;
        this.zwangineContext = zwangineContext;
        this.rejectExecution = rejectExecution;
        this.correlationExpression = correlation;
        this.maxRequestsExpression = ObjectHelper.notNull(maxRequestsExpression, "maxConcurrentRequestsExpression");
    }

    protected static boolean handleInterrupt(
            Exchange exchange, AsyncCallback callback, InterruptedException e, boolean doneSync) {
        // determine if we can still run, or the zwangine context is forcing a shutdown
        boolean forceShutdown = exchange.getContext().getShutdownStrategy().isForceShutdown();
        if (forceShutdown) {
            String msg = "Run not allowed as ShutdownStrategy is forcing shutting down, will reject executing exchange: "
                         + exchange;
            LOG.debug(msg);
            exchange.setException(new RejectedExecutionException(msg, e));
        } else {
            exchange.setException(e);
        }
        callback.done(doneSync);
        return doneSync;
    }

    protected static boolean handleException(Exchange exchange, AsyncCallback callback, Exception t, boolean doneSync) {
        exchange.setException(t);
        callback.done(doneSync);
        return doneSync;
    }

    @Override
    public boolean isRejectExecution() {
        return rejectExecution;
    }

    public void setRejectExecution(boolean rejectExecution) {
        this.rejectExecution = rejectExecution;
    }

    @Override
    public boolean isAsyncDelayed() {
        return asyncDelayed;
    }

    public void setAsyncDelayed(boolean asyncDelayed) {
        this.asyncDelayed = asyncDelayed;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(final String id) {
        this.id = id;
    }

    @Override
    public String getWorkflowId() {
        return workflowId;
    }

    @Override
    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    @Override
    public boolean isCallerRunsWhenRejected() {
        return callerRunsWhenRejected;
    }

    public void setCallerRunsWhenRejected(boolean callerRunsWhenRejected) {
        this.callerRunsWhenRejected = callerRunsWhenRejected;
    }

    /**
     * Sets the maximum number of concurrent requests.
     */
    @Override
    public void setMaximumRequestsExpression(Expression maxConcurrentRequestsExpression) {
        this.maxRequestsExpression = maxConcurrentRequestsExpression;
    }

    public Expression getMaximumRequestsExpression() {
        return maxRequestsExpression;
    }

    protected enum State {
        SYNC,
        ASYNC,
        ASYNC_REJECTED
    }

    @Override
    public abstract String getMode();
}
