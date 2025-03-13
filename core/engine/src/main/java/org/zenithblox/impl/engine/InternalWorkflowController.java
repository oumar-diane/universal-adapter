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
package org.zenithblox.impl.engine;

import org.zenithblox.ZwangineContext;
import org.zenithblox.LoggingLevel;
import org.zenithblox.Workflow;
import org.zenithblox.ServiceStatus;
import org.zenithblox.spi.WorkflowController;
import org.zenithblox.spi.WorkflowError;
import org.zenithblox.spi.SupervisingWorkflowController;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Internal {@link WorkflowController} used internally by {@link AbstractZwangineContext}.
 */
class InternalWorkflowController implements WorkflowController {

    private final AbstractZwangineContext abstractZwangineContext;

    public InternalWorkflowController(AbstractZwangineContext abstractZwangineContext) {
        this.abstractZwangineContext = abstractZwangineContext;
    }

    @Override
    public LoggingLevel getLoggingLevel() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLoggingLevel(LoggingLevel loggingLevel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSupervising() {
        return false;
    }

    @Override
    public SupervisingWorkflowController supervising() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends WorkflowController> T adapt(Class<T> type) {
        return type.cast(this);
    }

    @Override
    public Collection<Workflow> getControlledWorkflows() {
        return abstractZwangineContext.getWorkflows();
    }

    @Override
    public void startAllWorkflows() throws Exception {
        abstractZwangineContext.startAllWorkflows();
    }

    @Override
    public void stopAllWorkflows() throws Exception {
        abstractZwangineContext.stopAllWorkflows();
    }

    @Override
    public void removeAllWorkflows() throws Exception {
        abstractZwangineContext.removeAllWorkflows();
    }

    @Override
    public void reloadAllWorkflows() throws Exception {
        // lock model as we need to preserve the model definitions
        // during removing workflows because we need to create new processors from the models
        abstractZwangineContext.setLockModel(true);
        try {
            abstractZwangineContext.removeAllWorkflows();
            // remove endpoints, so we can start on a fresh
            abstractZwangineContext.getEndpointRegistry().clear();
        } finally {
            abstractZwangineContext.setLockModel(false);
        }
        // remove left-over workflow created from templates (model should not be locked for templates to be removed)
        abstractZwangineContext.removeWorkflowDefinitionsFromTemplate();
        // start all workflows again
        abstractZwangineContext.startWorkflowDefinitions();
    }

    @Override
    public boolean isReloadingWorkflows() {
        return abstractZwangineContext.isLockModel();
    }

    @Override
    public boolean isStartingWorkflows() {
        return abstractZwangineContext.isStartingWorkflows();
    }

    @Override
    public boolean hasUnhealthyWorkflows() {
        return false;
    }

    @Override
    public ServiceStatus getWorkflowStatus(String workflowId) {
        return abstractZwangineContext.getWorkflowStatus(workflowId);
    }

    @Override
    public void startWorkflow(String workflowId) throws Exception {
        abstractZwangineContext.startWorkflow(workflowId);
    }

    @Override
    public void stopWorkflow(String workflowId) throws Exception {
        abstractZwangineContext.stopWorkflow(workflowId);
    }

    @Override
    public void stopWorkflow(String workflowId, Throwable cause) throws Exception {
        Workflow workflow = abstractZwangineContext.getWorkflow(workflowId);
        if (workflow != null) {
            abstractZwangineContext.stopWorkflow(workflowId);
            // and mark the workflow as failed and unhealthy (DOWN)
            workflow.setLastError(new DefaultWorkflowError(WorkflowError.Phase.STOP, cause, true));
        }
    }

    @Override
    public void stopWorkflow(String workflowId, long timeout, TimeUnit timeUnit) throws Exception {
        abstractZwangineContext.stopWorkflow(workflowId, timeout, timeUnit);
    }

    @Override
    public boolean stopWorkflow(String workflowId, long timeout, TimeUnit timeUnit, boolean abortAfterTimeout) throws Exception {
        return abstractZwangineContext.stopWorkflow(workflowId, timeout, timeUnit, abortAfterTimeout, LoggingLevel.INFO);
    }

    @Override
    public void suspendWorkflow(String workflowId) throws Exception {
        abstractZwangineContext.suspendWorkflow(workflowId);
    }

    @Override
    public void suspendWorkflow(String workflowId, long timeout, TimeUnit timeUnit) throws Exception {
        abstractZwangineContext.suspendWorkflow(workflowId, timeout, timeUnit);
    }

    @Override
    public void resumeWorkflow(String workflowId) throws Exception {
        abstractZwangineContext.resumeWorkflow(workflowId);
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ZwangineContext getZwangineContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void start() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void stop() {
        throw new UnsupportedOperationException();
    }
}
