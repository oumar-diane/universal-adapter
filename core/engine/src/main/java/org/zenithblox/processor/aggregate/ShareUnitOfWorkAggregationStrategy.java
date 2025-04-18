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
package org.zenithblox.processor.aggregate;

import org.zenithblox.*;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.support.service.ServiceSupport;

/**
 * An {@link AggregationStrategy} which are used when the option <tt>shareUnitOfWork</tt> is enabled on EIPs such as
 * multicast, splitter or recipientList.
 * <p/>
 * This strategy wraps the actual in use strategy to provide the logic needed for making shareUnitOfWork work.
 * <p/>
 * This strategy is <b>not</b> intended for end users to use.
 */
public final class ShareUnitOfWorkAggregationStrategy extends ServiceSupport implements AggregationStrategy, ZwangineContextAware {

    private final AggregationStrategy strategy;
    private ZwangineContext zwangineContext;

    public ShareUnitOfWorkAggregationStrategy(AggregationStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
        ZwangineContextAware.trySetZwangineContext(strategy, zwangineContext);
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    public AggregationStrategy getDelegate() {
        return strategy;
    }

    @Override
    public boolean canPreComplete() {
        return strategy.canPreComplete();
    }

    @Override
    public boolean preComplete(Exchange oldExchange, Exchange newExchange) {
        return strategy.preComplete(oldExchange, newExchange);
    }

    @Override
    public void onCompletion(Exchange exchange) {
        strategy.onCompletion(exchange);
    }

    @Override
    public void onCompletion(Exchange exchange, Exchange inputExchange) {
        strategy.onCompletion(exchange, inputExchange);
    }

    @Override
    public void timeout(Exchange exchange, int index, int total, long timeout) {
        strategy.timeout(exchange, index, total, timeout);
    }

    @Override
    public void onOptimisticLockFailure(Exchange oldExchange, Exchange newExchange) {
        strategy.onOptimisticLockFailure(oldExchange, newExchange);
    }

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        // aggregate using the actual strategy first
        Exchange answer = strategy.aggregate(oldExchange, newExchange);
        // ensure any errors is propagated from the new exchange to the answer
        propagateFailure(answer, newExchange);

        return answer;
    }

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange, Exchange inputExchange) {
        // aggregate using the actual strategy first
        Exchange answer = strategy.aggregate(oldExchange, newExchange, inputExchange);
        // ensure any errors is propagated from the new exchange to the answer
        propagateFailure(answer, newExchange);

        return answer;
    }

    private void propagateFailure(Exchange answer, Exchange newExchange) {
        // if new exchange failed then propagate all the error related properties to the answer
        if (newExchange.isFailed() || newExchange.isRollbackOnly() || newExchange.isRollbackOnlyLast()
                || newExchange.getExchangeExtension().isErrorHandlerHandledSet()
                        && newExchange.getExchangeExtension().isErrorHandlerHandled()) {
            if (newExchange.getException() != null) {
                answer.setException(newExchange.getException());
            }
            if (newExchange.getProperty(ExchangePropertyKey.EXCEPTION_CAUGHT) != null) {
                answer.setProperty(ExchangePropertyKey.EXCEPTION_CAUGHT,
                        newExchange.getProperty(ExchangePropertyKey.EXCEPTION_CAUGHT));
            }
            if (newExchange.getProperty(ExchangePropertyKey.FAILURE_ENDPOINT) != null) {
                answer.setProperty(ExchangePropertyKey.FAILURE_ENDPOINT,
                        newExchange.getProperty(ExchangePropertyKey.FAILURE_ENDPOINT));
            }
            if (newExchange.getProperty(ExchangePropertyKey.FAILURE_ROUTE_ID) != null) {
                answer.setProperty(ExchangePropertyKey.FAILURE_ROUTE_ID,
                        newExchange.getProperty(ExchangePropertyKey.FAILURE_ROUTE_ID));
            }
            if (newExchange.getExchangeExtension().getErrorHandlerHandled() != null) {
                answer.getExchangeExtension()
                        .setErrorHandlerHandled(newExchange.getExchangeExtension().getErrorHandlerHandled());
            }
            answer.getExchangeExtension().setFailureHandled(newExchange.getExchangeExtension().isFailureHandled());
        }
    }

    @Override
    public String toString() {
        return "ShareUnitOfWorkAggregationStrategy";
    }

    @Override
    protected void doBuild() throws Exception {
        ServiceHelper.buildService(strategy);
    }

    @Override
    protected void doInit() throws Exception {
        ServiceHelper.initService(strategy);
    }

    @Override
    protected void doStart() throws Exception {
        ServiceHelper.startService(strategy);
    }

    @Override
    protected void doStop() throws Exception {
        ServiceHelper.stopAndShutdownServices(strategy);
    }
}
