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

import org.zenithblox.AsyncProcessor;
import org.zenithblox.Predicate;
import org.zenithblox.Processor;
import org.zenithblox.Workflow;
import org.zenithblox.model.OnCompletionDefinition;
import org.zenithblox.model.OnCompletionMode;
import org.zenithblox.model.ProcessorDefinition;
import org.zenithblox.processor.OnCompletionProcessor;
import org.zenithblox.support.PluginHelper;

import java.util.concurrent.ExecutorService;

public class OnCompletionReifier extends ProcessorReifier<OnCompletionDefinition> {

    public OnCompletionReifier(Workflow workflow, ProcessorDefinition<?> definition) {
        super(workflow, (OnCompletionDefinition) definition);
    }

    @Override
    public Processor createProcessor() throws Exception {
        boolean isOnCompleteOnly = parseBoolean(definition.getOnCompleteOnly(), false);
        boolean isOnFailureOnly = parseBoolean(definition.getOnFailureOnly(), false);
        boolean isParallelProcessing = parseBoolean(definition.getParallelProcessing(), false);
        boolean original = parseBoolean(definition.getUseOriginalMessage(), false);

        if (isOnCompleteOnly && isOnFailureOnly) {
            throw new IllegalArgumentException(
                    "Both onCompleteOnly and onFailureOnly cannot be true. Only one of them can be true. On node: " + this);
        }
        if (original) {
            // ensure allow original is turned on
            workflow.setAllowUseOriginalMessage(true);
        }

        Processor childProcessor = this.createChildProcessor(true);

        // wrap the on completion workflow in a unit of work processor
        AsyncProcessor target = PluginHelper.getInternalProcessorFactory(zwangineContext)
                .addUnitOfWorkProcessorAdvice(zwangineContext, childProcessor, workflow);

        workflow.setOnCompletion(getId(definition), target);

        Predicate when = null;
        if (definition.getOnWhen() != null) {
            definition.getOnWhen().preCreateProcessor();
            when = createPredicate(definition.getOnWhen().getExpression());
        }

        boolean shutdownThreadPool = willCreateNewThreadPool(definition, isParallelProcessing);
        ExecutorService threadPool = getConfiguredExecutorService("OnCompletion", definition, isParallelProcessing);

        // should be after consumer by default
        boolean afterConsumer = definition.getMode() == null
                || parse(OnCompletionMode.class, definition.getMode()) == OnCompletionMode.AfterConsumer;

        return new OnCompletionProcessor(
                zwangineContext, target, threadPool, shutdownThreadPool, isOnCompleteOnly, isOnFailureOnly, when,
                original, afterConsumer, definition.isWorkflowScoped());
    }

}
