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
import org.zenithblox.WorkflowTemplateContext;
import org.zenithblox.spi.Metadata;
import org.zenithblox.spi.Resource;
import org.zenithblox.spi.ResourceAware;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Defines a templated workflow (a workflow built from a workflow template)
 */
@Metadata(label = "configuration")
public class TemplatedWorkflowDefinition implements ZwangineContextAware, ResourceAware {

    private ZwangineContext zwangineContext;
    private Resource resource;

    private String workflowTemplateRef;
    private String workflowId;
    private String prefixId;
    @Metadata(description = "Adds an input parameter of the template to build the workflow")
    private List<TemplatedWorkflowParameterDefinition> parameters;
    @Metadata(description = "Adds a local bean as input of the template to build the workflow")
    private List<BeanFactoryDefinition<TemplatedWorkflowDefinition>> beans;

    public String getWorkflowTemplateRef() {
        return workflowTemplateRef;
    }

    public void setWorkflowTemplateRef(String workflowTemplateRef) {
        this.workflowTemplateRef = workflowTemplateRef;
    }

    public List<TemplatedWorkflowParameterDefinition> getParameters() {
        return parameters;
    }

    public void setParameters(List<TemplatedWorkflowParameterDefinition> parameters) {
        this.parameters = parameters;
    }

    public List<BeanFactoryDefinition<TemplatedWorkflowDefinition>> getBeans() {
        return beans;
    }

    public void setBeans(List<BeanFactoryDefinition<TemplatedWorkflowDefinition>> beans) {
        this.beans = beans;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getPrefixId() {
        return prefixId;
    }

    public void setPrefixId(String prefixId) {
        this.prefixId = prefixId;
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
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
     * Adds an input parameter to build the workflow from the workflow template.
     *
     * @param name  the name of the parameter
     * @param value the value of the parameter
     */
    public TemplatedWorkflowDefinition parameter(String name, String value) {
        addParameter(name, value);
        return this;
    }

    /**
     * Adds the input parameters to build the workflow from the workflow template.
     *
     * @param parameters the parameters
     */
    public TemplatedWorkflowDefinition parameters(Map<String, String> parameters) {
        parameters.forEach(this::addParameter);
        return this;
    }

    /**
     * Adds a local bean as input of the workflow template.
     *
     * @param name the name of the bean
     * @param type the type of the bean to associate the binding
     */
    public TemplatedWorkflowDefinition bean(String name, Class<?> type) {
        if (beans == null) {
            beans = new ArrayList<>();
        }
        BeanFactoryDefinition<TemplatedWorkflowDefinition> def = new BeanFactoryDefinition<>();
        def.setName(name);
        def.setBeanType(type);
        beans.add(def);
        return this;
    }

    /**
     * Adds a local bean as input of the workflow template.
     *
     * @param name the name of the bean
     * @param bean the bean, or reference to bean (#class or #type), or a supplier for the bean
     */
    @SuppressWarnings("unchecked")
    public TemplatedWorkflowDefinition bean(String name, Object bean) {
        if (beans == null) {
            beans = new ArrayList<>();
        }
        BeanFactoryDefinition<TemplatedWorkflowDefinition> def = new BeanFactoryDefinition<>();
        def.setName(name);
        if (bean instanceof WorkflowTemplateContext.BeanSupplier) {
            def.setBeanSupplier((WorkflowTemplateContext.BeanSupplier<Object>) bean);
        } else if (bean instanceof Supplier) {
            def.setBeanSupplier(ctx -> ((Supplier<?>) bean).get());
        } else if (bean instanceof String str) {
            // it is a string type
            def.setType(str);
        } else {
            def.setBeanSupplier(ctx -> bean);
        }
        beans.add(def);
        return this;
    }

    /**
     * Adds a local bean as input of the workflow template.
     *
     * @param name the name of the bean
     * @param bean the supplier for the bean
     */
    public TemplatedWorkflowDefinition bean(String name, Supplier<Object> bean) {
        if (beans == null) {
            beans = new ArrayList<>();
        }
        BeanFactoryDefinition<TemplatedWorkflowDefinition> def = new BeanFactoryDefinition<>();
        def.setName(name);
        def.setBeanSupplier(ctx -> ((Supplier<?>) bean).get());
        beans.add(def);
        return this;
    }

    /**
     * Adds a local bean as input of the workflow template.
     *
     * @param name the name of the bean
     * @param type the type of the bean to associate the binding
     * @param bean a supplier for the bean
     */
    public TemplatedWorkflowDefinition bean(String name, Class<?> type, WorkflowTemplateContext.BeanSupplier<Object> bean) {
        if (beans == null) {
            beans = new ArrayList<>();
        }
        BeanFactoryDefinition<TemplatedWorkflowDefinition> def = new BeanFactoryDefinition<>();
        def.setName(name);
        def.setBeanType(type);
        def.setBeanSupplier(bean);
        beans.add(def);
        return this;
    }

    /**
     * Adds a local bean as input of the workflow template.
     *
     * @param name     the name of the bean
     * @param language the language to use
     * @param script   the script to use for creating the local bean
     */
    public TemplatedWorkflowDefinition bean(String name, String language, String script) {
        if (beans == null) {
            beans = new ArrayList<>();
        }
        BeanFactoryDefinition<TemplatedWorkflowDefinition> def = new BeanFactoryDefinition<>();
        def.setName(name);
        def.setType(language);
        def.setScript(script);
        beans.add(def);
        return this;
    }

    /**
     * Adds a local bean as input of the workflow template.
     *
     * @param name     the name of the bean
     * @param type     the type of the bean to associate the binding
     * @param language the language to use
     * @param script   the script to use for creating the local bean
     */
    public TemplatedWorkflowDefinition bean(String name, Class<?> type, String language, String script) {
        if (beans == null) {
            beans = new ArrayList<>();
        }
        BeanFactoryDefinition<TemplatedWorkflowDefinition> def = new BeanFactoryDefinition<>();
        def.setName(name);
        def.setBeanType(type);
        def.setType(language);
        def.setScript(script);
        beans.add(def);
        return this;
    }

    /**
     * Adds a local bean as input of the workflow template. (via fluent builder)
     *
     * @param  name the name of the bean
     * @return      fluent builder to choose which language and script to use for creating the bean
     */
    public BeanFactoryDefinition<TemplatedWorkflowDefinition> bean(String name) {
        if (beans == null) {
            beans = new ArrayList<>();
        }
        BeanFactoryDefinition<TemplatedWorkflowDefinition> def = new BeanFactoryDefinition<>();
        def.setParent(this);
        def.setName(name);
        beans.add(def);
        return def;
    }

    /**
     * Sets a prefix to use for all node ids (not workflow id).
     *
     * @param id the prefix id
     */
    public TemplatedWorkflowDefinition prefixId(String id) {
        setPrefixId(id);
        return this;
    }

    /**
     * Sets the id of the workflow built from the workflow template.
     *
     * @param id the id the generated workflow
     */
    public TemplatedWorkflowDefinition workflowId(String id) {
        setWorkflowId(id);
        return this;
    }

    /**
     * Sets the id of the workflow template to use to build the workflow.
     *
     * @param ref the id of the workflow template
     */
    public TemplatedWorkflowDefinition workflowTemplateRef(String ref) {
        setWorkflowTemplateRef(ref);
        return this;
    }

    private void addParameter(String name, String value) {
        if (this.parameters == null) {
            this.parameters = new ArrayList<>();
        }
        this.parameters.add(new TemplatedWorkflowParameterDefinition(name, value));
    }
}
