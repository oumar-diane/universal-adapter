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
import org.zenithblox.spi.ShutdownAware;
import org.zenithblox.support.ExchangeHelper;
import org.zenithblox.support.processor.DelegateAsyncProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.LongAdder;

import static org.zenithblox.processor.WorkflowHelper.continueProcessing;

/**
 * The processor which sends messages in a loop.
 */
public class LoopProcessor extends DelegateAsyncProcessor implements Traceable, IdAware, WorkflowIdAware, ShutdownAware {

    private static final Logger LOG = LoggerFactory.getLogger(LoopProcessor.class);

    private String id;
    private String workflowId;
    private boolean shutdownPending;
    private final ReactiveExecutor reactiveExecutor;
    private final Expression expression;
    private final Predicate predicate;
    private final Processor onPrepare;
    private final boolean copy;
    private final boolean breakOnShutdown;
    private final LongAdder taskCount = new LongAdder();

    public LoopProcessor(ZwangineContext zwangineContext, Processor processor, Expression expression, Predicate predicate,
                         Processor onPrepare, boolean copy, boolean breakOnShutdown) {
        super(processor);
        this.reactiveExecutor = zwangineContext.getZwangineContextExtension().getReactiveExecutor();
        this.expression = expression;
        this.predicate = predicate;
        this.onPrepare = onPrepare;
        this.copy = copy;
        this.breakOnShutdown = breakOnShutdown;
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        try {
            LoopState state = new LoopState(exchange, callback);

            if (exchange.isTransacted()) {
                reactiveExecutor.scheduleQueue(state);
            } else {
                reactiveExecutor.scheduleMain(state);
            }
            return false;
        } catch (Exception e) {
            exchange.setException(e);
            callback.done(true);
            return true;
        }
    }

    @Override
    public boolean deferShutdown(ShutdownRunningTask shutdownRunningTask) {
        return !breakOnShutdown;
    }

    @Override
    public int getPendingExchangesSize() {
        return taskCount.intValue();
    }

    @Override
    public void prepareShutdown(boolean suspendOnly, boolean forced) {
        shutdownPending = true;
    }

    /**
     * Class holding state for loop processing
     */
    class LoopState implements Runnable {

        final Exchange exchange;
        final AsyncCallback callback;
        Exchange current;
        int index;
        int count;

        public LoopState(Exchange exchange, AsyncCallback callback) throws NoTypeConversionAvailableException {
            this.exchange = exchange;
            this.callback = callback;
            this.current = exchange;

            // evaluate expression / predicate
            if (expression != null) {
                // Intermediate conversion to String is needed when direct conversion to Integer is not available
                // but evaluation result is a textual representation of a numeric value.
                String text = expression.evaluate(exchange, String.class);
                count = ExchangeHelper.convertToMandatoryType(exchange, Integer.class, text);
                // keep track of pending task if loop with fixed value
                taskCount.add(count);
                exchange.setProperty(ExchangePropertyKey.LOOP_SIZE, count);
            }
        }

        @Override
        public void run() {
            try {
                // check for error if so we should break out
                boolean cont = continueProcessing(current, "so breaking out of loop", LOG);
                boolean doWhile = predicate == null || predicate.matches(current);
                boolean doLoop = expression == null || index < count;
                boolean isStopping = shutdownPending && breakOnShutdown;

                // iterate
                if (cont && doWhile && doLoop && !isStopping) {
                    // and prepare for next iteration
                    current = prepareExchange(exchange, index);

                    // set current index as property
                    LOG.debug("LoopProcessor: iteration #{}", index);
                    current.setProperty(ExchangePropertyKey.LOOP_INDEX, index);

                    processor.process(current, doneSync -> {
                        // increment counter after done
                        index++;
                        if (expression != null) {
                            // keep track of pending task if loop with fixed value
                            taskCount.decrement();
                        }
                        reactiveExecutor.schedule(this);
                    });
                } else {
                    // we are done so prepare the result
                    ExchangeHelper.copyResults(exchange, current);
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("Processing complete for exchangeId: {} >>> {}", exchange.getExchangeId(), exchange);
                    }
                    if (!cont && expression != null) {
                        // if we should stop due to an exception etc, then make sure to dec task count
                        int gap = count - index;
                        while (gap-- > 0) {
                            taskCount.decrement();
                        }
                    }
                    callback.done(false);
                }
            } catch (Exception e) {
                handleException(e);
                callback.done(false);
            }
        }

        private void handleException(Exception e) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Processing failed for exchangeId: {} >>> {}", exchange.getExchangeId(), e.getMessage());
            }
            if (expression != null) {
                // if we should stop due to an exception etc, then make sure to dec task count
                int gap = count - index;
                while (gap-- > 0) {
                    taskCount.decrement();
                }
            }
            exchange.setException(e);
        }

        @Override
        public String toString() {
            return "LoopState";
        }
    }

    /**
     * Prepares the exchange for the next iteration
     *
     * @param  exchange the exchange
     * @param  index    the index of the next iteration
     * @return          the exchange to use
     */
    protected Exchange prepareExchange(Exchange exchange, int index) throws Exception {
        Exchange answer = exchange;
        if (copy) {
            // use a copy but let it reuse the same exchange id so it appear as one exchange
            // use the original exchange rather than the looping exchange (esp. with the async routing engine)
            answer = ExchangeHelper.createCopy(exchange, true);
        } else {
            ExchangeHelper.prepareOutToIn(exchange);
        }
        if (onPrepare != null) {
            onPrepare.process(answer);
        }
        return answer;
    }

    public Expression getExpression() {
        return expression;
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public boolean isCopy() {
        return copy;
    }

    public boolean isBreakOnShutdown() {
        return breakOnShutdown;
    }

    @Override
    public String getTraceLabel() {
        if (predicate != null) {
            return "loopWhile[" + predicate + "]";
        } else {
            return "loop[" + expression + "]";
        }
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
    public String toString() {
        return id;
    }
}
