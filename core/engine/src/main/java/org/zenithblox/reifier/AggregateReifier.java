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
import org.zenithblox.model.AggregateDefinition;
import org.zenithblox.model.OptimisticLockRetryPolicyDefinition;
import org.zenithblox.model.ProcessorDefinition;
import org.zenithblox.processor.aggregate.AggregateController;
import org.zenithblox.processor.aggregate.AggregateProcessor;
import org.zenithblox.processor.aggregate.OptimisticLockRetryPolicy;
import org.zenithblox.spi.AggregationRepository;
import org.zenithblox.spi.ExecutorServiceManager;
import org.zenithblox.support.PluginHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public class AggregateReifier extends ProcessorReifier<AggregateDefinition> {

    private static final Logger LOG = LoggerFactory.getLogger(AggregateReifier.class);

    public AggregateReifier(Workflow workflow, ProcessorDefinition<?> definition) {
        super(workflow, AggregateDefinition.class.cast(definition));
    }

    @Override
    public Processor createProcessor() throws Exception {
        return createAggregator();
    }

    protected AggregateProcessor createAggregator() throws Exception {
        Processor childProcessor = this.createChildProcessor(true);

        // wrap the aggregate workflow in a unit of work processor
        AsyncProcessor target = PluginHelper.getInternalProcessorFactory(zwangineContext)
                .addUnitOfWorkProcessorAdvice(zwangineContext, childProcessor, workflow);

        // correlation expression is required
        if (definition.getExpression() == null) {
            throw new IllegalArgumentException("CorrelationExpression must be set on " + definition);
        }

        Expression correlation = createExpression(definition.getExpression());
        AggregationStrategy strategy = getConfiguredAggregationStrategy(definition);
        // strategy is required
        if (strategy == null) {
            throw new IllegalArgumentException("AggregationStrategy must be set on " + definition);
        }

        boolean parallel = parseBoolean(definition.getParallelProcessing(), false);
        boolean shutdownThreadPool = willCreateNewThreadPool(definition, parallel);
        ExecutorService threadPool = getConfiguredExecutorService("Aggregator", definition, parallel);
        if (threadPool == null && !parallel) {
            // executor service is mandatory for the Aggregator
            ExecutorServiceManager manager = zwangineContext.getExecutorServiceManager();
            // we do not run in parallel mode, but use a single thread executor must be used
            threadPool = manager.newSingleThreadExecutor(definition, "Aggregator");
            shutdownThreadPool = true;
        }

        AggregateProcessor answer
                = new AggregateProcessor(zwangineContext, target, correlation, strategy, threadPool, shutdownThreadPool);

        AggregationRepository repository = createAggregationRepository();
        if (repository != null) {
            answer.setAggregationRepository(repository);
        }
        AggregateController controller = createAggregateController();
        if (controller != null) {
            answer.setAggregateController(controller);
        }

        // this EIP supports using a shared timeout checker thread pool or
        // fallback to create a new thread pool
        boolean shutdownTimeoutThreadPool = false;
        ScheduledExecutorService timeoutThreadPool = definition.getTimeoutCheckerExecutorServiceBean();
        if (timeoutThreadPool == null && definition.getTimeoutCheckerExecutorService() != null) {
            // lookup existing thread pool
            timeoutThreadPool
                    = lookupByNameAndType(definition.getTimeoutCheckerExecutorService(), ScheduledExecutorService.class);
            if (timeoutThreadPool == null) {
                // then create a thread pool assuming the ref is a thread pool
                // profile id
                timeoutThreadPool = zwangineContext.getExecutorServiceManager().newScheduledThreadPool(this,
                        AggregateProcessor.AGGREGATE_TIMEOUT_CHECKER,
                        definition.getTimeoutCheckerExecutorService());
                if (timeoutThreadPool == null) {
                    throw new IllegalArgumentException(
                            "ExecutorServiceRef " + definition.getTimeoutCheckerExecutorService()
                                                       + " not found in registry (as an ScheduledExecutorService instance) or as a thread pool profile.");
                }
                shutdownTimeoutThreadPool = true;
            }
        }
        answer.setTimeoutCheckerExecutorService(timeoutThreadPool);
        answer.setShutdownTimeoutCheckerExecutorService(shutdownTimeoutThreadPool);

        if (parseBoolean(definition.getCompletionFromBatchConsumer(), false)
                && parseBoolean(definition.getDiscardOnAggregationFailure(), false)) {
            throw new IllegalArgumentException(
                    "Cannot use both completionFromBatchConsumer and discardOnAggregationFailure on: " + definition);
        }

        // set other options
        answer.setParallelProcessing(parallel);
        Boolean optimisticLocking = parseBoolean(definition.getOptimisticLocking());
        if (optimisticLocking != null) {
            answer.setOptimisticLocking(optimisticLocking);
        }
        if (definition.getCompletionPredicate() != null) {
            Predicate predicate = createPredicate(definition.getCompletionPredicate());
            answer.setCompletionPredicate(predicate);
        } else if (strategy instanceof Predicate predicate) {
            // if aggregation strategy implements predicate and was not
            // configured then use as fallback
            LOG.debug("Using AggregationStrategy as completion predicate: {}", strategy);
            answer.setCompletionPredicate(predicate);
        }
        if (definition.getCompletionTimeoutExpression() != null) {
            Expression expression = createExpression(definition.getCompletionTimeoutExpression());
            answer.setCompletionTimeoutExpression(expression);
        }
        Long completionTimeout = parseDuration(definition.getCompletionTimeout());
        if (completionTimeout != null) {
            answer.setCompletionTimeout(completionTimeout);
        }
        Long completionInterval = parseDuration(definition.getCompletionInterval());
        if (completionInterval != null) {
            answer.setCompletionInterval(completionInterval);
        }
        if (definition.getCompletionSizeExpression() != null) {
            Expression expression = createExpression(definition.getCompletionSizeExpression());
            answer.setCompletionSizeExpression(expression);
        }
        Integer completionSize = parseInt(definition.getCompletionSize());
        if (completionSize != null) {
            answer.setCompletionSize(completionSize);
        }
        Boolean completionFromBatchConsumer = parseBoolean(definition.getCompletionFromBatchConsumer());
        if (completionFromBatchConsumer != null) {
            answer.setCompletionFromBatchConsumer(completionFromBatchConsumer);
        }
        Boolean completionOnNewCorrelationGroup = parseBoolean(definition.getCompletionOnNewCorrelationGroup());
        if (completionOnNewCorrelationGroup != null) {
            answer.setCompletionOnNewCorrelationGroup(completionOnNewCorrelationGroup);
        }
        Boolean eagerCheckCompletion = parseBoolean(definition.getEagerCheckCompletion());
        if (eagerCheckCompletion != null) {
            answer.setEagerCheckCompletion(eagerCheckCompletion);
        }
        Boolean ignoreInvalidCorrelationKeys = parseBoolean(definition.getIgnoreInvalidCorrelationKeys());
        if (ignoreInvalidCorrelationKeys != null) {
            answer.setIgnoreInvalidCorrelationKeys(ignoreInvalidCorrelationKeys);
        }
        Integer closeCorrelationKeyOnCompletion = parseInt(definition.getCloseCorrelationKeyOnCompletion());
        if (closeCorrelationKeyOnCompletion != null) {
            answer.setCloseCorrelationKeyOnCompletion(closeCorrelationKeyOnCompletion);
        }
        Boolean discardOnCompletionTimeout = parseBoolean(definition.getDiscardOnCompletionTimeout());
        if (discardOnCompletionTimeout != null) {
            answer.setDiscardOnCompletionTimeout(discardOnCompletionTimeout);
        }
        Boolean discardOnAggregationFailure = parseBoolean(definition.getDiscardOnAggregationFailure());
        if (discardOnAggregationFailure != null) {
            answer.setDiscardOnAggregationFailure(discardOnAggregationFailure);
        }
        Boolean forceCompletionOnStop = parseBoolean(definition.getForceCompletionOnStop());
        if (forceCompletionOnStop != null) {
            answer.setForceCompletionOnStop(forceCompletionOnStop);
        }
        Boolean completeAllOnStop = parseBoolean(definition.getCompleteAllOnStop());
        if (completeAllOnStop != null) {
            answer.setCompleteAllOnStop(completeAllOnStop);
        }
        if (definition.getOptimisticLockRetryPolicy() == null) {
            if (definition.getOptimisticLockRetryPolicyDefinition() != null) {
                answer.setOptimisticLockRetryPolicy(
                        createOptimisticLockRetryPolicy(definition.getOptimisticLockRetryPolicyDefinition()));
            }
        } else {
            answer.setOptimisticLockRetryPolicy(definition.getOptimisticLockRetryPolicy());
        }

        Long completionTimeoutCheckerInterval = parseDuration(definition.getCompletionTimeoutCheckerInterval());
        if (completionTimeoutCheckerInterval != null) {
            answer.setCompletionTimeoutCheckerInterval(completionTimeoutCheckerInterval);
        }
        return answer;
    }

    public OptimisticLockRetryPolicy createOptimisticLockRetryPolicy(OptimisticLockRetryPolicyDefinition definition) {
        OptimisticLockRetryPolicy policy = new OptimisticLockRetryPolicy();
        Integer num = parseInt(definition.getMaximumRetries());
        if (num != null) {
            policy.setMaximumRetries(num);
        }
        Long dur = parseDuration(definition.getRetryDelay());
        if (dur != null) {
            policy.setRetryDelay(dur);
        }
        dur = parseDuration(definition.getMaximumRetryDelay());
        if (dur != null) {
            policy.setMaximumRetryDelay(dur);
        }
        if (definition.getExponentialBackOff() != null) {
            policy.setExponentialBackOff(parseBoolean(definition.getExponentialBackOff(), true));
        }
        if (definition.getRandomBackOff() != null) {
            policy.setRandomBackOff(parseBoolean(definition.getRandomBackOff(), false));
        }
        return policy;
    }

    private AggregationRepository createAggregationRepository() {
        AggregationRepository repository = definition.getAggregationRepositoryBean();
        if (repository == null && definition.getAggregationRepository() != null) {
            repository = mandatoryLookup(definition.getAggregationRepository(), AggregationRepository.class);
        }
        return repository;
    }

    private AggregateController createAggregateController() {
        AggregateController controller = definition.getAggregateControllerBean();
        if (controller == null && definition.getAggregateController() != null) {
            controller = mandatoryLookup(definition.getAggregateController(), AggregateController.class);
        }
        return controller;
    }

}
