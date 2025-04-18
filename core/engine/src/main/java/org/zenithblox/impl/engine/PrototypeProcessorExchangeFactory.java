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

import org.zenithblox.Endpoint;
import org.zenithblox.Exchange;
import org.zenithblox.ExchangePattern;
import org.zenithblox.Processor;
import org.zenithblox.spi.ProcessorExchangeFactory;
import org.zenithblox.support.DefaultExchange;
import org.zenithblox.support.ExchangeHelper;
import org.zenithblox.support.PooledObjectFactorySupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link org.zenithblox.spi.ProcessorExchangeFactory} that creates a new {@link Exchange} instance.
 */
public class PrototypeProcessorExchangeFactory extends PooledObjectFactorySupport<Exchange>
        implements ProcessorExchangeFactory {

    private static final Logger LOG = LoggerFactory.getLogger(PrototypeProcessorExchangeFactory.class);

    final Processor processor;
    String workflowId;
    String id;

    public PrototypeProcessorExchangeFactory() {
        this.processor = null;
    }

    public PrototypeProcessorExchangeFactory(Processor processor) {
        this.processor = processor;
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
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public Processor getProcessor() {
        return processor;
    }

    @Override
    public ProcessorExchangeFactory newProcessorExchangeFactory(Processor processor) {
        PrototypeProcessorExchangeFactory answer = new PrototypeProcessorExchangeFactory(processor);
        answer.setStatisticsEnabled(statisticsEnabled);
        answer.setCapacity(capacity);
        answer.setZwangineContext(zwangineContext);
        return answer;
    }

    @Override
    public Exchange createCopy(Exchange exchange) {
        return exchange.copy();
    }

    @Override
    public Exchange createCorrelatedCopy(Exchange exchange, boolean handover) {
        return ExchangeHelper.createCorrelatedCopy(exchange, handover);
    }

    @Override
    public Exchange create(Endpoint fromEndpoint, ExchangePattern exchangePattern) {
        return DefaultExchange.newFromEndpoint(fromEndpoint, exchangePattern);
    }

    @Override
    public Exchange acquire() {
        throw new UnsupportedOperationException("Not in use");
    }

    @Override
    public boolean release(Exchange exchange) {
        if (statisticsEnabled) {
            statistics.released.increment();
        }
        return true;
    }

    @Override
    public boolean isPooled() {
        return false;
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        logUsageSummary(LOG, "PrototypeProcessorExchangeFactory", 0);
    }

    void logUsageSummary(Logger log, String name, int pooled) {
        if (statisticsEnabled && processor != null) {
            // only log if there is any usage
            long created = statistics.getCreatedCounter();
            long acquired = statistics.getAcquiredCounter();
            long released = statistics.getReleasedCounter();
            long discarded = statistics.getDiscardedCounter();
            boolean shouldLog = pooled > 0 || created > 0 || acquired > 0 || released > 0 || discarded > 0;
            if (shouldLog) {
                String rid = getWorkflowId();
                String pid = getId();

                // are there any leaks?
                boolean leak = created + acquired > released + discarded;
                if (leak) {
                    long leaks = (created + acquired) - (released + discarded);
                    log.warn(
                            "{} {} ({}) usage (leaks detected: {}) [pooled: {}, created: {}, acquired: {} released: {}, discarded: {}]",
                            name, rid, pid, leaks, pooled, created, acquired, released, discarded);
                } else {
                    log.info("{} {} ({}) usage [pooled: {}, created: {}, acquired: {} released: {}, discarded: {}]",
                            name, rid, pid, pooled, created, acquired, released, discarded);
                }
            }
        }
    }

}
