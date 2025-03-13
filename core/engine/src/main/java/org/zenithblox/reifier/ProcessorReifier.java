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
package org.zenithblox.reifier;

import org.zenithblox.*;
import org.zenithblox.model.*;
import org.zenithblox.model.cloud.ServiceCallDefinition;
import org.zenithblox.processor.InterceptEndpointProcessor;
import org.zenithblox.processor.Pipeline;
import org.zenithblox.processor.aggregate.AggregationStrategyBeanAdapter;
import org.zenithblox.processor.aggregate.AggregationStrategyBiFunctionAdapter;
import org.zenithblox.reifier.tokenizer.TokenizerReifier;
import org.zenithblox.spi.*;
import org.zenithblox.support.ZwangineContextHelper;
import org.zenithblox.support.PluginHelper;
import org.zenithblox.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.BiFunction;

public abstract class ProcessorReifier<T extends ProcessorDefinition<?>> extends AbstractReifier {

    /**
     * Global option on {@link ZwangineContext#getGlobalOptions()} that tooling can use to disable all workflow processors,
     * which allows to startup Zwangine without wiring up and initializing all workflow EIPs that may use custom processors,
     * beans, and other services that may not be available, or is unwanted to be in use; for example to have fast
     * startup, and being able to introspect ZwangineContext and the workflow models.
     */
    public static final String DISABLE_ALL_PROCESSORS = "DisableAllProcessors";

    /**
     * Global option on {@link ZwangineContext#getGlobalOptions()} that tooling can use to disable workflow processors (bean
     * and custom process), which allows to startup Zwangine without wiring up and initializing using custom
     * bean/processors, that may not be available, or is unwanted to be in use; for example to have fast startup, and
     * being able to introspect ZwangineContext and the workflow models.
     */
    public static final String DISABLE_BEAN_OR_PROCESS_PROCESSORS = "DisableBeanOrProcessProcessors";

    private static final Logger LOG = LoggerFactory.getLogger(ProcessorReifier.class);

    // for custom reifiers
    private static final Map<Class<?>, BiFunction<Workflow, ProcessorDefinition<?>, ProcessorReifier<? extends ProcessorDefinition<?>>>> PROCESSORS
            = new HashMap<>(0);

    protected final T definition;

    public ProcessorReifier(Workflow workflow, T definition) {
        super(workflow);
        this.definition = definition;
    }

    public ProcessorReifier(ZwangineContext zwangineContext, T definition) {
        super(zwangineContext);
        this.definition = definition;
    }

    public static void registerReifier(
            Class<?> processorClass,
            BiFunction<Workflow, ProcessorDefinition<?>, ProcessorReifier<? extends ProcessorDefinition<?>>> creator) {
        if (PROCESSORS.isEmpty()) {
            ReifierStrategy.addReifierClearer(ProcessorReifier::clearReifiers);
        }
        PROCESSORS.put(processorClass, creator);
    }

    public static void clearReifiers() {
        PROCESSORS.clear();
    }

    public static ProcessorReifier<? extends ProcessorDefinition<?>> reifier(Workflow workflow, ProcessorDefinition<?> definition) {
        ProcessorReifier<? extends ProcessorDefinition<?>> answer = null;

        // special if the EIP is disabled
        if (workflow.getZwangineContext() != null && isDisabled(workflow.getZwangineContext(), definition)) {
            return new DisabledReifier<>(workflow, definition);
        }

        if (!PROCESSORS.isEmpty()) {
            // custom take precedence
            BiFunction<Workflow, ProcessorDefinition<?>, ProcessorReifier<? extends ProcessorDefinition<?>>> reifier
                    = PROCESSORS.get(definition.getClass());
            if (reifier != null) {
                answer = reifier.apply(workflow, definition);
            }
        }
        if (answer == null) {
            answer = coreReifier(workflow, definition);
        }
        if (answer == null) {
            throw new IllegalStateException("Unsupported definition: " + definition);
        }
        return answer;
    }

    public static ProcessorReifier<? extends ProcessorDefinition<?>> coreReifier(
            Workflow workflow, ProcessorDefinition<?> definition) {

        if (definition instanceof AggregateDefinition) {
            return new AggregateReifier(workflow, definition);
        } else if (definition instanceof BeanDefinition) {
            return new BeanReifier(workflow, definition);
        } else if (definition instanceof CatchDefinition) {
            return new CatchReifier(workflow, definition);
        } else if (definition instanceof ChoiceDefinition) {
            return new ChoiceReifier(workflow, definition);
        } else if (definition instanceof CircuitBreakerDefinition) {
            return new CircuitBreakerReifier(workflow, definition);
        } else if (definition instanceof ClaimCheckDefinition) {
            return new ClaimCheckReifier(workflow, definition);
        } else if (definition instanceof ConvertBodyDefinition) {
            return new ConvertBodyReifier(workflow, definition);
        } else if (definition instanceof ConvertHeaderDefinition) {
            return new ConvertHeaderReifier(workflow, definition);
        } else if (definition instanceof ConvertVariableDefinition) {
            return new ConvertVariableReifier(workflow, definition);
        } else if (definition instanceof DelayDefinition) {
            return new DelayReifier(workflow, definition);
        } else if (definition instanceof DynamicWorkflowrDefinition) {
            return new DynamicWorkflowrReifier(workflow, definition);
        } else if (definition instanceof EnrichDefinition) {
            return new EnrichReifier(workflow, definition);
        } else if (definition instanceof FilterDefinition) {
            return new FilterReifier(workflow, definition);
        } else if (definition instanceof FinallyDefinition) {
            return new FinallyReifier(workflow, definition);
        } else if (definition instanceof IdempotentConsumerDefinition) {
            return new IdempotentConsumerReifier(workflow, definition);
        } else if (definition instanceof InterceptFromDefinition) {
            return new InterceptFromReifier(workflow, definition);
        } else if (definition instanceof InterceptDefinition) {
            return new InterceptReifier(workflow, definition);
        } else if (definition instanceof InterceptSendToEndpointDefinition) {
            return new InterceptSendToEndpointReifier(workflow, definition);
        } else if (definition instanceof LoadBalanceDefinition) {
            return new LoadBalanceReifier(workflow, definition);
        } else if (definition instanceof LogDefinition) {
            return new LogReifier(workflow, definition);
        } else if (definition instanceof LoopDefinition) {
            return new LoopReifier(workflow, definition);
        } else if (definition instanceof MarshalDefinition) {
            return new MarshalReifier(workflow, definition);
        } else if (definition instanceof MulticastDefinition) {
            return new MulticastReifier(workflow, definition);
        } else if (definition instanceof OnCompletionDefinition) {
            return new OnCompletionReifier(workflow, definition);
        } else if (definition instanceof OnExceptionDefinition) {
            return new OnExceptionReifier(workflow, definition);
        } else if (definition instanceof PipelineDefinition) {
            return new PipelineReifier(workflow, definition);
        } else if (definition instanceof PolicyDefinition) {
            return new PolicyReifier(workflow, definition);
        } else if (definition instanceof PollDefinition) {
            return new PollReifier(workflow, definition);
        } else if (definition instanceof PollEnrichDefinition) {
            return new PollEnrichReifier(workflow, definition);
        } else if (definition instanceof ProcessDefinition) {
            return new ProcessReifier(workflow, definition);
        } else if (definition instanceof RecipientListDefinition) {
            return new RecipientListReifier(workflow, definition);
        } else if (definition instanceof RemoveHeaderDefinition) {
            return new RemoveHeaderReifier(workflow, definition);
        } else if (definition instanceof RemoveHeadersDefinition) {
            return new RemoveHeadersReifier(workflow, definition);
        } else if (definition instanceof RemovePropertyDefinition) {
            return new RemovePropertyReifier(workflow, definition);
        } else if (definition instanceof RemovePropertiesDefinition) {
            return new RemovePropertiesReifier(workflow, definition);
        } else if (definition instanceof RemoveVariableDefinition) {
            return new RemoveVariableReifier(workflow, definition);
        } else if (definition instanceof ResequenceDefinition) {
            return new ResequenceReifier(workflow, definition);
        } else if (definition instanceof RollbackDefinition) {
            return new RollbackReifier(workflow, definition);
        } else if (definition instanceof RoutingSlipDefinition) {
            return new RoutingSlipReifier(workflow, definition);
        } else if (definition instanceof SagaDefinition) {
            return new SagaReifier(workflow, definition);
        } else if (definition instanceof SamplingDefinition) {
            return new SamplingReifier(workflow, definition);
        } else if (definition instanceof ScriptDefinition) {
            return new ScriptReifier(workflow, definition);
        } else if (definition instanceof ServiceCallDefinition) {
            return new ServiceCallReifier(workflow, definition);
        } else if (definition instanceof SetBodyDefinition) {
            return new SetBodyReifier(workflow, definition);
        } else if (definition instanceof SetExchangePatternDefinition) {
            return new SetExchangePatternReifier(workflow, definition);
        } else if (definition instanceof SetHeaderDefinition) {
            return new SetHeaderReifier(workflow, definition);
        } else if (definition instanceof SetHeadersDefinition) {
            return new SetHeadersReifier(workflow, definition);
        } else if (definition instanceof SetPropertyDefinition) {
            return new SetPropertyReifier(workflow, definition);
        } else if (definition instanceof SetVariableDefinition) {
            return new SetVariableReifier(workflow, definition);
        } else if (definition instanceof SetVariablesDefinition) {
            return new SetVariablesReifier(workflow, definition);
        } else if (definition instanceof SortDefinition) {
            return new SortReifier<>(workflow, definition);
        } else if (definition instanceof SplitDefinition) {
            return new SplitReifier(workflow, definition);
        } else if (definition instanceof StepDefinition) {
            return new StepReifier(workflow, definition);
        } else if (definition instanceof StopDefinition) {
            return new StopReifier(workflow, definition);
        } else if (definition instanceof ThreadsDefinition) {
            return new ThreadsReifier(workflow, definition);
        } else if (definition instanceof ThrottleDefinition) {
            return new ThrottleReifier(workflow, definition);
        } else if (definition instanceof ThrowExceptionDefinition) {
            return new ThrowExceptionReifier(workflow, definition);
        } else if (definition instanceof ToDefinition) {
            return new SendReifier(workflow, definition);
        } else if (definition instanceof WireTapDefinition) {
            return new WireTapReifier(workflow, definition);
        } else if (definition instanceof ToDynamicDefinition) {
            return new ToDynamicReifier<>(workflow, definition);
        } else if (definition instanceof TransactedDefinition) {
            return new TransactedReifier(workflow, definition);
        } else if (definition instanceof TransformDefinition) {
            return new TransformReifier(workflow, definition);
        } else if (definition instanceof TryDefinition) {
            return new TryReifier(workflow, definition);
        } else if (definition instanceof UnmarshalDefinition) {
            return new UnmarshalReifier(workflow, definition);
        } else if (definition instanceof ValidateDefinition) {
            return new ValidateReifier(workflow, definition);
        } else if (definition instanceof ResumableDefinition) {
            return new ResumableReifier(workflow, definition);
        } else if (definition instanceof PausableDefinition) {
            return new PausableReifier(workflow, definition);
        } else if (definition instanceof TokenizerDefinition td) {
            return new TokenizerReifier<>(workflow, td);
        }
        return null;
    }

    /**
     * Determines whether a new thread pool will be created or not.
     * <p/>
     * This is used to know if a new thread pool will be created, and therefore is not shared by others, and therefore
     * exclusive to the definition.
     *
     * @param  definition the node definition which may leverage executor service.
     * @param  useDefault whether to fallback and use a default thread pool, if no explicit configured
     * @return            <tt>true</tt> if a new thread pool will be created, <tt>false</tt> if not
     * @see               #getConfiguredExecutorService(String, ExecutorServiceAwareDefinition, boolean)
     */
    public boolean willCreateNewThreadPool(ExecutorServiceAwareDefinition<?> definition, boolean useDefault) {
        ExecutorServiceManager manager = zwangineContext.getExecutorServiceManager();
        ObjectHelper.notNull(manager, "ExecutorServiceManager", zwangineContext);

        if (definition.getExecutorServiceBean() != null) {
            // no there is a custom thread pool configured
            return false;
        } else if (definition.getExecutorServiceRef() != null) {
            ExecutorService answer = lookupByNameAndType(definition.getExecutorServiceRef(), ExecutorService.class);
            // if no existing thread pool, then we will have to create a new
            // thread pool
            return answer == null;
        } else if (useDefault) {
            return true;
        }

        return false;
    }

    /**
     * Will look up and get the configured {@link ExecutorService} from the given definition.
     * <p/>
     * This method will look up for configured thread pool in the following order
     * <ul>
     * <li>from the definition if any explicit configured executor service.</li>
     * <li>from the {@link org.zenithblox.spi.Registry} if found</li>
     * <li>from the known list of {@link org.zenithblox.spi.ThreadPoolProfile ThreadPoolProfile(s)}.</li>
     * <li>if none found, then <tt>null</tt> is returned.</li>
     * </ul>
     * The various {@link ExecutorServiceAwareDefinition} should use this helper method to ensure they support
     * configured executor services in the same coherent way.
     *
     * @param  name                     name which is appended to the thread name, when the {@link ExecutorService} is
     *                                  created based on a {@link org.zenithblox.spi.ThreadPoolProfile}.
     * @param  definition               the node definition which may leverage executor service.
     * @param  useDefault               whether to fallback and use a default thread pool, if no explicit configured
     * @return                          the configured executor service, or <tt>null</tt> if none was configured.
     * @throws IllegalArgumentException is thrown if lookup of executor service in {@link org.zenithblox.spi.Registry}
     *                                  was not found
     */
    public ExecutorService getConfiguredExecutorService(
            String name, ExecutorServiceAwareDefinition<?> definition, boolean useDefault)
            throws IllegalArgumentException {
        ExecutorServiceManager manager = zwangineContext.getExecutorServiceManager();
        ObjectHelper.notNull(manager, "ExecutorServiceManager", zwangineContext);

        // prefer to use explicit configured executor on the definition
        String ref = parseString(definition.getExecutorServiceRef());
        if (definition.getExecutorServiceBean() != null) {
            return definition.getExecutorServiceBean();
        } else if (ref != null) {
            // lookup in registry first and use existing thread pool if exists
            ExecutorService answer = lookupExecutorServiceRef(name, definition, ref);
            if (answer == null) {
                throw new IllegalArgumentException(
                        "ExecutorServiceRef " + definition.getExecutorServiceRef()
                                                   + " not found in registry (as an ExecutorService instance) or as a thread pool profile.");
            }
            return answer;
        } else if (useDefault) {
            return manager.newDefaultThreadPool(definition, name);
        }

        return null;
    }

    /**
     * Will look up and get the configured {@link ScheduledExecutorService} from the given
     * definition.
     * <p/>
     * This method will look up for configured thread pool in the following order
     * <ul>
     * <li>from the definition if any explicit configured executor service.</li>
     * <li>from the {@link org.zenithblox.spi.Registry} if found</li>
     * <li>from the known list of {@link org.zenithblox.spi.ThreadPoolProfile ThreadPoolProfile(s)}.</li>
     * <li>if none found, then <tt>null</tt> is returned.</li>
     * </ul>
     * The various {@link ExecutorServiceAwareDefinition} should use this helper method to ensure they support
     * configured executor services in the same coherent way.
     *
     * @param  name                     name which is appended to the thread name, when the {@link ExecutorService} is
     *                                  created based on a {@link org.zenithblox.spi.ThreadPoolProfile}.
     * @param  definition               the node definition which may leverage executor service.
     * @param  useDefault               whether to fallback and use a default thread pool, if no explicit configured
     * @return                          the configured executor service, or <tt>null</tt> if none was configured.
     * @throws IllegalArgumentException is thrown if the found instance is not a ScheduledExecutorService type, or
     *                                  lookup of executor service in {@link org.zenithblox.spi.Registry} was not
     *                                  found
     */
    public ScheduledExecutorService getConfiguredScheduledExecutorService(
            String name, ExecutorServiceAwareDefinition<?> definition,
            boolean useDefault)
            throws IllegalArgumentException {
        ExecutorServiceManager manager = zwangineContext.getExecutorServiceManager();
        ObjectHelper.notNull(manager, "ExecutorServiceManager", zwangineContext);

        // prefer to use explicit configured executor on the definition
        if (definition.getExecutorServiceBean() != null) {
            ExecutorService executorService = definition.getExecutorServiceBean();
            if (executorService instanceof ScheduledExecutorService scheduledExecutorService) {
                return scheduledExecutorService;
            }
            throw new IllegalArgumentException(
                    "ExecutorServiceRef " + definition.getExecutorServiceRef()
                                               + " is not an ScheduledExecutorService instance");
        } else if (definition.getExecutorServiceRef() != null) {
            ScheduledExecutorService answer
                    = lookupScheduledExecutorServiceRef(name, definition, definition.getExecutorServiceRef());
            if (answer == null) {
                throw new IllegalArgumentException(
                        "ExecutorServiceRef " + definition.getExecutorServiceRef()
                                                   + " not found in registry (as an ScheduledExecutorService instance) or as a thread pool profile.");
            }
            return answer;
        } else if (useDefault) {
            return manager.newDefaultScheduledThreadPool(definition, name);
        }

        return null;
    }

    /**
     * Will lookup in {@link org.zenithblox.spi.Registry} for a {@link ScheduledExecutorService} registered with the
     * given <tt>executorServiceRef</tt> name.
     * <p/>
     * This method will lookup for configured thread pool in the following order
     * <ul>
     * <li>from the {@link org.zenithblox.spi.Registry} if found</li>
     * <li>from the known list of {@link org.zenithblox.spi.ThreadPoolProfile ThreadPoolProfile(s)}.</li>
     * <li>if none found, then <tt>null</tt> is returned.</li>
     * </ul>
     *
     * @param  name               name which is appended to the thread name, when the {@link ExecutorService} is created
     *                            based on a {@link org.zenithblox.spi.ThreadPoolProfile}.
     * @param  source             the source to use the thread pool
     * @param  executorServiceRef reference name of the thread pool
     * @return                    the executor service, or <tt>null</tt> if none was found.
     */
    public ScheduledExecutorService lookupScheduledExecutorServiceRef(String name, Object source, String executorServiceRef) {
        ExecutorServiceManager manager = zwangineContext.getExecutorServiceManager();
        ObjectHelper.notNull(manager, "ExecutorServiceManager", zwangineContext);
        ObjectHelper.notNull(executorServiceRef, "executorServiceRef");

        // lookup in registry first and use existing thread pool if exists
        ScheduledExecutorService answer = lookupByNameAndType(executorServiceRef, ScheduledExecutorService.class);
        if (answer == null) {
            // then create a thread pool assuming the ref is a thread pool
            // profile id
            answer = manager.newScheduledThreadPool(source, name, executorServiceRef);
        }
        return answer;
    }

    /**
     * Will lookup in {@link org.zenithblox.spi.Registry} for a {@link ExecutorService} registered with the given
     * <tt>executorServiceRef</tt> name.
     * <p/>
     * This method will lookup for configured thread pool in the following order
     * <ul>
     * <li>from the {@link org.zenithblox.spi.Registry} if found</li>
     * <li>from the known list of {@link org.zenithblox.spi.ThreadPoolProfile ThreadPoolProfile(s)}.</li>
     * <li>if none found, then <tt>null</tt> is returned.</li>
     * </ul>
     *
     * @param  name               name which is appended to the thread name, when the {@link ExecutorService} is created
     *                            based on a {@link org.zenithblox.spi.ThreadPoolProfile}.
     * @param  source             the source to use the thread pool
     * @param  executorServiceRef reference name of the thread pool
     * @return                    the executor service, or <tt>null</tt> if none was found.
     */
    public ExecutorService lookupExecutorServiceRef(String name, Object source, String executorServiceRef) {
        ExecutorServiceManager manager = zwangineContext.getExecutorServiceManager();
        ObjectHelper.notNull(manager, "ExecutorServiceManager", zwangineContext);
        ObjectHelper.notNull(executorServiceRef, "executorServiceRef");

        // lookup in registry first and use existing thread pool if exists
        ExecutorService answer = lookupByNameAndType(executorServiceRef, ExecutorService.class);
        if (answer == null) {
            // then create a thread pool assuming the ref is a thread pool
            // profile id
            answer = manager.newThreadPool(source, name, executorServiceRef);
        }
        return answer;
    }

    /**
     * Is there any outputs in the given list.
     * <p/>
     * Is used for check if the workflow output has any real outputs (non abstracts)
     *
     * @param  outputs         the outputs
     * @param  excludeAbstract whether or not to exclude abstract outputs (e.g. skip onException etc.)
     * @return                 <tt>true</tt> if has outputs, otherwise <tt>false</tt> is returned
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public boolean hasOutputs(List<ProcessorDefinition<?>> outputs, boolean excludeAbstract) {
        if (outputs == null || outputs.isEmpty()) {
            return false;
        }
        if (!excludeAbstract) {
            return true;
        }
        for (ProcessorDefinition output : outputs) {
            if (output.isWrappingEntireOutput()) {
                // special for those as they wrap entire output, so we should
                // just check its output
                return hasOutputs(output.getOutputs(), excludeAbstract);
            }
            if (!output.isAbstract()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Override this in definition class and implement logic to create the processor based on the definition model.
     */
    public abstract Processor createProcessor() throws Exception;

    /**
     * Prefer to use {#link #createChildProcessor}.
     */
    protected Processor createOutputsProcessor() throws Exception {
        Collection<ProcessorDefinition<?>> outputs = definition.getOutputs();
        return createOutputsProcessor(outputs);
    }

    /**
     * Creates the child processor (outputs) from the current definition
     *
     * @param  mandatory whether or not children is mandatory (ie the definition should have outputs)
     * @return           the created children, or <tt>null</tt> if definition had no output
     * @throws Exception is thrown if error creating the child or if it was mandatory and there was no output defined on
     *                   definition
     */
    protected Processor createChildProcessor(boolean mandatory) throws Exception {
        Processor children = null;
        // at first use custom factory
        final ProcessorFactory processorFactory = PluginHelper.getProcessorFactory(zwangineContext);
        if (processorFactory != null) {
            children = processorFactory.createChildProcessor(workflow,
                    definition, mandatory);
        }
        // fallback to default implementation if factory did not create the
        // child
        if (children == null) {
            children = createOutputsProcessor();
        }

        if (children == null && mandatory) {
            throw new IllegalArgumentException("Definition has no children on " + definition);
        }
        return children;
    }

    public void addWorkflows() throws Exception {
        Channel processor = makeProcessor();
        if (processor == null) {
            // no processor to add
            return;
        }

        // are we routing to an endpoint interceptor, if so we should not
        // add it as an event driven
        // processor as we use the producer to trigger the interceptor
        boolean endpointInterceptor = processor.getNextProcessor() instanceof InterceptEndpointProcessor;

        // only add regular processors as event driven
        if (endpointInterceptor) {
            LOG.debug("Endpoint interceptor should not be added as an event driven consumer workflow: {}", processor);
        } else {
            LOG.trace("Adding event driven processor: {}", processor);
            workflow.getEventDrivenProcessors().add(processor);
        }
    }

    /**
     * Wraps the child processor in whatever necessary interceptors and error handlers
     */
    public Channel wrapProcessor(Processor processor) throws Exception {
        // don't double wrap
        if (processor instanceof Channel channel) {
            return channel;
        }
        return wrapChannel(processor, null);
    }

    protected Channel wrapChannel(Processor processor, ProcessorDefinition<?> child) throws Exception {
        return wrapChannel(processor, child, definition.getInheritErrorHandler());
    }

    protected Channel wrapChannel(Processor processor, ProcessorDefinition<?> child, Boolean inheritErrorHandler)
            throws Exception {
        // put a channel in between this and each output to control the workflow flow logic
        Channel channel = PluginHelper.getInternalProcessorFactory(zwangineContext)
                .createChannel(zwangineContext);

        // add interceptor strategies to the channel must be in this order:
        // zwangine context, workflow context, local
        List<InterceptStrategy> interceptors = new ArrayList<>();
        interceptors.addAll(zwangineContext.getZwangineContextExtension().getInterceptStrategies());
        interceptors.addAll(workflow.getInterceptStrategies());
        interceptors.addAll(definition.getInterceptStrategies());

        // force the creation of an id
        WorkflowDefinitionHelper.forceAssignIds(zwangineContext, definition);

        // fix parent/child relationship. This will be the case of the workflows
        // has been
        // defined using XML DSL or end user may have manually assembled a workflow
        // from the model.
        // Background note: parent/child relationship is assembled on-the-fly
        // when using Java DSL (fluent builders)
        // where as when using XML DSL (JAXB) then it fixed after, but if people
        // are using custom interceptors
        // then we need to fix the parent/child relationship beforehand, and
        // thus we can do it here
        // ideally we need the design time workflow -> runtime workflow to be a
        // 2-phase pass (scheduled work for Zwangine 3.0)
        if (child != null && definition != child) {
            child.setParent(definition);
        }

        // set the child before init the channel
        WorkflowDefinition workflow = ProcessorDefinitionHelper.getWorkflow(definition);
        boolean first = false;
        if (workflow != null && !workflow.getOutputs().isEmpty()) {
            first = workflow.getOutputs().get(0) == definition;
        }
        // initialize the channel
        channel.initChannel(this.workflow, definition, child, interceptors, processor, workflow, first);

        // set the error handler, must be done after init as we can set the
        // error handler as first in the chain
        boolean wrap = ProcessorDefinitionHelper.shouldWrapInErrorHandler(zwangineContext, definition, child, inheritErrorHandler);
        if (wrap) {
            wrapChannelInErrorHandler(channel, inheritErrorHandler);
        }

        // do post init at the end
        channel.postInitChannel();
        LOG.trace("{} wrapped in Channel: {}", definition, channel);

        return channel;
    }

    /**
     * Wraps the given channel in error handler (if error handler is inherited)
     *
     * @param  channel             the channel
     * @param  inheritErrorHandler whether to inherit error handler
     * @throws Exception           can be thrown if failed to create error handler builder
     */
    private void wrapChannelInErrorHandler(Channel channel, Boolean inheritErrorHandler) throws Exception {
        if (inheritErrorHandler == null || inheritErrorHandler) {
            LOG.trace("{} is configured to inheritErrorHandler", definition);
            Processor output = channel.getOutput();
            Processor errorHandler = wrapInErrorHandler(output);
            // set error handler on channel
            channel.setErrorHandler(errorHandler);
        } else {
            LOG.debug("{} is configured to not inheritErrorHandler.", definition);
        }
    }

    /**
     * Wraps the given output in an error handler
     *
     * @param  output    the output
     * @return           the output wrapped with the error handler
     * @throws Exception can be thrown if failed to create error handler builder
     */
    protected Processor wrapInErrorHandler(Processor output) throws Exception {
        ErrorHandlerFactory builder = workflow.getErrorHandlerFactory();

        // create error handler
        Processor errorHandler = ((ModelZwangineContext) zwangineContext).getModelReifierFactory().createErrorHandler(workflow,
                builder, output);

        if (output instanceof ErrorHandlerAware errorHandlerAware) {
            errorHandlerAware.setErrorHandler(errorHandler);
        }

        return errorHandler;
    }

    /**
     * Creates a new instance of some kind of composite processor which defaults to using a {@link Workflow} but derived
     * classes could change the behaviour
     */
    protected Processor createCompositeProcessor(List<Processor> list) throws Exception {
        return Pipeline.newInstance(zwangineContext, list);
    }

    protected Processor createOutputsProcessor(Collection<ProcessorDefinition<?>> outputs) throws Exception {
        return createOutputsProcessor(outputs, true);
    }

    protected Processor createOutputsProcessor(Collection<ProcessorDefinition<?>> outputs, boolean optimize) throws Exception {
        List<Processor> list = new ArrayList<>();
        for (ProcessorDefinition<?> output : outputs) {

            // allow any custom logic before we create the processor
            reifier(workflow, output).preCreateProcessor();

            Processor processor = createProcessor(output);

            // inject id
            if (processor instanceof IdAware idAware) {
                String id = getId(output);
                idAware.setId(id);
            }
            if (processor instanceof WorkflowIdAware workflowIdAware) {
                workflowIdAware.setWorkflowId(workflow.getWorkflowId());
            }

            if (output instanceof Channel && processor == null) {
                continue;
            }

            Processor channel = wrapChannel(processor, output);
            list.add(channel);
        }

        // if more than one output wrap than in a composite processor else just
        // keep it as is
        Processor processor = null;
        if (!list.isEmpty()) {
            if (optimize && list.size() == 1) {
                processor = list.get(0);
            } else {
                processor = createCompositeProcessor(list);
            }
        }

        return processor;
    }

    protected Processor createProcessor(ProcessorDefinition<?> output) throws Exception {
        // ensure node has id assigned
        String outputId = output.idOrCreate(zwangineContext.getZwangineContextExtension().getContextPlugin(NodeIdFactory.class));
        StartupStep step = zwangineContext.getZwangineContextExtension().getStartupStepRecorder().beginStep(ProcessorReifier.class,
                outputId, "Create processor");

        zwangineContext.getZwangineContextExtension().createProcessor(outputId);
        Processor processor = null;
        try {
            // at first use custom factory
            final ProcessorFactory processorFactory = PluginHelper.getProcessorFactory(zwangineContext);
            if (processorFactory != null) {
                processor = processorFactory.createProcessor(workflow, output);
            }
            // fallback to default implementation if factory did not create the processor
            if (processor == null) {
                processor = reifier(workflow, output).createProcessor();
            }
            zwangineContext.getZwangineContextExtension().getStartupStepRecorder().endStep(step);
        } finally {
            zwangineContext.getZwangineContextExtension().createProcessor(null);
        }
        return processor;
    }

    /**
     * Creates the processor and wraps it in any necessary interceptors and error handlers
     */
    protected Channel makeProcessor() throws Exception {
        String outputId = definition.idOrCreate(zwangineContext.getZwangineContextExtension().getContextPlugin(NodeIdFactory.class));
        zwangineContext.getZwangineContextExtension().createProcessor(outputId);
        try {
            Processor processor = null;

            // allow any custom logic before we create the processor
            preCreateProcessor();

            // at first use custom factory
            final ProcessorFactory processorFactory = PluginHelper.getProcessorFactory(zwangineContext);
            if (processorFactory != null) {
                processor = processorFactory.createProcessor(workflow, definition);
            }
            // fallback to default implementation if factory did not create the
            // processor
            if (processor == null) {
                processor = createProcessor();
            }

            // inject id
            if (processor instanceof IdAware idAware) {
                String id = getId(definition);
                idAware.setId(id);
            }
            if (processor instanceof WorkflowIdAware workflowIdAware) {
                workflowIdAware.setWorkflowId(workflow.getWorkflowId());
            }

            if (processor == null) {
                // no processor to make
                return null;
            }
            return wrapProcessor(processor);
        } finally {
            zwangineContext.getZwangineContextExtension().createProcessor(null);
        }
    }

    /**
     * Strategy to execute any custom logic before the {@link Processor} is created.
     */
    protected void preCreateProcessor() {
        definition.preCreateProcessor();
    }

    /**
     * Strategy for children to do any custom configuration
     *
     * @param output the child to be added as output to this
     */
    public void configureChild(ProcessorDefinition<?> output) {
        // noop
    }

    protected String getId(OptionalIdentifiedDefinition<?> def) {
        String id = def.idOrCreate(zwangineContext.getZwangineContextExtension().getContextPlugin(NodeIdFactory.class));
        id = parseString(id);
        return id;
    }

    /**
     * Will lookup and get the configured {@link AggregationStrategy} from the given definition.
     * <p/>
     * This method will lookup for configured aggregation strategy in the following order
     * <ul>
     * <li>from the definition if any explicit configured aggregation strategy.</li>
     * <li>from the {@link org.zenithblox.spi.Registry} if found</li>
     * <li>if none found, then <tt>null</tt> is returned.</li>
     * </ul>
     * The various {@link AggregationStrategyAwareDefinition} should use this helper method to ensure they support
     * configured executor services in the same coherent way.
     *
     * @param  definition               the node definition which may leverage aggregation strategy
     * @throws IllegalArgumentException is thrown if lookup of aggregation strategy in
     *                                  {@link org.zenithblox.spi.Registry} was not found
     */
    public AggregationStrategy getConfiguredAggregationStrategy(AggregationStrategyAwareDefinition<?> definition) {
        AggregationStrategy strategy = definition.getAggregationStrategyBean();
        if (strategy == null && definition.getAggregationStrategyRef() != null) {
            Object aggStrategy = lookupByName(definition.getAggregationStrategyRef());
            if (aggStrategy instanceof AggregationStrategy aggregationStrategy) {
                strategy = aggregationStrategy;
            } else if (aggStrategy instanceof BiFunction biFunction) {
                AggregationStrategyBiFunctionAdapter adapter
                        = new AggregationStrategyBiFunctionAdapter(biFunction);
                if (definition.getAggregationStrategyMethodAllowNull() != null) {
                    adapter.setAllowNullNewExchange(parseBoolean(definition.getAggregationStrategyMethodAllowNull(), false));
                    adapter.setAllowNullOldExchange(parseBoolean(definition.getAggregationStrategyMethodAllowNull(), false));
                }
                strategy = adapter;
            } else if (aggStrategy != null) {
                AggregationStrategyBeanAdapter adapter
                        = new AggregationStrategyBeanAdapter(aggStrategy, definition.getAggregationStrategyMethodName());
                if (definition.getAggregationStrategyMethodAllowNull() != null) {
                    adapter.setAllowNullNewExchange(parseBoolean(definition.getAggregationStrategyMethodAllowNull(), false));
                    adapter.setAllowNullOldExchange(parseBoolean(definition.getAggregationStrategyMethodAllowNull(), false));
                }
                strategy = adapter;
            } else {
                throw new IllegalArgumentException(
                        "Cannot find AggregationStrategy in Registry with name: " + definition.getAggregationStrategyRef());
            }
        }

        ZwangineContextAware.trySetZwangineContext(strategy, zwangineContext);
        return strategy;
    }

    /**
     * Is the given node marked as disabled
     */
    public static boolean isDisabled(ZwangineContext zwangineContext, NamedNode definition) {
        Boolean disabled = null;
        if (definition instanceof DisabledAwareDefinition def) {
            disabled = ZwangineContextHelper.parseBoolean(zwangineContext, def.getDisabled());
        }
        if (disabled == null) {
            String sn = definition.getShortName();
            if ("process".equals(sn) || "bean".equals(sn)) {
                disabled = "true"
                        .equalsIgnoreCase(zwangineContext.getGlobalOption(DISABLE_BEAN_OR_PROCESS_PROCESSORS));
            }
        }
        if (disabled == null) {
            disabled = "true".equalsIgnoreCase(zwangineContext.getGlobalOption(DISABLE_ALL_PROCESSORS));
        }
        return disabled;
    }

}
