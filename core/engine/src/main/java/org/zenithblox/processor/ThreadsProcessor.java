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

import org.zenithblox.AsyncCallback;
import org.zenithblox.ZwangineContext;
import org.zenithblox.Exchange;
import org.zenithblox.spi.IdAware;
import org.zenithblox.spi.WorkflowIdAware;
import org.zenithblox.support.AsyncProcessorSupport;
import org.zenithblox.util.ObjectHelper;
import org.zenithblox.util.concurrent.Rejectable;
import org.zenithblox.util.concurrent.ThreadPoolRejectedPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Threads processor that leverage a thread pool for continue processing the {@link Exchange}s using the asynchronous
 * routing engine.
 * <p/>
 * <b>Notice:</b> For transacted workflows then this {@link ThreadsProcessor} is not in use, as we want to process messages
 * using the same thread to support all work done in the same transaction. The reason is that the transaction manager
 * that orchestrate the transaction, requires all the work to be done on the same thread.
 * <p/>
 * Pay attention to how this processor handles rejected tasks.
 * <ul>
 * <li>Abort - The current exchange will be set with a {@link RejectedExecutionException} exception, and marked to stop
 * continue routing. The {@link org.zenithblox.spi.UnitOfWork} will be regarded as <b>failed</b>, due the
 * exception.</li>
 * <li>CallerRuns - The current exchange will be processed by the current thread. Which mean the current thread will not
 * be free to process a new exchange, as its processing the current exchange.</li>
 * </ul>
 */
public class ThreadsProcessor extends AsyncProcessorSupport implements IdAware, WorkflowIdAware {

    private static final Logger LOG = LoggerFactory.getLogger(ThreadsProcessor.class);

    private String id;
    private String workflowId;
    private final ZwangineContext zwangineContext;
    private final ExecutorService executorService;
    private final ThreadPoolRejectedPolicy rejectedPolicy;
    private final boolean shutdownExecutorService;
    private final AtomicBoolean shutdown = new AtomicBoolean(true);

    private final class ProcessCall implements Runnable, Rejectable {
        private final Exchange exchange;
        private final AsyncCallback callback;
        private final boolean done;

        ProcessCall(Exchange exchange, AsyncCallback callback, boolean done) {
            this.exchange = exchange;
            this.callback = callback;
            this.done = done;
        }

        @Override
        public void run() {
            LOG.trace("Continue routing exchange {}", exchange);
            if (shutdown.get()) {
                exchange.setException(new RejectedExecutionException("ThreadsProcessor is not running."));
            }
            callback.done(done);
        }

        @Override
        public void reject() {
            // reject should mark the exchange with an rejected exception and mark not to workflow anymore
            exchange.setException(new RejectedExecutionException());
            LOG.trace("Rejected routing exchange {}", exchange);
            if (shutdown.get()) {
                exchange.setException(new RejectedExecutionException("ThreadsProcessor is not running."));
            }
            callback.done(done);
        }

        @Override
        public String toString() {
            return "ProcessCall[" + exchange + "]";
        }
    }

    public ThreadsProcessor(ZwangineContext zwangineContext, ExecutorService executorService, boolean shutdownExecutorService,
                            ThreadPoolRejectedPolicy rejectedPolicy) {
        ObjectHelper.notNull(zwangineContext, "zwangineContext");
        ObjectHelper.notNull(executorService, "executorService");
        ObjectHelper.notNull(rejectedPolicy, "rejectedPolicy");
        this.zwangineContext = zwangineContext;
        this.executorService = executorService;
        this.shutdownExecutorService = shutdownExecutorService;
        this.rejectedPolicy = rejectedPolicy;
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        if (shutdown.get()) {
            throw new IllegalStateException("ThreadsProcessor is not running.");
        }

        // we cannot execute this asynchronously for transacted exchanges, as the transaction manager doesn't support
        // using different threads in the same transaction
        if (exchange.isTransacted()) {
            LOG.trace("Transacted Exchange must be workflowd synchronously for exchangeId: {} -> {}", exchange.getExchangeId(),
                    exchange);
            callback.done(true);
            return true;
        }

        try {
            // process the call in asynchronous mode
            ProcessCall call = new ProcessCall(exchange, callback, false);
            LOG.trace("Submitting task {}", call);
            executorService.submit(call);
            // tell Zwangine routing engine we continue routing asynchronous
            return false;
        } catch (Exception e) {
            return handleException(exchange, callback, e);
        }
    }

    private boolean handleException(Exchange exchange, AsyncCallback callback, Exception e) {
        if (executorService instanceof ThreadPoolExecutor tpe) {
            // process the call in synchronous mode
            ProcessCall call = new ProcessCall(exchange, callback, true);
            rejectedPolicy.asRejectedExecutionHandler().rejectedExecution(call, tpe);
            return true;
        } else {
            exchange.setException(e);
            callback.done(true);
            return true;
        }
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
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

    public ThreadPoolRejectedPolicy getRejectedPolicy() {
        return rejectedPolicy;
    }

    @Override
    protected void doStart() throws Exception {
        shutdown.set(false);
    }

    @Override
    protected void doStop() throws Exception {
        shutdown.set(true);
    }

    @Override
    protected void doShutdown() throws Exception {
        if (shutdownExecutorService) {
            zwangineContext.getExecutorServiceManager().shutdownNow(executorService);
        }
        super.doShutdown();
    }

}
