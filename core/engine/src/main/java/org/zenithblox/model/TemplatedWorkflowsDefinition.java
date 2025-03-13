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

import org.zenithblox.ZwangineContext;
import org.zenithblox.ZwangineContextAware;
import org.zenithblox.spi.Metadata;
import org.zenithblox.spi.Resource;
import org.zenithblox.spi.ResourceAware;

import java.util.ArrayList;
import java.util.List;

/**
 * A series of templated workflows
 */
@Metadata(label = "configuration")
public class TemplatedWorkflowsDefinition extends OptionalIdentifiedDefinition<TemplatedWorkflowsDefinition>
        implements TemplatedWorkflowContainer, ZwangineContextAware, ResourceAware {

    private ZwangineContext zwangineContext;
    private Resource resource;

    private List<TemplatedWorkflowDefinition> templatedWorkflows = new ArrayList<>();

    public TemplatedWorkflowsDefinition() {
    }

    @Override
    public Resource getResource() {
        return resource;
    }

    @Override
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    @Override
    public String toString() {
        return "TemplatedWorkflows: " + templatedWorkflows;
    }

    @Override
    public String getShortName() {
        return "templatedWorkflows";
    }

    @Override
    public String getLabel() {
        return "TemplatedWorkflows " + getId();
    }

    // Properties
    // -----------------------------------------------------------------------

    @Override
    public List<TemplatedWorkflowDefinition> getTemplatedWorkflows() {
        return templatedWorkflows;
    }

    /**
     * The templated workflows
     */
    @Override
    public void setTemplatedWorkflows(List<TemplatedWorkflowDefinition> templatedWorkflows) {
        this.templatedWorkflows = templatedWorkflows;
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    // Fluent API
    // -------------------------------------------------------------------------

    /**
     * Creates a templated workflow
     *
     * @param workflowTemplateId the id of the workflow template
     */
    public TemplatedWorkflowDefinition templatedWorkflow(String workflowTemplateId) {
        TemplatedWorkflowDefinition template = createTemplatedWorkflowDefinition(workflowTemplateId);
        getTemplatedWorkflows().add(template);
        return template;
    }

    /**
     * Adds the {@link TemplatedWorkflowDefinition}
     */
    public TemplatedWorkflowDefinition templatedWorkflow(TemplatedWorkflowDefinition template) {
        getTemplatedWorkflows().add(template);
        return template;
    }

    // Implementation methods
    // -------------------------------------------------------------------------

    protected TemplatedWorkflowDefinition createTemplatedWorkflowDefinition(String id) {
        TemplatedWorkflowDefinition templatedWorkflow = new TemplatedWorkflowDefinition();
        if (id != null) {
            templatedWorkflow.setWorkflowTemplateRef(id);
        }
        if (resource != null) {
            templatedWorkflow.setResource(resource);
        }
        ZwangineContextAware.trySetZwangineContext(templatedWorkflow, zwangineContext);
        return templatedWorkflow;
    }

}
