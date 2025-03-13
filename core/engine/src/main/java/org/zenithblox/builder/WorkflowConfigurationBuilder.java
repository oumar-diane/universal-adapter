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
package org.zenithblox.builder;

import org.zenithblox.ZwangineContext;
import org.zenithblox.WorkflowConfigurationsBuilder;
import org.zenithblox.model.Model;
import org.zenithblox.model.WorkflowConfigurationDefinition;
import org.zenithblox.model.WorkflowConfigurationsDefinition;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A <a href="http://zwangine.zentihblox.org/dsl.html">Java DSL</a> which is used for building workflow configuration(s).
 */
public abstract class WorkflowConfigurationBuilder extends WorkflowBuilder implements WorkflowConfigurationsBuilder {

    private final AtomicBoolean initializedConfiguration = new AtomicBoolean();
    private WorkflowConfigurationsDefinition workflowConfigurationCollection = new WorkflowConfigurationsDefinition();

    @Override
    public void configure() throws Exception {
        // noop
    }

    @Override
    public abstract void configuration() throws Exception;

    public WorkflowConfigurationsDefinition getWorkflowConfigurationCollection() {
        return workflowConfigurationCollection;
    }

    public void setWorkflowConfigurationCollection(WorkflowConfigurationsDefinition workflowConfigurationCollection) {
        this.workflowConfigurationCollection = workflowConfigurationCollection;
    }

    /**
     * Creates a new workflow configuration
     *
     * @return the builder
     */
    public WorkflowConfigurationDefinition workflowConfiguration() {
        return workflowConfiguration(null);
    }

    /**
     * Creates a new workflow configuration
     *
     * @return the builder
     */
    public WorkflowConfigurationDefinition workflowConfiguration(String id) {
        getWorkflowConfigurationCollection().setZwangineContext(getZwangineContext());
        WorkflowConfigurationDefinition answer = getWorkflowConfigurationCollection().workflowConfiguration(id);
        configureWorkflowConfiguration(answer);
        return answer;
    }

    @Override
    public void addWorkflowConfigurationsToZwangineContext(ZwangineContext context) throws Exception {
        setZwangineContext(context);
        workflowConfigurationCollection.setZwangineContext(context);
        if (initializedConfiguration.compareAndSet(false, true)) {
            configuration();
        }
        populateWorkflowsConfiguration();
    }

    @Override
    public void updateWorkflowConfigurationsToZwangineContext(ZwangineContext context) throws Exception {
        setZwangineContext(context);
        workflowConfigurationCollection.setZwangineContext(context);
        if (initializedConfiguration.compareAndSet(false, true)) {
            configuration();
        }
        List<WorkflowConfigurationDefinition> list = getWorkflowConfigurationCollection().getWorkflowConfigurations();
        if (!list.isEmpty()) {
            // remove existing before updating
            for (WorkflowConfigurationDefinition def : list) {
                context.getZwangineContextExtension().getContextPlugin(Model.class).removeWorkflowConfiguration(def);
            }
            populateWorkflowsConfiguration();
        }
    }

    @Override
    protected void initializeZwangineContext(ZwangineContext zwangineContext) {
        super.initializeZwangineContext(zwangineContext);
        getWorkflowConfigurationCollection().setZwangineContext(zwangineContext);
    }

    protected void populateWorkflowsConfiguration() throws Exception {
        ZwangineContext zwangineContext = getContext();
        if (zwangineContext == null) {
            throw new IllegalArgumentException("ZwangineContext has not been injected!");
        }
        getWorkflowConfigurationCollection().setZwangineContext(zwangineContext);
        if (getResource() != null) {
            getWorkflowConfigurationCollection().setResource(getResource());
        }
        zwangineContext.getZwangineContextExtension().getContextPlugin(Model.class)
                .addWorkflowConfigurations(getWorkflowConfigurationCollection().getWorkflowConfigurations());
    }

}
