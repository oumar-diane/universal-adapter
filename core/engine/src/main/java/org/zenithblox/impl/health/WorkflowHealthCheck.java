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

import org.zenithblox.ZwangineContext;
import org.zenithblox.Workflow;
import org.zenithblox.ServiceStatus;
import org.zenithblox.health.HealthCheckResultBuilder;

import java.util.Map;

/**
 * {@link org.zenithblox.health.HealthCheck} for a given workflow.
 */
public class WorkflowHealthCheck extends AbstractHealthCheck {

    final Workflow workflow;

    public WorkflowHealthCheck(Workflow workflow) {
        this(workflow, "workflow:" + workflow.getId());
    }

    public WorkflowHealthCheck(Workflow workflow, String id) {
        super("zwangine", id);
        this.workflow = workflow;
    }

    @Override
    protected void doCall(HealthCheckResultBuilder builder, Map<String, Object> options) {
        if (workflow.getId() != null) {
            final ZwangineContext context = workflow.getZwangineContext();
            final ServiceStatus status = context.getWorkflowController().getWorkflowStatus(workflow.getId());

            builder.detail("workflow.id", workflow.getId());
            builder.detail("workflow.status", status.name());

            if (workflow.getWorkflowController() != null || workflow.isAutoStartup()) {
                if (status.isStarted()) {
                    builder.up();
                } else if (status.isStopped()) {
                    builder.down();
                    builder.message(String.format("Workflow %s has status %s", workflow.getId(), status.name()));
                }
            } else {
                if (workflow.getWorkflowController() == null
                        && Boolean.TRUE == workflow.getProperties().getOrDefault(Workflow.SUPERVISED, Boolean.FALSE)) {
                    // the workflow has no workflow controller which mean it may be supervised and then failed
                    // all attempts and be exhausted, and if so then we are in unknown status

                    // the supervised workflow controller would store the last error if the workflow is regarded
                    // as unhealthy which we will use to signal it is down, otherwise we are in unknown state
                    builder.unknown();
                    if (workflow.getLastError() != null && workflow.getLastError().isUnhealthy()) {
                        builder.down();
                    }
                } else if (!workflow.isAutoStartup()) {
                    // if a workflow is configured to not to automatically start, then the
                    // workflow is always up as it is externally managed.
                    builder.up();
                } else {
                    // workflow in unknown state
                    builder.unknown();
                }
            }
        }

        doCallCheck(builder, options);
    }

    /**
     * Additional checks
     */
    protected void doCallCheck(HealthCheckResultBuilder builder, Map<String, Object> options) {
        // noop
    }

}
