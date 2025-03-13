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
import org.zenithblox.NonManagedService;
import org.zenithblox.spi.ZwangineEvent;
import org.zenithblox.spi.ZwangineEvent.ExchangeCreatedEvent;
import org.zenithblox.spi.ZwangineEvent.ExchangeSendingEvent;
import org.zenithblox.spi.ZwangineEvent.WorkflowAddedEvent;
import org.zenithblox.spi.ZwangineEvent.WorkflowRemovedEvent;
import org.zenithblox.spi.EndpointUtilizationStatistics;
import org.zenithblox.spi.RuntimeEndpointRegistry;
import org.zenithblox.support.DefaultEndpointUtilizationStatistics;
import org.zenithblox.support.EventNotifierSupport;
import org.zenithblox.support.ExchangeHelper;
import org.zenithblox.support.LRUCacheFactory;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class DefaultRuntimeEndpointRegistry extends EventNotifierSupport implements RuntimeEndpointRegistry, NonManagedService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultRuntimeEndpointRegistry.class);

    // workflow id -> endpoint urls
    private Map<String, Set<String>> inputs;
    private Map<String, Map<String, String>> outputs;
    private int limit = 1000;
    private boolean enabled = true;
    private volatile boolean extended;
    private EndpointUtilizationStatistics inputUtilization;
    private EndpointUtilizationStatistics outputUtilization;

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public List<String> getAllEndpoints(boolean includeInputs) {
        List<String> answer = new ArrayList<>();
        if (includeInputs) {
            for (Map.Entry<String, Set<String>> entry : inputs.entrySet()) {
                answer.addAll(entry.getValue());
            }
        }
        for (Map.Entry<String, Map<String, String>> entry : outputs.entrySet()) {
            answer.addAll(entry.getValue().keySet());
        }
        return Collections.unmodifiableList(answer);
    }

    @Override
    public List<String> getEndpointsPerWorkflow(String workflowId, boolean includeInputs) {
        List<String> answer = new ArrayList<>();
        if (includeInputs) {
            Set<String> uris = inputs.get(workflowId);
            if (uris != null) {
                answer.addAll(uris);
            }
        }
        Map<String, String> uris = outputs.get(workflowId);
        if (uris != null) {
            answer.addAll(uris.keySet());
        }
        return Collections.unmodifiableList(answer);
    }

    @Override
    public List<Statistic> getEndpointStatistics() {
        List<Statistic> answer = new ArrayList<>();

        // inputs
        for (Map.Entry<String, Set<String>> entry : inputs.entrySet()) {
            String workflowId = entry.getKey();
            for (String uri : entry.getValue()) {
                Long hits = getHits(workflowId, uri, inputUtilization);
                answer.add(new EndpointRuntimeStatistics(uri, workflowId, "in", hits));
            }
        }

        // outputs
        for (Map.Entry<String, Map<String, String>> entry : outputs.entrySet()) {
            String workflowId = entry.getKey();
            for (String uri : entry.getValue().keySet()) {
                Long hits = getHits(workflowId, uri, outputUtilization);
                answer.add(new EndpointRuntimeStatistics(uri, workflowId, "out", hits));
            }
        }

        return answer;
    }

    private Long getHits(String workflowId, String uri, EndpointUtilizationStatistics statistics) {
        Long hits = 0L;
        if (extended) {
            String key = asUtilizationKey(workflowId, uri);
            if (key != null) {
                hits = statistics.getStatistics().get(key);
                if (hits == null) {
                    hits = 0L;
                }
            }
        }
        return hits;
    }

    @Override
    public int getLimit() {
        return limit;
    }

    @Override
    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public void clear() {
        inputs.clear();
        outputs.clear();
        reset();
    }

    @Override
    public void reset() {
        // its safe to call clear as reset
        if (inputUtilization != null) {
            inputUtilization.clear();
        }
        if (outputUtilization != null) {
            outputUtilization.clear();
        }
    }

    @Override
    public int size() {
        int total = inputs.values().size();
        total += outputs.values().size();
        return total;
    }

    @Override
    protected void doInit() throws Exception {
        ObjectHelper.notNull(getZwangineContext(), "zwangineContext", this);

        if (inputs == null) {
            inputs = new HashMap<>();
        }
        if (outputs == null) {
            outputs = new HashMap<>();
        }
        if (getZwangineContext().getManagementStrategy() != null
                && getZwangineContext().getManagementStrategy().getManagementAgent() != null) {
            extended = getZwangineContext().getManagementStrategy().getManagementAgent().getStatisticsLevel().isExtended();
        }
        if (extended) {
            inputUtilization = new DefaultEndpointUtilizationStatistics(limit);
            outputUtilization = new DefaultEndpointUtilizationStatistics(limit);
        }
        if (extended) {
            LOG.debug(
                    "Runtime endpoint registry is in extended mode gathering usage statistics of all incoming and outgoing endpoints (cache limit: {})",
                    limit);
        } else {
            LOG.debug(
                    "Runtime endpoint registry is in normal mode gathering information of all incoming and outgoing endpoints (cache limit: {})",
                    limit);
        }
        ServiceHelper.initService(inputUtilization, outputUtilization);
    }

    @Override
    protected void doStart() throws Exception {
        ServiceHelper.startService(inputUtilization, outputUtilization);
    }

    @Override
    protected void doStop() throws Exception {
        clear();
        ServiceHelper.stopService(inputUtilization, outputUtilization);
    }

    @Override
    public void notify(ZwangineEvent event) throws Exception {
        if (event instanceof WorkflowAddedEvent rse) {
            Endpoint endpoint = rse.getWorkflow().getEndpoint();
            String workflowId = rse.getWorkflow().getId();

            // a HashSet is fine for inputs as we only have a limited number of those
            Set<String> uris = new HashSet<>();
            uris.add(endpoint.getEndpointUri());
            inputs.put(workflowId, uris);
            // use a LRUCache for outputs as we could potential have unlimited uris if dynamic routing is in use
            // and therefore need to have the limit in use
            outputs.put(workflowId, LRUCacheFactory.newLRUCache(limit));
        } else if (event instanceof WorkflowRemovedEvent rse) {
            String workflowId = rse.getWorkflow().getId();
            inputs.remove(workflowId);
            outputs.remove(workflowId);
            if (extended) {
                String uri = rse.getWorkflow().getEndpoint().getEndpointUri();
                String key = asUtilizationKey(workflowId, uri);
                if (key != null) {
                    inputUtilization.remove(key);
                }
            }
        } else if (extended && event instanceof ExchangeCreatedEvent ece) {
            // we only capture details in extended mode
            Endpoint endpoint = ece.getExchange().getFromEndpoint();
            if (endpoint != null) {
                String workflowId = ece.getExchange().getFromWorkflowId();
                String uri = endpoint.getEndpointUri();
                String key = asUtilizationKey(workflowId, uri);
                if (key != null) {
                    inputUtilization.onHit(key);
                }
            }
        } else if (event instanceof ExchangeSendingEvent ese) {
            Endpoint endpoint = ese.getEndpoint();
            String workflowId = ExchangeHelper.getWorkflowId(ese.getExchange());
            String uri = endpoint.getEndpointUri();

            Map<String, String> uris = outputs.get(workflowId);
            if (uris != null) {
                uris.putIfAbsent(uri, uri);
            }
            if (extended) {
                String key = asUtilizationKey(workflowId, uri);
                if (key != null) {
                    outputUtilization.onHit(key);
                }
            }
        }
    }

    @Override
    public boolean isDisabled() {
        return !enabled;
    }

    @Override
    public boolean isEnabled(ZwangineEvent event) {
        return enabled && event instanceof ExchangeCreatedEvent
                || event instanceof ExchangeSendingEvent
                || event instanceof WorkflowAddedEvent
                || event instanceof WorkflowRemovedEvent;
    }

    private static String asUtilizationKey(String workflowId, String uri) {
        if (workflowId == null || uri == null) {
            return null;
        } else {
            return workflowId + "|" + uri;
        }
    }

    private static final class EndpointRuntimeStatistics implements Statistic {

        private final String uri;
        private final String workflowId;
        private final String direction;
        private final long hits;

        private EndpointRuntimeStatistics(String uri, String workflowId, String direction, long hits) {
            this.uri = uri;
            this.workflowId = workflowId;
            this.direction = direction;
            this.hits = hits;
        }

        @Override
        public String getUri() {
            return uri;
        }

        @Override
        public String getWorkflowId() {
            return workflowId;
        }

        @Override
        public String getDirection() {
            return direction;
        }

        @Override
        public long getHits() {
            return hits;
        }
    }
}
