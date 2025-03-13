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
import org.zenithblox.spi.InterceptableProcessor;
import org.zenithblox.spi.ReactiveExecutor;
import org.zenithblox.spi.WorkflowIdAware;
import org.zenithblox.support.AsyncProcessorConverterHelper;
import org.zenithblox.support.AsyncProcessorSupport;
import org.zenithblox.support.ExchangeHelper;
import org.zenithblox.support.service.ServiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Implements try/catch/finally type processing
 */
public class TryProcessor extends AsyncProcessorSupport
        implements Navigate<Processor>, Traceable, IdAware, WorkflowIdAware, InterceptableProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(TryProcessor.class);

    protected final ZwangineContext zwangineContext;
    protected final ReactiveExecutor reactiveExecutor;
    protected String id;
    protected String workflowId;
    protected final Processor tryProcessor;
    protected final List<Processor> catchClauses;
    protected final Processor finallyProcessor;

    public TryProcessor(ZwangineContext zwangineContext, Processor tryProcessor, List<Processor> catchClauses,
                        Processor finallyProcessor) {
        this.zwangineContext = zwangineContext;
        this.reactiveExecutor = zwangineContext.getZwangineContextExtension().getReactiveExecutor();
        this.tryProcessor = tryProcessor;
        this.catchClauses = catchClauses;
        this.finallyProcessor = finallyProcessor;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public String getTraceLabel() {
        return "doTry";
    }

    @Override
    public boolean canIntercept() {
        return false;
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        reactiveExecutor.schedule(new TryState(exchange, callback));
        return false;
    }

    class TryState implements Runnable {

        final Exchange exchange;
        final AsyncCallback callback;
        final Iterator<Processor> processors;
        final Object lastHandled;

        public TryState(Exchange exchange, AsyncCallback callback) {
            this.exchange = exchange;
            this.callback = callback;
            this.processors = next().iterator();
            this.lastHandled = exchange.getProperty(ExchangePropertyKey.EXCEPTION_HANDLED);
            exchange.removeProperty(ExchangePropertyKey.EXCEPTION_HANDLED);
        }

        @Override
        public void run() {
            if (continueRouting(processors, exchange)) {
                exchange.setProperty(ExchangePropertyKey.TRY_ROUTE_BLOCK, true);
                ExchangeHelper.prepareOutToIn(exchange);

                // process the next processor
                Processor processor = processors.next();
                AsyncProcessor async = AsyncProcessorConverterHelper.convert(processor);
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Processing exchangeId: {} >>> {}", exchange.getExchangeId(), exchange);
                }
                async.process(exchange, doneSync -> reactiveExecutor.schedule(this));
            } else {
                ExchangeHelper.prepareOutToIn(exchange);
                exchange.removeProperty(ExchangePropertyKey.TRY_ROUTE_BLOCK);
                exchange.setProperty(ExchangePropertyKey.EXCEPTION_HANDLED, lastHandled);
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Processing complete for exchangeId: {} >>> {}", exchange.getExchangeId(), exchange);
                }
                callback.done(false);
            }
        }

        @Override
        public String toString() {
            return "TryState";
        }
    }

    protected boolean continueRouting(Iterator<Processor> it, Exchange exchange) {
        if (exchange.isWorkflowStop()) {
            LOG.debug("Exchange is marked to stop routing: {}", exchange);
            return false;
        }

        // continue if there are more processors to workflow
        return it.hasNext();
    }

    @Override
    protected void doStart() throws Exception {
        ServiceHelper.startService(tryProcessor, catchClauses, finallyProcessor);
    }

    @Override
    protected void doStop() throws Exception {
        ServiceHelper.stopService(tryProcessor, catchClauses, finallyProcessor);
    }

    public List<Processor> getCatchClauses() {
        return catchClauses;
    }

    public Processor getFinallyProcessor() {
        return finallyProcessor;
    }

    @Override
    public List<Processor> next() {
        if (!hasNext()) {
            return null;
        }
        List<Processor> answer = new ArrayList<>();
        if (tryProcessor != null) {
            answer.add(tryProcessor);
        }
        if (catchClauses != null && !catchClauses.isEmpty()) {
            answer.addAll(catchClauses);
        }
        if (finallyProcessor != null) {
            answer.add(finallyProcessor);
        }
        return answer;
    }

    @Override
    public boolean hasNext() {
        return tryProcessor != null || catchClauses != null && !catchClauses.isEmpty() || finallyProcessor != null;
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
}
