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
import org.zenithblox.saga.ZwangineSagaCoordinator;
import org.zenithblox.saga.ZwangineSagaService;
import org.zenithblox.saga.ZwangineSagaStep;

import java.util.concurrent.CompletableFuture;

/**
 * Saga processor implementing the REQUIRED propagation mode.
 */
public class RequiredSagaProcessor extends SagaProcessor {

    public RequiredSagaProcessor(ZwangineContext zwangineContext, Processor childProcessor, ZwangineSagaService sagaService,
                                 SagaCompletionMode completionMode, ZwangineSagaStep step) {
        super(zwangineContext, childProcessor, sagaService, completionMode, step);
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        getCurrentSagaCoordinator(exchange)
                .whenComplete((existingCoordinator, ex) -> ifNotException(ex, exchange, callback, () -> {
                    CompletableFuture<ZwangineSagaCoordinator> coordinatorFuture;
                    final boolean inheritedCoordinator;
                    if (existingCoordinator != null) {
                        coordinatorFuture = CompletableFuture.completedFuture(existingCoordinator);
                        inheritedCoordinator = true;
                    } else {
                        coordinatorFuture = sagaService.newSaga(exchange);
                        inheritedCoordinator = false;
                    }

                    coordinatorFuture.whenComplete((coordinator, ex2) -> ifNotException(ex2, exchange, !inheritedCoordinator,
                            coordinator, existingCoordinator, callback, () -> {
                                setCurrentSagaCoordinator(exchange, coordinator);
                                coordinator.beginStep(exchange, step).whenComplete((done, ex3) -> ifNotException(ex3, exchange,
                                        !inheritedCoordinator, coordinator, existingCoordinator, callback, () -> {
                                            super.process(exchange, doneSync -> {
                                                if (!inheritedCoordinator) {
                                                    // Saga starts and ends here
                                                    handleSagaCompletion(exchange, coordinator, null, callback);
                                                } else {
                                                    callback.done(false);
                                                }
                                            });
                                        }));
                            }));
                }));

        return false;
    }
}
