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
import org.zenithblox.ErrorHandlerFactory;
import org.zenithblox.spi.Metadata;
import org.zenithblox.spi.Resource;
import org.zenithblox.spi.ResourceAware;

import java.util.ArrayList;
import java.util.List;

/**
 * A series of workflow templates
 */
@Metadata(label = "configuration")
public class WorkflowTemplatesDefinition extends OptionalIdentifiedDefinition<WorkflowTemplatesDefinition>
        implements WorkflowTemplateContainer, ZwangineContextAware, ResourceAware {

    private ZwangineContext zwangineContext;
    private ErrorHandlerFactory errorHandlerFactory;
    private Resource resource;

    private List<WorkflowTemplateDefinition> workflowTemplates = new ArrayList<>();

    public WorkflowTemplatesDefinition() {
    }

    @Override
    public String toString() {
        return "WorkflowTemplates: " + workflowTemplates;
    }

    @Override
    public String getShortName() {
        return "workflowTemplates";
    }

    @Override
    public String getLabel() {
        return "WorkflowTemplate " + getId();
    }

    // Properties
    // -----------------------------------------------------------------------

    @Override
    public List<WorkflowTemplateDefinition> getWorkflowTemplates() {
        return workflowTemplates;
    }

    /**
     * The workflow templates
     */
    public void setWorkflowTemplates(List<WorkflowTemplateDefinition> workflowTemplates) {
        this.workflowTemplates = workflowTemplates;
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    public ErrorHandlerFactory getErrorHandlerFactory() {
        return errorHandlerFactory;
    }

    public void setErrorHandlerFactory(ErrorHandlerFactory errorHandlerFactory) {
        this.errorHandlerFactory = errorHandlerFactory;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    // Fluent API
    // -------------------------------------------------------------------------

    /**
     * Creates a workflow template
     *
     * @param id the id of the workflow template
     */
    public WorkflowTemplateDefinition workflowTemplate(String id) {
        WorkflowTemplateDefinition workflowTemplate = createWorkflowTemplate();
        workflowTemplate.id(id);
        return workflowTemplate(workflowTemplate);
    }

    /**
     * Adds the {@link WorkflowTemplatesDefinition}
     */
    public WorkflowTemplateDefinition workflowTemplate(WorkflowTemplateDefinition template) {
        getWorkflowTemplates().add(template);
        return template;
    }

    // Implementation methods
    // -------------------------------------------------------------------------

    protected WorkflowTemplateDefinition createWorkflowTemplate() {
        WorkflowTemplateDefinition template = new WorkflowTemplateDefinition();
        ErrorHandlerFactory handler = getErrorHandlerFactory();
        if (handler != null) {
            template.getWorkflow().setErrorHandlerFactoryIfNull(handler);
        }
        if (resource != null) {
            template.setResource(resource);
        }
        return template;
    }

}
