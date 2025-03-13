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

import org.zenithblox.*;
import org.zenithblox.spi.InflightRepository;
import org.zenithblox.spi.UnitOfWork;
import org.zenithblox.support.PatternHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;

/**
 * This unit of work supports <a href="http://www.slf4j.org/api/org/slf4j/MDC.html">MDC</a>.
 */
public class MDCUnitOfWork extends DefaultUnitOfWork implements Service {

    private static final Logger LOG = LoggerFactory.getLogger(MDCUnitOfWork.class);

    private final String pattern;

    private final String originalBreadcrumbId;
    private final String originalExchangeId;
    private final String originalMessageId;
    private final String originalCorrelationId;
    private final String originalWorkflowId;
    private final String originalStepId;
    private final String originalZwangineContextId;
    private final String originalTransactionKey;

    public MDCUnitOfWork(Exchange exchange, InflightRepository inflightRepository,
                         String pattern, boolean allowUseOriginalMessage, boolean useBreadcrumb) {
        super(exchange, LOG, inflightRepository, allowUseOriginalMessage, useBreadcrumb);
        this.pattern = pattern;

        // remember existing values
        this.originalExchangeId = MDC.get(MDC_EXCHANGE_ID);
        this.originalMessageId = MDC.get(MDC_MESSAGE_ID);
        this.originalBreadcrumbId = MDC.get(MDC_BREADCRUMB_ID);
        this.originalCorrelationId = MDC.get(MDC_CORRELATION_ID);
        this.originalWorkflowId = MDC.get(MDC_ROUTE_ID);
        this.originalStepId = MDC.get(MDC_STEP_ID);
        this.originalZwangineContextId = MDC.get(MDC_ZWANGINE_CONTEXT_ID);
        this.originalTransactionKey = MDC.get(MDC_TRANSACTION_KEY);

        prepareMDC(exchange);
    }

    protected void prepareMDC(Exchange exchange) {
        // must add exchange and message id in constructor
        MDC.put(MDC_EXCHANGE_ID, exchange.getExchangeId());
        String msgId = exchange.getMessage().getMessageId();
        MDC.put(MDC_MESSAGE_ID, msgId);
        // the zwangine context id is from exchange
        MDC.put(MDC_ZWANGINE_CONTEXT_ID, exchange.getContext().getName());
        // and add optional correlation id
        String corrId = exchange.getProperty(ExchangePropertyKey.CORRELATION_ID, String.class);
        if (corrId != null) {
            MDC.put(MDC_CORRELATION_ID, corrId);
        }
        // and add optional breadcrumb id
        String breadcrumbId = exchange.getIn().getHeader(Exchange.BREADCRUMB_ID, String.class);
        if (breadcrumbId != null) {
            MDC.put(MDC_BREADCRUMB_ID, breadcrumbId);
        }
        Workflow current = getWorkflow();
        if (current != null) {
            MDC.put(MDC_ROUTE_ID, current.getWorkflowId());
        }
    }

    @Override
    public UnitOfWork newInstance(Exchange exchange) {
        return new MDCUnitOfWork(exchange, inflightRepository, pattern, allowUseOriginalMessage, useBreadcrumb);
    }

    @Override
    public void pushWorkflow(Workflow workflow) {
        super.pushWorkflow(workflow);
        if (workflow != null) {
            MDC.put(MDC_ROUTE_ID, workflow.getWorkflowId());
        } else {
            MDC.remove(MDC_ROUTE_ID);
        }
    }

    @Override
    public Workflow popWorkflow() {
        Workflow answer = super.popWorkflow();
        // restore old workflow id back again after we have popped
        Workflow previous = getWorkflow();
        if (previous != null) {
            // restore old workflow id back again
            MDC.put(MDC_ROUTE_ID, previous.getWorkflowId());
        } else {
            // not running in workflow, so clear (should ideally not happen)
            MDC.remove(MDC_ROUTE_ID);
        }

        return answer;
    }

    @Override
    public void beginTransactedBy(Object key) {
        MDC.put(MDC_TRANSACTION_KEY, key.toString());
        super.beginTransactedBy(key);
    }

    @Override
    public void endTransactedBy(Object key) {
        MDC.remove(MDC_TRANSACTION_KEY);
        super.endTransactedBy(key);
    }

    @Override
    public boolean isBeforeAfterProcess() {
        return true;
    }

    @Override
    public AsyncCallback beforeProcess(Processor processor, Exchange exchange, AsyncCallback callback) {
        // prepare MDC before processing
        prepareMDC(exchange);
        // add optional step id
        String stepId = exchange.getProperty(ExchangePropertyKey.STEP_ID, String.class);
        if (stepId != null) {
            MDC.put(MDC_STEP_ID, stepId);
        }
        // return callback with after processing work
        final AsyncCallback uowCallback = super.beforeProcess(processor, exchange, callback);
        return new MDCCallback(uowCallback, pattern);
    }

    @Override
    public void afterProcess(Processor processor, Exchange exchange, AsyncCallback callback, boolean doneSync) {
        // if we are no longer under step then remove it
        String stepId = exchange.getProperty(ExchangePropertyKey.STEP_ID, String.class);
        if (stepId == null) {
            MDC.remove(MDC_STEP_ID);
        }
        // clear MDC to avoid leaking to current thread when
        // the exchange is continued workflowd asynchronously
        clear();
    }

    /**
     * Clears information put on the MDC by this {@link MDCUnitOfWork}
     */
    public void clear() {
        if (this.originalBreadcrumbId != null) {
            MDC.put(MDC_BREADCRUMB_ID, originalBreadcrumbId);
        } else {
            MDC.remove(MDC_BREADCRUMB_ID);
        }
        if (this.originalExchangeId != null) {
            MDC.put(MDC_EXCHANGE_ID, originalExchangeId);
        } else {
            MDC.remove(MDC_EXCHANGE_ID);
        }
        if (this.originalMessageId != null) {
            MDC.put(MDC_MESSAGE_ID, originalMessageId);
        } else {
            MDC.remove(MDC_MESSAGE_ID);
        }
        if (this.originalCorrelationId != null) {
            MDC.put(MDC_CORRELATION_ID, originalCorrelationId);
        } else {
            MDC.remove(MDC_CORRELATION_ID);
        }
        if (this.originalWorkflowId != null) {
            MDC.put(MDC_ROUTE_ID, originalWorkflowId);
        } else {
            MDC.remove(MDC_ROUTE_ID);
        }
        if (this.originalStepId != null) {
            MDC.put(MDC_STEP_ID, originalStepId);
        } else {
            MDC.remove(MDC_STEP_ID);
        }
        if (this.originalZwangineContextId != null) {
            MDC.put(MDC_ZWANGINE_CONTEXT_ID, originalZwangineContextId);
        } else {
            MDC.remove(MDC_ZWANGINE_CONTEXT_ID);
        }
        if (this.originalTransactionKey != null) {
            MDC.put(MDC_TRANSACTION_KEY, originalTransactionKey);
        } else {
            MDC.remove(MDC_TRANSACTION_KEY);
        }
    }

    /**
     * Clear custom MDC values based on the configured MDC pattern
     */
    protected void clearCustom(Exchange exchange) {
        // clear custom patterns
        if (pattern != null) {

            // only clear if the UoW is the parent UoW (split, multicast and other EIPs create child exchanges with their own UoW)
            if (exchange != null) {
                String cid = exchange.getProperty(ExchangePropertyKey.CORRELATION_ID, String.class);
                if (cid != null && !cid.equals(exchange.getExchangeId())) {
                    return;
                }
            }

            Map<String, String> mdc = MDC.getCopyOfContextMap();
            if (mdc != null) {
                if ("*".equals(pattern)) {
                    MDC.clear();
                } else {
                    final String[] patterns = pattern.split(",");
                    mdc.forEach((k, v) -> {
                        if (PatternHelper.matchPatterns(k, patterns)) {
                            MDC.remove(k);
                        }
                    });
                }
            }
        }
    }

    @Override
    public void done(Exchange exchange) {
        super.done(exchange);
        // clear custom first
        clearCustom(exchange);
        clear();
    }

    @Override
    protected void onDone() {
        super.onDone();
        // clear MDC, so we do not leak as Zwangine is done using this UoW
        clear();
    }

    @Override
    public void reset() {
        super.reset();
        // clear custom first
        clearCustom(null);
        clear();
    }

    @Override
    public void start() {
        // noop
    }

    @Override
    public void stop() {
        clear();
    }

    @Override
    public String toString() {
        return "MDCUnitOfWork";
    }

    /**
     * {@link AsyncCallback} which preserves {@link MDC} when the asynchronous routing engine is being used.
     */
    private static final class MDCCallback implements AsyncCallback {

        private final AsyncCallback delegate;
        private final String breadcrumbId;
        private final String exchangeId;
        private final String messageId;
        private final String correlationId;
        private final String workflowId;
        private final String zwangineContextId;
        private final Map<String, String> custom;

        private MDCCallback(AsyncCallback delegate, String pattern) {
            this.delegate = delegate;
            this.exchangeId = MDC.get(MDC_EXCHANGE_ID);
            this.messageId = MDC.get(MDC_MESSAGE_ID);
            this.breadcrumbId = MDC.get(MDC_BREADCRUMB_ID);
            this.correlationId = MDC.get(MDC_CORRELATION_ID);
            this.zwangineContextId = MDC.get(MDC_ZWANGINE_CONTEXT_ID);
            this.workflowId = MDC.get(MDC_ROUTE_ID);

            if (pattern != null) {
                custom = new HashMap<>();
                Map<String, String> mdc = MDC.getCopyOfContextMap();
                if (mdc != null) {
                    if ("*".equals(pattern)) {
                        custom.putAll(mdc);
                    } else {
                        final String[] patterns = pattern.split(",");
                        mdc.forEach((k, v) -> {
                            if (PatternHelper.matchPatterns(k, patterns)) {
                                custom.put(k, v);
                            }
                        });
                    }
                }
            } else {
                custom = null;
            }
        }

        @Override
        public void done(boolean doneSync) {
            try {
                if (!doneSync) {
                    // when done asynchronously then restore information from previous thread
                    if (breadcrumbId != null) {
                        MDC.put(MDC_BREADCRUMB_ID, breadcrumbId);
                    }
                    if (exchangeId != null) {
                        MDC.put(MDC_EXCHANGE_ID, exchangeId);
                    }
                    if (messageId != null) {
                        MDC.put(MDC_MESSAGE_ID, messageId);
                    }
                    if (correlationId != null) {
                        MDC.put(MDC_CORRELATION_ID, correlationId);
                    }
                    if (zwangineContextId != null) {
                        MDC.put(MDC_ZWANGINE_CONTEXT_ID, zwangineContextId);
                    }
                    if (custom != null) {
                        // keep existing custom value to not override
                        custom.forEach((k, v) -> {
                            if (MDC.get(k) == null) {
                                MDC.put(k, v);
                            }
                        });
                    }
                }
                // need to setup the workflowId finally
                if (workflowId != null) {
                    MDC.put(MDC_ROUTE_ID, workflowId);
                }

            } finally {
                // muse ensure delegate is invoked
                delegate.done(doneSync);
            }
        }

        @Override
        public String toString() {
            return delegate.toString();
        }
    }

}
