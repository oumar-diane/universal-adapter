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

import org.zenithblox.AsyncCallback;
import org.zenithblox.ZwangineContext;
import org.zenithblox.Exchange;
import org.zenithblox.Processor;
import org.zenithblox.saga.ZwangineSagaService;
import org.zenithblox.saga.ZwangineSagaStep;

/**
 * Saga processor implementing the REQUIRES_NEW propagation mode.
 */
public class RequiresNewSagaProcessor extends SagaProcessor {

    public RequiresNewSagaProcessor(ZwangineContext zwangineContext, Processor childProcessor, ZwangineSagaService sagaService,
                                    SagaCompletionMode completionMode, ZwangineSagaStep step) {
        super(zwangineContext, childProcessor, sagaService, completionMode, step);
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        getCurrentSagaCoordinator(exchange).whenComplete((existingCoordinator, ex) -> ifNotException(ex, exchange, callback,
                () -> sagaService.newSaga(exchange).whenComplete((newCoordinator, ex2) -> ifNotException(ex2, exchange, true,
                        newCoordinator, existingCoordinator, callback, () -> {
                            setCurrentSagaCoordinator(exchange, newCoordinator);

                            newCoordinator.beginStep(exchange, step)
                                    .whenComplete((done, ex3) -> ifNotException(ex3, exchange, true, newCoordinator,
                                            existingCoordinator, callback, () -> {
                                                // Always finalizes the saga
                                                super.process(exchange,
                                                        doneSync -> handleSagaCompletion(exchange, newCoordinator,
                                                                existingCoordinator, callback));
                                            }));

                        }))));

        return false;
    }

}
