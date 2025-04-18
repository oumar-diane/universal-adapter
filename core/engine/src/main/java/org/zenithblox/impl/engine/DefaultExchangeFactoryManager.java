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

import org.zenithblox.ZwangineContext;
import org.zenithblox.ZwangineContextAware;
import org.zenithblox.Consumer;
import org.zenithblox.spi.ExchangeFactory;
import org.zenithblox.spi.ExchangeFactoryManager;
import org.zenithblox.support.service.ServiceSupport;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultExchangeFactoryManager extends ServiceSupport implements ExchangeFactoryManager, ZwangineContextAware {

    private final Map<Consumer, ExchangeFactory> factories = new ConcurrentHashMap<>();
    private final UtilizationStatistics statistics = new UtilizationStatistics();
    private ZwangineContext zwangineContext;
    private int capacity;
    private boolean statisticsEnabled;

    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    @Override
    public void addExchangeFactory(ExchangeFactory exchangeFactory) {
        factories.put(exchangeFactory.getConsumer(), exchangeFactory);
        // same for all factories
        capacity = exchangeFactory.getCapacity();
        statisticsEnabled = exchangeFactory.isStatisticsEnabled();
    }

    @Override
    public void removeExchangeFactory(ExchangeFactory exchangeFactory) {
        factories.remove(exchangeFactory.getConsumer());
    }

    @Override
    public Collection<ExchangeFactory> getExchangeFactories() {
        return Collections.unmodifiableCollection(factories.values());
    }

    @Override
    public int getConsumerCounter() {
        return factories.size();
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public int getPooledCounter() {
        int counter = 0;
        for (ExchangeFactory ef : factories.values()) {
            counter += ef.getSize();
        }
        return counter;
    }

    @Override
    public boolean isStatisticsEnabled() {
        return statisticsEnabled;
    }

    @Override
    public void setStatisticsEnabled(boolean statisticsEnabled) {
        this.statisticsEnabled = statisticsEnabled;
        for (ExchangeFactory ef : factories.values()) {
            ef.setStatisticsEnabled(statisticsEnabled);
        }
    }

    @Override
    public void resetStatistics() {
        factories.values().forEach(ExchangeFactory::resetStatistics);
    }

    @Override
    public void purge() {
        factories.values().forEach(ExchangeFactory::purge);
    }

    @Override
    public ExchangeFactory.Statistics getStatistics() {
        return statistics;
    }

    /**
     * Represents utilization statistics
     */
    final class UtilizationStatistics implements ExchangeFactory.Statistics {

        @Override
        public void reset() {
            resetStatistics();
        }

        @Override
        public long getCreatedCounter() {
            long answer = 0;
            if (statisticsEnabled) {
                for (ExchangeFactory ef : factories.values()) {
                    answer += ef.getStatistics().getCreatedCounter();
                }
            }
            return answer;
        }

        @Override
        public long getAcquiredCounter() {
            long answer = 0;
            if (statisticsEnabled) {
                for (ExchangeFactory ef : factories.values()) {
                    answer += ef.getStatistics().getAcquiredCounter();
                }
            }
            return answer;
        }

        @Override
        public long getReleasedCounter() {
            long answer = 0;
            if (statisticsEnabled) {
                for (ExchangeFactory ef : factories.values()) {
                    answer += ef.getStatistics().getReleasedCounter();
                }
            }
            return answer;
        }

        @Override
        public long getDiscardedCounter() {
            long answer = 0;
            if (statisticsEnabled) {
                for (ExchangeFactory ef : factories.values()) {
                    answer += ef.getStatistics().getDiscardedCounter();
                }
            }
            return answer;
        }

    }

    @Override
    protected void doShutdown() throws Exception {
        factories.clear();
    }
}
