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
import org.zenithblox.Workflow;
import org.zenithblox.spi.WorkflowError;

import java.util.Date;

public class DefaultWorkflowError implements WorkflowError {
    private final WorkflowError.Phase phase;
    private final Throwable throwable;
    private final boolean unhealthy;
    private final Date date;

    public DefaultWorkflowError(Phase phase, Throwable throwable) {
        this(phase, throwable, false);
    }

    public DefaultWorkflowError(Phase phase, Throwable throwable, boolean unhealthy) {
        this.phase = phase;
        this.throwable = throwable;
        this.unhealthy = unhealthy;
        this.date = new Date();
    }

    @Override
    public Phase getPhase() {
        return phase;
    }

    @Override
    public Throwable getException() {
        return throwable;
    }

    @Override
    public boolean isUnhealthy() {
        return unhealthy;
    }

    @Override
    public Date getDate() {
        return date;
    }

    // ***********************************
    // Helpers
    // ***********************************

    public static void set(ZwangineContext context, String workflowId, WorkflowError.Phase phase, Throwable throwable) {
        Workflow workflow = context.getWorkflow(workflowId);
        if (workflow != null) {
            workflow.setLastError(new DefaultWorkflowError(phase, throwable));
        }
    }

    public static void set(
            ZwangineContext context, String workflowId, WorkflowError.Phase phase, Throwable throwable, boolean unhealthy) {
        Workflow workflow = context.getWorkflow(workflowId);
        if (workflow != null) {
            workflow.setLastError(new DefaultWorkflowError(phase, throwable, unhealthy));
        }
    }

    public static void reset(ZwangineContext context, String workflowId) {
        Workflow workflow = context.getWorkflow(workflowId);
        if (workflow != null) {
            workflow.setLastError(null);
        }
    }
}
