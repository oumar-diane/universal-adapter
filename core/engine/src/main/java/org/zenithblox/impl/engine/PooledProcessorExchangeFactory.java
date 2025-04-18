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
import org.zenithblox.spi.ProcessorExchangeFactory;
import org.zenithblox.support.DefaultPooledExchange;
import org.zenithblox.support.ExchangeHelper;
import org.zenithblox.support.ResetableClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pooled {@link org.zenithblox.spi.ProcessorExchangeFactory} that reuses {@link Exchange} instance from a pool.
 */
public class PooledProcessorExchangeFactory extends PrototypeProcessorExchangeFactory {

    private static final Logger LOG = LoggerFactory.getLogger(PooledProcessorExchangeFactory.class);

    public PooledProcessorExchangeFactory() {
    }

    public PooledProcessorExchangeFactory(Processor processor) {
        super(processor);
    }

    @Override
    public boolean isPooled() {
        return true;
    }

    @Override
    public ProcessorExchangeFactory newProcessorExchangeFactory(Processor processor) {
        PooledProcessorExchangeFactory answer = new PooledProcessorExchangeFactory(processor);
        answer.setStatisticsEnabled(statisticsEnabled);
        answer.setCapacity(capacity);
        answer.setZwangineContext(zwangineContext);
        return answer;
    }

    @Override
    public Exchange createCopy(Exchange exchange) {
        Exchange answer = pool.poll();
        if (answer == null) {
            if (statisticsEnabled) {
                statistics.created.increment();
            }
            // create a new exchange as there was no free from the pool
            answer = new DefaultPooledExchange(exchange);
        } else {
            if (statisticsEnabled) {
                statistics.acquired.increment();
            }
        }

        // reset exchange for reuse
        ((ResetableClock) exchange.getClock()).reset();
        ExchangeHelper.copyResults(answer, exchange);
        return answer;
    }

    @Override
    public Exchange createCorrelatedCopy(Exchange exchange, boolean handover) {
        Exchange answer = pool.poll();
        if (answer == null) {
            if (statisticsEnabled) {
                statistics.created.increment();
            }
            // create a new exchange as there was no free from the pool
            answer = new DefaultPooledExchange(exchange);
            // if creating a copy via constructor (as above) then the unit of work is also
            // copied over to answer, which we then must set to null as we do not want to share unit of work
            answer.getExchangeExtension().setUnitOfWork(null);
        } else {
            if (statisticsEnabled) {
                statistics.acquired.increment();
            }
        }

        // reset exchange for reuse
        ((ResetableClock) exchange.getClock()).reset();

        ExchangeHelper.copyResults(answer, exchange);
        // do not reuse message id on copy
        answer.getIn().setMessageId(null);
        if (handover) {
            // Need to hand over the completion for async invocation
            answer.getExchangeExtension().handoverCompletions(exchange);
        }
        // set a correlation id so we can track back the original exchange
        answer.setProperty(ExchangePropertyKey.CORRELATION_ID, exchange.getExchangeId());
        return answer;
    }

    @Override
    public Exchange create(Endpoint fromEndpoint, ExchangePattern exchangePattern) {
        Exchange answer = pool.poll();
        if (answer == null) {
            // create a new exchange as there was no free from the pool
            answer = DefaultPooledExchange.newFromEndpoint(fromEndpoint, exchangePattern);
            if (statisticsEnabled) {
                statistics.created.increment();
            }
        } else {
            if (statisticsEnabled) {
                statistics.acquired.increment();
            }
        }

        // reset exchange for reuse
        ((ResetableClock) answer.getClock()).reset();
        return answer;
    }

    @Override
    public boolean release(Exchange exchange) {
        try {
            // done exchange before returning to pool
            PooledExchange ee = (PooledExchange) exchange;
            ee.done();

            // only release back in pool if reset was success
            boolean inserted = pool.offer(exchange);

            if (statisticsEnabled) {
                if (inserted) {
                    statistics.released.increment();
                } else {
                    statistics.discarded.increment();
                }
            }
            return inserted;
        } catch (Exception e) {
            if (statisticsEnabled) {
                statistics.discarded.increment();
            }
            LOG.debug("Error resetting exchange: {}. This exchange is discarded.", exchange);
            return false;
        }
    }

}
