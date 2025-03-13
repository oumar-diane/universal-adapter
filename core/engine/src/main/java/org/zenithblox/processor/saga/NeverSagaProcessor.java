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

import org.zenithblox.*;
import org.zenithblox.saga.ZwangineSagaService;
import org.zenithblox.saga.ZwangineSagaStep;

/**
 * Saga processor implementing the NEVER propagation mode.
 */
public class NeverSagaProcessor extends SagaProcessor {

    public NeverSagaProcessor(ZwangineContext zwangineContext, Processor childProcessor, ZwangineSagaService sagaService,
                              SagaCompletionMode completionMode, ZwangineSagaStep step) {
        super(zwangineContext, childProcessor, sagaService, completionMode, step);
        if (!step.isEmpty()) {
            throw new IllegalArgumentException("Saga configuration is not allowed when propagation is set to NEVER");
        }
        if (completionMode != null && completionMode != SagaCompletionMode.defaultCompletionMode()) {
            throw new IllegalArgumentException("CompletionMode cannot be specified when propagation is NEVER");
        }
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        getCurrentSagaCoordinator(exchange).whenComplete((coordinator, ex) -> ifNotException(ex, exchange, callback, () -> {
            if (coordinator != null) {
                exchange.setException(
                        new ZwangineExchangeException("Workflow cannot handle exchanges that are joining a saga", exchange));
                callback.done(false);
            } else {
                super.process(exchange, doneSync -> callback.done(false));
            }
        }));
        return false;
    }
}
