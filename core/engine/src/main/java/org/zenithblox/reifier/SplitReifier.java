/*
 * Licensed to the  Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the  License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.zentihblox.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zenithblox.reifier;

import org.zenithblox.*;
import org.zenithblox.model.ProcessorDefinition;
import org.zenithblox.model.SplitDefinition;
import org.zenithblox.processor.Splitter;
import org.zenithblox.processor.aggregate.AggregationStrategyBeanAdapter;
import org.zenithblox.processor.aggregate.AggregationStrategyBiFunctionAdapter;
import org.zenithblox.processor.aggregate.ShareUnitOfWorkAggregationStrategy;

import java.util.concurrent.ExecutorService;
import java.util.function.BiFunction;

public class SplitReifier extends ExpressionReifier<SplitDefinition> {

    public SplitReifier(Workflow workflow, ProcessorDefinition<?> definition) {
        super(workflow, (SplitDefinition) definition);
    }

    @Override
    public Processor createProcessor() throws Exception {
        Processor childProcessor = this.createChildProcessor(true);

        final AggregationStrategy strategy = createAggregationStrategy();

        boolean isParallelProcessing = parseBoolean(definition.getParallelProcessing(), false);
        boolean isSynchronous = parseBoolean(definition.getSynchronous(), false);
        boolean isStreaming = parseBoolean(definition.getStreaming(), false);
        boolean isShareUnitOfWork = parseBoolean(definition.getShareUnitOfWork(), false);
        boolean isParallelAggregate = parseBoolean(definition.getParallelAggregate(), false);
        boolean isStopOnException = parseBoolean(definition.getStopOnException(), false);
        boolean shutdownThreadPool = willCreateNewThreadPool(definition, isParallelProcessing);
        ExecutorService threadPool = getConfiguredExecutorService("Split", definition, isParallelProcessing);

        long timeout = parseDuration(definition.getTimeout(), 0);
        if (timeout > 0 && !isParallelProcessing) {
            throw new IllegalArgumentException("Timeout is used but ParallelProcessing has not been enabled.");
        }
        Processor prepare = definition.getOnPrepareProcessor();
        if (prepare == null && definition.getOnPrepare() != null) {
            prepare = mandatoryLookup(definition.getOnPrepare(), Processor.class);
        }

        Expression exp = createExpression(definition.getExpression());
        String delimiter = parseString(definition.getDelimiter());

        Splitter answer;
        if (delimiter != null) {
            answer = new Splitter(
                    zwangineContext, workflow, exp, childProcessor, strategy, isParallelProcessing,
                    threadPool, shutdownThreadPool, isStreaming, isStopOnException, timeout, prepare,
                    isShareUnitOfWork, isParallelAggregate, delimiter);
        } else {
            answer = new Splitter(
                    zwangineContext, workflow, exp, childProcessor, strategy, isParallelProcessing,
                    threadPool, shutdownThreadPool, isStreaming, isStopOnException, timeout, prepare,
                    isShareUnitOfWork, isParallelAggregate);
        }
        answer.setSynchronous(isSynchronous);

        return answer;
    }

    private AggregationStrategy createAggregationStrategy() {
        AggregationStrategy strategy = definition.getAggregationStrategyBean();
        if (strategy == null && definition.getAggregationStrategy() != null) {
            Object aggStrategy = lookupByName(definition.getAggregationStrategy());
            if (aggStrategy instanceof AggregationStrategy aggregationStrategy) {
                strategy = aggregationStrategy;
            } else if (aggStrategy instanceof BiFunction biFunction) {
                AggregationStrategyBiFunctionAdapter adapter
                        = new AggregationStrategyBiFunctionAdapter(biFunction);
                if (definition.getAggregationStrategyMethodAllowNull() != null) {
                    adapter.setAllowNullNewExchange(parseBoolean(definition.getAggregationStrategyMethodAllowNull(), false));
                    adapter.setAllowNullOldExchange(parseBoolean(definition.getAggregationStrategyMethodAllowNull(), false));
                }
                strategy = adapter;
            } else if (aggStrategy != null) {
                AggregationStrategyBeanAdapter adapter
                        = new AggregationStrategyBeanAdapter(aggStrategy, definition.getAggregationStrategyMethodName());
                if (definition.getAggregationStrategyMethodAllowNull() != null) {
                    adapter.setAllowNullNewExchange(parseBoolean(definition.getAggregationStrategyMethodAllowNull(), false));
                    adapter.setAllowNullOldExchange(parseBoolean(definition.getAggregationStrategyMethodAllowNull(), false));
                }
                strategy = adapter;
            } else {
                throw new IllegalArgumentException(
                        "Cannot find AggregationStrategy in Registry with name: " + definition.getAggregationStrategy());
            }
        }

        ZwangineContextAware.trySetZwangineContext(strategy, zwangineContext);
        if (strategy != null && parseBoolean(definition.getShareUnitOfWork(), false)) {
            // wrap strategy in share unit of work
            strategy = new ShareUnitOfWorkAggregationStrategy(strategy);
        }

        return strategy;
    }

}
