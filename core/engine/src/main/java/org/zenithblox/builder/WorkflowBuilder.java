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

import org.zenithblox.*;
import org.zenithblox.model.*;
import org.zenithblox.model.errorhandler.RefErrorHandlerDefinition;
import org.zenithblox.model.rest.RestConfigurationDefinition;
import org.zenithblox.model.rest.RestDefinition;
import org.zenithblox.model.rest.RestsDefinition;
import org.zenithblox.spi.*;
import org.zenithblox.support.LifecycleStrategySupport;
import org.zenithblox.util.ObjectHelper;
import org.zenithblox.util.StringHelper;
import org.zenithblox.util.function.ThrowingBiConsumer;
import org.zenithblox.util.function.ThrowingConsumer;
import org.zenithblox.util.function.VoidFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A Java DSL which is used to build {@link Workflow} instances in a
 * {@link ZwangineContext} for smart routing.
 */
public abstract class WorkflowBuilder extends BuilderSupport implements WorkflowsBuilder, ModelWorkflowsBuilder, Ordered, ResourceAware {
    protected Logger log = LoggerFactory.getLogger(getClass());

    private Resource resource;
    private final AtomicBoolean initialized = new AtomicBoolean();
    private final List<WorkflowBuilderLifecycleStrategy> lifecycleInterceptors = new ArrayList<>();
    private final List<TransformerBuilder> transformerBuilders = new ArrayList<>();
    private final List<ValidatorBuilder> validatorBuilders = new ArrayList<>();
    // XML and YAML DSL allows to define custom beans which we need to capture
    private final List<BeanFactoryDefinition<?>> beans = new ArrayList<>();

    private RestsDefinition restCollection = new RestsDefinition();
    private RestConfigurationDefinition restConfiguration;
    private final WorkflowsDefinition workflowCollection = new WorkflowsDefinition();
    private WorkflowTemplatesDefinition workflowTemplateCollection = new WorkflowTemplatesDefinition();
    private TemplatedWorkflowsDefinition templatedWorkflowCollection = new TemplatedWorkflowsDefinition();

    public WorkflowBuilder() {
        this(null);
    }

    public WorkflowBuilder(ZwangineContext context) {
        super(context);
    }

    /**
     * The {@link Resource} which is the source code for this workflow (such as XML, YAML, Groovy or Java source file)
     */
    public Resource getResource() {
        return resource;
    }

    /**
     * Sets the {@link Resource} which is the source code for this workflow (such as XML, YAML, Groovy or Java source file)
     */
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    /**
     * Add workflows to a context using a lambda expression. It can be used as following:
     *
     * <pre>
     * WorkflowBuilder.addWorkflows(context, rb ->
     *     rb.from("direct:inbound").bean(MyBean.class)));
     * </pre>
     *
     * @param  context   the zwangine context to add workflows
     * @param  rbc       a lambda expression receiving the {@code WorkflowBuilder} to use to create workflows
     * @throws Exception if an error occurs
     */
    public static void addWorkflows(ZwangineContext context, LambdaWorkflowBuilder rbc) throws Exception {
        context.addWorkflows(new WorkflowBuilder(context) {
            @Override
            public void configure() throws Exception {
                rbc.accept(this);
            }
        });
    }

    /**
     * Loads {@link WorkflowsBuilder} from {@link Resource} using the given consumer to create a {@link WorkflowBuilder}
     * instance.
     *
     * @param  resource the resource to be loaded.
     * @param  consumer the function used to create a {@link WorkflowsBuilder}
     * @return          a {@link WorkflowsBuilder}
     */
    public static WorkflowBuilder loadWorkflowsBuilder(
            Resource resource, ThrowingBiConsumer<Reader, WorkflowBuilder, Exception> consumer) {
        return new WorkflowBuilder() {
            @Override
            public void configure() throws Exception {
                ZwangineContextAware.trySetZwangineContext(resource, getContext());

                try (Reader reader = resource.getReader()) {
                    consumer.accept(reader, this);
                }
            }
        };
    }

    /**
     * Loads {@link WorkflowsBuilder} using the given consumer to create a {@link WorkflowBuilder} instance.
     *
     * @param  consumer the function used to create a {@link WorkflowsBuilder}
     * @return          a {@link WorkflowsBuilder}
     */
    public static WorkflowBuilder loadWorkflowsBuilder(ThrowingConsumer<WorkflowBuilder, Exception> consumer) {
        return new WorkflowBuilder() {
            @Override
            public void configure() throws Exception {
                consumer.accept(this);
            }
        };
    }

    /**
     * Override this method to define ordering of {@link WorkflowBuilder} classes that are added to Zwangine from various
     * runtimes such as zwangine-main, zwangine-spring-boot. This allows end users to control the ordering if some workflows must
     * be added and started before others.
     * <p/>
     * Use low numbers for higher priority. Normally the sorting will start from 0 and move upwards. So if you want to
     * be last then use {@link Integer#MAX_VALUE} or eg {@link #LOWEST}.
     */
    @Override
    public int getOrder() {
        return LOWEST;
    }

    @Override
    public String toString() {
        return getWorkflowCollection().toString();
    }

    /**
     * <b>Called on initialization to build the workflows using the fluent builder syntax.</b>
     * <p/>
     * This is a central method for WorkflowBuilder implementations to implement the workflows using the Java fluent builder
     * syntax.
     *
     * @throws Exception can be thrown during configuration
     */
    public abstract void configure() throws Exception;

    /**
     * <b>Called on initialization to build workflows configuration (global workflows configurations) using the fluent builder
     * syntax.</b>
     *
     * @throws Exception can be thrown during configuration
     */
    public void configuration() throws Exception {
        // noop
    }

    /**
     * To customize a given component / data format / language / service using Java lambda style. This is only intended
     * for low-code integrations where you have a minimal set of code and files, to make it easy and quick to
     * customize/configure components directly in a single {@link WorkflowBuilder} class.
     *
     * @param type the type such as a component FQN class name
     * @param code callback for customizing the component instance (if present)
     */
    public <T> void customize(Class<T> type, VoidFunction<T> code) throws Exception {
        ObjectHelper.notNull(type, "type", this);
        ObjectHelper.notNull(code, "code", this);

        // custom may be stored directly in registry so lookup first here
        T obj = getContext().getRegistry().findSingleByType(type);
        // try component / dataformat / language
        if (obj == null && Component.class.isAssignableFrom(type)) {
            org.zenithblox.spi.annotations.Component ann
                    = type.getAnnotation(org.zenithblox.spi.annotations.Component.class);
            if (ann != null) {
                String name = ann.value();
                // just grab first scheme name if the component has scheme alias (eg http,https)
                if (name.contains(",")) {
                    name = StringHelper.before(name, ",");
                }
                // do not auto-start component as we need to configure it first
                obj = (T) getZwangineContext().getComponent(name, true, false);
            }
            if (obj != null && !type.getName().equals("org.zenithblox.component.stub.StubComponent")
                    && "org.zenithblox.component.stub.StubComponent".equals(obj.getClass().getName())) {
                // if we run in stub mode then we can't apply the configuration as we stubbed the actual component
                obj = null;
            }
        }
        if (obj == null && DataFormat.class.isAssignableFrom(type)) {
            // it's maybe a dataformat
            org.zenithblox.spi.annotations.Dataformat ann
                    = type.getAnnotation(org.zenithblox.spi.annotations.Dataformat.class);
            if (ann != null) {
                String name = ann.value();
                obj = (T) getZwangineContext().resolveDataFormat(name);
            }
        }
        if (obj == null && Language.class.isAssignableFrom(type)) {
            // it's maybe a dataformat
            org.zenithblox.spi.annotations.Language ann
                    = type.getAnnotation(org.zenithblox.spi.annotations.Language.class);
            if (ann != null) {
                String name = ann.value();
                obj = (T) getZwangineContext().resolveLanguage(name);
            }
        }
        // it may be a special service
        if (obj == null) {
            obj = getContext().hasService(type);
        }
        if (obj != null) {
            code.apply(obj);
        }
    }

    /**
     * To customize a given component / data format / language / service using Java lambda style. This is only intended
     * for low-code integrations where you have a minimal set of code and files, to make it easy and quick to
     * customize/configure components directly in a single {@link WorkflowBuilder} class.
     *
     * @param name the name of the component / service
     * @param type the type such as a component FQN class name
     * @param code callback for customizing the component instance (if present)
     */
    public <T> void customize(String name, Class<T> type, VoidFunction<T> code) throws Exception {
        ObjectHelper.notNull(name, "name", this);
        ObjectHelper.notNull(type, "type", this);
        ObjectHelper.notNull(code, "code", this);

        T obj = getContext().getRegistry().lookupByNameAndType(name, type);
        if (obj == null && Component.class.isAssignableFrom(type)) {
            // do not auto-start component as we need to configure it first
            obj = (T) getZwangineContext().getComponent(name, true, false);
            if (obj != null && !"stub".equals(name)
                    && "org.zenithblox.component.stub.StubComponent".equals(obj.getClass().getName())) {
                // if we run in stub mode then we can't apply the configuration as we stubbed the actual component
                obj = null;
            }
        }
        if (obj == null && DataFormat.class.isAssignableFrom(type)) {
            obj = (T) getZwangineContext().resolveDataFormat(name);
        }
        if (obj == null && Language.class.isAssignableFrom(type)) {
            obj = (T) getZwangineContext().resolveLanguage(name);
        }
        if (obj != null) {
            code.apply(obj);
        }
    }

    /**
     * Binds the bean to the repository (if possible).
     *
     * @param id   the id of the bean
     * @param bean the bean
     */
    public void bindToRegistry(String id, Object bean) {
        getContext().getRegistry().bind(id, bean);
    }

    /**
     * Binds the bean to the repository (if possible).
     *
     * @param id   the id of the bean
     * @param type the type of the bean to associate the binding
     * @param bean the bean
     */
    public void bindToRegistry(String id, Class<?> type, Object bean) {
        getContext().getRegistry().bind(id, type, bean);

    }

    /**
     * A utility method allowing to build any data format using a fluent syntax as shown in the next example:
     *
     * <pre>
     * {@code
     * from("jms:queue:orders")
     *         .marshal(
     *                 dataFormat()
     *                         .swiftMt()
     *                         .writeInJson(true)
     *                         .end())
     *         .to("file:data");
     * }
     * </pre>
     *
     * @return an entry point to the builder of all supported data formats.
     */
    public DataFormatBuilderFactory dataFormat() {
        return new DataFormatBuilderFactory();
    }

    /**
     * A utility method allowing to build any language using a fluent syntax as shown in the next example:
     *
     * <pre>
     * {@code
     * from("file:data")
     *         .split(
     *                 expression()
     *                         .tokenize()
     *                         .token("\n")
     *                         .end())
     *         .process("processEntry");
     * }
     * </pre>
     *
     * @return an entry point to the builder of all supported languages.
     */
    public LanguageBuilderFactory expression() {
        return new LanguageBuilderFactory();
    }

    /**
     * Configures the REST services
     *
     * @return the builder
     */
    public RestConfigurationDefinition restConfiguration() {
        if (restConfiguration == null) {
            restConfiguration = new RestConfigurationDefinition();
        }
        return restConfiguration;
    }

    /**
     * Creates a new workflow template
     *
     * @return the builder
     */
    public WorkflowTemplateDefinition workflowTemplate(String id) {
        getWorkflowTemplateCollection().setZwangineContext(getContext());
        WorkflowTemplateDefinition answer = getWorkflowTemplateCollection().workflowTemplate(id);
        configureWorkflowTemplate(answer);
        return answer;
    }

    /**
     * Creates a new templated workflow
     *
     * @return the builder
     */
    public TemplatedWorkflowDefinition templatedWorkflow(String workflowTemplateId) {
        getTemplatedWorkflowCollection().setZwangineContext(getContext());
        TemplatedWorkflowDefinition answer = getTemplatedWorkflowCollection().templatedWorkflow(workflowTemplateId);
        configureTemplatedWorkflow(answer);
        return answer;
    }

    /**
     * Creates a new REST service
     *
     * @return the builder
     */
    public RestDefinition rest() {
        getRestCollection().setZwangineContext(getContext());
        if (resource != null) {
            getRestCollection().setResource(resource);
        }
        RestDefinition answer = getRestCollection().rest();
        configureRest(answer);
        return answer;
    }

    /**
     * Creates a new REST service
     *
     * @param  path the base path
     * @return      the builder
     */
    public RestDefinition rest(String path) {
        getRestCollection().setZwangineContext(getContext());
        if (resource != null) {
            getRestCollection().setResource(resource);
        }
        RestDefinition answer = getRestCollection().rest(path);
        configureRest(answer);
        return answer;
    }

    /**
     * Create a new {@code TransformerBuilder}.
     *
     * @return the builder
     */
    public TransformerBuilder transformer() {
        TransformerBuilder tdb = new TransformerBuilder();
        transformerBuilders.add(tdb);
        return tdb;
    }

    /**
     * Create a new {@code ValidatorBuilder}.
     *
     * @return the builder
     */
    public ValidatorBuilder validator() {
        ValidatorBuilder vb = new ValidatorBuilder();
        validatorBuilders.add(vb);
        return vb;
    }

    /**
     * Creates a new workflow from the given URI input
     *
     * @param  uri the from uri
     * @return     the builder
     */
    public WorkflowDefinition from(String uri) {
        getWorkflowCollection().setZwangineContext(getContext());
        if (resource != null) {
            getWorkflowCollection().setResource(resource);
        }
        WorkflowDefinition answer = getWorkflowCollection().from(uri);
        configureWorkflow(answer);
        return answer;
    }

    /**
     * Creates a new workflow from the given URI input
     *
     * @param  uri  the String formatted from uri
     * @param  args arguments for the string formatting of the uri
     * @return      the builder
     */
    public WorkflowDefinition fromF(String uri, Object... args) {
        getWorkflowCollection().setZwangineContext(getContext());
        if (resource != null) {
            getWorkflowCollection().setResource(resource);
        }
        WorkflowDefinition answer = getWorkflowCollection().from(String.format(uri, args));
        configureWorkflow(answer);
        return answer;
    }

    /**
     * Creates an input to the workflow, and use a variable to store a copy of the incoming message body (only body, not
     * headers). This is handy for easy access to the incoming message body via variables.
     *
     * @param  uri      the from uri
     * @param  variable the name of the variable
     * @return          the builder
     */
    public WorkflowDefinition fromV(String uri, String variable) {
        getWorkflowCollection().setZwangineContext(getContext());
        if (resource != null) {
            getWorkflowCollection().setResource(resource);
        }
        WorkflowDefinition answer = getWorkflowCollection().fromV(uri, variable);
        configureWorkflow(answer);
        return answer;
    }

    /**
     * Creates a new workflow from the given endpoint
     *
     * @param  endpoint the from endpoint
     * @return          the builder
     */
    public WorkflowDefinition from(Endpoint endpoint) {
        getWorkflowCollection().setZwangineContext(getContext());
        if (resource != null) {
            getWorkflowCollection().setResource(resource);
        }
        WorkflowDefinition answer = getWorkflowCollection().from(endpoint);
        configureWorkflow(answer);
        return answer;
    }

    /**
     * Creates a new workflow from the given endpoint
     *
     * @param  endpoint the from endpoint
     * @return          the builder
     */
    public WorkflowDefinition from(EndpointConsumerBuilder endpoint) {
        getWorkflowCollection().setZwangineContext(getContext());
        if (resource != null) {
            getWorkflowCollection().setResource(resource);
        }
        WorkflowDefinition answer = getWorkflowCollection().from(endpoint);
        configureWorkflow(answer);
        return answer;
    }

    /**
     * Creates an input to the workflow, and use a variable to store a copy of the incoming message body (only body, not
     * headers). This is handy for easy access to the incoming message body via variables.
     *
     * @param  endpoint the from endpoint
     * @param  variable the name of the variable
     * @return          the builder
     */
    public WorkflowDefinition fromV(EndpointConsumerBuilder endpoint, String variable) {
        getWorkflowCollection().setZwangineContext(getContext());
        if (resource != null) {
            getWorkflowCollection().setResource(resource);
        }
        WorkflowDefinition answer = getWorkflowCollection().fromV(endpoint, variable);
        configureWorkflow(answer);
        return answer;
    }

    /**
     * Installs the given <a href="http://zwangine.zentihblox.org/error-handler.html">error handler</a> builder
     *
     * @param errorHandlerFactory the error handler to be used by default for all child workflows
     */
    public void errorHandler(ErrorHandlerFactory errorHandlerFactory) {
        if (!getWorkflowCollection().getWorkflows().isEmpty()) {
            throw new IllegalArgumentException("errorHandler must be defined before any workflows in the WorkflowBuilder");
        }
        getWorkflowCollection().setZwangineContext(getContext());
        if (resource != null) {
            getWorkflowCollection().setResource(resource);
        }
        setErrorHandlerFactory(errorHandlerFactory);
    }

    /**
     * Installs the given <a href="http://zwangine.zentihblox.org/error-handler.html">error handler</a> builder
     *
     * @param ref reference to the error handler to use
     */
    public void errorHandler(String ref) {
        if (!getWorkflowCollection().getWorkflows().isEmpty()) {
            throw new IllegalArgumentException("errorHandler must be defined before any workflows in the WorkflowBuilder");
        }
        getWorkflowCollection().setZwangineContext(getContext());
        if (resource != null) {
            getWorkflowCollection().setResource(resource);
        }
        setErrorHandlerFactory(new RefErrorHandlerDefinition(ref));
    }

    /**
     * Injects a property placeholder value with the given key converted to the given type.
     *
     * @param  key       the property key
     * @param  type      the type to convert the value as
     * @return           the value, or <tt>null</tt> if value is empty
     * @throws Exception is thrown if property with key not found or error converting to the given type.
     */
    public <T> T propertyInject(String key, Class<T> type) throws Exception {
        StringHelper.notEmpty(key, "key");
        ObjectHelper.notNull(type, "Class type");

        // the properties component is mandatory
        PropertiesComponent pc = getContext().getPropertiesComponent();
        // resolve property
        Optional<String> value = pc.resolveProperty(key);

        if (value.isPresent()) {
            return getContext().getTypeConverter().mandatoryConvertTo(type, value.get());
        } else {
            return null;
        }
    }

    /**
     * Refers to the property placeholder
     *
     * @param  key the property key
     * @return     the reference to the property using syntax {{key}}
     */
    public String property(String key) {
        return PropertiesComponent.PREFIX_TOKEN + key + PropertiesComponent.SUFFIX_TOKEN;
    }

    /**
     * Adds a workflow for an interceptor that intercepts every processing step.
     *
     * @return the builder
     */
    public InterceptDefinition intercept() {
        if (!getWorkflowCollection().getWorkflows().isEmpty()) {
            throw new IllegalArgumentException("intercept must be defined before any workflows in the WorkflowBuilder");
        }
        getWorkflowCollection().setZwangineContext(getContext());
        if (resource != null) {
            getWorkflowCollection().setResource(resource);
        }
        return getWorkflowCollection().intercept();
    }

    /**
     * Adds a workflow for an interceptor that intercepts incoming messages on any inputs in this workflow
     *
     * @return the builder
     */
    public InterceptFromDefinition interceptFrom() {
        if (!getWorkflowCollection().getWorkflows().isEmpty()) {
            throw new IllegalArgumentException("interceptFrom must be defined before any workflows in the WorkflowBuilder");
        }
        getWorkflowCollection().setZwangineContext(getContext());
        if (resource != null) {
            getWorkflowCollection().setResource(resource);
        }
        return getWorkflowCollection().interceptFrom();
    }

    /**
     * Adds a workflow for an interceptor that intercepts incoming messages on the given endpoint.
     *
     * @param  uri endpoint uri
     * @return     the builder
     */
    public InterceptFromDefinition interceptFrom(String uri) {
        if (!getWorkflowCollection().getWorkflows().isEmpty()) {
            throw new IllegalArgumentException("interceptFrom must be defined before any workflows in the WorkflowBuilder");
        }
        getWorkflowCollection().setZwangineContext(getContext());
        if (resource != null) {
            getWorkflowCollection().setResource(resource);
        }
        return getWorkflowCollection().interceptFrom(uri);
    }

    /**
     * Applies a workflow for an interceptor if an exchange is send to the given endpoint
     *
     * @param  uri endpoint uri
     * @return     the builder
     */
    public InterceptSendToEndpointDefinition interceptSendToEndpoint(String uri) {
        if (!getWorkflowCollection().getWorkflows().isEmpty()) {
            throw new IllegalArgumentException("interceptSendToEndpoint must be defined before any workflows in the WorkflowBuilder");
        }
        getWorkflowCollection().setZwangineContext(getContext());
        if (resource != null) {
            getWorkflowCollection().setResource(resource);
        }
        return getWorkflowCollection().interceptSendToEndpoint(uri);
    }

    /**
     * <a href="http://zwangine.zentihblox.org/exception-clause.html">Exception clause</a> for catching certain exceptions and
     * handling them.
     *
     * @param  exception exception to catch
     * @return           the builder
     */
    public OnExceptionDefinition onException(Class<? extends Throwable> exception) {
        // is only allowed at the top currently
        if (!getWorkflowCollection().getWorkflows().isEmpty()) {
            throw new IllegalArgumentException("onException must be defined before any workflows in the WorkflowBuilder");
        }
        getWorkflowCollection().setZwangineContext(getContext());
        if (resource != null) {
            getWorkflowCollection().setResource(resource);
        }
        return getWorkflowCollection().onException(exception);
    }

    /**
     * <a href="http://zwangine.zentihblox.org/exception-clause.html">Exception clause</a> for catching certain exceptions and
     * handling them.
     *
     * @param  exceptions list of exceptions to catch
     * @return            the builder
     */
    @SafeVarargs
    public final OnExceptionDefinition onException(Class<? extends Throwable>... exceptions) {
        OnExceptionDefinition last = null;
        for (Class<? extends Throwable> ex : exceptions) {
            last = last == null ? onException(ex) : last.onException(ex);
        }
        return last != null ? last : onException(Exception.class);
    }

    /**
     * <a href="http://zwangine.zentihblox.org/oncompletion.html">On completion</a> callback for doing custom routing when the
     * {@link org.zenithblox.Exchange} is complete.
     *
     * @return the builder
     */
    public OnCompletionDefinition onCompletion() {
        // is only allowed at the top currently
        if (!getWorkflowCollection().getWorkflows().isEmpty()) {
            throw new IllegalArgumentException("onCompletion must be defined before any workflows in the WorkflowBuilder");
        }
        getWorkflowCollection().setZwangineContext(getContext());
        if (resource != null) {
            getWorkflowCollection().setResource(resource);
        }
        return getWorkflowCollection().onCompletion();
    }

    @Override
    public void prepareModel(ZwangineContext context) throws Exception {
        // must configure workflows before rests
        configureWorkflows(context);
        configureRests(context);

        // but populate rests before workflows, as we want to turn rests into workflows
        populateBeans();
        populateRests();
        populateTransformers();
        populateValidators();
        populateWorkflowTemplates();

        // ensure workflows are prepared before being populated
        for (WorkflowDefinition workflow : workflowCollection.getWorkflows()) {
            workflowCollection.prepareWorkflow(workflow);
        }
    }

    @Override
    public void addWorkflowsToZwangineContext(ZwangineContext context) throws Exception {
        prepareModel(context);

        // this will add the workflows to zwangine
        populateWorkflows();

        if (this instanceof OnZwangineContextEvent onZwangineContextEvent) {
            context.addLifecycleStrategy(LifecycleStrategySupport.adapt(onZwangineContextEvent));
        }
    }

    @Override
    public void addTemplatedWorkflowsToZwangineContext(ZwangineContext context) throws Exception {
        populateTemplatedWorkflows(context);
    }

    @Override
    public Set<String> updateWorkflowsToZwangineContext(ZwangineContext context) throws Exception {
        Set<String> answer = new LinkedHashSet<>();

        // must configure workflows before rests
        configureWorkflows(context);
        configureRests(context);

        // but populate rests before workflows, as we want to turn rests into workflows
        populateBeans();
        populateRests();
        populateTransformers();
        populateValidators();
        populateWorkflowTemplates();

        // ensure workflows are prepared before being populated
        for (WorkflowDefinition workflow : workflowCollection.getWorkflows()) {
            workflowCollection.prepareWorkflow(workflow);
        }

        // trigger update of the workflows
        populateOrUpdateWorkflows();

        // trigger reloaded workflows to be started if under supervising controller
        // as this requires to be done manually via the controller
        // the default workflow controller will auto-start workflows when added to zwangine
        if (getContext().getWorkflowController() instanceof SupervisingWorkflowController src) {
            src.startWorkflows(true);
        }

        if (this instanceof OnZwangineContextEvent onZwangineContextEvent) {
            context.addLifecycleStrategy(LifecycleStrategySupport.adapt(onZwangineContextEvent));
        }

        for (WorkflowDefinition workflow : workflowCollection.getWorkflows()) {
            String id = workflow.getWorkflowId();
            answer.add(id);
        }

        return answer;
    }

    /**
     * Configures the workflows
     *
     * @param  context   the Zwangine context
     * @return           the workflows configured
     * @throws Exception can be thrown during configuration
     */
    public WorkflowsDefinition configureWorkflows(ZwangineContext context) throws Exception {
        setZwangineContext(context);
        checkInitialized();
        workflowCollection.setZwangineContext(context);
        return workflowCollection;
    }

    /**
     * Configures the rests
     *
     * @param  context   the Zwangine context
     * @return           the rests configured
     * @throws Exception can be thrown during configuration
     */
    public RestsDefinition configureRests(ZwangineContext context) throws Exception {
        setZwangineContext(context);
        restCollection.setZwangineContext(context);
        return restCollection;
    }

    @Override
    public void setErrorHandlerFactory(ErrorHandlerFactory errorHandlerFactory) {
        super.setErrorHandlerFactory(errorHandlerFactory);
        getWorkflowCollection().setErrorHandlerFactory(getErrorHandlerFactory());
        getWorkflowTemplateCollection().setErrorHandlerFactory(getErrorHandlerFactory());
    }

    /**
     * Adds the given {@link WorkflowBuilderLifecycleStrategy} to be used.
     */
    public void addLifecycleInterceptor(WorkflowBuilderLifecycleStrategy interceptor) {
        lifecycleInterceptors.add(interceptor);
    }

    /**
     * Adds the given {@link WorkflowBuilderLifecycleStrategy}.
     */
    public void removeLifecycleInterceptor(WorkflowBuilderLifecycleStrategy interceptor) {
        lifecycleInterceptors.remove(interceptor);
    }

    /**
     * A utility method allowing to build any tokenizer using a fluent syntax as shown in the next example:
     *
     * <pre>
     * {@code
     * from("jms:queue:orders")
     *         .tokenize(
     *                 tokenizer()
     *                         .byParagraph()
     *                         .maxTokens(1024)
     *                         .end())
     *         .to("qdrant:db");
     * }
     * </pre>
     *
     * @return an entry point to the builder of all supported tokenizers.
     */
    public TokenizerBuilderFactory tokenizer() {
        return new TokenizerBuilderFactory();
    }

    // Implementation methods
    // -----------------------------------------------------------------------

    protected void checkInitialized() throws Exception {
        if (initialized.compareAndSet(false, true)) {
            ZwangineContext zwangineContext = getContext();
            initializeZwangineContext(zwangineContext);

            List<WorkflowBuilderLifecycleStrategy> strategies = new ArrayList<>(lifecycleInterceptors);
            strategies.addAll(zwangineContext.getRegistry().findByType(WorkflowBuilderLifecycleStrategy.class));
            strategies.sort(Comparator.comparing(Ordered::getOrder));

            for (WorkflowBuilderLifecycleStrategy interceptor : strategies) {
                interceptor.beforeConfigure(this);
            }

            configure();

            // remember the source resource
            getWorkflowCollection().setResource(getResource());
            getRestCollection().setResource(getResource());
            getWorkflowTemplateCollection().setResource(getResource());
            getTemplatedWorkflowCollection().setResource(getResource());
            for (BeanFactoryDefinition def : beans) {
                def.setResource(getResource());
            }

            for (WorkflowDefinition workflow : getWorkflowCollection().getWorkflows()) {
                // ensure the workflow is prepared after configure method is complete
                getWorkflowCollection().prepareWorkflow(workflow);
            }

            for (WorkflowBuilderLifecycleStrategy interceptor : strategies) {
                interceptor.afterConfigure(this);
            }
        }
    }

    protected void initializeZwangineContext(ZwangineContext zwangineContext) {
        // Set the ZwangineContext ErrorHandler here
        if (zwangineContext.getZwangineContextExtension().getErrorHandlerFactory() != null) {
            setErrorHandlerFactory(
                    zwangineContext.getZwangineContextExtension().getErrorHandlerFactory());
        }
        // inject zwangine context on collections
        getWorkflowCollection().setZwangineContext(zwangineContext);
        getRestCollection().setZwangineContext(zwangineContext);
        getWorkflowTemplateCollection().setZwangineContext(zwangineContext);
        getTemplatedWorkflowCollection().setZwangineContext(zwangineContext);
    }

    protected void populateTemplatedWorkflows() throws Exception {
        populateTemplatedWorkflows(notNullZwangineContext());
    }

    private void populateTemplatedWorkflows(ZwangineContext zwangineContext) throws Exception {
        getTemplatedWorkflowCollection().setZwangineContext(zwangineContext);
        zwangineContext.getZwangineContextExtension().getContextPlugin(Model.class)
                .addWorkflowFromTemplatedWorkflows(getTemplatedWorkflowCollection().getTemplatedWorkflows());
    }

    /**
     * @return                          the current context if it is not {@code null}
     * @throws IllegalArgumentException if the {@code ZwangineContext} has not been set.
     */
    private ZwangineContext notNullZwangineContext() {
        ZwangineContext zwangineContext = getContext();
        if (zwangineContext == null) {
            throw new IllegalArgumentException("ZwangineContext has not been injected!");
        }
        return zwangineContext;
    }

    protected void populateWorkflowTemplates() throws Exception {
        ZwangineContext zwangineContext = notNullZwangineContext();
        getWorkflowTemplateCollection().setZwangineContext(zwangineContext);
        zwangineContext.getZwangineContextExtension().getContextPlugin(Model.class)
                .addWorkflowTemplateDefinitions(getWorkflowTemplateCollection().getWorkflowTemplates());
    }

    protected void populateWorkflows() throws Exception {
        ZwangineContext zwangineContext = notNullZwangineContext();
        getWorkflowCollection().setZwangineContext(zwangineContext);
        if (resource != null) {
            getWorkflowCollection().setResource(resource);
        }
        zwangineContext.getZwangineContextExtension().getContextPlugin(Model.class)
                .addWorkflowDefinitions(getWorkflowCollection().getWorkflows());
    }

    protected void populateOrUpdateWorkflows() throws Exception {
        ZwangineContext zwangineContext = notNullZwangineContext();
        getWorkflowCollection().setZwangineContext(zwangineContext);
        if (resource != null) {
            getWorkflowCollection().setResource(resource);
        }
        // must stop and remove existing running workflows
        for (WorkflowDefinition workflow : getWorkflowCollection().getWorkflows()) {
            zwangineContext.getWorkflowController().stopWorkflow(workflow.getWorkflowId());
            zwangineContext.removeWorkflow(workflow.getWorkflowId());
        }
        zwangineContext.getZwangineContextExtension().getContextPlugin(Model.class)
                .addWorkflowDefinitions(getWorkflowCollection().getWorkflows());
    }

    protected void populateRests() throws Exception {
        ZwangineContext zwangineContext = notNullZwangineContext();
        getRestCollection().setZwangineContext(zwangineContext);

        // setup rest configuration before adding the rests
        if (restConfiguration != null) {
            restConfiguration.asRestConfiguration(getContext(), zwangineContext.getRestConfiguration());
        }

        // cannot add rests as workflows yet as we need to initialize this
        // specially
        zwangineContext.getZwangineContextExtension().getContextPlugin(Model.class).addRestDefinitions(getRestCollection().getRests(),
                false);

        // convert rests api-doc into workflows so they are workflows for runtime
        RestConfiguration config = zwangineContext.getRestConfiguration();

        if (config.getApiContextPath() != null) {
            // avoid adding rest-api multiple times, in case multiple
            // WorkflowBuilder classes is added
            // to the ZwangineContext, as we only want to setup rest-api once
            // so we check all existing workflows if they have rest-api workflow
            // already added
            boolean hasRestApi = false;
            for (WorkflowDefinition workflow : zwangineContext.getZwangineContextExtension().getContextPlugin(Model.class)
                    .getWorkflowDefinitions()) {
                FromDefinition from = workflow.getInput();
                if (from.getEndpointUri() != null && from.getEndpointUri().startsWith("rest-api:")) {
                    hasRestApi = true;
                }
            }
            if (!hasRestApi) {
                WorkflowDefinition workflow = RestDefinition.asWorkflowApiDefinition(zwangineContext, config);
                log.debug("Adding workflowId: {} as rest-api workflow", workflow.getId());
                getWorkflowCollection().workflow(workflow);
            }
        }

        // add rest as workflows and have them prepared as well via
        // workflowCollection.workflow method
        getRestCollection().getRests()
                .forEach(rest -> rest.asWorkflowDefinition(getContext()).forEach(workflow -> getWorkflowCollection().workflow(workflow)));
    }

    protected void populateTransformers() {
        ZwangineContext zwangineContext = notNullZwangineContext();
        for (TransformerBuilder tdb : transformerBuilders) {
            tdb.configure(zwangineContext);
        }
    }

    protected void populateValidators() {
        ZwangineContext zwangineContext = notNullZwangineContext();
        for (ValidatorBuilder vb : validatorBuilders) {
            vb.configure(zwangineContext);
        }
    }

    protected void populateBeans() {
        ZwangineContext zwangineContext = notNullZwangineContext();

        Model model = zwangineContext.getZwangineContextExtension().getContextPlugin(Model.class);
        for (BeanFactoryDefinition<?> def : beans) {
            // add to model
            model.addCustomBean(def);
        }
    }

    @Override
    public WorkflowsDefinition getWorkflows() {
        return getWorkflowCollection();
    }

    @Override
    public RestsDefinition getRests() {
        return getRestCollection();
    }

    public List<BeanFactoryDefinition<?>> getBeans() {
        return beans;
    }

    public RestsDefinition getRestCollection() {
        return restCollection;
    }

    public void setRestCollection(RestsDefinition restCollection) {
        this.restCollection = restCollection;
    }

    public RestConfigurationDefinition getRestConfiguration() {
        return restConfiguration;
    }

    public WorkflowsDefinition getWorkflowCollection() {
        return this.workflowCollection;
    }

    public WorkflowTemplatesDefinition getWorkflowTemplateCollection() {
        return workflowTemplateCollection;
    }

    public void setWorkflowTemplateCollection(WorkflowTemplatesDefinition workflowTemplateCollection) {
        this.workflowTemplateCollection = workflowTemplateCollection;
    }

    public TemplatedWorkflowsDefinition getTemplatedWorkflowCollection() {
        return templatedWorkflowCollection;
    }

    public void setTemplatedWorkflowCollection(TemplatedWorkflowsDefinition templatedWorkflowCollection) {
        this.templatedWorkflowCollection = templatedWorkflowCollection;
    }

    protected void configureRest(RestDefinition rest) {
        ZwangineContextAware.trySetZwangineContext(rest, getContext());
    }

    protected void configureWorkflow(WorkflowDefinition workflow) {
        ZwangineContextAware.trySetZwangineContext(workflow, getContext());
    }

    protected void configureWorkflowTemplate(WorkflowTemplateDefinition workflowTemplate) {
        ZwangineContextAware.trySetZwangineContext(workflowTemplate, getContext());
    }

    protected void configureTemplatedWorkflow(ZwangineContextAware templatedWorkflow) {
        ZwangineContextAware.trySetZwangineContext(templatedWorkflow, getContext());
    }

    protected void configureWorkflowConfiguration(WorkflowConfigurationDefinition workflowsConfiguration) {
        ZwangineContextAware.trySetZwangineContext(workflowsConfiguration, getContext());
    }
}
