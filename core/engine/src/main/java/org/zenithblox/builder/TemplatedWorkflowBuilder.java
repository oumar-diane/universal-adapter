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
import org.zenithblox.WorkflowTemplateContext;
import org.zenithblox.RuntimeZwangineException;
import org.zenithblox.model.DefaultWorkflowTemplateContext;
import org.zenithblox.model.ModelZwangineContext;
import org.zenithblox.model.WorkflowTemplateDefinition;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Fluent builder for adding new workflows from workflow templates.
 */
public final class TemplatedWorkflowBuilder {

    private final ZwangineContext zwangineContext;
    private final String workflowTemplateId;
    private final WorkflowTemplateContext workflowTemplateContext;
    private String workflowId;
    private String prefixId;
    private Consumer<WorkflowTemplateDefinition> handler;
    private Consumer<WorkflowTemplateContext> configurer;

    private TemplatedWorkflowBuilder(ZwangineContext zwangineContext, String workflowTemplateId) {
        this.zwangineContext = zwangineContext;
        this.workflowTemplateId = workflowTemplateId;
        this.workflowTemplateContext = new DefaultWorkflowTemplateContext(zwangineContext);
    }

    /**
     * Creates a new {@link TemplatedWorkflowBuilder} to specify input parameters, and others, for the workflow template.
     *
     * @param  zwangineContext    the zwangine context
     * @param  workflowTemplateId the id of the workflow template
     * @return                 the builder
     */
    public static TemplatedWorkflowBuilder builder(ZwangineContext zwangineContext, String workflowTemplateId) {
        return new TemplatedWorkflowBuilder(zwangineContext, workflowTemplateId);
    }

    /**
     * Sets the id of the workflow. If no workflow id is configured, then Zwangine will auto assign a workflow id, which is returned
     * from the build method.
     *
     * @param workflowId the workflow id
     */
    public TemplatedWorkflowBuilder workflowId(String workflowId) {
        this.workflowId = workflowId;
        return this;
    }

    /**
     * Sets a prefix to use for all node ids (not workflow id).
     *
     * @param prefixId the prefix id
     */
    public TemplatedWorkflowBuilder prefixId(String prefixId) {
        this.prefixId = prefixId;
        return this;
    }

    /**
     * Adds a parameter the workflow template will use when creating the workflow.
     *
     * @param name  parameter name
     * @param value parameter value
     */
    public TemplatedWorkflowBuilder parameter(String name, Object value) {
        workflowTemplateContext.setParameter(name, value);
        return this;
    }

    /**
     * Adds parameters the workflow template will use when creating the workflow.
     *
     * @param parameters the template parameters to add
     */
    public TemplatedWorkflowBuilder parameters(Map<String, Object> parameters) {
        parameters.forEach(workflowTemplateContext::setParameter);
        return this;
    }

    /**
     * Binds the bean to the template local repository (takes precedence over global beans)
     *
     * @param id   the id of the bean
     * @param bean the bean
     */
    public TemplatedWorkflowBuilder bean(String id, Object bean) {
        workflowTemplateContext.bind(id, bean);
        return this;
    }

    /**
     * Binds the bean to the template local repository (takes precedence over global beans)
     *
     * @param id   the id of the bean
     * @param type the type of the bean to associate the binding
     * @param bean the bean
     */
    public TemplatedWorkflowBuilder bean(String id, Class<?> type, Object bean) {
        workflowTemplateContext.bind(id, type, bean);
        return this;
    }

    /**
     * Binds the bean (via a supplier) to the template local repository (takes precedence over global beans)
     *
     * @param id   the id of the bean
     * @param type the type of the bean to associate the binding
     * @param bean the bean
     */
    public TemplatedWorkflowBuilder bean(String id, Class<?> type, Supplier<Object> bean) {
        workflowTemplateContext.bind(id, type, bean);
        return this;
    }

    /**
     * Sets a handler which gives access to the workflow template model that will be used for creating the workflow. This can
     * be used to do validation. Any changes to the model happens before the workflow is created and added, however these
     * changes affect future usage of the same template.
     *
     * @param handler the handler with callback to invoke with the given workflow template
     */
    public TemplatedWorkflowBuilder handler(Consumer<WorkflowTemplateDefinition> handler) {
        this.handler = handler;
        return this;
    }

    /**
     * Sets a configurer which allows to do configuration while the workflow template is being used to create a workflow. This
     * gives control over the creating process, such as binding local beans and doing other kind of customization.
     *
     * @param configurer the configurer with callback to invoke with the given workflow template context
     */
    public TemplatedWorkflowBuilder configure(Consumer<WorkflowTemplateContext> configurer) {
        this.configurer = configurer;
        return this;
    }

    /**
     * Adds the workflow to the {@link ZwangineContext} which is built from the configured workflow template.
     *
     * @return the workflow id of the workflow that was added.
     */
    public String add() {
        try {
            if (handler != null) {
                WorkflowTemplateDefinition def
                        = ((ModelZwangineContext) zwangineContext).getWorkflowTemplateDefinition(workflowTemplateId);
                if (def == null) {
                    throw new IllegalArgumentException("Cannot find WorkflowTemplate with id " + workflowTemplateId);
                }
                handler.accept(def);
            }
            // configurer is executed later controlled by the workflow template context
            if (configurer != null) {
                workflowTemplateContext.setConfigurer(configurer);
            }
            return zwangineContext.addWorkflowFromTemplate(workflowId, workflowTemplateId, prefixId, workflowTemplateContext);
        } catch (Exception e) {
            throw RuntimeZwangineException.wrapRuntimeException(e);
        }
    }
}
