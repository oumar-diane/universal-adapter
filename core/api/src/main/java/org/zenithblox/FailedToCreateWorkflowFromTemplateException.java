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

/**
 * Exception when failing to create a {@link Workflow} from a WorkflowTemplateDefinition.
 */
public class FailedToCreateWorkflowFromTemplateException extends RuntimeZwangineException {
    private final String templateId;
    private final String workflowId;

    public FailedToCreateWorkflowFromTemplateException(String workflowId, String templateId, String message) {
        super("Failed to create workflow " + workflowId + " from template " + templateId + " because of " + message);
        this.workflowId = workflowId;
        this.templateId = templateId;
    }

    public FailedToCreateWorkflowFromTemplateException(String workflowId, String templateId, String message, Throwable cause) {
        super("Failed to create workflow " + workflowId + " from template " + templateId + " because of " + message, cause);
        this.workflowId = workflowId;
        this.templateId = templateId;
    }

    public String getTemplateId() {
        return templateId;
    }

    public String getWorkflowId() {
        return workflowId;
    }
}
