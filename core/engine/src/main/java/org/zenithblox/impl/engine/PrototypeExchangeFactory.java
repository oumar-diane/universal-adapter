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

import org.zenithblox.Consumer;
import org.zenithblox.Endpoint;
import org.zenithblox.Exchange;
import org.zenithblox.spi.ExchangeFactory;
import org.zenithblox.spi.ExchangeFactoryManager;
import org.zenithblox.support.DefaultExchange;
import org.zenithblox.support.PooledObjectFactorySupport;
import org.zenithblox.util.URISupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ExchangeFactory} that creates a new {@link Exchange} instance.
 */
public class PrototypeExchangeFactory extends PooledObjectFactorySupport<Exchange> implements ExchangeFactory {

    private static final Logger LOG = LoggerFactory.getLogger(PrototypeExchangeFactory.class);

    final Consumer consumer;
    ExchangeFactoryManager exchangeFactoryManager;
    String workflowId;

    public PrototypeExchangeFactory() {
        this.consumer = null;
    }

    public PrototypeExchangeFactory(Consumer consumer) {
        this.consumer = consumer;
    }

    @Override
    protected void doBuild() throws Exception {
        super.doBuild();
        this.exchangeFactoryManager = zwangineContext.getZwangineContextExtension().getExchangeFactoryManager();
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
    public Consumer getConsumer() {
        return consumer;
    }

    @Override
    public ExchangeFactory newExchangeFactory(Consumer consumer) {
        PrototypeExchangeFactory answer = new PrototypeExchangeFactory(consumer);
        answer.setStatisticsEnabled(statisticsEnabled);
        answer.setCapacity(capacity);
        answer.setZwangineContext(zwangineContext);
        return answer;
    }

    @Override
    public Exchange acquire() {
        throw new UnsupportedOperationException("Not in use");
    }

    @Override
    public Exchange create(boolean autoRelease) {
        if (statisticsEnabled) {
            statistics.created.increment();
        }
        return new DefaultExchange(zwangineContext);
    }

    @Override
    public Exchange create(Endpoint fromEndpoint, boolean autoRelease) {
        if (statisticsEnabled) {
            statistics.created.increment();
        }
        return DefaultExchange.newFromEndpoint(fromEndpoint);
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
    protected void doStart() throws Exception {
        super.doStart();
        if (exchangeFactoryManager != null) {
            exchangeFactoryManager.addExchangeFactory(this);
        }
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        if (exchangeFactoryManager != null) {
            exchangeFactoryManager.removeExchangeFactory(this);
        }
        logUsageSummary(LOG, "PrototypeExchangeFactory", 0);
    }

    void logUsageSummary(Logger log, String name, int pooled) {
        if (statisticsEnabled && consumer != null) {
            // only log if there is any usage
            long created = statistics.getCreatedCounter();
            long acquired = statistics.getAcquiredCounter();
            long released = statistics.getReleasedCounter();
            long discarded = statistics.getDiscardedCounter();
            boolean shouldLog = pooled > 0 || created > 0 || acquired > 0 || released > 0 || discarded > 0;
            if (shouldLog) {
                String id = getWorkflowId();
                if (id == null) {
                    id = "";
                } else {
                    id = " " + id;
                }
                String uri = consumer.getEndpoint().getEndpointBaseUri();
                uri = URISupport.sanitizeUri(uri);

                // are there any leaks?
                boolean leak = created + acquired > released + discarded;
                if (leak) {
                    long leaks = (created + acquired) - (released + discarded);
                    log.warn(
                            "{}{} ({}) usage (leaks detected: {}) [pooled: {}, created: {}, acquired: {}, released: {}, discarded: {}]",
                            name, id, uri, leaks, pooled, created, acquired, released, discarded);
                } else {
                    log.info("{}{} ({}) usage [pooled: {}, created: {}, acquired: {}, released: {}, discarded: {}]",
                            name, id, uri, pooled, created, acquired, released, discarded);
                }
            }
        }
    }

}
