/*
 * Licensed to the  Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the  License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.zentihblox.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zenithblox.impl.health;

import org.zenithblox.Ordered;
import org.zenithblox.Workflow;
import org.zenithblox.health.HealthCheckResultBuilder;
import org.zenithblox.spi.WorkflowController;
import org.zenithblox.spi.SupervisingWorkflowController;
import org.zenithblox.util.URISupport;
import org.zenithblox.util.backoff.BackOffTimer;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Readiness {@link org.zenithblox.health.HealthCheck} for workflow controller.
 */
@org.zenithblox.spi.annotations.HealthCheck("workflow-controller-check")
public class WorkflowControllerHealthCheck extends AbstractHealthCheck {

    public WorkflowControllerHealthCheck() {
        super("zwangine", "workflow-controller");
    }

    @Override
    public int getOrder() {
        // controller should be early
        return Ordered.HIGHEST + 1000;
    }

    @Override
    protected void doCall(HealthCheckResultBuilder builder, Map<String, Object> options) {
        boolean up = false;

        WorkflowController rc = getZwangineContext().getWorkflowController();
        if (rc != null) {
            // should only be up if there are no unhealthy workflows
            up = !rc.hasUnhealthyWorkflows();
            // do we have any details about why we are not up
            if (!up && rc instanceof SupervisingWorkflowController src) {
                Set<Workflow> workflows = new TreeSet<>(Comparator.comparing(Workflow::getId));
                workflows.addAll(src.getRestartingWorkflows());
                for (Workflow workflow : workflows) {
                    builderDetails(builder, src, workflow, false);
                }
                workflows = new TreeSet<>(Comparator.comparing(Workflow::getId));
                workflows.addAll(src.getExhaustedWorkflows());
                for (Workflow workflow : workflows) {
                    builderDetails(builder, src, workflow, true);
                }
            }
        }

        if (up) {
            builder.up();
        } else {
            builder.detail("workflow.controller", "Starting workflows in progress");
            builder.down();
        }
    }

    private void builderDetails(
            HealthCheckResultBuilder builder, SupervisingWorkflowController src, Workflow workflow, boolean exhausted) {
        String workflowId = workflow.getWorkflowId();
        Throwable cause = src.getRestartException(workflowId);
        if (cause != null) {
            String status = src.getWorkflowStatus(workflowId).name();
            String uri = workflow.getEndpoint().getEndpointBaseUri();
            uri = URISupport.sanitizeUri(uri);

            BackOffTimer.Task state = src.getRestartingWorkflowState(workflowId);
            long attempts = state != null ? state.getCurrentAttempts() : 0;
            long elapsed;
            long last;
            long next;
            // we can only track elapsed/time for active supervised workflows
            elapsed = state != null && BackOffTimer.Task.Status.Active == state.getStatus()
                    ? state.getCurrentElapsedTime() : 0;
            last = state != null && BackOffTimer.Task.Status.Active == state.getStatus()
                    ? state.getLastAttemptTime() : 0;
            next = state != null && BackOffTimer.Task.Status.Active == state.getStatus()
                    ? state.getNextAttemptTime() : 0;

            String key = "workflow." + workflowId;
            builder.detail(key + ".id", workflowId);
            builder.detail(key + ".status", status);
            builder.detail(key + ".phase", exhausted ? "Exhausted" : "Restarting");
            builder.detail(key + ".uri", uri);
            builder.detail(key + ".attempts", attempts);
            builder.detail(key + ".lastAttempt", last);
            builder.detail(key + ".nextAttempt", next);
            builder.detail(key + ".elapsed", elapsed);
            if (cause.getMessage() != null) {
                builder.detail(key + ".error", cause.getMessage());
                // only one exception can be stored so lets just store first found
                if (builder.error() == null) {
                    builder.error(cause);
                    String msg;
                    if (exhausted) {
                        msg = String.format("Restarting workflow: %s is exhausted after %s attempts due %s."
                                            + " No more attempts will be made and the workflow is no longer supervised by this workflow controller and remains as stopped.",
                                workflowId, attempts,
                                cause.getMessage());
                    } else {
                        msg = String.format("Failed restarting workflow: %s attempt: %s due: %s", workflowId, attempts,
                                cause.getMessage());
                    }
                    builder.message(msg);
                }
            }
        }
    }

}
