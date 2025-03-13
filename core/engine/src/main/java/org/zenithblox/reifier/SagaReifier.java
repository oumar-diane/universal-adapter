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

import org.zenithblox.Endpoint;
import org.zenithblox.Expression;
import org.zenithblox.Processor;
import org.zenithblox.Workflow;
import org.zenithblox.model.ProcessorDefinition;
import org.zenithblox.model.PropertyExpressionDefinition;
import org.zenithblox.model.SagaActionUriDefinition;
import org.zenithblox.model.SagaDefinition;
import org.zenithblox.processor.saga.SagaCompletionMode;
import org.zenithblox.processor.saga.SagaProcessorBuilder;
import org.zenithblox.processor.saga.SagaPropagation;
import org.zenithblox.saga.ZwangineSagaService;
import org.zenithblox.saga.ZwangineSagaStep;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class SagaReifier extends ProcessorReifier<SagaDefinition> {

    public SagaReifier(Workflow workflow, ProcessorDefinition<?> definition) {
        super(workflow, (SagaDefinition) definition);
    }

    @Override
    public Processor createProcessor() throws Exception {
        Endpoint compensationEndpoint = Optional.ofNullable(definition.getCompensation())
                .map(SagaActionUriDefinition::getUri)
                .map(this::resolveEndpoint)
                .orElse(null);

        Endpoint completionEndpoint = Optional.ofNullable(definition.getCompletion())
                .map(SagaActionUriDefinition::getUri)
                .map(this::resolveEndpoint)
                .orElse(null);

        Map<String, Expression> optionsMap = new TreeMap<>();
        if (definition.getOptions() != null) {
            for (PropertyExpressionDefinition def : definition.getOptions()) {
                String optionName = def.getKey();
                Expression expr = createExpression(def.getExpression());
                optionsMap.put(optionName, expr);
            }
        }

        String timeout = definition.getTimeout();
        ZwangineSagaStep step = new ZwangineSagaStep(
                compensationEndpoint, completionEndpoint, optionsMap,
                parseDuration(timeout));

        SagaPropagation propagation = parse(SagaPropagation.class, definition.getPropagation());
        if (propagation == null) {
            // default propagation mode
            propagation = SagaPropagation.REQUIRED;
        }

        SagaCompletionMode completionMode = parse(SagaCompletionMode.class, definition.getCompletionMode());
        if (completionMode == null) {
            // default completion mode
            completionMode = SagaCompletionMode.defaultCompletionMode();
        }

        Processor childProcessor = this.createChildProcessor(true);
        ZwangineSagaService zwangineSagaService = resolveSagaService();

        zwangineSagaService.registerStep(step);

        return new SagaProcessorBuilder().zwangineContext(zwangineContext).childProcessor(childProcessor)
                .sagaService(zwangineSagaService).step(step)
                .propagation(propagation).completionMode(completionMode).build();
    }

    protected ZwangineSagaService resolveSagaService() {
        ZwangineSagaService sagaService = definition.getSagaServiceBean();
        if (sagaService != null) {
            return sagaService;
        }

        String ref = parseString(definition.getSagaService());
        if (ref != null) {
            return mandatoryLookup(ref, ZwangineSagaService.class);
        }

        sagaService = zwangineContext.hasService(ZwangineSagaService.class);
        if (sagaService != null) {
            return sagaService;
        }

        return mandatoryFindSingleByType(ZwangineSagaService.class);
    }

}
