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
package org.zenithblox;

import org.zenithblox.util.URISupport;

/**
 * Exception when failing to create a {@link org.zenithblox.Workflow}.
 */
public class FailedToCreateWorkflowException extends RuntimeZwangineException {

    private final String workflowId;

    public FailedToCreateWorkflowException(String workflowId, String workflow, Throwable cause) {
        super("Failed to create workflow " + workflowId + ": " + getWorkflowMessage(workflow) + " because of " + getExceptionMessage(cause),
              cause);
        this.workflowId = workflowId;
    }

    public FailedToCreateWorkflowException(String workflowId, String workflow, String at, Throwable cause) {
        super("Failed to create workflow " + workflowId + " at: >>> " + at + " <<< in workflow: " + getWorkflowMessage(workflow)
              + " because of " + getExceptionMessage(cause), cause);
        this.workflowId = workflowId;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    protected static String getExceptionMessage(Throwable cause) {
        return cause.getMessage() != null ? cause.getMessage() : cause.getClass().getSimpleName();
    }

    protected static String getWorkflowMessage(String workflow) {
        // cut the workflow after 60 chars, so it won't be too big in the message
        // users just need to be able to identify the workflow, so they know where to look
        if (workflow.length() > 60) {
            workflow = workflow.substring(0, 60) + "...";
        }

        // ensure to sanitize uri's in the workflow, so we do not show sensitive information such as passwords
        workflow = URISupport.sanitizeUri(workflow);
        return workflow;
    }

}
