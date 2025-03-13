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

import org.zenithblox.*;
import org.zenithblox.spi.WorkflowController;
import org.zenithblox.spi.SupervisingWorkflowController;
import org.zenithblox.support.service.ServiceSupport;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * A default {@link WorkflowController} that starts the workflows in a fail-fast mode, which means if any of the workflows fail
 * to startup then this causes Zwangine to fail to startup as well.
 *
 * @see DefaultSupervisingWorkflowController
 */
public class DefaultWorkflowController extends ServiceSupport implements WorkflowController, NonManagedService {

    // mark this as non managed service as its registered specially as a workflow controller

    private ZwangineContext zwangineContext;

    private LoggingLevel loggingLevel = LoggingLevel.DEBUG;

    public DefaultWorkflowController() {
        this(null);
    }

    public DefaultWorkflowController(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    // ***************************************************
    // Properties
    // ***************************************************

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public LoggingLevel getLoggingLevel() {
        return loggingLevel;
    }

    @Override
    public void setLoggingLevel(LoggingLevel loggingLevel) {
        this.loggingLevel = loggingLevel;
    }

    @Override
    public boolean isSupervising() {
        return this instanceof SupervisingWorkflowController;
    }

    // ***************************************************
    // Workflow management
    // ***************************************************

    protected WorkflowController getInternalWorkflowController() {
        return zwangineContext.getZwangineContextExtension().getInternalWorkflowController();
    }

    @Override
    public void startAllWorkflows() throws Exception {
        getInternalWorkflowController().startAllWorkflows();
    }

    @Override
    public void stopAllWorkflows() throws Exception {
        getInternalWorkflowController().stopAllWorkflows();
    }

    @Override
    public void removeAllWorkflows() throws Exception {
        getInternalWorkflowController().removeAllWorkflows();
    }

    @Override
    public boolean isStartingWorkflows() {
        return getInternalWorkflowController().isStartingWorkflows();
    }

    @Override
    public boolean hasUnhealthyWorkflows() {
        return getInternalWorkflowController().hasUnhealthyWorkflows();
    }

    @Override
    public void reloadAllWorkflows() throws Exception {
        getInternalWorkflowController().reloadAllWorkflows();
    }

    @Override
    public boolean isReloadingWorkflows() {
        return getInternalWorkflowController().isReloadingWorkflows();
    }

    @Override
    public ServiceStatus getWorkflowStatus(String workflowId) {
        return getInternalWorkflowController().getWorkflowStatus(workflowId);
    }

    @Override
    public void startWorkflow(String workflowId) throws Exception {
        getInternalWorkflowController().startWorkflow(workflowId);
    }

    @Override
    public void stopWorkflow(String workflowId) throws Exception {
        getInternalWorkflowController().stopWorkflow(workflowId);
    }

    @Override
    public void stopWorkflow(String workflowId, Throwable cause) throws Exception {
        getInternalWorkflowController().stopWorkflow(workflowId, cause);
    }

    @Override
    public void stopWorkflow(String workflowId, long timeout, TimeUnit timeUnit) throws Exception {
        getInternalWorkflowController().stopWorkflow(workflowId, timeout, timeUnit);
    }

    @Override
    public boolean stopWorkflow(String workflowId, long timeout, TimeUnit timeUnit, boolean abortAfterTimeout) throws Exception {
        return getInternalWorkflowController().stopWorkflow(workflowId, timeout, timeUnit, abortAfterTimeout);
    }

    @Override
    public void suspendWorkflow(String workflowId) throws Exception {
        getInternalWorkflowController().suspendWorkflow(workflowId);
    }

    @Override
    public void suspendWorkflow(String workflowId, long timeout, TimeUnit timeUnit) throws Exception {
        getInternalWorkflowController().suspendWorkflow(workflowId, timeout, timeUnit);
    }

    @Override
    public void resumeWorkflow(String workflowId) throws Exception {
        getInternalWorkflowController().resumeWorkflow(workflowId);
    }

    // ***************************************************
    //
    // ***************************************************

    @Override
    public <T extends WorkflowController> T adapt(Class<T> type) {
        return type.cast(this);
    }

    @Override
    public SupervisingWorkflowController supervising() {
        if (this instanceof SupervisingWorkflowController src) {
            return src;
        } else {
            // change current workflow controller to be supervising
            SupervisingWorkflowController src = new DefaultSupervisingWorkflowController();
            src.setZwangineContext(zwangineContext);
            zwangineContext.setWorkflowController(src);
            return src;
        }
    }

    @Override
    public Collection<Workflow> getControlledWorkflows() {
        return Collections.emptyList();
    }
}
