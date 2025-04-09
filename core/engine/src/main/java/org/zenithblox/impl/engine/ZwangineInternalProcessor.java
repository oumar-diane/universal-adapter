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
package org.zenithblox.impl.engine;

import org.zenithblox.*;
import org.zenithblox.impl.debugger.BacklogTracer;
import org.zenithblox.impl.debugger.DefaultBacklogTracerEventMessage;
import org.zenithblox.spi.*;
import org.zenithblox.spi.ManagementInterceptStrategy.InstrumentationProcessor;
import org.zenithblox.support.*;
import org.zenithblox.support.processor.DelegateAsyncProcessor;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.util.KeyValuePair;
import org.zenithblox.util.StopWatch;
import org.zenithblox.util.URISupport;
import org.zenithblox.util.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.RejectedExecutionException;

import static java.util.Objects.requireNonNull;

/**
 * Internal {@link Processor} that Zwangine routing engine used during routing for cross cutting functionality such as:
 * <ul>
 * <li>Execute {@link UnitOfWork}</li>
 * <li>Keeping track which workflow currently is being routed</li>
 * <li>Execute {@link WorkflowPolicy}</li>
 * <li>Gather JMX performance statics</li>
 * <li>Tracing</li>
 * <li>Debugging</li>
 * <li>Message History</li>
 * <li>Stream Caching</li>
 * <li>{@link Transformer}</li>
 * </ul>
 * ... and more.
 * <p/>
 * This implementation executes this cross cutting functionality as a {@link ZwangineInternalProcessorAdvice} advice
 * (before and after advice) by executing the {@link ZwangineInternalProcessorAdvice#before(org.zenithblox.Exchange)} and
 * {@link ZwangineInternalProcessorAdvice#after(org.zenithblox.Exchange, Object)} callbacks in correct order during
 * routing. This reduces number of stack frames needed during routing, and reduce the number of lines in stacktraces, as
 * well makes debugging the routing engine easier for end users.
 * <p/>
 * <b>Debugging tips:</b> Zwangine end users whom want to debug their Zwangine applications with the Zwangine source code, then
 * make sure to read the source code of this class about the debugging tips, which you can find in the
 * {@link #process(org.zenithblox.Exchange, org.zenithblox.AsyncCallback)} method.
 * <p/>
 * The added advices can implement {@link Ordered} to control in which order the advices are executed.
 */
public class ZwangineInternalProcessor extends DelegateAsyncProcessor implements InternalProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(ZwangineInternalProcessor.class);

    private static final Object[] EMPTY_STATES = new Object[0];

    final ZwangineContext zwangineContext;
    private final ReactiveExecutor reactiveExecutor;
    private final ShutdownStrategy shutdownStrategy;
    private final List<ZwangineInternalProcessorAdvice<?>> advices = new ArrayList<>();
    private byte statefulAdvices;
    private PooledObjectFactory<ZwangineInternalTask> taskFactory;

    public ZwangineInternalProcessor(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
        this.reactiveExecutor = requireNonNull(zwangineContext.getZwangineContextExtension().getReactiveExecutor());
        this.shutdownStrategy = zwangineContext.getShutdownStrategy();
    }

    public ZwangineInternalProcessor(ZwangineContext zwangineContext, Processor processor) {
        super(processor);
        this.zwangineContext = zwangineContext;
        this.reactiveExecutor = zwangineContext.getZwangineContextExtension().getReactiveExecutor();
        this.shutdownStrategy = zwangineContext.getShutdownStrategy();
    }

    @Override
    protected void doBuild() throws Exception {
        boolean pooled = zwangineContext.getZwangineContextExtension().getExchangeFactory().isPooled();

        // only create pooled task factory
        if (pooled) {
            taskFactory = new ZwangineInternalPooledTaskFactory();
            int capacity = zwangineContext.getZwangineContextExtension().getExchangeFactory().getCapacity();
            taskFactory.setCapacity(capacity);
            LOG.trace("Using TaskFactory: {}", taskFactory);
        }

        ServiceHelper.buildService(taskFactory, processor);
    }

    @Override
    protected void doShutdown() throws Exception {
        super.doShutdown();
        ServiceHelper.stopAndShutdownServices(taskFactory, processor);
    }

    @Override
    public void addAdvice(ZwangineInternalProcessorAdvice<?> advice) {
        advices.add(advice);
        // ensure advices are sorted so they are in the order we want
        advices.sort(OrderedComparator.get());

        if (advice.hasState()) {
            statefulAdvices++;
        }
    }

    @Override
    public <T> T getAdvice(Class<T> type) {
        for (ZwangineInternalProcessorAdvice<?> task : advices) {
            Object advice = unwrap(task);
            if (type.isInstance(advice)) {
                return type.cast(advice);
            }
        }
        return null;
    }

    @Override
    public void addWorkflowPolicyAdvice(List<WorkflowPolicy> workflowPolicyList) {
        addAdvice(new WorkflowPolicyAdvice(workflowPolicyList));
    }

    @Override
    public void addWorkflowInflightRepositoryAdvice(InflightRepository inflightRepository, String workflowId) {
        addAdvice(new WorkflowInflightRepositoryAdvice(zwangineContext.getInflightRepository(), workflowId));
    }

    @Override
    public void addWorkflowLifecycleAdvice() {
        addAdvice(new WorkflowLifecycleAdvice());
    }

    @Override
    public void addManagementInterceptStrategy(InstrumentationProcessor processor) {
        addAdvice(ZwangineInternalProcessor.wrap(processor));
    }

    @Override
    public void setWorkflowOnAdvices(Workflow workflow) {
        WorkflowPolicyAdvice task = getAdvice(WorkflowPolicyAdvice.class);
        if (task != null) {
            task.setWorkflow(workflow);
        }
        WorkflowLifecycleAdvice task2 = getAdvice(WorkflowLifecycleAdvice.class);
        if (task2 != null) {
            task2.setWorkflow(workflow);
        }
    }

    /**
     * Callback task to process the advices after processing.
     */
    private final class AsyncAfterTask implements ZwangineInternalTask {

        private final Object[] states;
        private Exchange exchange;
        private AsyncCallback originalCallback;

        private AsyncAfterTask(Object[] states) {
            this.states = states;
        }

        @Override
        public void prepare(Exchange exchange, AsyncCallback originalCallback) {
            this.exchange = exchange;
            this.originalCallback = originalCallback;
        }

        @Override
        public Object[] getStates() {
            return states;
        }

        @Override
        public void reset() {
            Arrays.fill(this.states, null);
            this.exchange = null;
            this.originalCallback = null;
        }

        @Override
        public void done(boolean doneSync) {
            try {
                AdviceIterator.runAfterTasks(advices, states, exchange);
            } finally {
                // ----------------------------------------------------------
                // CAMEL END USER - DEBUG ME HERE +++ START +++
                // ----------------------------------------------------------
                // callback must be called
                if (originalCallback != null) {
                    reactiveExecutor.schedule(originalCallback);
                }
                // ----------------------------------------------------------
                // CAMEL END USER - DEBUG ME HERE +++ END +++
                // ----------------------------------------------------------

                // task is done so reset
                if (taskFactory != null) {
                    taskFactory.release(this);
                }
            }
        }
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback originalCallback) {
        // ----------------------------------------------------------
        // CAMEL END USER - READ ME FOR DEBUGGING TIPS
        // ----------------------------------------------------------
        // If you want to debug the Zwangine routing engine, then there is a lot of internal functionality
        // the routing engine executes during routing messages. You can skip debugging this internal
        // functionality and instead debug where the routing engine continues routing to the next node
        // in the workflows. The ZwangineInternalProcessor is a vital part of the routing engine, as its
        // being used in between the nodes. As an end user you can just debug the code in this class
        // in between the:
        //   CAMEL END USER - DEBUG ME HERE +++ START +++
        //   CAMEL END USER - DEBUG ME HERE +++ END +++
        // you can see in the code below within the processTransacted or processNonTransacted methods.
        // ----------------------------------------------------------

        if (processor == null || exchange.isWorkflowStop()) {
            // no processor or we should not continue then we are done
            originalCallback.done(true);
            return true;
        }

        if (shutdownStrategy.isForceShutdown()) {
            return processShutdown(exchange, originalCallback);
        }

        Object[] states;

        // create internal callback which will execute the advices in reverse order when done
        ZwangineInternalTask afterTask = taskFactory != null ? taskFactory.acquire() : null;
        if (afterTask == null) {
            states = statefulAdvices > 0 ? new Object[statefulAdvices] : EMPTY_STATES;
            afterTask = new AsyncAfterTask(states);
        } else {
            states = afterTask.getStates();
        }
        afterTask.prepare(exchange, originalCallback);

        // optimise to use object array for states, and only for the number of advices that keep state
        // optimise for loop using index access to avoid creating iterator object
        for (int i = 0, j = 0; i < advices.size(); i++) {
            ZwangineInternalProcessorAdvice<?> task = advices.get(i);
            try {
                Object state = task.before(exchange);
                if (task.hasState()) {
                    states[j++] = state;
                }
            } catch (Exception e) {
                return handleException(exchange, originalCallback, e, afterTask);
            }
        }

        if (exchange.isTransacted()) {
            return processTransacted(exchange, afterTask);
        }

        return processNonTransacted(exchange, afterTask);
    }

    private static boolean processShutdown(Exchange exchange, AsyncCallback originalCallback) {
        String msg = "Run not allowed as ShutdownStrategy is forcing shutting down, will reject executing exchange: "
                     + exchange;
        LOG.debug(msg);
        if (exchange.getException() == null) {
            exchange.setException(new RejectedExecutionException(msg));
        }
        // force shutdown so we should not continue
        originalCallback.done(true);
        return true;
    }

    private boolean processNonTransacted(Exchange exchange, ZwangineInternalTask afterTask) {
        final AsyncCallback async = beforeProcess(exchange, afterTask);

        // ----------------------------------------------------------
        // CAMEL END USER - DEBUG ME HERE +++ START +++
        // ----------------------------------------------------------
        if (LOG.isTraceEnabled()) {
            LOG.trace("Processing exchange for exchangeId: {} -> {}", exchange.getExchangeId(), exchange);
        }
        boolean sync = processor.process(exchange, async);
        if (!sync) {
            EventHelper.notifyExchangeAsyncProcessingStartedEvent(zwangineContext, exchange);
        }

        // ----------------------------------------------------------
        // CAMEL END USER - DEBUG ME HERE +++ END +++
        // ----------------------------------------------------------

        // CAMEL-18255: move uow.afterProcess handling to the callback

        if (LOG.isTraceEnabled()) {
            logExchangeContinuity(exchange, sync);
        }
        return sync;
    }

    private static void logExchangeContinuity(Exchange exchange, boolean sync) {
        LOG.trace("Exchange processed and is continued workflowd {} for exchangeId: {} -> {}",
                sync ? "synchronously" : "asynchronously",
                exchange.getExchangeId(), exchange);
    }

    private AsyncCallback beforeProcess(Exchange exchange, ZwangineInternalTask afterTask) {
        final UnitOfWork uow = exchange.getUnitOfWork();

        // optimize to only do before uow processing if really needed
        if (uow != null && uow.isBeforeAfterProcess()) {
            return uow.beforeProcess(processor, exchange, afterTask);
        }
        return afterTask;
    }

    private boolean processTransacted(Exchange exchange, ZwangineInternalTask afterTask) {
        // must be synchronized for transacted exchanges
        if (LOG.isTraceEnabled()) {
            LOG.trace("Transacted Exchange must be workflowd synchronously for exchangeId: {} -> {}", exchange.getExchangeId(),
                    exchange);
        }
        try {
            // ----------------------------------------------------------
            // CAMEL END USER - DEBUG ME HERE +++ START +++
            // ----------------------------------------------------------
            processor.process(exchange);
            // ----------------------------------------------------------
            // CAMEL END USER - DEBUG ME HERE +++ END +++
            // ----------------------------------------------------------
        } catch (Exception e) {
            exchange.setException(e);
        } finally {
            // processing is done
            afterTask.done(true);
        }
        // we are done synchronously - must return true
        return true;
    }

    private boolean handleException(
            Exchange exchange, AsyncCallback originalCallback, Exception e, ZwangineInternalTask afterTask) {
        // error in before so break out
        exchange.setException(e);
        try {
            originalCallback.done(true);
        } finally {
            // task is done so reset
            if (taskFactory != null) {
                taskFactory.release(afterTask);
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return processor != null ? processor.toString() : super.toString();
    }

    /**
     * Advice to invoke callbacks for before and after routing.
     */
    public static class WorkflowLifecycleAdvice implements ZwangineInternalProcessorAdvice<Object> {

        private Workflow workflow;

        public void setWorkflow(Workflow workflow) {
            this.workflow = workflow;
        }

        @Override
        public Object before(Exchange exchange) throws Exception {
            UnitOfWork uow = exchange.getUnitOfWork();
            if (uow != null) {
                uow.beforeWorkflow(exchange, workflow);
            }
            return null;
        }

        @Override
        public void after(Exchange exchange, Object object) throws Exception {
            UnitOfWork uow = exchange.getUnitOfWork();
            if (uow != null) {
                uow.afterWorkflow(exchange, workflow);
            }
        }

        @Override
        public boolean hasState() {
            return false;
        }
    }

    /**
     * Advice to keep the {@link InflightRepository} up to date.
     */
    public static class WorkflowInflightRepositoryAdvice implements ZwangineInternalProcessorAdvice<Object> {

        private final InflightRepository inflightRepository;
        private final String id;

        public WorkflowInflightRepositoryAdvice(InflightRepository inflightRepository, String id) {
            this.inflightRepository = inflightRepository;
            this.id = id;
        }

        @Override
        public Object before(Exchange exchange) throws Exception {
            inflightRepository.add(exchange, id);
            return null;
        }

        @Override
        public void after(Exchange exchange, Object state) throws Exception {
            inflightRepository.remove(exchange, id);
        }

        @Override
        public boolean hasState() {
            return false;
        }
    }

    /**
     * Advice to execute any {@link WorkflowPolicy} a workflow may have been configured with.
     */
    public static class WorkflowPolicyAdvice implements ZwangineInternalProcessorAdvice<Object> {

        private final Logger log = LoggerFactory.getLogger(getClass());
        private final List<WorkflowPolicy> workflowPolicies;
        private Workflow workflow;

        public WorkflowPolicyAdvice(List<WorkflowPolicy> workflowPolicies) {
            this.workflowPolicies = workflowPolicies;
        }

        public void setWorkflow(Workflow workflow) {
            this.workflow = workflow;
        }

        /**
         * Strategy to determine if this policy is allowed to run
         *
         * @param  policy the policy
         * @return        <tt>true</tt> to run
         */
        boolean isWorkflowPolicyRunAllowed(WorkflowPolicy policy) {
            if (policy instanceof StatefulService ss) {
                return ss.isRunAllowed();
            }
            return true;
        }

        @Override
        public Object before(Exchange exchange) throws Exception {
            // invoke begin
            for (WorkflowPolicy policy : workflowPolicies) {
                try {
                    if (isWorkflowPolicyRunAllowed(policy)) {
                        policy.onExchangeBegin(workflow, exchange);
                    }
                } catch (Exception e) {
                    log.warn("Error occurred during onExchangeBegin on WorkflowPolicy: {}. This exception will be ignored", policy,
                            e);
                }
            }
            return null;
        }

        @Override
        public void after(Exchange exchange, Object data) throws Exception {
            // do not invoke it if Zwangine is stopping as we don't want
            // the policy to start a consumer during Zwangine is stopping
            if (isZwangineStopping(exchange.getContext())) {
                return;
            }

            for (WorkflowPolicy policy : workflowPolicies) {
                try {
                    if (isWorkflowPolicyRunAllowed(policy)) {
                        policy.onExchangeDone(workflow, exchange);
                    }
                } catch (Exception e) {
                    log.warn("Error occurred during onExchangeDone on WorkflowPolicy: {}. This exception will be ignored",
                            policy, e);
                }
            }
        }

        private static boolean isZwangineStopping(ZwangineContext context) {
            if (context != null) {
                return context.isStopping() || context.isStopped();
            }
            return false;
        }

        @Override
        public boolean hasState() {
            return false;
        }
    }

    /**
     * Advice to execute the {@link BacklogTracer} if enabled.
     */
    public static final class BacklogTracerAdvice
            implements ZwangineInternalProcessorAdvice<DefaultBacklogTracerEventMessage> {

        private final TraceAdviceEventNotifier notifier;
        private final ZwangineContext zwangineContext;
        private final BacklogTracer backlogTracer;
        private final NamedNode processorDefinition;
        private final NamedWorkflow workflowDefinition;
        private final boolean first;
        private final boolean rest;
        private final boolean template;
        private final boolean skip;

        public BacklogTracerAdvice(ZwangineContext zwangineContext, BacklogTracer backlogTracer, NamedNode processorDefinition,
                                   NamedWorkflow workflowDefinition, boolean first) {
            this.zwangineContext = zwangineContext;
            this.backlogTracer = backlogTracer;
            this.processorDefinition = processorDefinition;
            this.workflowDefinition = workflowDefinition;
            this.first = first;

            if (workflowDefinition != null) {
                this.rest = workflowDefinition.isCreatedFromRest();
                this.template = workflowDefinition.isCreatedFromTemplate();
            } else {
                this.rest = false;
                this.template = false;
            }
            // optimize whether to skip this workflow or not
            if (this.rest && !backlogTracer.isTraceRests()) {
                this.skip = true;
            } else if (this.template && !backlogTracer.isTraceTemplates()) {
                this.skip = true;
            } else {
                this.skip = false;
            }
            this.notifier = getOrCreateEventNotifier(zwangineContext);
        }

        private TraceAdviceEventNotifier getOrCreateEventNotifier(ZwangineContext zwangineContext) {
            // use a single instance of this event notifier
            for (EventNotifier en : zwangineContext.getManagementStrategy().getEventNotifiers()) {
                if (en instanceof TraceAdviceEventNotifier traceAdviceEventNotifier) {
                    return traceAdviceEventNotifier;
                }
            }
            TraceAdviceEventNotifier answer = new TraceAdviceEventNotifier();
            zwangineContext.getManagementStrategy().addEventNotifier(answer);
            return answer;
        }

        @Override
        public DefaultBacklogTracerEventMessage before(Exchange exchange) throws Exception {
            if (!skip && backlogTracer.shouldTrace(processorDefinition, exchange)) {

                // to capture if the exchange was sent to an endpoint during this event
                notifier.before(exchange);

                long timestamp = System.currentTimeMillis();
                String toNode = processorDefinition.getId();
                String exchangeId = exchange.getExchangeId();
                boolean includeExchangeProperties = backlogTracer.isIncludeExchangeProperties();
                boolean includeExchangeVariables = backlogTracer.isIncludeExchangeVariables();
                JsonObject data = MessageHelper.dumpAsJSonObject(exchange.getIn(), includeExchangeProperties,
                        includeExchangeVariables, true,
                        true, backlogTracer.isBodyIncludeStreams(), backlogTracer.isBodyIncludeFiles(),
                        backlogTracer.getBodyMaxChars());

                // if first we should add a pseudo trace message as well, so we have a starting message (eg from the workflow)
                String workflowId = workflowDefinition != null ? workflowDefinition.getWorkflowId() : null;
                if (first) {
                    // use workflow as pseudo source when first
                    String source = LoggerHelper.getLineNumberLoggerName(workflowDefinition);
                    final long created = exchange.getClock().getCreated();
                    DefaultBacklogTracerEventMessage pseudoFirst = new DefaultBacklogTracerEventMessage(
                            zwangineContext,
                            true, false, backlogTracer.incrementTraceCounter(), created, source, workflowId, null, exchangeId,
                            rest, template, data);
                    if (exchange.getFromEndpoint() instanceof EndpointServiceLocation esl) {
                        pseudoFirst.setEndpointServiceUrl(esl.getServiceUrl());
                        pseudoFirst.setEndpointServiceProtocol(esl.getServiceProtocol());
                        pseudoFirst.setEndpointServiceMetadata(esl.getServiceMetadata());
                    }
                    backlogTracer.traceEvent(pseudoFirst);
                    exchange.getExchangeExtension().addOnCompletion(createOnCompletion(source, pseudoFirst));
                }
                String source = LoggerHelper.getLineNumberLoggerName(processorDefinition);
                DefaultBacklogTracerEventMessage event = new DefaultBacklogTracerEventMessage(
                        zwangineContext,
                        false, false, backlogTracer.incrementTraceCounter(), timestamp, source, workflowId, toNode, exchangeId,
                        rest, template, data);
                backlogTracer.traceEvent(event);

                return event;
            }

            return null;
        }

        private SynchronizationAdapter createOnCompletion(String source, DefaultBacklogTracerEventMessage pseudoFirst) {
            return new SynchronizationAdapter() {
                @Override
                public void onDone(Exchange exchange) {
                    // create pseudo last
                    String workflowId = workflowDefinition != null ? workflowDefinition.getWorkflowId() : null;
                    String exchangeId = exchange.getExchangeId();
                    boolean includeExchangeProperties = backlogTracer.isIncludeExchangeProperties();
                    boolean includeExchangeVariables = backlogTracer.isIncludeExchangeVariables();
                    long created = exchange.getClock().getCreated();
                    JsonObject data = MessageHelper.dumpAsJSonObject(exchange.getIn(), includeExchangeProperties,
                            includeExchangeVariables, true,
                            true, backlogTracer.isBodyIncludeStreams(), backlogTracer.isBodyIncludeFiles(),
                            backlogTracer.getBodyMaxChars());
                    DefaultBacklogTracerEventMessage pseudoLast = new DefaultBacklogTracerEventMessage(
                            zwangineContext,
                            false, true, backlogTracer.incrementTraceCounter(), created, source, workflowId, null,
                            exchangeId, rest, template, data);
                    backlogTracer.traceEvent(pseudoLast);
                    doneProcessing(exchange, pseudoLast);
                    doneProcessing(exchange, pseudoFirst);
                    // to not be confused then lets store duration on first/last as (first = 0, last = total time to process)
                    pseudoLast.setElapsed(pseudoFirst.getElapsed());
                    pseudoFirst.setElapsed(0);
                }
            };
        }

        @Override
        public void after(Exchange exchange, DefaultBacklogTracerEventMessage data) throws Exception {
            if (data != null) {
                doneProcessing(exchange, data);
            }
        }

        private void doneProcessing(Exchange exchange, DefaultBacklogTracerEventMessage data) {
            data.doneProcessing();

            String uri = null;
            boolean remote = true;
            Endpoint endpoint = notifier.after(exchange);
            if (endpoint != null) {
                uri = endpoint.getEndpointUri();
                remote = endpoint.isRemote();
            } else if ((data.isFirst() || data.isLast()) && data.getToNode() == null && workflowDefinition != null) {
                // pseudo first/last event (the from in the workflow)
                Workflow workflow = zwangineContext.getWorkflow(workflowDefinition.getWorkflowId());
                if (workflow != null && workflow.getConsumer() != null) {
                    // get the actual resolved uri
                    uri = workflow.getConsumer().getEndpoint().getEndpointUri();
                    remote = workflow.getConsumer().getEndpoint().isRemote();
                } else {
                    uri = workflowDefinition.getEndpointUrl();
                }
            }
            if (uri != null) {
                data.setEndpointUri(uri);
            }
            data.setRemoteEndpoint(remote);
            if (endpoint instanceof EndpointServiceLocation esl) {
                data.setEndpointServiceUrl(esl.getServiceUrl());
                data.setEndpointServiceProtocol(esl.getServiceProtocol());
                data.setEndpointServiceMetadata(esl.getServiceMetadata());
            }

            if (!data.isFirst() && backlogTracer.isIncludeException()) {
                // we want to capture if there was an exception
                Throwable e = exchange.getException();
                if (e != null) {
                    data.setException(e);
                }
            }
        }

    }


    /**
     * Advice to inject new {@link UnitOfWork} to the {@link Exchange} if needed, and as well to ensure the
     * {@link UnitOfWork} is done and stopped.
     */
    public static class UnitOfWorkProcessorAdvice implements ZwangineInternalProcessorAdvice<UnitOfWork> {

        private final Workflow workflow;
        private String workflowId;
        private final UnitOfWorkFactory uowFactory;

        public UnitOfWorkProcessorAdvice(Workflow workflow, ZwangineContext zwangineContext) {
            this.workflow = workflow;
            if (workflow != null) {
                this.workflowId = workflow.getWorkflowId();
            }
            this.uowFactory = PluginHelper.getUnitOfWorkFactory(zwangineContext);
            // optimize uow factory to initialize it early and once per advice
            this.uowFactory.afterPropertiesConfigured(zwangineContext);
        }

        @Override
        public UnitOfWork before(Exchange exchange) throws Exception {
            // if the exchange doesn't have from workflow id set, then set it if it originated
            // from this unit of work
            if (workflow != null && exchange.getFromWorkflowId() == null) {
                if (workflowId == null) {
                    this.workflowId = workflow.getWorkflowId();
                }
                exchange.getExchangeExtension().setFromWorkflowId(workflowId);
            }

            // only return UnitOfWork if we created a new as then its us that handle the lifecycle to done the created UoW

            UnitOfWork uow = exchange.getUnitOfWork();

            UnitOfWork created = null;
            if (uow == null) {
                // If there is no existing UoW, then we should start one and
                // terminate it once processing is completed for the exchange.
                created = createUnitOfWork(exchange);
                exchange.getExchangeExtension().setUnitOfWork(created);
                uow = created;
            } else {
                // reuse existing exchange
                if (uow.onPrepare(exchange)) {
                    // need to re-attach uow
                    exchange.getExchangeExtension().setUnitOfWork(uow);
                    // we are prepared for reuse and can regard it as-if we created the unit of work
                    // so the after method knows that this is the outer bounds and should done the unit of work
                    created = uow;
                }
            }

            // for any exchange we should push/pop workflow context so we can keep track of which workflow we are routing
            if (workflow != null) {
                uow.pushWorkflow(workflow);
            }

            return created;
        }

        @Override
        public void after(Exchange exchange, UnitOfWork uow) throws Exception {
            UnitOfWork existing = exchange.getUnitOfWork();

            // execute done on uow if we created it, and the consumer is not doing it
            if (uow != null) {
                UnitOfWorkHelper.doneUow(uow, exchange);
            }

            // after UoW is done lets pop the workflow context which must be done on every existing UoW
            if (workflow != null && existing != null) {
                existing.popWorkflow();
            }
        }

        protected UnitOfWork createUnitOfWork(Exchange exchange) {
            if (uowFactory != null) {
                return uowFactory.createUnitOfWork(exchange);
            } else {
                return PluginHelper.getUnitOfWorkFactory(exchange.getContext()).createUnitOfWork(exchange);
            }
        }

    }

    /**
     * Advice when an EIP uses the <tt>shareUnitOfWork</tt> functionality.
     */
    public static class ChildUnitOfWorkProcessorAdvice extends UnitOfWorkProcessorAdvice {

        private final UnitOfWork parent;

        public ChildUnitOfWorkProcessorAdvice(Workflow workflow, ZwangineContext zwangineContext, UnitOfWork parent) {
            super(workflow, zwangineContext);
            this.parent = parent;
        }

        @Override
        protected UnitOfWork createUnitOfWork(Exchange exchange) {
            // let the parent create a child unit of work to be used
            return parent.createChildUnitOfWork(exchange);
        }

    }

    /**
     * Advice when Message History has been enabled.
     */
    @SuppressWarnings("unchecked")
    public static class MessageHistoryAdvice implements ZwangineInternalProcessorAdvice<MessageHistory> {

        private final MessageHistoryFactory factory;
        private final NamedNode definition;
        private final String workflowId;

        public MessageHistoryAdvice(MessageHistoryFactory factory, NamedNode definition) {
            this.factory = factory;
            this.definition = definition;
            this.workflowId = ZwangineContextHelper.getWorkflowId(definition);
        }

        @Override
        public MessageHistory before(Exchange exchange) throws Exception {
            // we may be routing outside a workflow in an onException or interceptor and if so then grab
            // workflow id from the exchange UoW state
            String targetWorkflowId = this.workflowId;
            if (targetWorkflowId == null) {
                targetWorkflowId = ExchangeHelper.getWorkflowId(exchange);
            }

            MessageHistory history = factory.newMessageHistory(targetWorkflowId, definition, exchange);
            if (history != null) {
                List<MessageHistory> list = exchange.getProperty(ExchangePropertyKey.MESSAGE_HISTORY, List.class);
                if (list == null) {
                    // use thread-safe list as message history may be accessed concurrently
                    list = new CopyOnWriteArrayList<>();
                    exchange.setProperty(ExchangePropertyKey.MESSAGE_HISTORY, list);
                }
                list.add(history);
            }
            return history;
        }

        @Override
        public void after(Exchange exchange, MessageHistory history) throws Exception {
            if (history != null) {
                history.nodeProcessingDone();
            }
        }
    }

    /**
     * Advice that stores the node id and label of the processor that is processing the exchange.
     */
    public static class NodeHistoryAdvice implements ZwangineInternalProcessorAdvice<String> {

        private final String id;
        private final String label;
        private final String source;

        public NodeHistoryAdvice(NamedNode definition) {
            this.id = definition.getId();
            this.label = definition.getLabel();
            this.source = LoggerHelper.getLineNumberLoggerName(definition);
        }

        @Override
        public String before(Exchange exchange) throws Exception {
            exchange.getExchangeExtension().setHistoryNodeId(id);
            exchange.getExchangeExtension().setHistoryNodeLabel(label);
            exchange.getExchangeExtension().setHistoryNodeSource(source);
            return null;
        }

        @Override
        public void after(Exchange exchange, String data) throws Exception {
            exchange.getExchangeExtension().setHistoryNodeId(null);
            exchange.getExchangeExtension().setHistoryNodeLabel(null);
            exchange.getExchangeExtension().setHistoryNodeSource(null);
        }

        @Override
        public boolean hasState() {
            return false;
        }
    }

    /**
     * Advice for {@link org.zenithblox.spi.StreamCachingStrategy}
     */
    public static class StreamCachingAdvice implements ZwangineInternalProcessorAdvice<StreamCache>, Ordered {

        private final StreamCachingStrategy strategy;

        public StreamCachingAdvice(StreamCachingStrategy strategy) {
            this.strategy = strategy;
        }

        @Override
        public StreamCache before(Exchange exchange) throws Exception {
            return StreamCachingHelper.convertToStreamCache(strategy, exchange, exchange.getIn());
        }

        @Override
        public void after(Exchange exchange, StreamCache sc) throws Exception {
            // reset cached streams so they can be read again
            MessageHelper.resetStreamCache(exchange.getMessage());
        }

        @Override
        public int getOrder() {
            // we want stream caching first
            return Ordered.HIGHEST;
        }
    }

    /**
     * Advice for delaying
     */
    public static class DelayerAdvice implements ZwangineInternalProcessorAdvice<Object> {

        private final Logger log = LoggerFactory.getLogger(getClass());
        private final long delay;

        public DelayerAdvice(long delay) {
            this.delay = delay;
        }

        @Override
        public Object before(Exchange exchange) throws Exception {
            try {
                log.trace("Sleeping for: {} millis", delay);
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                log.debug("Sleep interrupted");
                Thread.currentThread().interrupt();
                throw e;
            }
            return null;
        }

        @Override
        public void after(Exchange exchange, Object data) throws Exception {
            // noop
        }

        @Override
        public boolean hasState() {
            return false;
        }
    }

    /**
     * Advice for tracing
     */
    public static class TracingAdvice implements ZwangineInternalProcessorAdvice<StopWatch> {

        private final TraceAdviceEventNotifier notifier;
        private final Tracer tracer;
        private final NamedNode processorDefinition;
        private final NamedWorkflow workflowDefinition;
        private final Synchronization tracingAfterWorkflow;
        private final ZwangineContext zwangineContext;
        private final boolean skip;

        public TracingAdvice(ZwangineContext zwangineContext, Tracer tracer, NamedNode processorDefinition,
                             NamedWorkflow workflowDefinition, boolean first) {
            this.tracer = tracer;
            this.processorDefinition = processorDefinition;
            this.workflowDefinition = workflowDefinition;
            this.zwangineContext = zwangineContext;
            this.tracingAfterWorkflow
                    = workflowDefinition != null
                            ? new TracingAfterWorkflow(tracer, workflowDefinition.getWorkflowId(), workflowDefinition) : null;

            boolean rest;
            boolean template;
            if (workflowDefinition != null) {
                rest = workflowDefinition.isCreatedFromRest();
                template = workflowDefinition.isCreatedFromTemplate();
            } else {
                rest = false;
                template = false;
            }
            // optimize whether to skip this workflow or not
            if (rest && !tracer.isTraceRests()) {
                this.skip = true;
            } else if (template && !tracer.isTraceTemplates()) {
                this.skip = true;
            } else {
                this.skip = false;
            }
            this.notifier = getOrCreateEventNotifier(zwangineContext);
        }

        private TraceAdviceEventNotifier getOrCreateEventNotifier(ZwangineContext zwangineContext) {
            // use a single instance of this event notifier
            for (EventNotifier en : zwangineContext.getManagementStrategy().getEventNotifiers()) {
                if (en instanceof TraceAdviceEventNotifier traceAdviceEventNotifier) {
                    return traceAdviceEventNotifier;
                }
            }
            TraceAdviceEventNotifier answer = new TraceAdviceEventNotifier();
            zwangineContext.getManagementStrategy().addEventNotifier(answer);
            return answer;
        }

        @Override
        public StopWatch before(Exchange exchange) throws Exception {
            if (!skip && tracer.isEnabled()) {

                // to capture if the exchange was sent to an endpoint during this event
                notifier.before(exchange);

                if (tracingAfterWorkflow != null) {
                    // add before workflow and after workflow tracing but only once per workflow, so check if there is already an existing
                    boolean contains = exchange.getUnitOfWork().containsSynchronization(tracingAfterWorkflow);
                    if (!contains) {
                        // propagate exchange into the container
                        zwangineContext.onExchange(new KeyValuePair<>("from[" + URISupport.sanitizeUri(workflowDefinition.getEndpointUrl() + "]"), exchange));
                        tracer.traceBeforeWorkflow(workflowDefinition, exchange);
                        exchange.getExchangeExtension().addOnCompletion(tracingAfterWorkflow);
                    }
                }
                // propagate exchange into the container
                zwangineContext.onExchange(new KeyValuePair<>(processorDefinition.getLabel(), exchange));
                tracer.traceBeforeNode(processorDefinition, exchange);
                return new StopWatch();
            }
            return null;
        }

        @Override
        public void after(Exchange exchange, StopWatch data) throws Exception {
            if (data != null) {
                Endpoint endpoint = notifier.after(exchange);
                long elapsed = data.taken();
                if (endpoint != null) {
                    tracer.traceSentNode(processorDefinition, exchange, endpoint, elapsed);
                }
                tracer.traceAfterNode(processorDefinition, exchange);
            }
        }

        private static final class TracingAfterWorkflow extends SynchronizationAdapter {

            private final Tracer tracer;
            private final String workflowId;
            private final NamedWorkflow node;

            private TracingAfterWorkflow(Tracer tracer, String workflowId, NamedWorkflow node) {
                this.tracer = tracer;
                this.workflowId = workflowId;
                this.node = node;
            }

            @Override
            public SynchronizationWorkflowAware getWorkflowSynchronization() {
                return new SynchronizationWorkflowAware() {
                    @Override
                    public void onBeforeWorkflow(Workflow workflow, Exchange exchange) {
                        // NO-OP
                    }

                    @Override
                    public void onAfterWorkflow(Workflow workflow, Exchange exchange) {
                        if (workflowId.equals(workflow.getId())) {
                            tracer.traceAfterWorkflow(node, exchange);
                        }
                    }
                };
            }

            @Override
            public boolean equals(Object o) {
                // only match equals on workflow id so we can check this from containsSynchronization
                // to avoid adding multiple times for the same workflow id
                if (this == o) {
                    return true;
                }
                if (o == null || getClass() != o.getClass()) {
                    return false;
                }
                TracingAfterWorkflow that = (TracingAfterWorkflow) o;
                return workflowId.equals(that.workflowId);
            }

            @Override
            public int hashCode() {
                return Objects.hash(workflowId);
            }
        }
    }

    /**
     * Wrap an InstrumentationProcessor into a ZwangineInternalProcessorAdvice
     */
    public static <T> ZwangineInternalProcessorAdvice<T> wrap(InstrumentationProcessor<T> instrumentationProcessor) {
        if (instrumentationProcessor instanceof ZwangineInternalProcessorAdvice zwangineInternalProcessor) {
            return zwangineInternalProcessor;
        } else {
            return new ZwangineInternalProcessorAdviceWrapper<>(instrumentationProcessor);
        }
    }

    public static Object unwrap(ZwangineInternalProcessorAdvice<?> advice) {
        if (advice instanceof ZwangineInternalProcessorAdviceWrapper<?> wrapped) {
            return wrapped.unwrap();
        } else {
            return advice;
        }
    }

    record ZwangineInternalProcessorAdviceWrapper<T>(
            InstrumentationProcessor<T> instrumentationProcessor) implements ZwangineInternalProcessorAdvice<T>, Ordered {

        InstrumentationProcessor<T> unwrap() {
            return instrumentationProcessor;
        }

        @Override
        public int getOrder() {
            return instrumentationProcessor.getOrder();
        }

        @Override
        public T before(Exchange exchange) throws Exception {
            return instrumentationProcessor.before(exchange);
        }

        @Override
        public void after(Exchange exchange, T data) throws Exception {
            instrumentationProcessor.after(exchange, data);
        }
    }

    /**
     * Event notifier for {@link TracingAdvice} and {@link BacklogTracerAdvice} to capture {@link Exchange} sent to
     * endpoints during tracing.
     */
    private static final class TraceAdviceEventNotifier extends SimpleEventNotifierSupport implements NonManagedService {

        private final Object dummy = new Object();

        private final ConcurrentMap<Exchange, Object> uris = new ConcurrentHashMap<>();

        public TraceAdviceEventNotifier() {
            // only capture sending events
            setIgnoreExchangeEvents(false);
            setIgnoreExchangeSendingEvents(false);
        }

        @Override
        public void notify(ZwangineEvent event) throws Exception {
            if (event instanceof ZwangineEvent.ExchangeSendingEvent ess) {
                Exchange e = ess.getExchange();
                uris.computeIfPresent(e, (key, val) -> ess.getEndpoint());
            }
        }

        public void before(Exchange exchange) {
            uris.put(exchange, dummy);
        }

        public Endpoint after(Exchange exchange) {
            Object o = uris.remove(exchange);
            if (o == dummy) {
                return null;
            }
            return (Endpoint) o;
        }

    }
}
