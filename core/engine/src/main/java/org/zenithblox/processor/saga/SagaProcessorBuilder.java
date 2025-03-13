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
package org.zenithblox.processor.saga;

import org.zenithblox.ZwangineContext;
import org.zenithblox.Processor;
import org.zenithblox.saga.ZwangineSagaService;
import org.zenithblox.saga.ZwangineSagaStep;

/**
 * Builder of Saga processors.
 */
public class SagaProcessorBuilder {

    private ZwangineContext zwangineContext;

    private Processor childProcessor;

    private ZwangineSagaService sagaService;

    private ZwangineSagaStep step;

    private SagaPropagation propagation;

    private SagaCompletionMode completionMode;

    public SagaProcessorBuilder() {
    }

    public SagaProcessorBuilder zwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
        return this;
    }

    public SagaProcessorBuilder childProcessor(Processor childProcessor) {
        this.childProcessor = childProcessor;
        return this;
    }

    public SagaProcessorBuilder sagaService(ZwangineSagaService sagaService) {
        this.sagaService = sagaService;
        return this;
    }

    public SagaProcessorBuilder step(ZwangineSagaStep step) {
        this.step = step;
        return this;
    }

    public SagaProcessorBuilder propagation(SagaPropagation propagation) {
        this.propagation = propagation;
        return this;
    }

    public SagaProcessorBuilder completionMode(SagaCompletionMode completionMode) {
        this.completionMode = completionMode;
        return this;
    }

    public SagaProcessor build() {
        if (propagation == null) {
            throw new IllegalStateException("A propagation mode has not been set");
        }

        switch (propagation) {
            case REQUIRED:
                return new RequiredSagaProcessor(zwangineContext, childProcessor, sagaService, completionMode, step);
            case REQUIRES_NEW:
                return new RequiresNewSagaProcessor(zwangineContext, childProcessor, sagaService, completionMode, step);
            case SUPPORTS:
                return new SupportsSagaProcessor(zwangineContext, childProcessor, sagaService, completionMode, step);
            case NOT_SUPPORTED:
                return new NotSupportedSagaProcessor(zwangineContext, childProcessor, sagaService, completionMode, step);
            case NEVER:
                return new NeverSagaProcessor(zwangineContext, childProcessor, sagaService, completionMode, step);
            case MANDATORY:
                return new MandatorySagaProcessor(zwangineContext, childProcessor, sagaService, completionMode, step);
            default:
                throw new IllegalStateException("Unsupported propagation mode: " + propagation);
        }
    }

}
