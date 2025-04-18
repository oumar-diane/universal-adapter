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
import org.zenithblox.spi.ClaimCheckRepository;
import org.zenithblox.spi.IdAware;
import org.zenithblox.spi.WorkflowIdAware;
import org.zenithblox.support.AsyncProcessorSupport;
import org.zenithblox.support.ExchangeHelper;
import org.zenithblox.support.LanguageSupport;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ClaimCheck EIP implementation.
 * <p/>
 * The current Claim Check EIP implementation in Zwangine is only intended for temporary memory repository. Likewise the
 * repository is not shared among {@link Exchange}s, but a private instance is created per {@link Exchange}. This guards
 * against concurrent and thread-safe issues. For off-memory persistent storage of data, then use any of the many Zwangine
 * components that support persistent storage, and do not use this Claim Check EIP implementation.
 */
public class ClaimCheckProcessor extends AsyncProcessorSupport implements IdAware, WorkflowIdAware, ZwangineContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(ClaimCheckProcessor.class);

    private ZwangineContext zwangineContext;
    private String id;
    private String workflowId;
    private String operation;
    private AggregationStrategy aggregationStrategy;
    private String key;
    private Expression keyExpression;
    private String filter;

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
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

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public AggregationStrategy getAggregationStrategy() {
        return aggregationStrategy;
    }

    public void setAggregationStrategy(AggregationStrategy aggregationStrategy) {
        this.aggregationStrategy = aggregationStrategy;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        // the repository is scoped per exchange
        ClaimCheckRepository repo = getClaimCheckRepository(exchange);

        try {
            String claimKey = keyExpression.evaluate(exchange, String.class);
            switch (operation) {
                case "Set" -> operationSetHandler(exchange, claimKey, repo);
                case "Get" -> operationGetHandler(exchange, claimKey, repo);
                case "GetAndRemove" -> operationGetAndRemoveHandler(exchange, claimKey, repo);
                case "Push" -> operationPushHandler(exchange, claimKey, repo);
                case "Pop" -> operationPopHandler(exchange, claimKey, repo);
            }
        } catch (Exception e) {
            exchange.setException(e);
        }

        callback.done(true);
        return true;
    }

    private void operationPopHandler(Exchange exchange, String claimKey, ClaimCheckRepository repo) {
        Exchange copy = repo.pop();
        LOG.debug("Pop: {} -> {}", claimKey, exchange);
        if (copy != null) {
            // prepare the exchanges for aggregation
            ExchangeHelper.prepareAggregation(exchange, copy);
            Exchange result = aggregationStrategy.aggregate(exchange, copy);
            if (result != null) {
                ExchangeHelper.copyResultsPreservePattern(exchange, result);
            }
        }
    }

    private static void operationPushHandler(Exchange exchange, String claimKey, ClaimCheckRepository repo) {
        // copy exchange, and do not share the unit of work
        Exchange copy = ExchangeHelper.createCorrelatedCopy(exchange, false);
        LOG.debug("Push: {} -> {}", claimKey, copy);
        repo.push(copy);
    }

    private void operationGetAndRemoveHandler(Exchange exchange, String claimKey, ClaimCheckRepository repo) {
        Exchange copy = repo.getAndRemove(claimKey);
        LOG.debug("GetAndRemove: {} -> {}", claimKey, exchange);
        if (copy != null) {
            // prepare the exchanges for aggregation
            ExchangeHelper.prepareAggregation(exchange, copy);
            Exchange result = aggregationStrategy.aggregate(exchange, copy);
            if (result != null) {
                ExchangeHelper.copyResultsPreservePattern(exchange, result);
            }
        }
    }

    private void operationGetHandler(Exchange exchange, String claimKey, ClaimCheckRepository repo) {
        Exchange copy = repo.get(claimKey);
        LOG.debug("Get: {} -> {}", claimKey, exchange);
        if (copy != null) {
            Exchange result = aggregationStrategy.aggregate(exchange, copy);
            if (result != null) {
                ExchangeHelper.copyResultsPreservePattern(exchange, result);
            }
        }
    }

    private static void operationSetHandler(Exchange exchange, String claimKey, ClaimCheckRepository repo) {

        // copy exchange, and do not share the unit of work
        Exchange copy = ExchangeHelper.createCorrelatedCopy(exchange, false);
        boolean addedNew = repo.add(claimKey, copy);
        if (addedNew) {
            LOG.debug("Add: {} -> {}", claimKey, copy);
        } else {
            LOG.debug("Override: {} -> {}", claimKey, copy);
        }
    }

    private static ClaimCheckRepository getClaimCheckRepository(Exchange exchange) {
        ClaimCheckRepository repo
                = exchange.getProperty(ExchangePropertyKey.CLAIM_CHECK_REPOSITORY, ClaimCheckRepository.class);
        if (repo == null) {
            repo = new DefaultClaimCheckRepository();
            exchange.setProperty(ExchangePropertyKey.CLAIM_CHECK_REPOSITORY, repo);
        }
        return repo;
    }

    @Override
    protected void doInit() throws Exception {
        if (aggregationStrategy == null) {
            aggregationStrategy = createAggregationStrategy();
        }
        ZwangineContextAware.trySetZwangineContext(aggregationStrategy, zwangineContext);

        if (LanguageSupport.hasSimpleFunction(key)) {
            keyExpression = zwangineContext.resolveLanguage("simple").createExpression(key);
        } else {
            keyExpression = zwangineContext.resolveLanguage("constant").createExpression(key);
        }
    }

    @Override
    protected void doStart() throws Exception {
        ObjectHelper.notNull(operation, "operation", this);
        ServiceHelper.startService(aggregationStrategy);
    }

    @Override
    protected void doStop() throws Exception {
        ServiceHelper.stopService(aggregationStrategy);
    }

    @Override
    public String toString() {
        return id;
    }

    protected AggregationStrategy createAggregationStrategy() {
        ClaimCheckAggregationStrategy answer = new ClaimCheckAggregationStrategy();
        answer.setFilter(filter);
        return answer;
    }
}
