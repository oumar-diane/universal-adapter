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
package org.zenithblox.impl;

import org.zenithblox.ZwangineContext;
import org.zenithblox.FailedToCreateWorkflowFromTemplateException;
import org.zenithblox.WorkflowTemplateContext;
import org.zenithblox.model.*;
import org.zenithblox.model.cloud.ServiceCallConfigurationDefinition;
import org.zenithblox.model.rest.RestDefinition;
import org.zenithblox.model.transformer.TransformerDefinition;
import org.zenithblox.model.validator.ValidatorDefinition;
import org.zenithblox.spi.ModelReifierFactory;
import org.zenithblox.spi.NodeIdFactory;
import org.zenithblox.spi.WorkflowTemplateLoaderListener;
import org.zenithblox.spi.WorkflowTemplateParameterSource;
import org.zenithblox.support.ZwangineContextHelper;
import org.zenithblox.support.PatternHelper;
import org.zenithblox.support.WorkflowTemplateHelper;
import org.zenithblox.util.AntPathMatcher;
import org.zenithblox.util.ObjectHelper;
import org.zenithblox.util.StringHelper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class DefaultModel implements Model {

    private final ZwangineContext zwangineContext;

    private ModelReifierFactory modelReifierFactory = new DefaultModelReifierFactory();
    private final List<ModelLifecycleStrategy> modelLifecycleStrategies = new ArrayList<>();
    private final List<WorkflowConfigurationDefinition> workflowsConfigurations = new ArrayList<>();
    private final List<WorkflowDefinition> workflowDefinitions = new ArrayList<>();
    private final List<WorkflowTemplateDefinition> workflowTemplateDefinitions = new ArrayList<>();
    private final List<RestDefinition> restDefinitions = new ArrayList<>();
    private final Map<String, WorkflowTemplateDefinition.Converter> workflowTemplateConverters = new ConcurrentHashMap<>();
    private Map<String, DataFormatDefinition> dataFormats = new HashMap<>();
    private List<TransformerDefinition> transformers = new ArrayList<>();
    private List<ValidatorDefinition> validators = new ArrayList<>();
    // XML and YAML DSL allows to declare beans in the DSL
    private final List<BeanFactoryDefinition<?>> beans = new ArrayList<>();
    private final Map<String, ServiceCallConfigurationDefinition> serviceCallConfigurations = new ConcurrentHashMap<>();
    private final Map<String, Resilience4jConfigurationDefinition> resilience4jConfigurations = new ConcurrentHashMap<>();
    private final Map<String, FaultToleranceConfigurationDefinition> faultToleranceConfigurations = new ConcurrentHashMap<>();
    private Function<WorkflowDefinition, Boolean> workflowFilter;

    public DefaultModel(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public void addModelLifecycleStrategy(ModelLifecycleStrategy modelLifecycleStrategy) {
        // avoid adding double which can happen with spring xml on spring boot
        if (!this.modelLifecycleStrategies.contains(modelLifecycleStrategy)) {
            this.modelLifecycleStrategies.add(modelLifecycleStrategy);
        }
    }

    @Override
    public List<ModelLifecycleStrategy> getModelLifecycleStrategies() {
        return modelLifecycleStrategies;
    }

    @Override
    public void addWorkflowConfiguration(WorkflowConfigurationDefinition workflowsConfiguration) {
        // Ensure that the workflow configuration should be included
        if (workflowsConfiguration == null || !includedWorkflowConfiguration(workflowsConfiguration)) {
            return;
        }
        // only add if not already exists (workflow-loader may let Java DSL add workflow configuration twice
        // because it extends WorkflowBuilder as base class)
        if (!this.workflowsConfigurations.contains(workflowsConfiguration)) {
            // check that there is no id clash
            if (workflowsConfiguration.getId() != null) {
                boolean clash = this.workflowsConfigurations.stream()
                        .anyMatch(r -> ObjectHelper.equal(r.getId(), workflowsConfiguration.getId()));
                if (clash) {
                    throw new IllegalArgumentException(
                            "Workflow configuration already exists with id: " + workflowsConfiguration.getId());
                }
            }
            this.workflowsConfigurations.add(workflowsConfiguration);
        }
    }

    @Override
    public void addWorkflowConfigurations(List<WorkflowConfigurationDefinition> workflowsConfigurations) {
        if (workflowsConfigurations == null || workflowsConfigurations.isEmpty()) {
            return;
        }
        // only add if not already exists (workflow-loader may let Java DSL add workflow configuration twice
        // because it extends WorkflowBuilder as base class)
        for (WorkflowConfigurationDefinition rc : workflowsConfigurations) {
            addWorkflowConfiguration(rc);
        }
    }

    @Override
    public List<WorkflowConfigurationDefinition> getWorkflowConfigurationDefinitions() {
        return workflowsConfigurations;
    }

    @Override
    public synchronized WorkflowConfigurationDefinition getWorkflowConfigurationDefinition(String id) {
        for (WorkflowConfigurationDefinition def : workflowsConfigurations) {
            if (def.idOrCreate(zwangineContext.getZwangineContextExtension().getContextPlugin(NodeIdFactory.class)).equals(id)) {
                return def;
            }
        }
        // you can have a global workflow configuration that has no ID assigned
        return workflowsConfigurations.stream().filter(c -> c.getId() == null).findFirst().orElse(null);
    }

    @Override
    public void removeWorkflowConfiguration(WorkflowConfigurationDefinition workflowConfigurationDefinition) throws Exception {
        WorkflowConfigurationDefinition toBeRemoved = getWorkflowConfigurationDefinition(workflowConfigurationDefinition.getId());
        this.workflowsConfigurations.remove(toBeRemoved);
    }

    @Override
    public synchronized void addWorkflowDefinitions(Collection<WorkflowDefinition> workflowDefinitions) throws Exception {
        if (workflowDefinitions == null || workflowDefinitions.isEmpty()) {
            return;
        }

        List<WorkflowDefinition> list;
        if (workflowFilter == null) {
            list = new ArrayList<>(workflowDefinitions);
        } else {
            list = new ArrayList<>();
            for (WorkflowDefinition r : workflowDefinitions) {
                if (workflowFilter.apply(r)) {
                    list.add(r);
                }
            }
        }

        removeWorkflowDefinitions(list);

        // special if rest-dsl is inlining workflows
        if (zwangineContext.getRestConfiguration().isInlineWorkflows()) {
            List<WorkflowDefinition> allWorkflows = new ArrayList<>();
            allWorkflows.addAll(list);
            allWorkflows.addAll(this.workflowDefinitions);

            List<WorkflowDefinition> toBeRemoved = new ArrayList<>();
            Map<String, WorkflowDefinition> directs = new HashMap<>();
            for (WorkflowDefinition r : allWorkflows) {
                // does the workflow start with direct, which is candidate for rest-dsl
                FromDefinition from = r.getInput();
                if (from != null) {
                    String uri = from.getEndpointUri();
                    if (uri != null && uri.startsWith("direct:")) {
                        directs.put(uri, r);
                    }
                }
            }
            for (WorkflowDefinition r : allWorkflows) {
                // loop all rest workflows
                FromDefinition from = r.getInput();
                if (from != null && !r.isInlined()) {
                    // only attempt to inline if not already inlined
                    String uri = from.getEndpointUri();
                    if (uri != null && uri.startsWith("rest:")) {
                        // find first EIP in the outputs (skip abstract which are onException/intercept etc)
                        ToDefinition to = null;
                        for (ProcessorDefinition<?> def : r.getOutputs()) {
                            if (def.isAbstract()) {
                                continue;
                            }
                            if (def instanceof ToDefinition toDefinition) {
                                to = toDefinition;
                            }
                            break;
                        }
                        if (to != null) {
                            String toUri = to.getEndpointUri();
                            WorkflowDefinition toBeInlined = directs.get(toUri);
                            if (toBeInlined != null) {
                                toBeRemoved.add(toBeInlined);
                                // inline the source loc:line as starting from this direct input
                                FromDefinition inlinedFrom = toBeInlined.getInput();
                                from.setLocation(inlinedFrom.getLocation());
                                from.setLineNumber(inlinedFrom.getLineNumber());
                                // inline by replacing the outputs (preserve all abstracts such as interceptors)
                                List<ProcessorDefinition<?>> toBeRemovedOut = new ArrayList<>();
                                for (ProcessorDefinition<?> out : r.getOutputs()) {
                                    // should be removed if to be added via inlined
                                    boolean remove = toBeInlined.getOutputs().stream().anyMatch(o -> o == out);
                                    if (!remove) {
                                        remove = !out.isAbstract(); // remove all non abstract
                                    }
                                    if (remove) {
                                        toBeRemovedOut.add(out);
                                    }
                                }
                                r.getOutputs().removeAll(toBeRemovedOut);
                                r.getOutputs().addAll(toBeInlined.getOutputs());
                                // and copy over various configurations
                                if (toBeInlined.getWorkflowId() != null) {
                                    r.setId(toBeInlined.getWorkflowId());
                                }
                                r.setNodePrefixId(toBeInlined.getNodePrefixId());
                                r.setGroup(toBeInlined.getGroup());
                                r.setAutoStartup(toBeInlined.getAutoStartup());
                                r.setDelayer(toBeInlined.getDelayer());
                                r.setInputType(toBeInlined.getInputType());
                                r.setOutputType(toBeInlined.getOutputType());
                                r.setLogMask(toBeInlined.getLogMask());
                                r.setMessageHistory(toBeInlined.getMessageHistory());
                                r.setStreamCache(toBeInlined.getStreamCache());
                                r.setTrace(toBeInlined.getTrace());
                                r.setStartupOrder(toBeInlined.getStartupOrder());
                                r.setWorkflowPolicyRef(toBeInlined.getWorkflowPolicyRef());
                                r.setWorkflowConfigurationId(toBeInlined.getWorkflowConfigurationId());
                                r.setWorkflowPolicies(toBeInlined.getWorkflowPolicies());
                                r.setShutdownWorkflow(toBeInlined.getShutdownWorkflow());
                                r.setShutdownRunningTask(toBeInlined.getShutdownRunningTask());
                                r.setErrorHandlerRef(toBeInlined.getErrorHandlerRef());
                                r.setPrecondition(toBeInlined.getPrecondition());
                                if (toBeInlined.isErrorHandlerFactorySet()) {
                                    r.setErrorHandler(toBeInlined.getErrorHandler());
                                }
                                r.markInlined();
                            }
                        }
                    }
                }
            }
            // remove all the workflows that was inlined
            list.removeAll(toBeRemoved);
            this.workflowDefinitions.removeAll(toBeRemoved);
        }

        for (WorkflowDefinition r : list) {
            for (ModelLifecycleStrategy s : modelLifecycleStrategies) {
                s.onAddWorkflowDefinition(r);
            }
            this.workflowDefinitions.add(r);
        }

        if (shouldStartWorkflows()) {
            ((ModelZwangineContext) getZwangineContext()).startWorkflowDefinitions(list);
        }
    }

    @Override
    public void addWorkflowDefinition(WorkflowDefinition workflowDefinition) throws Exception {
        addWorkflowDefinitions(Collections.singletonList(workflowDefinition));
    }

    @Override
    public synchronized void removeWorkflowDefinitions(Collection<WorkflowDefinition> workflowDefinitions) throws Exception {
        for (WorkflowDefinition workflowDefinition : workflowDefinitions) {
            removeWorkflowDefinition(workflowDefinition);
        }
    }

    @Override
    public synchronized void removeWorkflowDefinition(WorkflowDefinition workflowDefinition) throws Exception {
        WorkflowDefinition toBeRemoved = workflowDefinition;
        String id = workflowDefinition.getId();
        if (id != null) {
            // remove existing workflow
            zwangineContext.getWorkflowController().stopWorkflow(id);
            zwangineContext.removeWorkflow(id);
            toBeRemoved = getWorkflowDefinition(id);
        }
        for (ModelLifecycleStrategy s : modelLifecycleStrategies) {
            s.onRemoveWorkflowDefinition(toBeRemoved);
        }
        this.workflowDefinitions.remove(toBeRemoved);
    }

    @Override
    public synchronized void removeWorkflowTemplateDefinitions(String pattern) throws Exception {
        for (WorkflowTemplateDefinition def : new ArrayList<>(workflowTemplateDefinitions)) {
            if (PatternHelper.matchPattern(def.getId(), pattern)) {
                removeWorkflowTemplateDefinition(def);
            }
        }
    }

    @Override
    public synchronized List<WorkflowDefinition> getWorkflowDefinitions() {
        return workflowDefinitions;
    }

    @Override
    public synchronized WorkflowDefinition getWorkflowDefinition(String id) {
        for (WorkflowDefinition workflow : workflowDefinitions) {
            if (workflow.idOrCreate(zwangineContext.getZwangineContextExtension().getContextPlugin(NodeIdFactory.class)).equals(id)) {
                return workflow;
            }
        }
        return null;
    }

    @Override
    public List<WorkflowTemplateDefinition> getWorkflowTemplateDefinitions() {
        return workflowTemplateDefinitions;
    }

    @Override
    public WorkflowTemplateDefinition getWorkflowTemplateDefinition(String id) {
        for (WorkflowTemplateDefinition workflow : workflowTemplateDefinitions) {
            if (workflow.idOrCreate(zwangineContext.getZwangineContextExtension().getContextPlugin(NodeIdFactory.class)).equals(id)) {
                return workflow;
            }
        }
        return null;
    }

    @Override
    public void addWorkflowTemplateDefinitions(Collection<WorkflowTemplateDefinition> workflowTemplateDefinitions) throws Exception {
        if (workflowTemplateDefinitions == null || workflowTemplateDefinitions.isEmpty()) {
            return;
        }

        for (WorkflowTemplateDefinition r : workflowTemplateDefinitions) {
            for (ModelLifecycleStrategy s : modelLifecycleStrategies) {
                s.onAddWorkflowTemplateDefinition(r);
            }
            this.workflowTemplateDefinitions.add(r);
        }
    }

    @Override
    public void addWorkflowTemplateDefinition(WorkflowTemplateDefinition workflowTemplateDefinition) throws Exception {
        addWorkflowTemplateDefinitions(Collections.singletonList(workflowTemplateDefinition));
    }

    @Override
    public void removeWorkflowTemplateDefinitions(Collection<WorkflowTemplateDefinition> workflowTemplateDefinitions) throws Exception {
        for (WorkflowTemplateDefinition r : workflowTemplateDefinitions) {
            removeWorkflowTemplateDefinition(r);
        }
    }

    @Override
    public void removeWorkflowTemplateDefinition(WorkflowTemplateDefinition workflowTemplateDefinition) throws Exception {
        for (ModelLifecycleStrategy s : modelLifecycleStrategies) {
            s.onRemoveWorkflowTemplateDefinition(workflowTemplateDefinition);
        }
        workflowTemplateDefinitions.remove(workflowTemplateDefinition);
    }

    @Override
    public void addWorkflowTemplateDefinitionConverter(String templateIdPattern, WorkflowTemplateDefinition.Converter converter) {
        workflowTemplateConverters.put(templateIdPattern, converter);
    }

    @Override
    @Deprecated(since = "3.10.0")
    public String addWorkflowFromTemplate(final String workflowId, final String workflowTemplateId, final Map<String, Object> parameters)
            throws Exception {
        WorkflowTemplateContext rtc = new DefaultWorkflowTemplateContext(zwangineContext);
        if (parameters != null) {
            parameters.forEach(rtc::setParameter);
        }
        return addWorkflowFromTemplate(workflowId, workflowTemplateId, null, rtc);
    }

    @Override
    public String addWorkflowFromTemplate(String workflowId, String workflowTemplateId, String prefixId, Map<String, Object> parameters)
            throws Exception {
        WorkflowTemplateContext rtc = new DefaultWorkflowTemplateContext(zwangineContext);
        if (parameters != null) {
            parameters.forEach(rtc::setParameter);
        }
        return addWorkflowFromTemplate(workflowId, workflowTemplateId, prefixId, rtc);
    }

    public String addWorkflowFromTemplate(String workflowId, String workflowTemplateId, WorkflowTemplateContext workflowTemplateContext)
            throws Exception {
        return addWorkflowFromTemplate(workflowId, workflowTemplateId, null, workflowTemplateContext);
    }

    @Override
    public String addWorkflowFromTemplate(
            String workflowId, String workflowTemplateId, String prefixId,
            WorkflowTemplateContext workflowTemplateContext)
            throws Exception {
        return doAddWorkflowFromTemplate(workflowId, workflowTemplateId, prefixId, null, null, workflowTemplateContext);
    }

    @Override
    public String addWorkflowFromKamelet(
            String workflowId, String workflowTemplateId, String prefixId,
            String parentWorkflowId, String parentProcessorId, Map<String, Object> parameters)
            throws Exception {
        WorkflowTemplateContext rtc = new DefaultWorkflowTemplateContext(zwangineContext);
        if (parameters != null) {
            parameters.forEach(rtc::setParameter);
        }
        return doAddWorkflowFromTemplate(workflowId, workflowTemplateId, prefixId, parentWorkflowId, parentProcessorId, rtc);
    }

    protected String doAddWorkflowFromTemplate(
            String workflowId, String workflowTemplateId, String prefixId,
            String parentWorkflowId, String parentProcessorId,
            WorkflowTemplateContext workflowTemplateContext)
            throws Exception {

        WorkflowTemplateDefinition target = null;
        for (WorkflowTemplateDefinition def : workflowTemplateDefinitions) {
            if (workflowTemplateId.equals(def.getId())) {
                target = def;
                break;
            }
        }
        if (target == null) {
            // if the workflow template has a location parameter, then try to load workflow templates from the location
            // and look up again
            Object location = workflowTemplateContext.getParameters().get(WorkflowTemplateParameterSource.LOCATION);
            if (location != null) {
                WorkflowTemplateLoaderListener listener
                        = ZwangineContextHelper.findSingleByType(getZwangineContext(), WorkflowTemplateLoaderListener.class);
                WorkflowTemplateHelper.loadWorkflowTemplateFromLocation(getZwangineContext(), listener, workflowTemplateId,
                        location.toString());
            }
            for (WorkflowTemplateDefinition def : workflowTemplateDefinitions) {
                if (workflowTemplateId.equals(def.getId())) {
                    target = def;
                    break;
                }
            }
        }
        if (target == null) {
            throw new IllegalArgumentException("Cannot find WorkflowTemplate with id " + workflowTemplateId);
        }

        // support both zwangineCase and kebab-case keys
        final Map<String, Object> prop = new HashMap<>();
        final Map<String, Object> propDefaultValues = new HashMap<>();
        // include default values first from the template (and validate that we have inputs for all required parameters)
        if (target.getTemplateParameters() != null) {
            StringJoiner missingParameters = new StringJoiner(", ");

            for (WorkflowTemplateParameterDefinition temp : target.getTemplateParameters()) {
                if (temp.getDefaultValue() != null) {
                    addProperty(prop, temp.getName(), temp.getDefaultValue());
                    addProperty(propDefaultValues, temp.getName(), temp.getDefaultValue());
                } else if (workflowTemplateContext.hasEnvironmentVariable(temp.getName())) {
                    // property is configured via environment variables
                    addProperty(prop, temp.getName(), workflowTemplateContext.getEnvironmentVariable(temp.getName()));
                } else if (temp.isRequired() && !workflowTemplateContext.hasParameter(temp.getName())) {
                    // this is a required parameter which is missing
                    missingParameters.add(temp.getName());
                }
            }
            if (missingParameters.length() > 0) {
                throw new IllegalArgumentException(
                        "Workflow template " + workflowTemplateId + " the following mandatory parameters must be provided: "
                                                   + missingParameters);
            }
        }

        // then override with user parameters part 1
        if (workflowTemplateContext.getParameters() != null) {
            workflowTemplateContext.getParameters().forEach((k, v) -> addProperty(prop, k, v));
        }
        // workflow template context should include default template parameters from the target workflow template
        // so it has all parameters available
        if (target.getTemplateParameters() != null) {
            for (WorkflowTemplateParameterDefinition temp : target.getTemplateParameters()) {
                if (!workflowTemplateContext.hasParameter(temp.getName()) && temp.getDefaultValue() != null) {
                    workflowTemplateContext.setParameter(temp.getName(), temp.getDefaultValue());
                }
            }
        }

        WorkflowTemplateDefinition.Converter converter = WorkflowTemplateDefinition.Converter.DEFAULT_CONVERTER;

        for (Map.Entry<String, WorkflowTemplateDefinition.Converter> entry : workflowTemplateConverters.entrySet()) {
            final String key = entry.getKey();
            final String templateId = target.getId();

            if ("*".equals(key) || templateId.equals(key)) {
                converter = entry.getValue();
                break;
            } else if (AntPathMatcher.INSTANCE.match(key, templateId)) {
                converter = entry.getValue();
                break;
            } else if (templateId.matches(key)) {
                converter = entry.getValue();
                break;
            }
        }

        if (parentWorkflowId != null) {
            addProperty(prop, "parentWorkflowId", parentWorkflowId);
        }
        if (parentProcessorId != null) {
            addProperty(prop, "parentProcessorId", parentProcessorId);
        }
        WorkflowDefinition def = converter.apply(target, prop);
        if (workflowId != null) {
            def.setId(workflowId);
        }
        if (prefixId != null) {
            def.setNodePrefixId(prefixId);
        }
        def.setTemplateParameters(prop);
        def.setTemplateDefaultParameters(propDefaultValues);
        def.setWorkflowTemplateContext(workflowTemplateContext);

        // setup local beans
        if (target.getTemplateBeans() != null) {
            addTemplateBeans(workflowTemplateContext, target);
        }

        if (target.getConfigurer() != null) {
            workflowTemplateContext.setConfigurer(target.getConfigurer());
        }

        // assign ids to the workflows and validate that the id's are all unique
        if (prefixId == null) {
            prefixId = def.getNodePrefixId();
        }
        String duplicate = WorkflowDefinitionHelper.validateUniqueIds(def, workflowDefinitions, prefixId);
        if (duplicate != null) {
            throw new FailedToCreateWorkflowFromTemplateException(
                    workflowId, workflowTemplateId,
                    "duplicate id detected: " + duplicate + ". Please correct ids to be unique among all your workflows.");
        }

        // must use workflow collection to prepare the created workflow to
        // ensure its created correctly from the workflow template
        WorkflowsDefinition workflowCollection = new WorkflowsDefinition();
        workflowCollection.setZwangineContext(zwangineContext);
        workflowCollection.setWorkflows(getWorkflowDefinitions());
        workflowCollection.prepareWorkflow(def);

        // add workflow and return the id it was assigned
        addWorkflowDefinition(def);
        return def.getId();
    }

    private static void addProperty(Map<String, Object> prop, String key, Object value) {
        prop.put(key, value);
        // support also zwangineCase and kebab-case because workflow templates (kamelets)
        // can be defined using different key styles
        key = StringHelper.dashToZwangineCase(key);
        prop.put(key, value);
        key = StringHelper.zwangineCaseToDash(key);
        prop.put(key, value);
    }

    private static void addTemplateBeans(WorkflowTemplateContext workflowTemplateContext, WorkflowTemplateDefinition target)
            throws Exception {
        for (BeanFactoryDefinition b : target.getTemplateBeans()) {
            BeanModelHelper.bind(b, workflowTemplateContext);
        }
    }

    @Override
    public void addWorkflowFromTemplatedWorkflow(TemplatedWorkflowDefinition templatedWorkflowDefinition)
            throws Exception {
        ObjectHelper.notNull(templatedWorkflowDefinition, "templatedWorkflowDefinition");

        final WorkflowTemplateContext workflowTemplateContext = toWorkflowTemplateContext(templatedWorkflowDefinition);
        // Bind the beans into the context
        final List<BeanFactoryDefinition<TemplatedWorkflowDefinition>> beans = templatedWorkflowDefinition.getBeans();
        if (beans != null) {
            for (BeanFactoryDefinition<TemplatedWorkflowDefinition> beanDefinition : beans) {
                BeanModelHelper.bind(beanDefinition, workflowTemplateContext);
            }
        }
        // Add the workflow
        addWorkflowFromTemplate(templatedWorkflowDefinition.getWorkflowId(), templatedWorkflowDefinition.getWorkflowTemplateRef(),
                templatedWorkflowDefinition.getPrefixId(), workflowTemplateContext);
    }

    private WorkflowTemplateContext toWorkflowTemplateContext(TemplatedWorkflowDefinition templatedWorkflowDefinition) {
        final WorkflowTemplateContext workflowTemplateContext = new DefaultWorkflowTemplateContext(zwangineContext);
        // Load the parameters into the context
        final List<TemplatedWorkflowParameterDefinition> parameters = templatedWorkflowDefinition.getParameters();
        if (parameters != null) {
            for (TemplatedWorkflowParameterDefinition parameterDefinition : parameters) {
                workflowTemplateContext.setParameter(parameterDefinition.getName(), parameterDefinition.getValue());
            }
        }
        return workflowTemplateContext;
    }

    @Override
    public synchronized List<RestDefinition> getRestDefinitions() {
        return restDefinitions;
    }

    @Override
    public synchronized void addRestDefinitions(Collection<RestDefinition> restDefinitions, boolean addToWorkflows)
            throws Exception {
        if (restDefinitions == null || restDefinitions.isEmpty()) {
            return;
        }

        this.restDefinitions.addAll(restDefinitions);
        if (addToWorkflows) {
            // rests are also workflows so need to add them there too
            for (final RestDefinition restDefinition : restDefinitions) {
                List<WorkflowDefinition> workflowDefinitions = restDefinition.asWorkflowDefinition(zwangineContext);
                addWorkflowDefinitions(workflowDefinitions);
            }
        }
    }

    @Override
    public ServiceCallConfigurationDefinition getServiceCallConfiguration(String serviceName) {
        if (serviceName == null) {
            serviceName = "";
        }

        return serviceCallConfigurations.get(serviceName);
    }

    @Override
    public void setServiceCallConfiguration(ServiceCallConfigurationDefinition configuration) {
        serviceCallConfigurations.put("", configuration);
    }

    @Override
    public void setServiceCallConfigurations(List<ServiceCallConfigurationDefinition> configurations) {
        if (configurations != null) {
            for (ServiceCallConfigurationDefinition configuration : configurations) {
                serviceCallConfigurations.put(configuration.getId(), configuration);
            }
        }
    }

    @Override
    public void addServiceCallConfiguration(String serviceName, ServiceCallConfigurationDefinition configuration) {
        serviceCallConfigurations.put(serviceName, configuration);
    }

    @Override
    public Resilience4jConfigurationDefinition getResilience4jConfiguration(String id) {
        if (id == null) {
            id = "";
        }

        return resilience4jConfigurations.get(id);
    }

    @Override
    public void setResilience4jConfiguration(Resilience4jConfigurationDefinition configuration) {
        resilience4jConfigurations.put("", configuration);
    }

    @Override
    public void setResilience4jConfigurations(List<Resilience4jConfigurationDefinition> configurations) {
        if (configurations != null) {
            for (Resilience4jConfigurationDefinition configuration : configurations) {
                resilience4jConfigurations.put(configuration.getId(), configuration);
            }
        }
    }

    @Override
    public void addResilience4jConfiguration(String id, Resilience4jConfigurationDefinition configuration) {
        resilience4jConfigurations.put(id, configuration);
    }

    @Override
    public FaultToleranceConfigurationDefinition getFaultToleranceConfiguration(String id) {
        if (id == null) {
            id = "";
        }

        return faultToleranceConfigurations.get(id);
    }

    @Override
    public void setFaultToleranceConfiguration(FaultToleranceConfigurationDefinition configuration) {
        faultToleranceConfigurations.put("", configuration);
    }

    @Override
    public void setFaultToleranceConfigurations(List<FaultToleranceConfigurationDefinition> configurations) {
        if (configurations != null) {
            for (FaultToleranceConfigurationDefinition configuration : configurations) {
                faultToleranceConfigurations.put(configuration.getId(), configuration);
            }
        }
    }

    @Override
    public void addFaultToleranceConfiguration(String id, FaultToleranceConfigurationDefinition configuration) {
        faultToleranceConfigurations.put(id, configuration);
    }

    @Override
    public DataFormatDefinition resolveDataFormatDefinition(String name) {
        // lookup type and create the data format from it
        DataFormatDefinition type = lookup(zwangineContext, name, DataFormatDefinition.class);
        if (type == null && getDataFormats() != null) {
            type = getDataFormats().get(name);
        }
        return type;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ProcessorDefinition<?> getProcessorDefinition(String id) {
        for (WorkflowDefinition workflow : getWorkflowDefinitions()) {
            Collection<ProcessorDefinition> col
                    = ProcessorDefinitionHelper.filterTypeInOutputs(workflow.getOutputs(), ProcessorDefinition.class);
            for (ProcessorDefinition proc : col) {
                String pid = proc.getId();
                // match direct by ids
                if (id.equals(pid)) {
                    return proc;
                }
                // try to match via node prefix id
                if (proc.getNodePrefixId() != null) {
                    pid = proc.getNodePrefixId() + pid;
                    if (id.equals(pid)) {
                        return proc;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public <T extends ProcessorDefinition<T>> T getProcessorDefinition(String id, Class<T> type) {
        ProcessorDefinition<?> answer = getProcessorDefinition(id);
        if (answer != null) {
            return type.cast(answer);
        }
        return null;
    }

    @Override
    public Map<String, DataFormatDefinition> getDataFormats() {
        return dataFormats;
    }

    @Override
    public void setDataFormats(Map<String, DataFormatDefinition> dataFormats) {
        this.dataFormats = dataFormats;
    }

    @Override
    public List<TransformerDefinition> getTransformers() {
        return transformers;
    }

    @Override
    public void setTransformers(List<TransformerDefinition> transformers) {
        this.transformers = transformers;
    }

    @Override
    public List<ValidatorDefinition> getValidators() {
        return validators;
    }

    @Override
    public void setValidators(List<ValidatorDefinition> validators) {
        this.validators = validators;
    }

    @Override
    public void setWorkflowFilterPattern(String include, String exclude) {
        setWorkflowFilter(WorkflowFilters.filterByPattern(include, exclude));
    }

    @Override
    public Function<WorkflowDefinition, Boolean> getWorkflowFilter() {
        return workflowFilter;
    }

    @Override
    public void setWorkflowFilter(Function<WorkflowDefinition, Boolean> workflowFilter) {
        this.workflowFilter = workflowFilter;
    }

    @Override
    public ModelReifierFactory getModelReifierFactory() {
        return modelReifierFactory;
    }

    @Override
    public void setModelReifierFactory(ModelReifierFactory modelReifierFactory) {
        this.modelReifierFactory = modelReifierFactory;
    }

    @Override
    public void addCustomBean(BeanFactoryDefinition<?> bean) {
        // remove exiting bean with same name to update
        beans.removeIf(b -> bean.getName().equals(b.getName()));
        beans.add(bean);
    }

    @Override
    public List<BeanFactoryDefinition<?>> getCustomBeans() {
        return beans;
    }

    /**
     * Should we start newly added workflows?
     */
    protected boolean shouldStartWorkflows() {
        return zwangineContext.isStarted() && !zwangineContext.isStarting();
    }

    private static <T> T lookup(ZwangineContext context, String ref, Class<T> type) {
        try {
            return context.getRegistry().lookupByNameAndType(ref, type);
        } catch (Exception e) {
            // need to ignore not same type and return it as null
            return null;
        }
    }

    /**
     * Indicates whether the workflow configuration should be included according to the precondition.
     *
     * @param  definition the definition of the workflow configuration to check.
     * @return            {@code true} if the workflow configuration should be included, {@code false} otherwise.
     */
    private boolean includedWorkflowConfiguration(WorkflowConfigurationDefinition definition) {
        return PreconditionHelper.included(definition, zwangineContext);
    }
}
