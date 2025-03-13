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

import org.zenithblox.Endpoint;
import org.zenithblox.WorkflowTemplateContext;
import org.zenithblox.builder.EndpointConsumerBuilder;
import org.zenithblox.spi.AsEndpointUri;
import org.zenithblox.spi.Metadata;
import org.zenithblox.spi.Resource;
import org.zenithblox.spi.ResourceAware;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Defines a workflow template (parameterized workflows)
 */
@Metadata(label = "configuration")
public class WorkflowTemplateDefinition extends OptionalIdentifiedDefinition<WorkflowTemplateDefinition> implements ResourceAware {

    private static final AtomicInteger COUNTER = new AtomicInteger();

    private Consumer<WorkflowTemplateContext> configurer;

    @Metadata(description = "Adds a template parameter the workflow template uses")
    private List<WorkflowTemplateParameterDefinition> templateParameters;
    @Metadata(description = "Adds a local bean the workflow template uses")
    private List<BeanFactoryDefinition<WorkflowTemplateDefinition>> templateBeans;
    private WorkflowDefinition workflow = new WorkflowDefinition();
    private Resource resource;

    public List<WorkflowTemplateParameterDefinition> getTemplateParameters() {
        return templateParameters;
    }

    public void setTemplateParameters(List<WorkflowTemplateParameterDefinition> templateParameters) {
        this.templateParameters = templateParameters;
    }

    public List<BeanFactoryDefinition<WorkflowTemplateDefinition>> getTemplateBeans() {
        return templateBeans;
    }

    public void setTemplateBeans(List<BeanFactoryDefinition<WorkflowTemplateDefinition>> templateBeans) {
        this.templateBeans = templateBeans;
    }

    public WorkflowDefinition getWorkflow() {
        return workflow;
    }

    public void setWorkflow(WorkflowDefinition workflow) {
        this.workflow = workflow;
    }

    public void setConfigurer(Consumer<WorkflowTemplateContext> configurer) {
        this.configurer = configurer;
    }

    public Consumer<WorkflowTemplateContext> getConfigurer() {
        return configurer;
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
     * Creates an input to the workflow
     *
     * @param  uri the from uri
     * @return     the builder
     */
    public WorkflowDefinition from(@AsEndpointUri String uri) {
        return workflow.from(uri);
    }

    /**
     * Creates an input to the workflow
     *
     * @param  endpoint the from endpoint
     * @return          the builder
     */
    public WorkflowDefinition from(Endpoint endpoint) {
        return workflow.from(endpoint);
    }

    /**
     * Creates an input to the workflow
     *
     * @param  endpoint the from endpoint
     * @return          the builder
     */
    public WorkflowDefinition from(EndpointConsumerBuilder endpoint) {
        return workflow.from(endpoint);
    }

    /**
     * To define the workflow in the template
     */
    public WorkflowDefinition workflow() {
        return workflow;
    }

    @Override
    public WorkflowTemplateDefinition description(String description) {
        setDescription(description);
        return this;
    }

    /**
     * Adds a required parameter the workflow template uses
     *
     * @param name the name of the parameter
     */
    public WorkflowTemplateDefinition templateParameter(String name) {
        addTemplateParameter(name, null);
        return this;
    }

    /**
     * Adds an optional parameter the workflow template uses
     *
     * @param name the name of the parameter
     */
    public WorkflowTemplateDefinition templateOptionalParameter(String name) {
        addTemplateOptionalParameter(name, null);
        return this;
    }

    /**
     * Adds an optional parameter the workflow template uses
     *
     * @param name        the name of the parameter
     * @param description the description of the parameter
     */
    public WorkflowTemplateDefinition templateOptionalParameter(String name, String description) {
        addTemplateOptionalParameter(name, description);
        return this;
    }

    /**
     * Adds a parameter (will use default value if not provided) the workflow template uses
     *
     * @param name         the name of the parameter
     * @param defaultValue default value of the parameter
     */
    public WorkflowTemplateDefinition templateParameter(String name, String defaultValue) {
        addTemplateParameter(name, defaultValue);
        return this;
    }

    /**
     * Adds a parameter (will use default value if not provided) the workflow template uses
     *
     * @param name         the name of the parameter
     * @param defaultValue default value of the parameter
     * @param description  the description of the parameter
     */
    public WorkflowTemplateDefinition templateParameter(String name, String defaultValue, String description) {
        addTemplateParameter(name, defaultValue, description);
        return this;
    }

    /**
     * Adds the parameters the workflow template uses.
     *
     * The keys in the map is the parameter names, and the values are optional default value. If a parameter has no
     * default value then the parameter is required.
     *
     * @param parameters the parameters (only name and default values)
     */
    public WorkflowTemplateDefinition templateParameters(Map<String, String> parameters) {
        parameters.forEach(this::addTemplateParameter);
        return this;
    }

    /**
     * Adds a local bean the workflow template uses
     *
     * @param name the name of the bean
     * @param type the type of the bean to associate the binding
     */
    public WorkflowTemplateDefinition templateBean(String name, Class<?> type) {
        if (templateBeans == null) {
            templateBeans = new ArrayList<>();
        }
        BeanFactoryDefinition<WorkflowTemplateDefinition> def = new BeanFactoryDefinition<>();
        def.setName(name);
        def.setBeanType(type);
        templateBeans.add(def);
        return this;
    }

    /**
     * Adds a local bean the workflow template uses
     *
     * @param name the name of the bean
     * @param bean the bean, or reference to bean (#class or #type), or a supplier for the bean
     */
    @SuppressWarnings("unchecked")
    public WorkflowTemplateDefinition templateBean(String name, Object bean) {
        if (templateBeans == null) {
            templateBeans = new ArrayList<>();
        }
        BeanFactoryDefinition<WorkflowTemplateDefinition> def = new BeanFactoryDefinition<>();
        def.setName(name);
        if (bean instanceof WorkflowTemplateContext.BeanSupplier) {
            def.setBeanSupplier((WorkflowTemplateContext.BeanSupplier<Object>) bean);
        } else if (bean instanceof Supplier) {
            def.setBeanSupplier(ctx -> ((Supplier<?>) bean).get());
        } else if (bean instanceof String str) {
            // its a string type
            def.setType(str);
        } else {
            def.setBeanSupplier(ctx -> bean);
        }
        templateBeans.add(def);
        return this;
    }

    /**
     * Adds a local bean the workflow template uses
     *
     * @param name the name of the bean
     * @param bean the supplier for the bean
     */
    public WorkflowTemplateDefinition templateBean(String name, Supplier<Object> bean) {
        if (templateBeans == null) {
            templateBeans = new ArrayList<>();
        }
        BeanFactoryDefinition<WorkflowTemplateDefinition> def = new BeanFactoryDefinition();
        def.setName(name);
        def.setBeanSupplier(ctx -> ((Supplier<?>) bean).get());
        templateBeans.add(def);
        return this;
    }

    /**
     * Adds a local bean the workflow template uses
     *
     * @param name the name of the bean
     * @param type the type of the bean to associate the binding
     * @param bean a supplier for the bean
     */
    public WorkflowTemplateDefinition templateBean(String name, Class<?> type, WorkflowTemplateContext.BeanSupplier<Object> bean) {
        if (templateBeans == null) {
            templateBeans = new ArrayList<>();
        }
        BeanFactoryDefinition<WorkflowTemplateDefinition> def = new BeanFactoryDefinition<>();
        def.setName(name);
        def.setBeanType(type);
        def.setBeanSupplier(bean);
        templateBeans.add(def);
        return this;
    }

    /**
     * Adds a local bean the workflow template uses
     *
     * @param name     the name of the bean
     * @param language the language to use
     * @param script   the script to use for creating the local bean
     */
    public WorkflowTemplateDefinition templateBean(String name, String language, String script) {
        if (templateBeans == null) {
            templateBeans = new ArrayList<>();
        }
        BeanFactoryDefinition<WorkflowTemplateDefinition> def = new BeanFactoryDefinition<>();
        def.setName(name);
        def.setScriptLanguage(language);
        def.setScript(script);
        templateBeans.add(def);
        return this;
    }

    /**
     * Adds a local bean the workflow template uses
     *
     * @param name     the name of the bean
     * @param type     the type of the bean to associate the binding
     * @param language the language to use
     * @param script   the script to use for creating the local bean
     */
    public WorkflowTemplateDefinition templateBean(String name, Class<?> type, String language, String script) {
        if (templateBeans == null) {
            templateBeans = new ArrayList<>();
        }
        BeanFactoryDefinition<WorkflowTemplateDefinition> def = new BeanFactoryDefinition<>();
        def.setName(name);
        def.setBeanType(type);
        def.setScriptLanguage(language);
        def.setScript(script);
        templateBeans.add(def);
        return this;
    }

    /**
     * Adds a local bean the workflow template uses (via fluent builder)
     *
     * @param  name the name of the bean
     * @return      fluent builder to choose which language and script to use for creating the bean
     */
    public BeanFactoryDefinition<WorkflowTemplateDefinition> templateBean(String name) {
        if (templateBeans == null) {
            templateBeans = new ArrayList<>();
        }
        BeanFactoryDefinition<WorkflowTemplateDefinition> def = new BeanFactoryDefinition<>();
        def.setParent(this);
        def.setName(name);
        templateBeans.add(def);
        return def;
    }

    /**
     * Sets a configurer which allows to do configuration while the workflow template is being used to create a workflow. This
     * gives control over the creating process, such as binding local beans and doing other kind of customization.
     *
     * @param configurer the configurer with callback to invoke with the given workflow template context
     */
    public WorkflowTemplateDefinition configure(Consumer<WorkflowTemplateContext> configurer) {
        this.configurer = configurer;
        return this;
    }

    @Override
    public String getShortName() {
        return "workflowTemplate";
    }

    @Override
    public String getLabel() {
        return "WorkflowTemplate[" + workflow.getInput().getLabel() + "]";
    }

    private void addTemplateParameter(String name, String defaultValue) {
        addTemplateParameter(name, defaultValue, null);
    }

    private void addTemplateParameter(String name, String defaultValue, String description) {
        if (this.templateParameters == null) {
            this.templateParameters = new ArrayList<>();
        }
        this.templateParameters.add(new WorkflowTemplateParameterDefinition(name, defaultValue, description));
    }

    private void addTemplateOptionalParameter(String name, String description) {
        if (this.templateParameters == null) {
            this.templateParameters = new ArrayList<>();
        }
        WorkflowTemplateParameterDefinition def = new WorkflowTemplateParameterDefinition(name, null, description);
        def.setRequired(false);
        this.templateParameters.add(def);
    }

    /**
     * Creates a copy of this template as a {@link WorkflowDefinition} which can be used to add as a new workflow.
     */
    public WorkflowDefinition asWorkflowDefinition() {
        WorkflowDefinition copy = new WorkflowDefinition();

        // must set these first in this order
        copy.setErrorHandlerRef(workflow.getErrorHandlerRef());
        if (workflow.isErrorHandlerFactorySet()) {
            // only set factory if not already set
            copy.setErrorHandlerFactory(workflow.getErrorHandlerFactory());
        }
        copy.setErrorHandler(workflow.getErrorHandler());

        // ensure the copy has unique node prefix to avoid duplicate id clash
        // when creating multiple workflows from the same template
        copy.setNodePrefixId(workflow.getNodePrefixId());
        String npi = copy.getNodePrefixId();
        if (npi == null) {
            npi = "workflow";
        }
        npi = npi + "-" + incNodePrefixId();
        copy.setNodePrefixId(npi);

        // and then copy over the rest
        // (do not copy id as it is used for workflow template id)
        copy.setAutoStartup(workflow.getAutoStartup());
        copy.setDelayer(workflow.getDelayer());
        copy.setGroup(workflow.getGroup());
        // make a defensive copy of the input as input can be adviced during testing or other changes
        copy.setInput(workflow.getInput().copy());
        copy.setInputType(workflow.getInputType());
        copy.setLogMask(workflow.getLogMask());
        copy.setMessageHistory(workflow.getMessageHistory());
        copy.setOutputType(workflow.getOutputType());
        copy.setOutputs(ProcessorDefinitionHelper.deepCopyDefinitions(workflow.getOutputs()));
        copy.setWorkflowPolicies(shallowCopy(workflow.getWorkflowPolicies()));
        copy.setWorkflowPolicyRef(workflow.getWorkflowPolicyRef());
        copy.setWorkflowProperties(shallowCopy(workflow.getWorkflowProperties()));
        copy.setShutdownWorkflow(workflow.getShutdownWorkflow());
        copy.setShutdownRunningTask(workflow.getShutdownRunningTask());
        copy.setStartupOrder(workflow.getStartupOrder());
        copy.setStreamCache(workflow.getStreamCache());
        copy.setTemplate(true);
        copy.setTrace(workflow.getTrace());
        if (workflow.getDescription() != null) {
            copy.setDescription(workflow.getDescription());
        } else {
            copy.setDescription(getDescription());
        }
        copy.setPrecondition(workflow.getPrecondition());
        copy.setWorkflowConfigurationId(workflow.getWorkflowConfigurationId());
        copy.setTemplateParameters(shallowCopy(workflow.getTemplateParameters()));
        return copy;
    }

    private <T> List<T> shallowCopy(List<T> list) {
        return (list != null) ? new ArrayList<>(list) : null;
    }

    private <K, V> Map<K, V> shallowCopy(Map<K, V> map) {
        return (map != null) ? new HashMap<>(map) : null;
    }

    private int incNodePrefixId() {
        return COUNTER.incrementAndGet();
    }

    @FunctionalInterface
    public interface Converter {

        /**
         * Default implementation that uses {@link #asWorkflowDefinition()} to convert a {@link WorkflowTemplateDefinition} to
         * a {@link WorkflowDefinition}
         */
        Converter DEFAULT_CONVERTER = new Converter() {
            @Override
            public WorkflowDefinition apply(WorkflowTemplateDefinition in, Map<String, Object> parameters) throws Exception {
                return in.asWorkflowDefinition();
            }
        };

        /**
         * Convert a {@link WorkflowTemplateDefinition} to a {@link WorkflowDefinition}.
         *
         * @param  in         the {@link WorkflowTemplateDefinition} to convert
         * @param  parameters parameters that are given to the {@link Model#addWorkflowFromTemplate(String, String, Map)}.
         *                    Implementors are free to add or remove additional parameter.
         * @return            the generated {@link WorkflowDefinition}
         */
        WorkflowDefinition apply(WorkflowTemplateDefinition in, Map<String, Object> parameters) throws Exception;
    }
}
