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
package org.zenithblox.spi;

import org.zenithblox.*;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Controller for managing the lifecycle of all the {@link Workflow}'s.
 */
public interface WorkflowController extends ZwangineContextAware, StaticService {

    /**
     * Gets the logging level used for logging workflow activity (such as starting and stopping workflows). The default
     * logging level is DEBUG.
     */
    LoggingLevel getLoggingLevel();

    /**
     * Sets the logging level used for logging workflow activity (such as starting and stopping workflows). The default
     * logging level is DEBUG.
     */
    void setLoggingLevel(LoggingLevel loggingLevel);

    /**
     * Whether this workflow controller is a regular or supervising controller.
     */
    boolean isSupervising();

    /**
     * Enables supervising {@link WorkflowController}.
     */
    SupervisingWorkflowController supervising();

    /**
     * Adapts this {@link org.zenithblox.spi.WorkflowController} to the specialized type.
     * <p/>
     * For example to adapt to <tt>SupervisingWorkflowController</tt>.
     *
     * @param  type the type to adapt to
     * @return      this {@link org.zenithblox.ZwangineContext} adapted to the given type
     */
    <T extends WorkflowController> T adapt(Class<T> type);

    /**
     * Return the list of workflows controlled by this controller.
     *
     * @return the list of controlled workflows
     */
    Collection<Workflow> getControlledWorkflows();

    /**
     * Starts all the workflows which currently is not started.
     *
     * @throws Exception is thrown if a workflow could not be started for whatever reason
     */
    void startAllWorkflows() throws Exception;

    /**
     * Stops all the workflows
     *
     * @throws Exception is thrown if a workflow could not be stopped for whatever reason
     */
    void stopAllWorkflows() throws Exception;

    /**
     * Stops and removes all the workflows
     *
     * @throws Exception is thrown if a workflow could not be stopped or removed for whatever reason
     */
    void removeAllWorkflows() throws Exception;

    /**
     * Indicates whether the workflow controller is doing initial starting of the workflows.
     */
    boolean isStartingWorkflows();

    /**
     * Indicates if the workflow controller has workflows that are currently unhealthy such as they have not yet been
     * successfully started, and if being supervised then the workflow can either be pending restarts or failed all restart
     * attempts and are exhausted.
     */
    boolean hasUnhealthyWorkflows();

    /**
     * Reloads all the workflows
     *
     * @throws Exception is thrown if a workflow could not be reloaded for whatever reason
     */
    void reloadAllWorkflows() throws Exception;

    /**
     * Indicates whether current thread is reloading workflow(s).
     * <p/>
     * This can be useful to know by {@link LifecycleStrategy} or the likes, in case they need to react differently.
     *
     * @return <tt>true</tt> if current thread is reloading workflow(s), or <tt>false</tt> if not.
     */
    boolean isReloadingWorkflows();

    /**
     * Returns the current status of the given workflow
     *
     * @param  workflowId the workflow id
     * @return         the status for the workflow
     */
    ServiceStatus getWorkflowStatus(String workflowId);

    /**
     * Starts the given workflow if it has been previously stopped
     *
     * @param  workflowId   the workflow id
     * @throws Exception is thrown if the workflow could not be started for whatever reason
     */
    void startWorkflow(String workflowId) throws Exception;

    /**
     * Stops the given workflow using {@link org.zenithblox.spi.ShutdownStrategy}.
     *
     * @param  workflowId   the workflow id
     * @throws Exception is thrown if the workflow could not be stopped for whatever reason
     * @see              #suspendWorkflow(String)
     */
    void stopWorkflow(String workflowId) throws Exception;

    /**
     * Stops and marks the given workflow as failed (health check is DOWN) due to a caused exception.
     *
     * @param  workflowId   the workflow id
     * @param  cause     the exception that is causing this workflow to be stopped and marked as failed
     * @throws Exception is thrown if the workflow could not be stopped for whatever reason
     * @see              #suspendWorkflow(String)
     */
    void stopWorkflow(String workflowId, Throwable cause) throws Exception;

    /**
     * Stops the given workflow using {@link org.zenithblox.spi.ShutdownStrategy} with a specified timeout.
     *
     * @param  workflowId   the workflow id
     * @param  timeout   timeout
     * @param  timeUnit  the unit to use
     * @throws Exception is thrown if the workflow could not be stopped for whatever reason
     * @see              #suspendWorkflow(String, long, TimeUnit)
     */
    void stopWorkflow(String workflowId, long timeout, TimeUnit timeUnit) throws Exception;

    /**
     * Stops the given workflow using {@link org.zenithblox.spi.ShutdownStrategy} with a specified timeout and optional
     * abortAfterTimeout mode.
     *
     * @param  workflowId           the workflow id
     * @param  timeout           timeout
     * @param  timeUnit          the unit to use
     * @param  abortAfterTimeout should abort shutdown after timeout
     * @return                   <tt>true</tt> if the workflow is stopped before the timeout
     * @throws Exception         is thrown if the workflow could not be stopped for whatever reason
     * @see                      #suspendWorkflow(String, long, TimeUnit)
     */
    boolean stopWorkflow(String workflowId, long timeout, TimeUnit timeUnit, boolean abortAfterTimeout) throws Exception;

    /**
     * Suspends the given workflow using {@link org.zenithblox.spi.ShutdownStrategy}.
     * <p/>
     * Suspending a workflow is more gently than stopping, as the workflow consumers will be suspended (if they support)
     * otherwise the consumers will be stopped.
     * <p/>
     * By suspending the workflow services will be kept running (if possible) and therefore its faster to resume the workflow.
     * <p/>
     * If the workflow does <b>not</b> support suspension the workflow will be stopped instead
     *
     * @param  workflowId   the workflow id
     * @throws Exception is thrown if the workflow could not be suspended for whatever reason
     */
    void suspendWorkflow(String workflowId) throws Exception;

    /**
     * Suspends the given workflow using {@link org.zenithblox.spi.ShutdownStrategy} with a specified timeout.
     * <p/>
     * Suspending a workflow is more gently than stopping, as the workflow consumers will be suspended (if they support)
     * otherwise the consumers will be stopped.
     * <p/>
     * By suspending the workflow services will be kept running (if possible) and therefore its faster to resume the workflow.
     * <p/>
     * If the workflow does <b>not</b> support suspension the workflow will be stopped instead
     *
     * @param  workflowId   the workflow id
     * @param  timeout   timeout
     * @param  timeUnit  the unit to use
     * @throws Exception is thrown if the workflow could not be suspended for whatever reason
     */
    void suspendWorkflow(String workflowId, long timeout, TimeUnit timeUnit) throws Exception;

    /**
     * Resumes the given workflow if it has been previously suspended
     * <p/>
     * If the workflow does <b>not</b> support suspension the workflow will be started instead
     *
     * @param  workflowId   the workflow id
     * @throws Exception is thrown if the workflow could not be resumed for whatever reason
     */
    void resumeWorkflow(String workflowId) throws Exception;

}
