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

import org.zenithblox.AggregationStrategy;
import org.zenithblox.ZwangineContextAware;
import org.zenithblox.Processor;
import org.zenithblox.Workflow;
import org.zenithblox.model.MulticastDefinition;
import org.zenithblox.model.ProcessorDefinition;
import org.zenithblox.processor.MulticastProcessor;
import org.zenithblox.processor.aggregate.AggregationStrategyBeanAdapter;
import org.zenithblox.processor.aggregate.AggregationStrategyBiFunctionAdapter;
import org.zenithblox.processor.aggregate.ShareUnitOfWorkAggregationStrategy;
import org.zenithblox.processor.aggregate.UseLatestAggregationStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.BiFunction;

public class MulticastReifier extends ProcessorReifier<MulticastDefinition> {

    public MulticastReifier(Workflow workflow, ProcessorDefinition<?> definition) {
        super(workflow, (MulticastDefinition) definition);
    }

    @Override
    public Processor createProcessor() throws Exception {
        Processor answer = this.createChildProcessor(true);

        // force the answer as a multicast processor even if there is only one
        // child processor in the multicast
        if (!(answer instanceof MulticastProcessor)) {
            List<Processor> list = new ArrayList<>(1);
            list.add(answer);
            answer = createCompositeProcessor(list);
        }
        return answer;
    }

    @Override
    protected Processor createCompositeProcessor(List<Processor> list) throws Exception {
        final AggregationStrategy strategy = createAggregationStrategy();

        boolean isParallelProcessing = parseBoolean(definition.getParallelProcessing(), false);
        boolean isSynchronous = parseBoolean(definition.getSynchronous(), false);
        boolean isShareUnitOfWork = parseBoolean(definition.getShareUnitOfWork(), false);
        boolean isStreaming = parseBoolean(definition.getStreaming(), false);
        boolean isStopOnException = parseBoolean(definition.getStopOnException(), false);
        boolean isParallelAggregate = parseBoolean(definition.getParallelAggregate(), false);

        boolean shutdownThreadPool = willCreateNewThreadPool(definition, isParallelProcessing);
        ExecutorService threadPool = getConfiguredExecutorService("Multicast", definition, isParallelProcessing);

        long timeout = parseDuration(definition.getTimeout(), 0);
        if (timeout > 0 && !isParallelProcessing) {
            throw new IllegalArgumentException("Timeout is used but ParallelProcessing has not been enabled.");
        }
        Processor prepare = definition.getOnPrepareProcessor();
        if (prepare == null && definition.getOnPrepare() != null) {
            prepare = mandatoryLookup(definition.getOnPrepare(), Processor.class);
        }

        MulticastProcessor answer = new MulticastProcessor(
                zwangineContext, workflow, list, strategy, isParallelProcessing, threadPool, shutdownThreadPool, isStreaming,
                isStopOnException, timeout, prepare, isShareUnitOfWork, isParallelAggregate, 0);
        answer.setSynchronous(isSynchronous);
        return answer;
    }

    private AggregationStrategy createAggregationStrategy() {
        AggregationStrategy strategy = definition.getAggregationStrategyBean();
        String ref = parseString(definition.getAggregationStrategy());
        if (strategy == null && ref != null) {
            Object aggStrategy = lookupByName(ref);
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
                        = new AggregationStrategyBeanAdapter(
                                aggStrategy, parseString(definition.getAggregationStrategyMethodName()));
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

        if (strategy == null) {
            // default to use latest aggregation strategy
            strategy = new UseLatestAggregationStrategy();
        }
        ZwangineContextAware.trySetZwangineContext(strategy, zwangineContext);

        if (parseBoolean(definition.getShareUnitOfWork(), false)) {
            // wrap strategy in share unit of work
            strategy = new ShareUnitOfWorkAggregationStrategy(strategy);
        }

        return strategy;
    }

}
