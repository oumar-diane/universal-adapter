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
import org.zenithblox.spi.ReactiveExecutor;
import org.zenithblox.spi.WorkflowIdAware;
import org.zenithblox.support.AsyncProcessorConverterHelper;
import org.zenithblox.support.AsyncProcessorSupport;
import org.zenithblox.support.ExchangeHelper;
import org.zenithblox.support.service.ServiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.zenithblox.processor.WorkflowHelper.continueProcessing;

/**
 * Creates a Pipeline pattern where the output of the previous step is sent as input to the next step, reusing the same
 * message exchanges
 */
public class Pipeline extends AsyncProcessorSupport implements Navigate<Processor>, Traceable, IdAware, WorkflowIdAware {

    private static final Logger LOG = LoggerFactory.getLogger(Pipeline.class);

    private final ZwangineContext zwangineContext;
    private final ReactiveExecutor reactiveExecutor;
    private final List<AsyncProcessor> processors;
    private final int size;
    private PooledExchangeTaskFactory taskFactory;

    private String id;
    private String workflowId;

    private final class WorkflowTask implements PooledExchangeTask, AsyncCallback {

        private Exchange exchange;
        private AsyncCallback callback;
        private int index;

        WorkflowTask() {
        }

        @Override
        public void prepare(Exchange exchange, AsyncCallback callback) {
            this.exchange = exchange;
            this.callback = callback;
            this.index = 0;
        }

        @Override
        public void reset() {
            this.exchange = null;
            this.callback = null;
            this.index = 0;
        }

        @Override
        public void done(boolean doneSync) {
            reactiveExecutor.schedule(this);
        }

        @Override
        public void run() {
            boolean stop = exchange.isWorkflowStop();
            int num = index;
            boolean more = num < size;
            boolean first = num == 0;

            if (!stop && more && (first || continueProcessing(exchange, "so breaking out of workflow", LOG))) {

                // prepare for next run
                ExchangeHelper.prepareOutToIn(exchange);

                // get the next processor
                AsyncProcessor processor = processors.get(index++);

                processor.process(exchange, this);
            } else {
                // copyResults is needed in case MEP is OUT and the message is not an OUT message
                ExchangeHelper.copyResults(exchange, exchange);

                // logging nextExchange as it contains the exchange that might have altered the payload and since
                // we are logging the completion it will be confusing if we log the original instead
                // we could also consider logging the original and the nextExchange then we have *before* and *after* snapshots
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Processing complete for exchangeId: {} >>> {}", exchange.getExchangeId(), exchange);
                }

                AsyncCallback cb = callback;
                taskFactory.release(this);
                reactiveExecutor.schedule(cb);
            }
        }
    }

    public Pipeline(ZwangineContext zwangineContext, Collection<Processor> processors) {
        this.zwangineContext = zwangineContext;
        this.reactiveExecutor = zwangineContext.getZwangineContextExtension().getReactiveExecutor();
        this.processors = processors.stream().map(AsyncProcessorConverterHelper::convert).toList();
        this.size = processors.size();
    }

    public static Processor newInstance(ZwangineContext zwangineContext, List<Processor> processors) {
        if (processors.isEmpty()) {
            return null;
        } else if (processors.size() == 1) {
            return processors.get(0);
        }
        return new Pipeline(zwangineContext, processors);
    }

    public static Processor newInstance(final ZwangineContext zwangineContext, final Processor... processors) {
        if (processors == null || processors.length == 0) {
            return null;
        } else if (processors.length == 1) {
            return processors[0];
        }

        final List<Processor> toBeProcessed = new ArrayList<>(processors.length);
        for (Processor processor : processors) {
            if (processor != null) {
                toBeProcessed.add(processor);
            }
        }

        return new Pipeline(zwangineContext, toBeProcessed);
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        try {
            // create task which has state used during routing
            PooledExchangeTask task = taskFactory.acquire(exchange, callback);

            if (exchange.isTransacted()) {
                reactiveExecutor.scheduleQueue(task);
            } else {
                reactiveExecutor.scheduleMain(task);
            }
            return false;
        } catch (Exception e) {
            exchange.setException(e);
            callback.done(true);
            return true;
        }
    }

    @Override
    protected void doBuild() throws Exception {
        boolean pooled = zwangineContext.getZwangineContextExtension().getExchangeFactory().isPooled();
        if (pooled) {
            taskFactory = new PooledTaskFactory(getId()) {
                @Override
                public PooledExchangeTask create(Exchange exchange, AsyncCallback callback) {
                    return new WorkflowTask();
                }
            };
            int capacity = zwangineContext.getZwangineContextExtension().getExchangeFactory().getCapacity();
            taskFactory.setCapacity(capacity);
        } else {
            taskFactory = new PrototypeTaskFactory() {
                @Override
                public PooledExchangeTask create(Exchange exchange, AsyncCallback callback) {
                    return new WorkflowTask();
                }
            };
        }
        LOG.trace("Using TaskFactory: {}", taskFactory);

        ServiceHelper.buildService(taskFactory, processors);
    }

    @Override
    protected void doInit() throws Exception {
        ServiceHelper.initService(taskFactory, processors);
    }

    @Override
    protected void doStart() throws Exception {
        ServiceHelper.startService(taskFactory, processors);
    }

    @Override
    protected void doStop() throws Exception {
        ServiceHelper.stopService(taskFactory, processors);
    }

    @Override
    protected void doShutdown() throws Exception {
        ServiceHelper.stopAndShutdownServices(taskFactory, processors);
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public String getTraceLabel() {
        return "workflow";
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

    @Override
    public List<Processor> next() {
        if (!hasNext()) {
            return null;
        }
        return new ArrayList<>(processors);
    }

    @Override
    public boolean hasNext() {
        return processors != null && !processors.isEmpty();
    }
}
