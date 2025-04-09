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

import org.zenithblox.*;
import org.zenithblox.impl.engine.DefaultExecutorServiceManager;
import org.zenithblox.impl.engine.WorkflowService;
import org.zenithblox.impl.engine.SimpleZwangineContext;
import org.zenithblox.impl.scan.AssignableToPackageScanFilter;
import org.zenithblox.impl.scan.InvertingPackageScanFilter;
import org.zenithblox.model.*;
import org.zenithblox.model.cloud.ServiceCallConfigurationDefinition;
import org.zenithblox.model.language.ExpressionDefinition;
import org.zenithblox.model.rest.RestDefinition;
import org.zenithblox.model.transformer.TransformerDefinition;
import org.zenithblox.model.validator.ValidatorDefinition;
import org.zenithblox.spi.*;
import org.zenithblox.support.*;
import org.zenithblox.util.ObjectHelper;
import org.zenithblox.util.OrderedLocationProperties;
import org.zenithblox.util.StopWatch;
import org.zenithblox.util.concurrent.NamedThreadLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;

/**
 * Represents the context used to configure workflows and the policies to use.
 */
public class DefaultZwangineContext extends SimpleZwangineContext implements ModelZwangineContext {

    // global options that can be set on ZwangineContext as part of concurrent testing
    // which means options should be isolated via thread-locals and not a static instance
    // use a HashMap to store only JDK classes in the thread-local so there will not be any Zwangine classes leaking
    private static final ThreadLocal<Map<String, Object>> OPTIONS = new NamedThreadLocal<>("ZwangineContextOptions", HashMap::new);
    private static final String OPTION_NO_START = "OptionNoStart";
    private static final String OPTION_DISABLE_JMX = "OptionDisableJMX";
    private static final String OPTION_EXCLUDE_ROUTES = "OptionExcludeWorkflows";

    private static final Logger LOG = LoggerFactory.getLogger(DefaultZwangineContext.class);
    private static final UuidGenerator UUID = new SimpleUuidGenerator();

    private final Model model = new DefaultModel(this);

    /**
     * Creates the {@link org.zenithblox.model.ModelZwangineContext} using {@link org.zenithblox.support.DefaultRegistry} as registry.
     * <p/>
     * Use one of the other constructors to force use an explicit registry.
     */
    public DefaultZwangineContext() {
        this(true);
    }

    /**
     * Creates the {@link ZwangineContext} using the given {@link BeanRepository} as first-choice repository, and the
     * {@link org.zenithblox.support.SimpleRegistry} as fallback, via the {@link DefaultRegistry} implementation.
     *
     * @param repository the bean repository.
     */
    public DefaultZwangineContext(BeanRepository repository) {
        this(new DefaultRegistry(repository));
    }

    /**
     * Creates the {@link ModelZwangineContext} using the given registry
     *
     * @param registry the registry
     */
    public DefaultZwangineContext(Registry registry) {
        this();
        getZwangineContextExtension().setRegistry(registry);
    }

    public DefaultZwangineContext(boolean init) {
        super(init);
        // setup model factory which must be done very early
        setModelReifierFactory(createModelReifierFactory());
        if (isDisableJmx()) {
            disableJMX();
        }
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        OPTIONS.remove();
    }

    @Override
    protected void doDumpWorkflows() {
        DumpWorkflowsStrategy strategy = ZwangineContextHelper.findSingleByType(this, DumpWorkflowsStrategy.class);
        if (strategy == null) {
            strategy = getZwangineContextExtension().getContextPlugin(DumpWorkflowsStrategy.class);
        }
        if (strategy != null) {
            strategy.dumpWorkflows(getDumpWorkflows());
        }
    }

    public static void setNoStart(boolean b) {
        getOptions().put(OPTION_NO_START, b);
    }

    public static boolean isNoStart() {
        return (Boolean) getOptions().getOrDefault(OPTION_NO_START, Boolean.FALSE);
    }

    public static void setDisableJmx(boolean b) {
        getOptions().put(OPTION_DISABLE_JMX, b);
    }

    public static boolean isDisableJmx() {
        return (Boolean) getOptions().getOrDefault(OPTION_DISABLE_JMX, Boolean.getBoolean("disableJmx"));
    }

    @Override
    public String getTestExcludeWorkflows() {
        return getExcludeWorkflows();
    }

    public static String getExcludeWorkflows() {
        return (String) getOptions().get(OPTION_EXCLUDE_ROUTES);
    }

    public static void setExcludeWorkflows(String s) {
        getOptions().put(OPTION_EXCLUDE_ROUTES, s);
    }

    public static void clearOptions() {
        OPTIONS.get().clear();
    }

    private static Map<String, Object> getOptions() {
        return OPTIONS.get();
    }

    @Override
    public void start() {
        // for example from unit testing we want to start Zwangine later (manually)
        if (isNoStart()) {
            LOG.trace("Ignoring start() as NO_START is true");
            return;
        }

        if (!isStarted() && !isStarting()) {
            StopWatch watch = new StopWatch();
            super.start();
            LOG.debug("start() took {} millis", watch.taken());
        } else {
            // ignore as Zwangine is already started
            LOG.trace("Ignoring start() as Zwangine is already started");
        }
    }

    @Override
    protected PackageScanClassResolver createPackageScanClassResolver() {
        PackageScanClassResolver resolver = super.createPackageScanClassResolver();
        String excluded = getExcludeWorkflows();
        if (ObjectHelper.isNotEmpty(excluded)) {
            Set<Class<?>> excludedClasses = new HashSet<>();
            for (String str : excluded.split(",")) {
                excludedClasses.add(getClassResolver().resolveClass(str));
            }
            resolver.addFilter(new InvertingPackageScanFilter(new AssignableToPackageScanFilter(excludedClasses)));
        }
        return resolver;
    }

    @Override
    public void addModelLifecycleStrategy(ModelLifecycleStrategy modelLifecycleStrategy) {
        model.addModelLifecycleStrategy(modelLifecycleStrategy);
    }

    @Override
    public List<ModelLifecycleStrategy> getModelLifecycleStrategies() {
        return model.getModelLifecycleStrategies();
    }

    @Override
    public void addWorkflowConfiguration(WorkflowConfigurationDefinition workflowsConfiguration) {
        model.addWorkflowConfiguration(workflowsConfiguration);
    }

    @Override
    public void addWorkflowConfigurations(List<WorkflowConfigurationDefinition> workflowsConfigurations) {
        model.addWorkflowConfigurations(workflowsConfigurations);
    }

    @Override
    public List<WorkflowConfigurationDefinition> getWorkflowConfigurationDefinitions() {
        return model.getWorkflowConfigurationDefinitions();
    }

    @Override
    public WorkflowConfigurationDefinition getWorkflowConfigurationDefinition(String id) {
        return model.getWorkflowConfigurationDefinition(id);
    }

    @Override
    public void removeWorkflowConfiguration(WorkflowConfigurationDefinition workflowConfigurationDefinition) throws Exception {
        model.removeWorkflowConfiguration(workflowConfigurationDefinition);
    }

    @Override
    public List<WorkflowDefinition> getWorkflowDefinitions() {
        return model.getWorkflowDefinitions();
    }

    @Override
    public WorkflowDefinition getWorkflowDefinition(String id) {
        return model.getWorkflowDefinition(id);
    }

    @Override
    public void addWorkflowDefinitions(Collection<WorkflowDefinition> workflowDefinitions) throws Exception {
        model.addWorkflowDefinitions(workflowDefinitions);
    }

    @Override
    public void addWorkflowDefinition(WorkflowDefinition workflowDefinition) throws Exception {
        model.addWorkflowDefinition(workflowDefinition);
    }

    @Override
    public void removeWorkflowDefinitions(Collection<WorkflowDefinition> workflowDefinitions) throws Exception {
        if (!isLockModel()) {
            model.removeWorkflowDefinitions(workflowDefinitions);
        }
    }

    @Override
    public void removeWorkflowDefinition(WorkflowDefinition workflowDefinition) throws Exception {
        if (!isLockModel()) {
            model.removeWorkflowDefinition(workflowDefinition);
        }
    }

    @Override
    public List<WorkflowTemplateDefinition> getWorkflowTemplateDefinitions() {
        return model.getWorkflowTemplateDefinitions();
    }

    @Override
    public WorkflowTemplateDefinition getWorkflowTemplateDefinition(String id) {
        return model.getWorkflowTemplateDefinition(id);
    }

    @Override
    public void addWorkflowTemplateDefinitions(Collection<WorkflowTemplateDefinition> workflowTemplateDefinitions) throws Exception {
        model.addWorkflowTemplateDefinitions(workflowTemplateDefinitions);
    }

    @Override
    public void addWorkflowTemplateDefinition(WorkflowTemplateDefinition workflowTemplateDefinition) throws Exception {
        model.addWorkflowTemplateDefinition(workflowTemplateDefinition);
    }

    @Override
    public void removeWorkflowTemplateDefinitions(Collection<WorkflowTemplateDefinition> workflowTemplateDefinitions) throws Exception {
        if (!isLockModel()) {
            model.removeWorkflowTemplateDefinitions(workflowTemplateDefinitions);
        }
    }

    @Override
    public void removeWorkflowTemplateDefinition(WorkflowTemplateDefinition workflowTemplateDefinition) throws Exception {
        if (!isLockModel()) {
            model.removeWorkflowTemplateDefinition(workflowTemplateDefinition);
        }
    }

    @Override
    public void removeWorkflowTemplateDefinitions(String pattern) throws Exception {
        if (!isLockModel()) {
            model.removeWorkflowTemplateDefinitions(pattern);
        }
    }

    @Override
    public void addWorkflowTemplateDefinitionConverter(String templateIdPattern, WorkflowTemplateDefinition.Converter converter) {
        model.addWorkflowTemplateDefinitionConverter(templateIdPattern, converter);
    }

    @Override
    public String addWorkflowFromTemplate(String workflowId, String workflowTemplateId, Map<String, Object> parameters)
            throws Exception {
        return model.addWorkflowFromTemplate(workflowId, workflowTemplateId, parameters);
    }

    @Override
    public String addWorkflowFromTemplate(String workflowId, String workflowTemplateId, String prefixId, Map<String, Object> parameters)
            throws Exception {
        return model.addWorkflowFromTemplate(workflowId, workflowTemplateId, prefixId, parameters);
    }

    @Override
    public String addWorkflowFromTemplate(
            String workflowId, String workflowTemplateId, String prefixId, WorkflowTemplateContext workflowTemplateContext)
            throws Exception {
        return model.addWorkflowFromTemplate(workflowId, workflowTemplateId, prefixId, workflowTemplateContext);
    }

    @Override
    public String addWorkflowFromKamelet(
            String workflowId, String workflowTemplateId, String prefixId,
            String parentWorkflowId, String parentProcessorId, Map<String, Object> parameters)
            throws Exception {
        return model.addWorkflowFromKamelet(workflowId, workflowTemplateId, prefixId, parentWorkflowId, parentProcessorId, parameters);
    }

    @Override
    public void addWorkflowFromTemplatedWorkflows(Collection<TemplatedWorkflowDefinition> templatedWorkflowDefinitions) throws Exception {
        model.addWorkflowFromTemplatedWorkflows(templatedWorkflowDefinitions);
    }

    @Override
    public void addWorkflowFromTemplatedWorkflow(TemplatedWorkflowDefinition templatedWorkflowDefinition)
            throws Exception {
        model.addWorkflowFromTemplatedWorkflow(templatedWorkflowDefinition);
    }

    @Override
    public void removeWorkflowTemplates(String pattern) throws Exception {
        if (!isLockModel()) {
            model.removeWorkflowTemplateDefinitions(pattern);
        }
    }


    @Override
    public List<RestDefinition> getRestDefinitions() {
        return model.getRestDefinitions();
    }

    @Override
    public void addRestDefinitions(Collection<RestDefinition> restDefinitions, boolean addToWorkflows) throws Exception {
        model.addRestDefinitions(restDefinitions, addToWorkflows);
    }

    @Override
    public void setDataFormats(Map<String, DataFormatDefinition> dataFormats) {
        model.setDataFormats(dataFormats);
    }

    @Override
    public Map<String, DataFormatDefinition> getDataFormats() {
        return model.getDataFormats();
    }

    @Override
    public DataFormatDefinition resolveDataFormatDefinition(String name) {
        return model.resolveDataFormatDefinition(name);
    }

    @Override
    public ProcessorDefinition<?> getProcessorDefinition(String id) {
        return model.getProcessorDefinition(id);
    }

    @Override
    public <T extends ProcessorDefinition<T>> T getProcessorDefinition(String id, Class<T> type) {
        return model.getProcessorDefinition(id, type);
    }

    @Override
    public void setValidators(List<ValidatorDefinition> validators) {
        model.setValidators(validators);
    }

    @Override
    public Resilience4jConfigurationDefinition getResilience4jConfiguration(String id) {
        return model.getResilience4jConfiguration(id);
    }

    @Override
    public void setResilience4jConfiguration(Resilience4jConfigurationDefinition configuration) {
        model.setResilience4jConfiguration(configuration);
    }

    @Override
    public void setResilience4jConfigurations(List<Resilience4jConfigurationDefinition> configurations) {
        model.setResilience4jConfigurations(configurations);
    }

    @Override
    public void addResilience4jConfiguration(String id, Resilience4jConfigurationDefinition configuration) {
        model.addResilience4jConfiguration(id, configuration);
    }

    @Override
    public FaultToleranceConfigurationDefinition getFaultToleranceConfiguration(String id) {
        return model.getFaultToleranceConfiguration(id);
    }

    @Override
    public void setFaultToleranceConfiguration(FaultToleranceConfigurationDefinition configuration) {
        model.setFaultToleranceConfiguration(configuration);
    }

    @Override
    public void setFaultToleranceConfigurations(List<FaultToleranceConfigurationDefinition> configurations) {
        model.setFaultToleranceConfigurations(configurations);
    }

    @Override
    public void addFaultToleranceConfiguration(String id, FaultToleranceConfigurationDefinition configuration) {
        model.addFaultToleranceConfiguration(id, configuration);
    }

    @Override
    public List<ValidatorDefinition> getValidators() {
        return model.getValidators();
    }

    @Override
    public void setTransformers(List<TransformerDefinition> transformers) {
        model.setTransformers(transformers);
    }

    @Override
    public List<TransformerDefinition> getTransformers() {
        return model.getTransformers();
    }

    @Override
    public ServiceCallConfigurationDefinition getServiceCallConfiguration(String serviceName) {
        return model.getServiceCallConfiguration(serviceName);
    }

    @Override
    public void setServiceCallConfiguration(ServiceCallConfigurationDefinition configuration) {
        model.setServiceCallConfiguration(configuration);
    }

    @Override
    public void setServiceCallConfigurations(List<ServiceCallConfigurationDefinition> configurations) {
        model.setServiceCallConfigurations(configurations);
    }

    @Override
    public void addServiceCallConfiguration(String serviceName, ServiceCallConfigurationDefinition configuration) {
        model.addServiceCallConfiguration(serviceName, configuration);
    }

    @Override
    public void setWorkflowFilterPattern(String include, String exclude) {
        model.setWorkflowFilterPattern(include, exclude);
    }

    @Override
    public void setWorkflowFilter(Function<WorkflowDefinition, Boolean> filter) {
        model.setWorkflowFilter(filter);
    }

    @Override
    public Function<WorkflowDefinition, Boolean> getWorkflowFilter() {
        return model.getWorkflowFilter();
    }

    @Override
    public void addCustomBean(BeanFactoryDefinition<?> bean) {
        model.addCustomBean(bean);
    }

    @Override
    public List<BeanFactoryDefinition<?>> getCustomBeans() {
        return model.getCustomBeans();
    }

    @Override
    public ModelReifierFactory getModelReifierFactory() {
        return model.getModelReifierFactory();
    }

    @Override
    public void setModelReifierFactory(ModelReifierFactory modelReifierFactory) {
        model.setModelReifierFactory(modelReifierFactory);
    }

    @Override
    protected void bindDataFormats() throws Exception {
        // eager lookup data formats and bind to registry so the dataformats can
        // be looked up and used
        if (model != null) {
            for (Map.Entry<String, DataFormatDefinition> e : model.getDataFormats().entrySet()) {
                String id = e.getKey();
                DataFormatDefinition def = e.getValue();
                LOG.debug("Creating Dataformat with id: {} and definition: {}", id, def);
                DataFormat df = model.getModelReifierFactory().createDataFormat(this, def);
                addService(df, true);
                getRegistry().bind(id, df);
            }
        }
    }

    @Override
    protected void shutdownWorkflowService(WorkflowService workflowService) throws Exception {
        getLock().lock();
        try {
            if (model != null) {
                WorkflowDefinition rd = model.getWorkflowDefinition(workflowService.getId());
                if (rd != null) {
                    model.getWorkflowDefinitions().remove(rd);
                }
            }
            super.shutdownWorkflowService(workflowService);
        } finally {
            getLock().unlock();
        }
    }

    @Override
    protected boolean isStreamCachingInUse() throws Exception {
        boolean streamCachingInUse = super.isStreamCachingInUse();
        if (!streamCachingInUse) {
            for (WorkflowDefinition workflow : model.getWorkflowDefinitions()) {
                Boolean workflowCache = ZwangineContextHelper.parseBoolean(this, workflow.getStreamCache());
                if (workflowCache != null && workflowCache) {
                    streamCachingInUse = true;
                    break;
                }
            }
        }
        return streamCachingInUse;
    }

    @Override
    public void startWorkflowDefinitions() throws Exception {
        List<WorkflowDefinition> workflowDefinitions = model.getWorkflowDefinitions();
        if (workflowDefinitions != null) {
            // defensive copy of workflows to be started as kamelets
            // can add workflow definitions from existing workflows
            List<WorkflowDefinition> toBeStarted = new ArrayList<>(workflowDefinitions);
            startWorkflowDefinitions(toBeStarted);
        }
    }

    @Override
    public void removeWorkflowDefinitionsFromTemplate() throws Exception {
        List<WorkflowDefinition> toBeRemoved = new ArrayList<>();
        for (WorkflowDefinition rd : model.getWorkflowDefinitions()) {
            if (rd.isTemplate() != null && rd.isTemplate()) {
                toBeRemoved.add(rd);
            }
        }
        removeWorkflowDefinitions(toBeRemoved);
    }

    public void startWorkflowDefinitions(List<WorkflowDefinition> workflowDefinitions) throws Exception {
        // indicate we are staring the workflow using this thread so
        // we are able to query this if needed
        boolean alreadyStartingWorkflows = isStartingWorkflows();
        if (!alreadyStartingWorkflows) {
            setStartingWorkflows(true);
        }

        PropertiesComponent pc = getZwangineContextReference().getPropertiesComponent();
        // workflow templates supports binding beans that are local for the template only
        // in this local mode then we need to check for side-effects (see further)
        LocalBeanRepositoryAware localBeans = null;
        final Registry registry = getZwangineContextReference().getRegistry();
        if (registry instanceof LocalBeanRepositoryAware localBeanRepositoryAware) {
            localBeans = localBeanRepositoryAware;
        }
        try {
            WorkflowDefinitionHelper.forceAssignIds(getZwangineContextReference(), workflowDefinitions);
            List<WorkflowDefinition> workflowDefinitionsToRemove = null;
            for (WorkflowDefinition workflowDefinition : workflowDefinitions) {
                try {
                    // assign ids to the workflows and validate that the id's is all unique
                    String duplicate = WorkflowDefinitionHelper.validateUniqueIds(workflowDefinition, workflowDefinitions,
                            workflowDefinition.getNodePrefixId());
                    if (duplicate != null) {
                        throw new FailedToStartWorkflowException(
                                workflowDefinition.getId(),
                                "duplicate id detected: " + duplicate
                                                         + ". Please correct ids to be unique among all your workflows.");
                    }

                    // if the workflow definition was created via a workflow template then we need to prepare its parameters when the workflow is being created and started
                    if (workflowDefinition.isTemplate() != null && workflowDefinition.isTemplate()
                            && workflowDefinition.getTemplateParameters() != null) {

                        // apply configurer if any present
                        if (workflowDefinition.getWorkflowTemplateContext().getConfigurer() != null) {
                            workflowDefinition.getWorkflowTemplateContext().getConfigurer()
                                    .accept(workflowDefinition.getWorkflowTemplateContext());
                        }

                        // copy parameters/bean repository to not cause side effect
                        Map<String, Object> params = new HashMap<>(workflowDefinition.getTemplateParameters());
                        LocalBeanRegistry bbr
                                = (LocalBeanRegistry) workflowDefinition.getWorkflowTemplateContext().getLocalBeanRepository();
                        LocalBeanRegistry bbrCopy = new LocalBeanRegistry();

                        // make all bean in the bean repository use unique keys (need to add uuid counter)
                        // so when the workflow template is used again to create another workflow, then there is
                        // no side-effect from previously used values that Zwangine may use in its endpoint
                        // registry and elsewhere
                        if (bbr != null && !bbr.isEmpty()) {
                            for (Map.Entry<String, Object> param : params.entrySet()) {
                                Object value = param.getValue();
                                if (value instanceof String oldKey) {
                                    boolean clash = bbr.keys().stream().anyMatch(k -> k.equals(oldKey));
                                    if (clash) {
                                        String newKey = oldKey + "-" + UUID.generateUuid();
                                        LOG.debug(
                                                "Workflow: {} re-assigning local-bean id: {} to: {} to ensure ids are globally unique",
                                                workflowDefinition.getId(), oldKey, newKey);
                                        bbrCopy.put(newKey, bbr.remove(oldKey));
                                        param.setValue(newKey);
                                    }
                                }
                            }
                            // the remainder of the local beans must also have their ids made global unique
                            for (Map.Entry<String, Map<Class<?>, Object>> entry : bbr.entrySet()) {
                                String oldKey = entry.getKey();
                                String newKey = oldKey + "-" + UUID.generateUuid();
                                LOG.debug(
                                        "Workflow: {} re-assigning local-bean id: {} to: {} to ensure ids are globally unique",
                                        workflowDefinition.getId(), oldKey, newKey);
                                bbrCopy.put(newKey, entry.getValue());
                                if (!params.containsKey(oldKey)) {
                                    // if a bean was bound as local bean with a key and it was not defined as template parameter
                                    // then store it as if it was a template parameter with same key=value which allows us
                                    // to use this local bean in the workflow without any problem such as:
                                    //   to("bean:{{myBean}}")
                                    // and myBean is the local bean id.
                                    params.put(oldKey, newKey);
                                }
                            }
                        }

                        OrderedLocationProperties prop = new OrderedLocationProperties();
                        if (workflowDefinition.getTemplateDefaultParameters() != null) {
                            // need to keep track if a parameter is set as default value or end user configured value
                            params.forEach((k, v) -> {
                                Object dv = workflowDefinition.getTemplateDefaultParameters().get(k);
                                prop.put(workflowDefinition.getLocation(), k, v, dv);
                            });
                        } else {
                            prop.putAll(workflowDefinition.getLocation(), params);
                        }
                        pc.setLocalProperties(prop);

                        // we need to shadow the bean registry on the ZwangineContext with the local beans from the workflow template context
                        if (localBeans != null) {
                            localBeans.setLocalBeanRepository(bbrCopy);
                        }

                        // need to reset auto assigned ids, so there is no clash when creating workflows
                        ProcessorDefinitionHelper.resetAllAutoAssignedNodeIds(workflowDefinition);
                        // must re-init parent when created from a template
                        WorkflowDefinitionHelper.initParent(workflowDefinition);
                    }
                    // Check if the workflow is included
                    if (includedWorkflow(workflowDefinition)) {
                        // must ensure workflow is prepared, before we can start it
                        if (!workflowDefinition.isPrepared()) {
                            WorkflowDefinitionHelper.prepareWorkflow(getZwangineContextReference(), workflowDefinition);
                            workflowDefinition.markPrepared();
                        }
                        // force the creation of ids on all nodes in the workflow
                        WorkflowDefinitionHelper.forceAssignIds(this, workflowDefinition.getInput());
                        WorkflowDefinitionHelper.forceAssignIds(this, workflowDefinition);

                        StartupStepRecorder recorder
                                = getZwangineContextReference().getZwangineContextExtension().getStartupStepRecorder();
                        StartupStep step = recorder.beginStep(Workflow.class, workflowDefinition.getWorkflowId(), "Create Workflow");

                        getZwangineContextExtension().createWorkflow(workflowDefinition.getWorkflowId());

                        Workflow workflow = model.getModelReifierFactory().createWorkflow(this, workflowDefinition);
                        recorder.endStep(step);

                        WorkflowService workflowService = new WorkflowService(workflow);
                        startWorkflowService(workflowService, true);
                    } else {
                        // Add the definition to the list of definitions to remove as the workflow is excluded
                        if (workflowDefinitionsToRemove == null) {
                            workflowDefinitionsToRemove = new ArrayList<>(workflowDefinitions.size());
                        }
                        workflowDefinitionsToRemove.add(workflowDefinition);
                    }
                } finally {
                    // clear local after the workflow is created via the reifier
                    pc.setLocalProperties(null);
                    if (localBeans != null) {
                        localBeans.setLocalBeanRepository(null);
                    }
                }
            }
            if (workflowDefinitionsToRemove != null) {
                // Remove all the excluded workflows
                model.removeWorkflowDefinitions(workflowDefinitionsToRemove);
            }
        } finally {
            if (!alreadyStartingWorkflows) {
                setStartingWorkflows(false);
            }
            getZwangineContextExtension().createWorkflow(null);
        }
    }

    @Override
    protected ExecutorServiceManager createExecutorServiceManager() {
        return new DefaultExecutorServiceManager(this);
    }

    @Override
    public Processor createErrorHandler(Workflow workflow, Processor processor) throws Exception {
        return model.getModelReifierFactory().createErrorHandler(workflow, processor);
    }

    @Override
    public Expression createExpression(ExpressionDefinition definition) {
        return model.getModelReifierFactory().createExpression(this, definition);
    }

    @Override
    public Predicate createPredicate(ExpressionDefinition definition) {
        return model.getModelReifierFactory().createPredicate(this, definition);
    }

    @Override
    public void registerValidator(ValidatorDefinition def) {
        model.getValidators().add(def);
        Validator validator = model.getModelReifierFactory().createValidator(this, def);
        getValidatorRegistry().put(createValidatorKey(def), validator);
    }

    private static ValidatorKey createValidatorKey(ValidatorDefinition def) {
        return new ValidatorKey(new DataType(def.getType()));
    }

    @Override
    public void registerTransformer(TransformerDefinition def) {
        model.getTransformers().add(def);
        Transformer transformer = model.getModelReifierFactory().createTransformer(this, def);
        getTransformerRegistry().put(createTransformerKey(def), transformer);
    }

    @Override
    protected boolean removeWorkflow(String workflowId, LoggingLevel loggingLevel) throws Exception {
        // synchronize on model first to avoid deadlock with concurrent 'addWorkflows' calls:
        synchronized (model) {
            getLock().lock();
            try {
                boolean removed = super.removeWorkflow(workflowId, loggingLevel);
                if (removed) {
                    // must also remove the workflow definition
                    WorkflowDefinition def = getWorkflowDefinition(workflowId);
                    if (def != null) {
                        removeWorkflowDefinition(def);
                    }
                }
                return removed;
            } finally {
                getLock().unlock();
            }
        }
    }

    @Override
    public boolean removeWorkflow(String workflowId) throws Exception {
        // synchronize on model first to avoid deadlock with concurrent 'addWorkflows' calls:
        synchronized (model) {
            return super.removeWorkflow(workflowId);
        }
    }

    /**
     * Indicates whether the workflow should be included according to the precondition.
     *
     * @param  definition the definition of the workflow to check.
     * @return            {@code true} if the workflow should be included, {@code false} otherwise.
     */
    private boolean includedWorkflow(WorkflowDefinition definition) {
        return PreconditionHelper.included(definition, this);
    }

    private static TransformerKey createTransformerKey(TransformerDefinition def) {
        if (ObjectHelper.isNotEmpty(def.getScheme())) {
            return ObjectHelper.isNotEmpty(def.getName())
                    ? new TransformerKey(def.getScheme() + ":" + def.getName()) : new TransformerKey(def.getScheme());
        }
        if (ObjectHelper.isNotEmpty(def.getName())) {
            return new TransformerKey(def.getName());
        } else {
            return new TransformerKey(new DataType(def.getFromType()), new DataType(def.getToType()));
        }
    }

    protected ModelReifierFactory createModelReifierFactory() {
        Optional<ModelReifierFactory> result = ResolverHelper.resolveService(
                this,
                this.getZwangineContextExtension().getBootstrapFactoryFinder(),
                ModelReifierFactory.FACTORY,
                ModelReifierFactory.class);
        return result.orElseGet(DefaultModelReifierFactory::new);
    }

}
