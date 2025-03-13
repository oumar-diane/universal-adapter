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

import org.zenithblox.Workflow;
import org.zenithblox.util.backoff.BackOffTimer;

import java.util.Collection;
import java.util.Set;

/**
 * A supervising capable {@link WorkflowController} that delays the startup of the workflows after the zwangine context startup
 * and takes control of starting the workflows in a safe manner. This controller is able to retry starting failing workflows,
 * and have various options to configure settings for backoff between restarting workflows.
 */
public interface SupervisingWorkflowController extends WorkflowController {

    String getIncludeWorkflows();

    /**
     * Pattern for filtering workflows to be included as supervised.
     *
     * The pattern is matching on workflow id, and endpoint uri for the workflow. Multiple patterns can be separated by comma.
     *
     * For example to include all kafka workflows, you can say <tt>kafka:*</tt>. And to include workflows with specific workflow
     * ids <tt>myWorkflow,myOtherWorkflow</tt>. The pattern supports wildcards and uses the matcher from
     * org.zenithblox.support.PatternHelper#matchPattern.
     */
    void setIncludeWorkflows(String includeWorkflows);

    String getExcludeWorkflows();

    /**
     * Pattern for filtering workflows to be excluded as supervised.
     *
     * The pattern is matching on workflow id, and endpoint uri for the workflow. Multiple patterns can be separated by comma.
     *
     * For example to exclude all JMS workflows, you can say <tt>jms:*</tt>. And to exclude workflows with specific workflow ids
     * <tt>mySpecialWorkflow,myOtherSpecialWorkflow</tt>. The pattern supports wildcards and uses the matcher from
     * org.zenithblox.support.PatternHelper#matchPattern.
     */
    void setExcludeWorkflows(String excludeWorkflows);

    int getThreadPoolSize();

    /**
     * The number of threads used by the scheduled thread pool that are used for restarting workflows. The pool uses 1
     * thread by default, but you can increase this to allow the controller to concurrently attempt to restart multiple
     * workflows in case more than one workflow has problems starting.
     */
    void setThreadPoolSize(int threadPoolSize);

    long getInitialDelay();

    /**
     * Initial delay in milli seconds before the workflow controller starts, after ZwangineContext has been started.
     */
    void setInitialDelay(long initialDelay);

    long getBackOffDelay();

    /**
     * Backoff delay in millis when restarting a workflow that failed to startup.
     */
    void setBackOffDelay(long backOffDelay);

    long getBackOffMaxDelay();

    /**
     * Backoff maximum delay in millis when restarting a workflow that failed to startup.
     */
    void setBackOffMaxDelay(long backOffMaxDelay);

    long getBackOffMaxElapsedTime();

    /**
     * Backoff maximum elapsed time in millis, after which the backoff should be considered exhausted and no more
     * attempts should be made.
     */
    void setBackOffMaxElapsedTime(long backOffMaxElapsedTime);

    long getBackOffMaxAttempts();

    /**
     * Backoff maximum number of attempts to restart a workflow that failed to startup. When this threshold has been
     * exceeded then the controller will give up attempting to restart the workflow, and the workflow will remain as stopped.
     */
    void setBackOffMaxAttempts(long backOffMaxAttempts);

    double getBackOffMultiplier();

    /**
     * Backoff multiplier to use for exponential backoff. This is used to extend the delay between restart attempts.
     */
    void setBackOffMultiplier(double backOffMultiplier);

    boolean isUnhealthyOnExhausted();

    /**
     * Whether to mark the workflow as unhealthy (down) when all restarting attempts (backoff) have failed and the workflow is
     * not successfully started and the workflow manager is giving up.
     *
     * If setting this to false will make health checks ignore this problem and allow to report the Zwangine application as
     * UP.
     */
    void setUnhealthyOnExhausted(boolean unhealthyOnExhausted);

    boolean isUnhealthyOnRestarting();

    /**
     * Whether to mark the workflow as unhealthy (down) when the workflow failed to initially start, and is being controlled
     * for restarting (backoff).
     *
     * If setting this to false will make health checks ignore this problem and allow to report the Zwangine application as
     * UP.
     */
    void setUnhealthyOnRestarting(boolean unhealthyOnRestarting);

    /**
     * Return the list of workflows that are currently under restarting by this controller.
     *
     * In other words the workflows which has failed during startup and are know managed to be restarted.
     */
    Collection<Workflow> getRestartingWorkflows();

    /**
     * Return the list of workflows that have failed all attempts to startup and are now exhausted.
     */
    Collection<Workflow> getExhaustedWorkflows();

    /**
     * Returns the workflow ids of workflows which are non controlled (such as workflows that was excluded)
     */
    Set<String> getNonControlledWorkflowIds();

    /**
     * Gets the state of the backoff for the given workflow if its managed and under restarting.
     *
     * @param  workflowId the workflow id
     * @return         the state, or <tt>null</tt> if the workflow is not under restarting
     */
    BackOffTimer.Task getRestartingWorkflowState(String workflowId);

    /**
     * Gets the last exception that caused the workflow to not startup for the given workflow
     *
     * @param  workflowId the workflow id
     * @return         the caused exception
     */
    Throwable getRestartException(String workflowId);

    /**
     * Whether the workflow controller is currently starting workflows for the first time. This only reports on the first time
     * start phase.
     */
    boolean isStartingWorkflows();

    /**
     * Started workflows
     */
    default void startWorkflows() {
        startWorkflows(false);
    }

    /**
     * Started workflows
     *
     * @param reloaded whether the workflows to be started is part of reloading workflows
     */
    void startWorkflows(boolean reloaded);

}
