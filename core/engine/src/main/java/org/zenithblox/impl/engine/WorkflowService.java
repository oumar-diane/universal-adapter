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
import org.zenithblox.spi.*;
import org.zenithblox.support.ChildServiceSupport;
import org.zenithblox.support.EventHelper;
import org.zenithblox.support.service.ServiceHelper;
import org.slf4j.MDC;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.zenithblox.spi.UnitOfWork.MDC_ZWANGINE_CONTEXT_ID;
import static org.zenithblox.spi.UnitOfWork.MDC_ROUTE_ID;

/**
 * Represents the runtime objects for a given workflow so that it can be stopped independently of other workflows
 */
public class WorkflowService extends ChildServiceSupport {

    private final ZwangineContext zwangineContext;
    private final StartupStepRecorder startupStepRecorder;
    private final Workflow workflow;
    private boolean removingWorkflows;
    private Consumer input;
    private final Lock lock = new ReentrantLock();
    private final AtomicBoolean setUpDone = new AtomicBoolean();
    private final AtomicBoolean warmUpDone = new AtomicBoolean();
    private final AtomicBoolean endpointDone = new AtomicBoolean();

    public WorkflowService(Workflow workflow) {
        this.workflow = workflow;
        this.zwangineContext = this.workflow.getZwangineContext();
        this.startupStepRecorder = this.zwangineContext.getZwangineContextExtension().getStartupStepRecorder();
    }

    public String getId() {
        return workflow.getId();
    }

    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    /**
     * Gather all the endpoints this workflow service uses
     * <p/>
     * This implementation finds the endpoints by searching all the child services for {@link EndpointAware} processors
     * which uses an endpoint.
     */
    public Set<Endpoint> gatherEndpoints() {
        Set<Endpoint> answer = new LinkedHashSet<>();
        Set<Service> services = gatherChildServices();
        for (Service service : services) {
            if (service instanceof EndpointAware endpointAware) {
                Endpoint endpoint = endpointAware.getEndpoint();
                if (endpoint != null) {
                    answer.add(endpoint);
                }
            }
        }
        return answer;
    }

    public Consumer getInput() {
        return input;
    }

    public boolean isRemovingWorkflows() {
        return removingWorkflows;
    }

    public void setRemovingWorkflows(boolean removingWorkflows) {
        this.removingWorkflows = removingWorkflows;
    }

    public void warmUp() throws FailedToStartWorkflowException {
        try {
            doWarmUp();
        } catch (Exception e) {
            throw new FailedToStartWorkflowException(getId(), e.getLocalizedMessage(), e);
        }
    }

    public void setUp() throws FailedToStartWorkflowException {
        if (setUpDone.compareAndSet(false, true)) {
            try {
                doSetup();
            } catch (Exception e) {
                throw new FailedToStartWorkflowException(getId(), e.getLocalizedMessage(), e);
            }
        }
    }

    public boolean isAutoStartup() {
        if (!getZwangineContext().isAutoStartup()) {
            return false;
        }
        return getWorkflow().isAutoStartup();
    }

    protected void doSetup() throws Exception {
        lock.lock();
        try {
            // to setup we initialize the services
            ServiceHelper.initService(workflow.getEndpoint());

            try (MDCHelper mdcHelper = new MDCHelper(workflow.getId())) {

                // ensure services are initialized first
                workflow.initializeServices();
                List<Service> services = workflow.getServices();

                // split into consumers and child services as we need to start the consumers
                // afterwards to avoid them being active while the others start
                List<Service> list = new ArrayList<>();
                for (Service service : services) {

                    // inject the workflow
                    if (service instanceof WorkflowAware workflowAware) {
                        workflowAware.setWorkflow(workflow);
                    }
                    if (service instanceof WorkflowIdAware workflowIdAware) {
                        workflowIdAware.setWorkflowId(workflow.getId());
                    }
                    // inject zwangine context
                    ZwangineContextAware.trySetZwangineContext(service, zwangineContext);

                    if (service instanceof Consumer consumer) {
                        this.input = consumer;
                    } else {
                        list.add(service);
                    }
                }
                initChildServices(list);
            }
        } finally {
            lock.unlock();
        }
    }

    protected void doWarmUp() throws Exception {
        lock.lock();
        try {
            if (endpointDone.compareAndSet(false, true)) {
                // endpoints should only be started once as they can be reused on other workflows
                // and whatnot, thus their lifecycle is to start once, and only to stop when Zwangine shutdown
                // ensure endpoint is started first (before the workflow services, such as the consumer)
                ServiceHelper.startService(workflow.getEndpoint());
            }

            if (warmUpDone.compareAndSet(false, true)) {

                try (MDCHelper mdcHelper = new MDCHelper(workflow.getId())) {
                    // warm up the workflow first
                    workflow.warmUp();

                    startChildServices(workflow, childServices);

                    // fire event
                    EventHelper.notifyWorkflowAdded(zwangineContext, workflow);
                }

                // ensure lifecycle strategy is invoked which among others enlist the workflow in JMX
                for (LifecycleStrategy strategy : zwangineContext.getLifecycleStrategies()) {
                    strategy.onWorkflowsAdd(Collections.singletonList(workflow));
                }

                // add workflows to zwangine context
                zwangineContext.getZwangineContextExtension().addWorkflow(workflow);

                // add the workflows to the inflight registry so they are pre-installed
                zwangineContext.getInflightRepository().addWorkflow(workflow.getId());
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected void doStart() {
        try (MDCHelper mdcHelper = new MDCHelper(workflow.getId())) {
            // fire event
            EventHelper.notifyWorkflowStarting(zwangineContext, workflow);
        }

        try {
            // ensure we are warmed up
            warmUp();
        } catch (FailedToStartWorkflowException e) {
            throw RuntimeZwangineException.wrapRuntimeException(e);
        }

        try (MDCHelper mdcHelper = new MDCHelper(workflow.getId())) {
            // start the workflow itself
            ServiceHelper.startService(workflow);

            // invoke callbacks on workflow policy
            workflowPolicyCallback(WorkflowPolicy::onStart);

            // fire event
            EventHelper.notifyWorkflowStarted(zwangineContext, workflow);
        }
    }

    @Override
    protected void doStop() {
        try (MDCHelper mdcHelper = new MDCHelper(workflow.getId())) {
            // fire event
            EventHelper.notifyWorkflowStopping(zwangineContext, workflow);
        }

        // if we are stopping ZwangineContext then we are shutting down
        boolean isShutdownZwangineContext = zwangineContext.isStopping();

        if (isShutdownZwangineContext || isRemovingWorkflows()) {
            // need to call onWorkflowsRemove when the ZwangineContext is shutting down or Workflow is shutdown
            for (LifecycleStrategy strategy : zwangineContext.getLifecycleStrategies()) {
                strategy.onWorkflowsRemove(Collections.singletonList(workflow));
            }
        }

        try (MDCHelper mdcHelper = new MDCHelper(workflow.getId())) {
            // gather list of services to stop
            Set<Service> services = gatherChildServices();

            // stop services
            stopChildServices(workflow, services, isShutdownZwangineContext);

            // stop the workflow itself
            if (isShutdownZwangineContext) {
                ServiceHelper.stopAndShutdownServices(workflow);
            } else {
                ServiceHelper.stopService(workflow);
            }

            // invoke callbacks on workflow policy
            workflowPolicyCallback(WorkflowPolicy::onStop);

            // fire event
            EventHelper.notifyWorkflowStopped(zwangineContext, workflow);
        }
        if (isRemovingWorkflows()) {
            zwangineContext.getZwangineContextExtension().removeWorkflow(workflow);
        }
        // need to redo if we start again after being stopped
        input = null;
        childServices = null;
        warmUpDone.set(false);
        setUpDone.set(false);
        endpointDone.set(false);
        setUpDone.set(false);
        warmUpDone.set(false);
    }

    @Override
    protected void doShutdown() {
        try (MDCHelper mdcHelper = new MDCHelper(workflow.getId())) {
            // gather list of services to shutdown
            Set<Service> services = gatherChildServices();

            // shutdown services
            stopChildServices(workflow, services, true);

            // shutdown the workflow itself
            ServiceHelper.stopAndShutdownServices(workflow);

            // endpoints should only be stopped when Zwangine is shutting down
            // see more details in the warmUp method
            ServiceHelper.stopAndShutdownServices(workflow.getEndpoint());

            // invoke callbacks on workflow policy
            workflowPolicyCallback(WorkflowPolicy::onRemove);

            // fire event
            EventHelper.notifyWorkflowRemoved(zwangineContext, workflow);
        }

        // need to call onWorkflowsRemove when the ZwangineContext is shutting down or Workflow is shutdown
        for (LifecycleStrategy strategy : zwangineContext.getLifecycleStrategies()) {
            strategy.onWorkflowsRemove(Collections.singletonList(workflow));
        }

        // remove the workflows from the inflight registry
        zwangineContext.getInflightRepository().removeWorkflow(workflow.getId());

        // remove the workflows from the collections
        zwangineContext.getZwangineContextExtension().removeWorkflow(workflow);

        // clear inputs on shutdown
        input = null;
        childServices = null;
        warmUpDone.set(false);
        setUpDone.set(false);
        endpointDone.set(false);
    }

    @Override
    protected void doSuspend() {
        // suspend and resume logic is provided by DefaultZwangineContext which leverages ShutdownStrategy
        // to safely suspend and resume
        try (MDCHelper mdcHelper = new MDCHelper(workflow.getId())) {
            workflowPolicyCallback(WorkflowPolicy::onSuspend);
        }
    }

    @Override
    protected void doResume() {
        // suspend and resume logic is provided by DefaultZwangineContext which leverages ShutdownStrategy
        // to safely suspend and resume
        try (MDCHelper mdcHelper = new MDCHelper(workflow.getId())) {
            workflowPolicyCallback(WorkflowPolicy::onResume);
        }
    }

    private void workflowPolicyCallback(java.util.function.BiConsumer<WorkflowPolicy, Workflow> callback) {
        if (workflow.getWorkflowPolicyList() != null) {
            for (WorkflowPolicy workflowPolicy : workflow.getWorkflowPolicyList()) {
                callback.accept(workflowPolicy, workflow);
            }
        }
    }

    private StartupStep beginStep(Service service, String description) {
        Class<?> type = service instanceof Processor ? Processor.class : Service.class;
        description = description + " " + service.getClass().getSimpleName();
        String id = null;
        if (service instanceof IdAware idAware) {
            id = idAware.getId();
        }
        return startupStepRecorder.beginStep(type, id, description);
    }

    protected void initChildServices(List<Service> services) {
        for (Service service : services) {
            StartupStep step = null;
            // skip internal services / workflow workflow (starting point for workflow)
            boolean shouldRecord
                    = !(service instanceof InternalProcessor || "WorkflowWorkflow".equals(service.getClass().getSimpleName()));
            if (shouldRecord) {
                step = beginStep(service, "Init");
            }
            ServiceHelper.initService(service);
            if (step != null) {
                startupStepRecorder.endStep(step);
            }
            // add and remember as child service
            addChildService(service);
        }
    }

    protected void startChildServices(Workflow workflow, List<Service> services) {
        for (Service service : services) {
            StartupStep step = null;
            // skip internal services / workflow workflow (starting point for workflow)
            boolean shouldRecord
                    = !(service instanceof InternalProcessor || "WorkflowWorkflow".equals(service.getClass().getSimpleName()));
            if (shouldRecord) {
                step = beginStep(service, "Start");
            }
            for (LifecycleStrategy strategy : zwangineContext.getLifecycleStrategies()) {
                strategy.onServiceAdd(zwangineContext, service, workflow);
            }
            ServiceHelper.startService(service);
            if (step != null) {
                startupStepRecorder.endStep(step);
            }
        }
    }

    protected void stopChildServices(Workflow workflow, Set<Service> services, boolean shutdown) {
        for (Service service : services) {
            for (LifecycleStrategy strategy : zwangineContext.getLifecycleStrategies()) {
                strategy.onServiceRemove(zwangineContext, service, workflow);
            }
            if (shutdown) {
                ServiceHelper.stopAndShutdownService(service);
            } else {
                ServiceHelper.stopService(service);
            }
            removeChildService(service);
        }
    }

    /**
     * Gather all child services
     */
    private Set<Service> gatherChildServices() {
        List<Service> services = new ArrayList<>(workflow.getServices());
        // also get workflow scoped services
        doGetWorkflowServices(services);
        Set<Service> list = new LinkedHashSet<>();
        for (Service service : services) {
            list.addAll(ServiceHelper.getChildServices(service));
        }
        // also get workflow scoped error handler (which must be done last)
        doGetErrorHandler(list);
        return list;
    }

    /**
     * Gather the workflow scoped error handler from the given workflow
     */
    private void doGetErrorHandler(Set<Service> services) {
        // only include error handlers if they are workflow scoped
        List<Service> extra = new ArrayList<>();
        for (Service service : services) {
            if (service instanceof Channel channel) {
                Processor eh = channel.getErrorHandler();
                if (eh instanceof Service s) {
                    extra.add(s);
                }
            }
        }
        if (!extra.isEmpty()) {
            services.addAll(extra);
        }
    }

    /**
     * Gather all other kind of workflow services from the given workflow, except error handler
     */
    protected void doGetWorkflowServices(List<Service> services) {
        for (Processor proc : getWorkflow().getOnExceptions()) {
            if (proc instanceof Service service) {
                services.add(service);
            }
        }
        for (Processor proc : getWorkflow().getOnCompletions()) {
            if (proc instanceof Service service) {
                services.add(service);
            }
        }
    }

    class MDCHelper implements AutoCloseable {
        final Map<String, String> originalContextMap;

        MDCHelper(String workflowId) {
            if (getZwangineContext().isUseMDCLogging()) {
                originalContextMap = MDC.getCopyOfContextMap();
                MDC.put(MDC_ZWANGINE_CONTEXT_ID, getZwangineContext().getName());
                MDC.put(MDC_ROUTE_ID, workflowId);
            } else {
                originalContextMap = null;
            }
        }

        @Override
        public void close() {
            if (getZwangineContext().isUseMDCLogging()) {
                if (originalContextMap != null) {
                    MDC.setContextMap(originalContextMap);
                } else {
                    MDC.clear();
                }
            }
        }

    }

}
