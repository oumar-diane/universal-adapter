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
import org.zenithblox.spi.WorkflowIdAware;
import org.zenithblox.support.ExchangeHelper;
import org.zenithblox.support.processor.DelegateAsyncProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processor to handle do finally supporting asynchronous routing engine
 */
public class FinallyProcessor extends DelegateAsyncProcessor
        implements Traceable, IdAware, WorkflowIdAware, InterceptableProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(FinallyProcessor.class);

    private String id;
    private String workflowId;

    public FinallyProcessor(Processor processor) {
        super(processor);
    }

    @Override
    public boolean process(final Exchange exchange, final AsyncCallback callback) {
        Exception exception = exchange.getException();
        if (exception != null) {
            // store the caught exception as a property
            exchange.setException(null);
            exchange.setProperty(ExchangePropertyKey.EXCEPTION_CAUGHT, exception);

            // store the last to endpoint as the failure endpoint
            if (exchange.getProperty(ExchangePropertyKey.FAILURE_ENDPOINT) == null) {
                exchange.setProperty(ExchangePropertyKey.FAILURE_ENDPOINT,
                        exchange.getProperty(ExchangePropertyKey.TO_ENDPOINT));
            }
            // and store the workflow id so we know in which workflow we failed
            String workflowId = ExchangeHelper.getAtWorkflowId(exchange);
            if (workflowId != null) {
                exchange.setProperty(ExchangePropertyKey.FAILURE_ROUTE_ID, workflowId);
            }
        }

        // continue processing
        return processor.process(exchange, new FinallyAsyncCallback(exchange, callback, exception));
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public String getTraceLabel() {
        return "finally";
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
    public boolean canIntercept() {
        return false;
    }

    private static final class FinallyAsyncCallback implements AsyncCallback {

        private final Exchange exchange;
        private final AsyncCallback callback;
        private final Exception exception;

        FinallyAsyncCallback(Exchange exchange, AsyncCallback callback, Exception exception) {
            this.exchange = exchange;
            this.callback = callback;
            this.exception = exception;
        }

        @Override
        public void done(boolean doneSync) {
            try {
                if (exception == null) {
                    exchange.removeProperty(ExchangePropertyKey.FAILURE_ENDPOINT);
                    exchange.removeProperty(ExchangePropertyKey.FAILURE_ROUTE_ID);
                } else {
                    // set exception back on exchange
                    exchange.setException(exception);
                    exchange.setProperty(ExchangePropertyKey.EXCEPTION_CAUGHT, exception);
                }

                if (!doneSync) {
                    // signal callback to continue routing async
                    ExchangeHelper.prepareOutToIn(exchange);
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("Processing complete for exchangeId: {} >>> {}", exchange.getExchangeId(), exchange);
                    }
                }
            } finally {
                // callback must always be called
                callback.done(doneSync);
            }
        }

        @Override
        public String toString() {
            return "FinallyAsyncCallback";
        }
    }

}
