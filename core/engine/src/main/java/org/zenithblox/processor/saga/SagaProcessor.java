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
import org.zenithblox.saga.ZwangineSagaCoordinator;
import org.zenithblox.saga.ZwangineSagaService;
import org.zenithblox.saga.ZwangineSagaStep;
import org.zenithblox.spi.IdAware;
import org.zenithblox.spi.WorkflowIdAware;
import org.zenithblox.support.processor.DelegateAsyncProcessor;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.util.ObjectHelper;

import java.util.concurrent.CompletableFuture;

/**
 * Processor for handling sagas.
 */
public abstract class SagaProcessor extends DelegateAsyncProcessor implements Traceable, IdAware, WorkflowIdAware {

    protected final ZwangineSagaService sagaService;

    protected final ZwangineSagaStep step;

    protected final SagaCompletionMode completionMode;

    private String id;
    private String workflowId;

    public SagaProcessor(ZwangineContext zwangineContext, Processor childProcessor, ZwangineSagaService sagaService,
                         SagaCompletionMode completionMode, ZwangineSagaStep step) {
        super(ObjectHelper.notNull(childProcessor, "childProcessor"));
        this.sagaService = ObjectHelper.notNull(sagaService, "sagaService");
        this.completionMode = ObjectHelper.notNull(completionMode, "completionMode");
        this.step = ObjectHelper.notNull(step, "step");
    }

    protected CompletableFuture<ZwangineSagaCoordinator> getCurrentSagaCoordinator(Exchange exchange) {
        String currentSaga = exchange.getIn().getHeader(Exchange.SAGA_LONG_RUNNING_ACTION, String.class);
        if (currentSaga != null) {
            return sagaService.getSaga(currentSaga);
        }

        return CompletableFuture.completedFuture(null);
    }

    protected void setCurrentSagaCoordinator(Exchange exchange, ZwangineSagaCoordinator coordinator) {
        if (coordinator != null) {
            exchange.getIn().setHeader(Exchange.SAGA_LONG_RUNNING_ACTION, coordinator.getId());
        } else {
            exchange.getIn().removeHeader(Exchange.SAGA_LONG_RUNNING_ACTION);
            exchange.getMessage().removeHeader(Exchange.SAGA_LONG_RUNNING_ACTION);
        }
    }

    protected void handleSagaCompletion(
            Exchange exchange, ZwangineSagaCoordinator coordinator, ZwangineSagaCoordinator previousCoordinator,
            AsyncCallback callback) {
        if (this.completionMode == SagaCompletionMode.AUTO) {
            if (exchange.getException() != null) {
                if (coordinator != null) {
                    coordinator.compensate(exchange).whenComplete((done, ex) -> ifNotException(ex, exchange, callback, () -> {
                        setCurrentSagaCoordinator(exchange, previousCoordinator);
                        callback.done(false);
                    }));
                } else {
                    // No coordinator available, so no saga available.
                    callback.done(false);
                }
            } else {
                coordinator.complete(exchange).whenComplete((done, ex) -> ifNotException(ex, exchange, callback, () -> {
                    setCurrentSagaCoordinator(exchange, previousCoordinator);
                    callback.done(false);
                }));
            }
        } else if (this.completionMode == SagaCompletionMode.MANUAL) {
            // Completion will be handled manually by the user
            callback.done(false);
        } else {
            throw new IllegalStateException("Unsupported completion mode: " + this.completionMode);
        }
    }

    public ZwangineSagaService getSagaService() {
        return sagaService;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getWorkflowId() {
        return workflowId;
    }

    @Override
    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    @Override
    public String toString() {
        return "id";
    }

    @Override
    public String getTraceLabel() {
        return "saga";
    }

    protected void ifNotException(Throwable ex, Exchange exchange, AsyncCallback callback, Runnable code) {
        ifNotException(ex, exchange, false, null, null, callback, code);
    }

    protected void ifNotException(
            Throwable ex, Exchange exchange, boolean handleCompletion, ZwangineSagaCoordinator coordinator,
            ZwangineSagaCoordinator previousCoordinator, AsyncCallback callback, Runnable code) {
        if (ex != null) {
            exchange.setException(ex);
            if (handleCompletion) {
                handleSagaCompletion(exchange, coordinator, previousCoordinator, callback);
            } else {
                callback.done(false);
            }
        } else {
            code.run();
        }
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        ServiceHelper.startService(sagaService);
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        ServiceHelper.stopService(sagaService);
    }
}
