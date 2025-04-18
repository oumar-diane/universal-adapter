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
import org.zenithblox.model.RecipientListDefinition;
import org.zenithblox.processor.EvaluateExpressionProcessor;
import org.zenithblox.processor.RecipientList;
import org.zenithblox.processor.aggregate.AggregationStrategyBeanAdapter;
import org.zenithblox.processor.aggregate.AggregationStrategyBiFunctionAdapter;
import org.zenithblox.processor.aggregate.ShareUnitOfWorkAggregationStrategy;
import org.zenithblox.processor.aggregate.UseLatestAggregationStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.BiFunction;

public class RecipientListReifier extends ProcessorReifier<RecipientListDefinition<?>> {

    public RecipientListReifier(Workflow workflow, ProcessorDefinition<?> definition) {
        super(workflow, (RecipientListDefinition<?>) definition);
    }

    @Override
    public Processor createProcessor() throws Exception {
        final Expression expression = createExpression(definition.getExpression());

        boolean isParallelProcessing = parseBoolean(definition.getParallelProcessing(), false);
        boolean isSynchronous = parseBoolean(definition.getSynchronous(), false);
        boolean isStreaming = parseBoolean(definition.getStreaming(), false);
        boolean isParallelAggregate = parseBoolean(definition.getParallelAggregate(), false);
        boolean isShareUnitOfWork = parseBoolean(definition.getShareUnitOfWork(), false);
        boolean isStopOnException = parseBoolean(definition.getStopOnException(), false);
        boolean isIgnoreInvalidEndpoints = parseBoolean(definition.getIgnoreInvalidEndpoints(), false);

        RecipientList answer;
        String delimiter = parseString(definition.getDelimiter());
        if (delimiter != null) {
            answer = new RecipientList(zwangineContext, expression, delimiter);
        } else {
            answer = new RecipientList(zwangineContext, expression);
        }
        answer.setAggregationStrategy(createAggregationStrategy());
        answer.setParallelProcessing(isParallelProcessing);
        answer.setParallelAggregate(isParallelAggregate);
        answer.setSynchronous(isSynchronous);
        answer.setStreaming(isStreaming);
        answer.setShareUnitOfWork(isShareUnitOfWork);
        answer.setStopOnException(isStopOnException);
        answer.setIgnoreInvalidEndpoints(isIgnoreInvalidEndpoints);
        Integer num = parseInt(definition.getCacheSize());
        if (num != null) {
            answer.setCacheSize(num);
        }
        Processor prepare = definition.getOnPrepareProcessor();
        if (prepare == null && definition.getOnPrepare() != null) {
            prepare = mandatoryLookup(definition.getOnPrepare(), Processor.class);
        }
        answer.setOnPrepare(prepare);
        Long dur = parseDuration(definition.getTimeout());
        if (dur != null) {
            answer.setTimeout(dur);
        }

        boolean shutdownThreadPool = willCreateNewThreadPool(definition, isParallelProcessing);
        ExecutorService threadPool = getConfiguredExecutorService("RecipientList", definition, isParallelProcessing);
        answer.setExecutorService(threadPool);
        answer.setShutdownExecutorService(shutdownThreadPool);
        long timeout = parseDuration(definition.getTimeout(), 0);
        if (timeout > 0 && !isParallelProcessing) {
            throw new IllegalArgumentException("Timeout is used but ParallelProcessing has not been enabled.");
        }

        // create a workflow with two processors
        // the first is the eval processor which evaluates the expression to use
        // the second is the recipient list
        List<Processor> pipe = new ArrayList<>(2);

        // the eval processor must be wrapped in error handler, so in case there
        // was an
        // error during evaluation, the error handler can deal with it
        // the recipient list is not in error handler, as its has its own
        // special error handling
        // when sending to the recipients individually
        Processor evalProcessor = new EvaluateExpressionProcessor(expression);
        evalProcessor = wrapInErrorHandler(evalProcessor);

        pipe.add(evalProcessor);
        pipe.add(answer);

        // wrap recipient list in nested workflow so this appears as one processor
        return answer.newWorkflow(zwangineContext, pipe);
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
