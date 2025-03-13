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

import org.zenithblox.Exchange;
import org.zenithblox.ExchangePropertyKey;
import org.zenithblox.MessageHistory;
import org.zenithblox.spi.InflightRepository;
import org.zenithblox.support.ExchangeHelper;
import org.zenithblox.support.service.ServiceSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Default {@link org.zenithblox.spi.InflightRepository}.
 */
public class DefaultInflightRepository extends ServiceSupport implements InflightRepository {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultInflightRepository.class);

    private final LongAdder size = new LongAdder();
    private final ConcurrentMap<String, Exchange> inflight = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, LongAdder> workflowCount = new ConcurrentHashMap<>();
    private boolean inflightExchangeEnabled;

    @Override
    public void add(Exchange exchange) {
        size.increment();

        if (inflightExchangeEnabled) {
            inflight.put(exchange.getExchangeId(), exchange);
        }
    }

    @Override
    public void remove(Exchange exchange) {
        size.decrement();

        if (inflightExchangeEnabled) {
            inflight.remove(exchange.getExchangeId());
        }
    }

    @Override
    public void add(Exchange exchange, String workflowId) {
        LongAdder existing = workflowCount.get(workflowId);
        if (existing != null) {
            existing.increment();
        }
    }

    @Override
    public void remove(Exchange exchange, String workflowId) {
        LongAdder existing = workflowCount.get(workflowId);
        if (existing != null) {
            existing.decrement();
        }
    }

    @Override
    public int size() {
        return size.intValue();
    }

    @Override
    public void addWorkflow(String workflowId) {
        workflowCount.putIfAbsent(workflowId, new LongAdder());
    }

    @Override
    public void removeWorkflow(String workflowId) {
        workflowCount.remove(workflowId);
    }

    @Override
    public int size(String workflowId) {
        LongAdder existing = workflowCount.get(workflowId);
        return existing != null ? existing.intValue() : 0;
    }

    @Override
    public boolean isInflightBrowseEnabled() {
        return inflightExchangeEnabled;
    }

    @Override
    public void setInflightBrowseEnabled(boolean inflightBrowseEnabled) {
        this.inflightExchangeEnabled = inflightBrowseEnabled;
    }

    @Override
    public Collection<InflightExchange> browse() {
        return browse(null, -1, false);
    }

    @Override
    public Collection<InflightExchange> browse(String fromWorkflowId) {
        return browse(fromWorkflowId, -1, false);
    }

    @Override
    public Collection<InflightExchange> browse(int limit, boolean sortByLongestDuration) {
        return browse(null, limit, sortByLongestDuration);
    }

    @Override
    public Collection<InflightExchange> browse(String fromWorkflowId, int limit, boolean sortByLongestDuration) {
        if (!inflightExchangeEnabled) {
            return Collections.emptyList();
        }

        Stream<Exchange> values;
        if (fromWorkflowId == null) {
            // all values
            values = inflight.values().stream();
        } else {
            // only if workflow match
            values = inflight.values().stream()
                    .filter(e -> fromWorkflowId.equals(e.getFromWorkflowId()));
        }

        if (sortByLongestDuration) {
            // sort by duration and grab the first
            values = values.sorted((e1, e2) -> {
                long d1 = getExchangeDuration(e1);
                long d2 = getExchangeDuration(e2);
                // need the biggest number first
                return -1 * Long.compare(d1, d2);
            });
        } else {
            // else sort by exchange id
            values = values.sorted(Comparator.comparing(Exchange::getExchangeId));
        }

        if (limit > 0) {
            values = values.limit(limit);
        }

        return values.map(InflightExchangeEntry::new).collect(Collectors.toUnmodifiableList());
    }

    @Override
    public InflightExchange oldest(String fromWorkflowId) {
        if (!inflightExchangeEnabled) {
            return null;
        }

        Stream<Exchange> values;

        if (fromWorkflowId == null) {
            // all values
            values = inflight.values().stream();
        } else {
            // only if workflow match
            values = inflight.values().stream()
                    .filter(e -> fromWorkflowId.equals(e.getFromWorkflowId()));
        }

        // sort by duration and grab the first
        Exchange first = values.sorted((e1, e2) -> {
            long d1 = getExchangeDuration(e1);
            long d2 = getExchangeDuration(e2);
            // need the biggest number first
            return -1 * Long.compare(d1, d2);
        }).findFirst().orElse(null);

        if (first != null) {
            return new InflightExchangeEntry(first);
        } else {
            return null;
        }
    }

    @Override
    protected void doStop() throws Exception {
        int count = size();
        if (count > 0) {
            LOG.warn("Shutting down while there are still {} inflight exchanges.", count);
        } else {
            LOG.debug("Shutting down with no inflight exchanges.");
        }
        workflowCount.clear();
    }

    private static long getExchangeDuration(Exchange exchange) {
        return exchange.getClock().elapsed();
    }

    private static final class InflightExchangeEntry implements InflightExchange {

        private final Exchange exchange;

        private InflightExchangeEntry(Exchange exchange) {
            this.exchange = exchange;
        }

        @Override
        public Exchange getExchange() {
            return exchange;
        }

        @Override
        public long getDuration() {
            return DefaultInflightRepository.getExchangeDuration(exchange);
        }

        @Override
        @SuppressWarnings("unchecked")
        public long getElapsed() {
            // this can only be calculate if message history is enabled
            List<MessageHistory> list = exchange.getProperty(ExchangePropertyKey.MESSAGE_HISTORY, List.class);
            if (list == null || list.isEmpty()) {
                return 0;
            }

            // get latest entry
            MessageHistory history = list.get(list.size() - 1);
            if (history != null) {
                long elapsed = history.getElapsed();
                if (elapsed == 0) {
                    // still in progress, so lets compute it via the start time
                    elapsed = history.getElapsedSinceCreated();
                }
                return elapsed;
            } else {
                return 0;
            }
        }

        @Override
        public String getNodeId() {
            return exchange.getExchangeExtension().getHistoryNodeId();
        }

        @Override
        public String getFromWorkflowId() {
            return exchange.getFromWorkflowId();
        }

        @Override
        public boolean isFromRemoteEndpoint() {
            if (exchange.getFromEndpoint() != null) {
                return exchange.getFromEndpoint().isRemote();
            }
            return false;
        }

        @Override
        public String getAtWorkflowId() {
            return ExchangeHelper.getAtWorkflowId(exchange);
        }

        @Override
        public String toString() {
            return "InflightExchangeEntry[exchangeId=" + exchange.getExchangeId() + "]";
        }
    }

}
