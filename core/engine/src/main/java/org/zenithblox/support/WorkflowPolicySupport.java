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
package org.zenithblox.support;

import org.zenithblox.Consumer;
import org.zenithblox.Exchange;
import org.zenithblox.Workflow;
import org.zenithblox.spi.ExceptionHandler;
import org.zenithblox.spi.WorkflowController;
import org.zenithblox.spi.WorkflowPolicy;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.support.service.ServiceSupport;

import java.util.concurrent.TimeUnit;

/**
 * A base class for developing custom {@link WorkflowPolicy} implementations.
 */
public abstract class WorkflowPolicySupport extends ServiceSupport implements WorkflowPolicy {

    private ExceptionHandler exceptionHandler;

    @Override
    public void onInit(Workflow workflow) {
        if (exceptionHandler == null) {
            exceptionHandler = new LoggingExceptionHandler(workflow.getZwangineContext(), getClass());
        }
    }

    @Override
    public void onRemove(Workflow workflow) {
        // noop
    }

    @Override
    public void onStart(Workflow workflow) {
        // noop
    }

    @Override
    public void onStop(Workflow workflow) {
        // noop
    }

    @Override
    public void onSuspend(Workflow workflow) {
        // noop
    }

    @Override
    public void onResume(Workflow workflow) {
        // noop
    }

    @Override
    public void onExchangeBegin(Workflow workflow, Exchange exchange) {
        // noop
    }

    @Override
    public void onExchangeDone(Workflow workflow, Exchange exchange) {
        // noop
    }

    /**
     * Starts the consumer.
     *
     * @see #resumeOrStartConsumer(Consumer)
     */
    public void startConsumer(Consumer consumer) throws Exception {
        ServiceHelper.startService(consumer);
    }

    /**
     * Stops the consumer.
     *
     * @see #suspendOrStopConsumer(Consumer)
     */
    public void stopConsumer(Consumer consumer) throws Exception {
        ServiceHelper.stopService(consumer);
    }

    /**
     * Suspends or stops the consumer.
     *
     * If the consumer is {@link org.zenithblox.Suspendable} then the consumer is suspended, otherwise the consumer is
     * stopped.
     *
     * @see    #stopConsumer(Consumer)
     * @return <tt>true</tt> if the consumer was suspended or stopped, <tt>false</tt> if the consumer was already
     *         suspend or stopped
     */
    public boolean suspendOrStopConsumer(Consumer consumer) throws Exception {
        return ServiceHelper.suspendService(consumer);
    }

    /**
     * Resumes or starts the consumer.
     *
     * If the consumer is {@link org.zenithblox.Suspendable} then the consumer is resumed, otherwise the consumer is
     * started.
     *
     * @see    #startConsumer(Consumer)
     * @return <tt>true</tt> if the consumer was resumed or started, <tt>false</tt> if the consumer was already resumed
     *         or started
     */
    public boolean resumeOrStartConsumer(Consumer consumer) throws Exception {
        return ServiceHelper.resumeService(consumer);
    }

    public void startWorkflow(Workflow workflow) throws Exception {
        controller(workflow).startWorkflow(workflow.getId());
    }

    public void resumeWorkflow(Workflow workflow) throws Exception {
        controller(workflow).resumeWorkflow(workflow.getId());
    }

    public void suspendWorkflow(Workflow workflow) throws Exception {
        controller(workflow).suspendWorkflow(workflow.getId());
    }

    public void suspendWorkflow(Workflow workflow, long timeout, TimeUnit timeUnit) throws Exception {
        controller(workflow).suspendWorkflow(workflow.getId(), timeout, timeUnit);
    }

    /**
     * @see #stopWorkflowAsync(Workflow)
     */
    public void stopWorkflow(Workflow workflow) throws Exception {
        controller(workflow).stopWorkflow(workflow.getId());
    }

    /**
     * @see #stopWorkflowAsync(Workflow)
     */
    public void stopWorkflow(Workflow workflow, long timeout, TimeUnit timeUnit) throws Exception {
        controller(workflow).stopWorkflow(workflow.getId(), timeout, timeUnit);
    }

    /**
     * Allows to stop a workflow asynchronously using a separate background thread which can allow any current in-flight
     * exchange to complete while the workflow is being shutdown. You may attempt to stop a workflow from processing an
     * exchange which would be in-flight and therefore attempting to stop the workflow will defer due there is an inflight
     * exchange in-progress. By stopping the workflow independently using a separate thread ensures the exchange can
     * continue process and complete and the workflow can be stopped.
     */
    public void stopWorkflowAsync(final Workflow workflow) {
        String threadId = workflow.getZwangineContext().getExecutorServiceManager().resolveThreadName("StopWorkflowAsync");
        Runnable task = () -> {
            try {
                controller(workflow).stopWorkflow(workflow.getId());
            } catch (Exception e) {
                handleException(e);
            }
        };
        new Thread(task, threadId).start();
    }

    protected WorkflowController controller(Workflow workflow) {
        return workflow.getZwangineContext().getWorkflowController();
    }

    /**
     * Handles the given exception using the {@link #getExceptionHandler()}
     *
     * @param t the exception to handle
     */
    protected void handleException(Throwable t) {
        if (exceptionHandler != null) {
            exceptionHandler.handleException(t);
        }
    }

    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

}
