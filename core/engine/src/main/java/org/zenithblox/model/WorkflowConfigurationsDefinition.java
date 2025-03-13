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
 * A series of workflow configurations
 */
@Metadata(label = "configuration")
public class WorkflowConfigurationsDefinition extends OptionalIdentifiedDefinition<WorkflowConfigurationsDefinition>
        implements WorkflowConfigurationContainer, ResourceAware {

    private ZwangineContext zwangineContext;
    private Resource resource;

    private List<WorkflowConfigurationDefinition> workflowConfigurations = new ArrayList<>();

    public WorkflowConfigurationsDefinition() {
    }

    @Override
    public String getShortName() {
        return "workflowConfigurations";
    }

    @Override
    public String getLabel() {
        return "WorkflowConfigurations " + getId();
    }

    @Override
    public String toString() {
        return "WorkflowConfigurations";
    }

    public List<WorkflowConfigurationDefinition> getWorkflowConfigurations() {
        return workflowConfigurations;
    }

    public void setWorkflowConfigurations(List<WorkflowConfigurationDefinition> workflowConfigurations) {
        this.workflowConfigurations = workflowConfigurations;
    }

    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    @Override
    public Resource getResource() {
        return resource;
    }

    @Override
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    // Fluent API
    // -------------------------------------------------------------------------

    /**
     * Creates a new workflow configuration
     *
     * @return the builder
     */
    public WorkflowConfigurationDefinition workflowConfiguration() {
        WorkflowConfigurationDefinition config = createWorkflowConfiguration(null);
        getWorkflowConfigurations().add(config);
        return config;
    }

    /**
     * Creates a new workflow configuration
     *
     * @return the builder
     */
    public WorkflowConfigurationDefinition workflowConfiguration(String id) {
        WorkflowConfigurationDefinition config = createWorkflowConfiguration(id);
        getWorkflowConfigurations().add(config);
        return config;
    }

    /**
     * Adds the workflow configuration
     *
     * @return the builder
     */
    public WorkflowConfigurationDefinition workflowConfiguration(WorkflowConfigurationDefinition config) {
        getWorkflowConfigurations().add(config);
        return config;
    }

    // Implementation methods
    // -------------------------------------------------------------------------

    protected WorkflowConfigurationDefinition createWorkflowConfiguration(String id) {
        WorkflowConfigurationDefinition config = new WorkflowConfigurationDefinition();
        if (id != null) {
            config.setId(id);
        }
        if (resource != null) {
            config.setResource(resource);
        }
        ZwangineContextAware.trySetZwangineContext(config, zwangineContext);
        return config;
    }

}
