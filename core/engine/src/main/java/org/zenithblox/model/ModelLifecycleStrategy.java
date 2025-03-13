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
package org.zenithblox.model;

/**
 * Strategy for model definitions notifications.
 *
 * A custom strategy must be added to {@link ModelZwangineContext} before any workflows or workflow templates are added. In other
 * words add your custom strategy as early as possible.
 */
public interface ModelLifecycleStrategy {

    /**
     * Notification when a workflow definition is being added to {@link org.zenithblox.ZwangineContext}
     *
     * @param workflowDefinition the workflow definition
     */
    void onAddWorkflowDefinition(WorkflowDefinition workflowDefinition);

    /**
     * Notification when a workflow definition is being removed from {@link org.zenithblox.ZwangineContext}
     *
     * @param workflowDefinition the workflow definition
     */
    void onRemoveWorkflowDefinition(WorkflowDefinition workflowDefinition) throws Exception;

    /**
     * Notification when a workflow template definition is added to {@link org.zenithblox.ZwangineContext}
     *
     * @param workflowTemplateDefinition the workflow template definition
     */
    void onAddWorkflowTemplateDefinition(WorkflowTemplateDefinition workflowTemplateDefinition);

    /**
     * Notification when a workflow template definition is removed from {@link org.zenithblox.ZwangineContext}
     *
     * @param workflowTemplateDefinition the workflow template definition
     */
    void onRemoveWorkflowTemplateDefinition(WorkflowTemplateDefinition workflowTemplateDefinition);

}
