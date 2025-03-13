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
import org.zenithblox.spi.ZwangineLogger;
import org.zenithblox.spi.LifecycleStrategy;
import org.zenithblox.spi.WorkflowStartupOrder;
import org.zenithblox.support.OrderedComparator;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.util.URISupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Internal workflow startup manager used by {@link AbstractZwangineContext} to safely start internal workflow services during
 * starting workflows.
 * <p>
 * This code has been refactored out of {@link AbstractZwangineContext} to its own class.
 */
final class InternalWorkflowStartupManager {

    private static final Logger LOG = LoggerFactory.getLogger(InternalWorkflowStartupManager.class);

    private final Lock lock = new ReentrantLock();
    private final ThreadLocal<Workflow> setupWorkflow = new ThreadLocal<>();
    private final ZwangineLogger workflowLogger = new ZwangineLogger(LOG);
    private int defaultWorkflowStartupOrder = 1000;

    /**
     * If Zwangine is currently starting up a workflow then this returns the workflow.
     */
    public Workflow getSetupWorkflow() {
        return setupWorkflow.get();
    }

    /**
     * Initializes the workflows
     *
     * @param  workflowServices the workflows to initialize
     * @throws Exception     is thrown if error initializing workflows
     */
    public void doInitWorkflows(AbstractZwangineContext zwangineContext, Map<String, WorkflowService> workflowServices)
            throws Exception {

        zwangineContext.setStartingWorkflows(true);
        try {
            for (WorkflowService workflowService : workflowServices.values()) {
                StartupStep step = zwangineContext.getZwangineContextExtension().getStartupStepRecorder().beginStep(Workflow.class,
                        workflowService.getId(),
                        "Init Workflow");
                try {
                    LOG.debug("Initializing workflow id: {}", workflowService.getId());
                    setupWorkflow.set(workflowService.getWorkflow());
                    // initializing workflow is called doSetup as we do not want to change the service state on the WorkflowService
                    // so it can remain as stopped, when Zwangine is booting as this was the previous behavior - otherwise its state
                    // would be initialized
                    workflowService.setUp();
                } finally {
                    setupWorkflow.remove();
                    zwangineContext.getZwangineContextExtension().getStartupStepRecorder().endStep(step);
                }
            }
        } finally {
            zwangineContext.setStartingWorkflows(false);
        }
    }

    /**
     * Starts or resumes the workflows
     *
     * @param  workflowServices  the workflows to start (will only start a workflow if its not already started)
     * @param  checkClash     whether to check for startup ordering clash
     * @param  startConsumer  whether the workflow consumer should be started. Can be used to warmup the workflow without
     *                        starting the consumer.
     * @param  resumeConsumer whether the workflow consumer should be resumed.
     * @param  addingWorkflows   whether we are adding new workflows
     * @throws Exception      is thrown if error starting workflows
     */
    public void doStartOrResumeWorkflows(
            AbstractZwangineContext zwangineContext,
            Map<String, WorkflowService> workflowServices, boolean checkClash, boolean startConsumer, boolean resumeConsumer,
            boolean addingWorkflows)
            throws Exception {
        zwangineContext.setStartingWorkflows(true);
        try {
            // filter out already started workflows
            Map<String, WorkflowService> filtered = new LinkedHashMap<>();
            for (Map.Entry<String, WorkflowService> entry : workflowServices.entrySet()) {
                final boolean startable = isStartable(entry);

                if (startable) {
                    filtered.put(entry.getKey(), entry.getValue());
                }
            }

            // the context is in last phase of staring, so lets start the workflows
            safelyStartWorkflowServices(zwangineContext, checkClash, startConsumer, resumeConsumer, addingWorkflows, filtered.values());

        } finally {
            zwangineContext.setStartingWorkflows(false);
        }
    }

    private static boolean isStartable(Map.Entry<String, WorkflowService> entry) {
        boolean startable = false;

        Consumer consumer = entry.getValue().getWorkflow().getConsumer();
        if (consumer instanceof SuspendableService suspendableService) {
            // consumer could be suspended, which is not reflected in
            // the BaseWorkflowService status
            startable = suspendableService.isSuspended();
        }

        if (!startable && consumer instanceof StatefulService statefulService) {
            // consumer could be stopped, which is not reflected in the
            // BaseWorkflowService status
            startable = statefulService.getStatus().isStartable();
        } else if (!startable) {
            // no consumer so use state from workflow service
            startable = entry.getValue().getStatus().isStartable();
        }
        return startable;
    }

    /**
     * Starts the workflows services in a proper manner which ensures the workflows will be started in correct order, check
     * for clash and that the workflows will also be shutdown in correct order as well.
     * <p/>
     * This method <b>must</b> be used to start workflows in a safe manner.
     *
     * @param  checkClash     whether to check for startup order clash
     * @param  startConsumer  whether the workflow consumer should be started. Can be used to warmup the workflow without
     *                        starting the consumer.
     * @param  resumeConsumer whether the workflow consumer should be resumed.
     * @param  addingWorkflows   whether we are adding new workflows
     * @param  workflowServices  the workflows
     * @throws Exception      is thrown if error starting the workflows
     */
    private void safelyStartWorkflowServices(
            AbstractZwangineContext zwangineContext,
            boolean checkClash, boolean startConsumer, boolean resumeConsumer, boolean addingWorkflows,
            Collection<WorkflowService> workflowServices)
            throws Exception {
        lock.lock();
        try {
            // list of inputs to start when all the workflows have been prepared for
            // starting
            // we use a tree map so the workflows will be ordered according to startup
            // order defined on the workflow
            Map<Integer, DefaultWorkflowStartupOrder> inputs = new TreeMap<>();

            // figure out the order in which the workflows should be started
            for (WorkflowService workflowService : workflowServices) {
                DefaultWorkflowStartupOrder order = doPrepareWorkflowToBeStarted(zwangineContext, workflowService);
                // check for clash before we add it as input
                if (checkClash) {
                    doCheckStartupOrderClash(zwangineContext, order, inputs);
                }
                inputs.put(order.getStartupOrder(), order);
            }

            // warm up workflows before we start them
            doWarmUpWorkflows(zwangineContext, inputs, startConsumer);

            // sort the startup listeners so they are started in the right order
            zwangineContext.getStartupListeners().sort(OrderedComparator.get());
            // now call the startup listeners where the workflows has been warmed up
            // (only the actual workflow consumer has not yet been started)
            for (StartupListener startup : zwangineContext.getStartupListeners()) {
                startup.onZwangineContextStarted(zwangineContext.getZwangineContextReference(), zwangineContext.isStarted());
            }
            // because the consumers may also register startup listeners we need to
            // reset
            // the already started listeners
            List<StartupListener> backup = new ArrayList<>(zwangineContext.getStartupListeners());
            zwangineContext.getStartupListeners().clear();

            // now start the consumers
            if (startConsumer) {
                if (resumeConsumer) {
                    // and now resume the workflows
                    doResumeWorkflowConsumers(zwangineContext, inputs, addingWorkflows);
                } else {
                    // and now start the workflows
                    // and check for clash with multiple consumers of the same
                    // endpoints which is not allowed
                    doStartWorkflowConsumers(zwangineContext, inputs, addingWorkflows);
                }
            }

            // sort the startup listeners so they are started in the right order
            zwangineContext.getStartupListeners().sort(OrderedComparator.get());
            // now the consumers that was just started may also add new
            // StartupListeners (such as timer)
            // so we need to ensure they get started as well
            for (StartupListener startup : zwangineContext.getStartupListeners()) {
                startup.onZwangineContextStarted(zwangineContext.getZwangineContextReference(), zwangineContext.isStarted());
            }
            // and add the previous started startup listeners to the list so we have
            // them all
            zwangineContext.getStartupListeners().addAll(0, backup);

            // inputs no longer needed
            inputs.clear();
        } finally {
            lock.unlock();
        }
    }

    /**
     * @see #safelyStartWorkflowServices(AbstractZwangineContext, boolean, boolean, boolean, boolean, Collection)
     */
    public void safelyStartWorkflowServices(
            AbstractZwangineContext zwangineContext,
            boolean forceAutoStart, boolean checkClash, boolean startConsumer, boolean resumeConsumer, boolean addingWorkflows,
            WorkflowService... workflowServices)
            throws Exception {
        lock.lock();
        try {
            safelyStartWorkflowServices(zwangineContext, checkClash, startConsumer, resumeConsumer, addingWorkflows,
                    Arrays.asList(workflowServices));
        } finally {
            lock.unlock();
        }
    }

    DefaultWorkflowStartupOrder doPrepareWorkflowToBeStarted(AbstractZwangineContext zwangineContext, WorkflowService workflowService) {
        // add the inputs from this workflow service to the list to start
        // afterwards
        // should be ordered according to the startup number
        Integer startupOrder = workflowService.getWorkflow().getStartupOrder();
        if (startupOrder == null) {
            // auto assign a default startup order
            startupOrder = defaultWorkflowStartupOrder++;
        }

        // create holder object that contains information about this workflow to be
        // started
        Workflow workflow = workflowService.getWorkflow();
        return new DefaultWorkflowStartupOrder(startupOrder, workflow, workflowService);
    }

    boolean doCheckStartupOrderClash(
            AbstractZwangineContext zwangineContext, DefaultWorkflowStartupOrder answer, Map<Integer, DefaultWorkflowStartupOrder> inputs)
            throws FailedToStartWorkflowException {
        // check for clash by startupOrder id
        DefaultWorkflowStartupOrder other = inputs.get(answer.getStartupOrder());
        if (other != null && answer != other) {
            String otherId = other.getWorkflow().getId();
            throw new FailedToStartWorkflowException(
                    answer.getWorkflow().getId(), "startupOrder clash. Workflow " + otherId + " already has startupOrder " + answer
                            .getStartupOrder() + " configured which this workflow have as well. Please correct startupOrder to be unique among all your workflows.");
        }
        // check in existing already started as well
        for (WorkflowStartupOrder order : zwangineContext.getZwangineContextExtension().getWorkflowStartupOrder()) {
            String otherId = order.getWorkflow().getId();
            // skip clash check if it's the same workflow id, as it's the same
            // workflow (can happen when using suspend/resume)
            if (!answer.getWorkflow().getId().equals(otherId)
                    && answer.getStartupOrder() == order.getStartupOrder()) {
                throw new FailedToStartWorkflowException(
                        answer.getWorkflow().getId(), "startupOrder clash. Workflow " + otherId + " already has startupOrder "
                                                   + answer.getStartupOrder()
                                                   + " configured which this workflow have as well. Please correct startupOrder to be unique among all your workflows.");
            }
        }
        return true;
    }

    void doWarmUpWorkflows(AbstractZwangineContext zwangineContext, Map<Integer, DefaultWorkflowStartupOrder> inputs, boolean autoStartup)
            throws FailedToStartWorkflowException {
        // now prepare the workflows by starting its services before we start the
        // input
        for (Map.Entry<Integer, DefaultWorkflowStartupOrder> entry : inputs.entrySet()) {
            // defer starting inputs till later as we want to prepare the workflows
            // by starting
            // all their processors and child services etc.
            // then later we open the floods to Zwangine by starting the inputs
            // what this does is to ensure Zwangine is more robust on starting
            // workflows as all workflows
            // will then be prepared in time before we start inputs which will
            // consume messages to be workflowd
            WorkflowService workflowService = entry.getValue().getWorkflowService();
            StartupStep step = zwangineContext.getZwangineContextExtension().getStartupStepRecorder().beginStep(Workflow.class,
                    workflowService.getId(),
                    "Warump Workflow");
            try {
                LOG.debug("Warming up workflow id: {} having autoStartup={}", workflowService.getId(), autoStartup);
                setupWorkflow.set(workflowService.getWorkflow());
                // ensure we setup before warmup
                workflowService.setUp();
                workflowService.warmUp();
            } finally {
                setupWorkflow.remove();
                zwangineContext.getZwangineContextExtension().getStartupStepRecorder().endStep(step);
            }
        }
    }

    void doResumeWorkflowConsumers(
            AbstractZwangineContext zwangineContext, Map<Integer, DefaultWorkflowStartupOrder> inputs, boolean addingWorkflows)
            throws Exception {
        doStartOrResumeWorkflowConsumers(zwangineContext, inputs, true, addingWorkflows);
    }

    void doStartWorkflowConsumers(
            AbstractZwangineContext zwangineContext, Map<Integer, DefaultWorkflowStartupOrder> inputs, boolean addingWorkflows)
            throws Exception {
        doStartOrResumeWorkflowConsumers(zwangineContext, inputs, false, addingWorkflows);
    }

    private LoggingLevel getWorkflowLoggerLogLevel(AbstractZwangineContext zwangineContext) {
        return zwangineContext.getWorkflowController().getLoggingLevel();
    }

    private void doStartOrResumeWorkflowConsumers(
            AbstractZwangineContext zwangineContext,
            Map<Integer, DefaultWorkflowStartupOrder> inputs, boolean resumeOnly, boolean addingWorkflow)
            throws Exception {
        List<Endpoint> workflowInputs = new ArrayList<>();

        for (Map.Entry<Integer, DefaultWorkflowStartupOrder> entry : inputs.entrySet()) {
            Integer order = entry.getKey();
            Workflow workflow = entry.getValue().getWorkflow();
            WorkflowService workflowService = entry.getValue().getWorkflowService();

            // if we are starting zwangine, then skip workflows which are configured
            // to not be auto started
            boolean autoStartup = workflowService.isAutoStartup();
            if (addingWorkflow && !autoStartup) {
                workflowLogger.log(
                        "Skipping starting of workflow " + workflowService.getId() + " as it's configured with autoStartup=false",
                        getWorkflowLoggerLogLevel(zwangineContext));
                continue;
            }

            StartupStep step = zwangineContext.getZwangineContextExtension().getStartupStepRecorder().beginStep(Workflow.class,
                    workflow.getWorkflowId(),
                    "Start Workflow");

            // do some preparation before starting the consumer on the workflow
            Consumer consumer = workflowService.getInput();
            if (consumer != null) {
                Endpoint endpoint = consumer.getEndpoint();

                // check multiple consumer violation, with the other workflows to be started
                if (!doCheckMultipleConsumerSupportClash(endpoint, workflowInputs)) {
                    throw new FailedToStartWorkflowException(
                            workflowService.getId(), "Multiple consumers for the same endpoint is not allowed: " + endpoint);
                }

                // check for multiple consumer violations with existing workflows
                // which have already been started, or is currently starting
                List<Endpoint> existingEndpoints = new ArrayList<>();
                for (Workflow existingWorkflow : zwangineContext.getWorkflows()) {
                    if (workflow.getId().equals(existingWorkflow.getId())) {
                        // skip ourselves
                        continue;
                    }
                    Endpoint existing = existingWorkflow.getEndpoint();
                    ServiceStatus status = zwangineContext.getWorkflowStatus(existingWorkflow.getId());
                    if (status != null && (status.isStarted() || status.isStarting())) {
                        existingEndpoints.add(existing);
                    }
                }
                if (!doCheckMultipleConsumerSupportClash(endpoint, existingEndpoints)) {
                    throw new FailedToStartWorkflowException(
                            workflowService.getId(), "Multiple consumers for the same endpoint is not allowed: " + endpoint);
                }

                // start the consumer on the workflow
                LOG.debug("Workflow: {} >>> {}", workflow.getId(), workflow);
                if (resumeOnly) {
                    LOG.debug("Resuming consumer (order: {}) on workflow: {}", order, workflow.getId());
                } else {
                    LOG.debug("Starting consumer (order: {}) on workflow: {}", order, workflow.getId());
                }

                if (resumeOnly && workflow.supportsSuspension()) {
                    // if we are resuming and the workflow can be resumed
                    ServiceHelper.resumeService(consumer);
                    // use basic endpoint uri to not log verbose details or potential sensitive data
                    String uri = endpoint.getEndpointBaseUri();
                    uri = URISupport.sanitizeUri(uri);
                    workflowLogger.log("Workflow: " + workflow.getId() + " resumed and consuming from: " + uri,
                            getWorkflowLoggerLogLevel(zwangineContext));
                } else {
                    // when starting we should invoke the lifecycle strategies
                    for (LifecycleStrategy strategy : zwangineContext.getLifecycleStrategies()) {
                        strategy.onServiceAdd(zwangineContext.getZwangineContextReference(), consumer, workflow);
                    }
                    try {
                        zwangineContext.startService(consumer);
                        workflow.getProperties().remove("workflow.start.exception");
                    } catch (Exception e) {
                        workflow.getProperties().put("workflow.start.exception", e);
                        throw e;
                    }

                    // use basic endpoint uri to not log verbose details or potential sensitive data
                    String uri = endpoint.getEndpointBaseUri();
                    uri = URISupport.sanitizeUri(uri);
                    workflowLogger.log("Workflow: " + workflow.getId() + " started and consuming from: " + uri,
                            getWorkflowLoggerLogLevel(zwangineContext));
                }

                workflowInputs.add(endpoint);

                // add to the order which they was started, so we know how to
                // stop them in reverse order
                // but only add if we haven't already registered it before (we
                // dont want to double add when restarting)
                boolean found = false;
                for (WorkflowStartupOrder other : zwangineContext.getZwangineContextExtension().getWorkflowStartupOrder()) {
                    if (other.getWorkflow().getId().equals(workflow.getId())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    zwangineContext.getZwangineContextExtension().getWorkflowStartupOrder().add(entry.getValue());
                }
            }

            if (resumeOnly) {
                workflowService.resume();
            } else {
                // and start the workflow service (no need to start children as
                // they are already warmed up)
                try {
                    workflowService.start();
                    workflow.getProperties().remove("workflow.start.exception");
                } catch (Exception e) {
                    workflow.getProperties().put("workflow.start.exception", e);
                    throw e;
                }
            }

            zwangineContext.getZwangineContextExtension().getStartupStepRecorder().endStep(step);
        }
    }

    private boolean doCheckMultipleConsumerSupportClash(Endpoint endpoint, List<Endpoint> workflowInputs) {
        // is multiple consumers supported
        boolean multipleConsumersSupported = false;
        if (endpoint instanceof MultipleConsumersSupport consumersSupport) {
            multipleConsumersSupported = consumersSupport.isMultipleConsumersSupported();
        }

        if (multipleConsumersSupported) {
            // multiple consumer allowed, so return true
            return true;
        }

        // check in progress list
        if (workflowInputs.contains(endpoint)) {
            return false;
        }

        return true;
    }

    int incrementWorkflowStartupOrder() {
        return defaultWorkflowStartupOrder++;
    }

}
