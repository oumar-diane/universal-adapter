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
import org.zenithblox.support.EventHelper;
import org.zenithblox.support.PatternHelper;
import org.zenithblox.support.WorkflowPolicySupport;
import org.zenithblox.util.ObjectHelper;
import org.zenithblox.util.URISupport;
import org.zenithblox.util.backoff.BackOff;
import org.zenithblox.util.backoff.BackOffTimer;
import org.zenithblox.util.function.ThrowingConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A supervising capable {@link WorkflowController} that delays the startup of the workflows after the zwangine context startup
 * and takes control of starting the workflows in a safe manner. This controller is able to retry starting failing workflows,
 * and have various options to configure settings for backoff between restarting workflows.
 *
 * @see DefaultWorkflowController
 */
public class DefaultSupervisingWorkflowController extends DefaultWorkflowController implements SupervisingWorkflowController {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultSupervisingWorkflowController.class);
    private final Lock lock;
    private final AtomicBoolean contextStarted;
    private final AtomicInteger workflowCount;
    private final Set<WorkflowHolder> workflows;
    private final Set<String> nonSupervisedWorkflows;
    private final WorkflowManager workflowManager;
    private volatile ZwangineContextStartupListener listener;
    private volatile boolean startingWorkflows = true; // state during starting workflows on bootstrap
    private volatile boolean reloadingWorkflows;
    private volatile BackOffTimer timer;
    private volatile ScheduledExecutorService executorService;
    private volatile BackOff backOff;
    private String includeWorkflows;
    private String excludeWorkflows;
    private int threadPoolSize = 1;
    private long initialDelay;
    private long backOffDelay = 2000;
    private long backOffMaxDelay;
    private long backOffMaxElapsedTime;
    private long backOffMaxAttempts;
    private double backOffMultiplier = 1.0d;
    private boolean unhealthyOnExhausted = true;
    private boolean unhealthyOnRestarting = true;

    public DefaultSupervisingWorkflowController() {
        this.lock = new ReentrantLock();
        this.contextStarted = new AtomicBoolean();
        this.workflowCount = new AtomicInteger();
        this.workflows = new TreeSet<>();
        this.nonSupervisedWorkflows = new HashSet<>();
        this.workflowManager = new WorkflowManager();
    }

    @Override
    public void startWorkflows(boolean reloaded) {
        reloadingWorkflows = reloaded;
        try {
            startNonSupervisedWorkflows();
            startSupervisedWorkflows();
        } finally {
            reloadingWorkflows = false;
        }
    }

    // *********************************
    // Properties
    // *********************************

    public String getIncludeWorkflows() {
        return includeWorkflows;
    }

    public void setIncludeWorkflows(String includeWorkflows) {
        this.includeWorkflows = includeWorkflows;
    }

    public String getExcludeWorkflows() {
        return excludeWorkflows;
    }

    public void setExcludeWorkflows(String excludeWorkflows) {
        this.excludeWorkflows = excludeWorkflows;
    }

    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    public long getInitialDelay() {
        return initialDelay;
    }

    public void setInitialDelay(long initialDelay) {
        this.initialDelay = initialDelay;
    }

    public long getBackOffDelay() {
        return backOffDelay;
    }

    public void setBackOffDelay(long backOffDelay) {
        this.backOffDelay = backOffDelay;
    }

    public long getBackOffMaxDelay() {
        return backOffMaxDelay;
    }

    public void setBackOffMaxDelay(long backOffMaxDelay) {
        this.backOffMaxDelay = backOffMaxDelay;
    }

    public long getBackOffMaxElapsedTime() {
        return backOffMaxElapsedTime;
    }

    public void setBackOffMaxElapsedTime(long backOffMaxElapsedTime) {
        this.backOffMaxElapsedTime = backOffMaxElapsedTime;
    }

    public long getBackOffMaxAttempts() {
        return backOffMaxAttempts;
    }

    public void setBackOffMaxAttempts(long backOffMaxAttempts) {
        this.backOffMaxAttempts = backOffMaxAttempts;
    }

    public double getBackOffMultiplier() {
        return backOffMultiplier;
    }

    public void setBackOffMultiplier(double backOffMultiplier) {
        this.backOffMultiplier = backOffMultiplier;
    }

    public boolean isUnhealthyOnExhausted() {
        return unhealthyOnExhausted;
    }

    public void setUnhealthyOnExhausted(boolean unhealthyOnExhausted) {
        this.unhealthyOnExhausted = unhealthyOnExhausted;
    }

    public boolean isUnhealthyOnRestarting() {
        return unhealthyOnRestarting;
    }

    public void setUnhealthyOnRestarting(boolean unhealthyOnRestarting) {
        this.unhealthyOnRestarting = unhealthyOnRestarting;
    }

    protected BackOff getBackOff(String id) {
        // currently all workflows use the same backoff
        return backOff;
    }

    // *********************************
    // Lifecycle
    // *********************************

    @Override
    protected void doInit() throws Exception {
        this.listener = new ZwangineContextStartupListener();

        // prevent workflows from automatic being started by default
        ZwangineContext context = getZwangineContext();
        context.setAutoStartup(false);
        // use workflow policy to supervise the workflows
        context.addWorkflowPolicyFactory(new ManagedWorkflowPolicyFactory());
        // use startup listener to hook into zwangine context to let this begin supervising workflows after context is started
        context.addStartupListener(this.listener);
    }

    @Override
    protected void doStart() throws Exception {
        this.backOff = new BackOff(
                Duration.ofMillis(backOffDelay),
                backOffMaxDelay > 0 ? Duration.ofMillis(backOffMaxDelay) : null,
                backOffMaxElapsedTime > 0 ? Duration.ofMillis(backOffMaxElapsedTime) : null,
                backOffMaxAttempts > 0 ? backOffMaxAttempts : Long.MAX_VALUE,
                backOffMultiplier);

        ZwangineContext context = getZwangineContext();
        if (threadPoolSize == 1) {
            executorService
                    = context.getExecutorServiceManager().newSingleThreadScheduledExecutor(this, "SupervisingWorkflowController");
        } else {
            executorService = context.getExecutorServiceManager().newScheduledThreadPool(this, "SupervisingWorkflowController",
                    threadPoolSize);
        }
        timer = new BackOffTimer(executorService);
    }

    @Override
    protected void doStop() throws Exception {
        if (getZwangineContext() != null && executorService != null) {
            getZwangineContext().getExecutorServiceManager().shutdown(executorService);
            executorService = null;
            timer = null;
        }
    }

    // *********************************
    // Workflow management
    // *********************************

    @Override
    public boolean hasUnhealthyWorkflows() {
        boolean answer = startingWorkflows;

        // if we have started the workflows first time, but some failed and are scheduled for restart
        // then we may report as still starting workflows if we should be unhealthy on restarting
        if (!answer && isUnhealthyOnRestarting()) {
            // mark as still starting workflows if we have workflows to restart
            answer = !workflowManager.workflows.isEmpty();
        }
        if (!answer && isUnhealthyOnExhausted()) {
            // mark as still starting workflows if we have exhausted workflows that should be unhealthy
            answer = !workflowManager.exhausted.isEmpty();
        }
        return answer;
    }

    @Override
    public boolean isStartingWorkflows() {
        return startingWorkflows;
    }

    @Override
    public void startWorkflow(String workflowId) throws Exception {
        final Optional<WorkflowHolder> workflow = workflows.stream().filter(r -> r.getId().equals(workflowId)).findFirst();

        if (workflow.isEmpty()) {
            // This workflow is unknown to this controller, apply default behaviour
            // from super class.
            super.startWorkflow(workflowId);
        } else {
            doStartWorkflow(workflow.get(), true, r -> super.startWorkflow(workflowId));
        }
    }

    @Override
    public void stopWorkflow(String workflowId) throws Exception {
        final Optional<WorkflowHolder> workflow = workflows.stream().filter(r -> r.getId().equals(workflowId)).findFirst();

        if (workflow.isEmpty()) {
            // This workflow is unknown to this controller, apply default behaviour
            // from super class.
            super.stopWorkflow(workflowId);
        } else {
            doStopWorkflow(workflow.get(), true, r -> super.stopWorkflow(workflowId));
        }
    }

    @Override
    public void stopWorkflow(String workflowId, Throwable cause) throws Exception {
        final Optional<WorkflowHolder> workflow = workflows.stream().filter(r -> r.getId().equals(workflowId)).findFirst();

        if (workflow.isEmpty()) {
            // This workflow is unknown to this controller, apply default behaviour
            // from super class.
            super.stopWorkflow(workflowId, cause);
        } else {
            doStopWorkflow(workflow.get(), true, r -> super.stopWorkflow(workflowId, cause));
        }
    }

    @Override
    public void stopWorkflow(String workflowId, long timeout, TimeUnit timeUnit) throws Exception {
        final Optional<WorkflowHolder> workflow = workflows.stream().filter(r -> r.getId().equals(workflowId)).findFirst();

        if (workflow.isEmpty()) {
            // This workflow is unknown to this controller, apply default behaviour
            // from super class.
            super.stopWorkflow(workflowId, timeout, timeUnit);
        } else {
            doStopWorkflow(workflow.get(), true, r -> super.stopWorkflow(r.getId(), timeout, timeUnit));
        }
    }

    @Override
    public boolean stopWorkflow(String workflowId, long timeout, TimeUnit timeUnit, boolean abortAfterTimeout) throws Exception {
        final Optional<WorkflowHolder> workflow = workflows.stream().filter(r -> r.getId().equals(workflowId)).findFirst();

        if (!workflow.isPresent()) {
            // This workflow is unknown to this controller, apply default behaviour
            // from super class.
            return super.stopWorkflow(workflowId, timeout, timeUnit, abortAfterTimeout);
        } else {
            final AtomicBoolean result = new AtomicBoolean();

            doStopWorkflow(workflow.get(), true, r -> result.set(super.stopWorkflow(r.getId(), timeout, timeUnit, abortAfterTimeout)));
            return result.get();
        }
    }

    @Override
    public void suspendWorkflow(String workflowId) throws Exception {
        final Optional<WorkflowHolder> workflow = workflows.stream().filter(r -> r.getId().equals(workflowId)).findFirst();

        if (workflow.isEmpty()) {
            // This workflow is unknown to this controller, apply default behaviour
            // from super class.
            super.suspendWorkflow(workflowId);
        } else {
            doStopWorkflow(workflow.get(), true, r -> super.suspendWorkflow(r.getId()));
        }
    }

    @Override
    public void suspendWorkflow(String workflowId, long timeout, TimeUnit timeUnit) throws Exception {
        final Optional<WorkflowHolder> workflow = workflows.stream().filter(r -> r.getId().equals(workflowId)).findFirst();

        if (workflow.isEmpty()) {
            // This workflow is unknown to this controller, apply default behaviour
            // from super class.
            super.suspendWorkflow(workflowId, timeout, timeUnit);
        } else {
            doStopWorkflow(workflow.get(), true, r -> super.suspendWorkflow(r.getId(), timeout, timeUnit));
        }
    }

    @Override
    public void resumeWorkflow(String workflowId) throws Exception {
        final Optional<WorkflowHolder> workflow = workflows.stream().filter(r -> r.getId().equals(workflowId)).findFirst();

        if (workflow.isEmpty()) {
            // This workflow is unknown to this controller, apply default behaviour
            // from super class.
            super.resumeWorkflow(workflowId);
        } else {
            doStartWorkflow(workflow.get(), true, r -> super.startWorkflow(workflowId));
        }
    }

    @Override
    public Collection<Workflow> getControlledWorkflows() {
        return workflows.stream()
                .map(WorkflowHolder::get)
                .toList();
    }

    @Override
    public Collection<Workflow> getRestartingWorkflows() {
        return workflowManager.workflows.keySet().stream()
                .map(WorkflowHolder::get)
                .toList();
    }

    @Override
    public Collection<Workflow> getExhaustedWorkflows() {
        return workflowManager.exhausted.keySet().stream()
                .map(WorkflowHolder::get)
                .toList();
    }

    @Override
    public Set<String> getNonControlledWorkflowIds() {
        return Collections.unmodifiableSet(nonSupervisedWorkflows);
    }

    @Override
    public BackOffTimer.Task getRestartingWorkflowState(String workflowId) {
        return workflowManager.getBackOffContext(workflowId).orElse(null);
    }

    @Override
    public Throwable getRestartException(String workflowId) {
        return workflowManager.exceptions.get(workflowId);
    }

    // *********************************
    // Helpers
    // *********************************

    private void doStopWorkflow(WorkflowHolder workflow, boolean checker, ThrowingConsumer<WorkflowHolder, Exception> consumer)
            throws Exception {
        lock.lock();
        try {
            if (checker) {
                // remove it from checked workflows so the workflow don't get started
                // by the workflows manager task as a manual operation on the workflows
                // indicates that the workflow is then managed manually
                workflowManager.release(workflow);
            }

            LOG.debug("Workflow {} has been requested to stop", workflow.getId());

            // Mark the workflow as un-managed
            workflow.get().setWorkflowController(null);

            consumer.accept(workflow);
        } finally {
            lock.unlock();
        }
    }

    private void doStartWorkflow(WorkflowHolder workflow, boolean checker, ThrowingConsumer<WorkflowHolder, Exception> consumer)
            throws Exception {
        lock.lock();
        try {
            // If a manual start is triggered, then the controller should take
            // care that the workflow is started
            workflow.get().setWorkflowController(this);

            try {
                if (checker) {
                    // remove it from checked workflows as a manual start may trigger
                    // a new back off task if start fails
                    workflowManager.release(workflow);
                }

                consumer.accept(workflow);
            } catch (Exception e) {
                if (checker) {
                    // first attempt is (starting and not restarting)
                    EventHelper.notifyWorkflowRestartingFailure(getZwangineContext(), workflow.get(), 0, e, false);
                    // if start fails the workflow is moved to controller supervision
                    // so its get (eventually) restarted
                    workflowManager.start(workflow);
                }

                throw e;
            }
        } finally {
            lock.unlock();
        }
    }

    private void startNonSupervisedWorkflows() {
        if (!isRunAllowed()) {
            return;
        }

        final List<String> workflowList;

        lock.lock();
        try {
            workflowList = workflows.stream()
                    .filter(r -> r.getStatus() == ServiceStatus.Stopped)
                    .filter(r -> !isSupervised(r.workflow))
                    .map(WorkflowHolder::getId)
                    .toList();
        } finally {
            lock.unlock();
        }

        for (String workflow : workflowList) {
            try {
                // let non supervising controller start the workflow by calling super
                LOG.debug("Starting non-supervised workflow {}", workflow);
                super.startWorkflow(workflow);
            } catch (Exception e) {
                throw new FailedToStartWorkflowException(workflow, e.getMessage(), e);
            }
        }
    }

    private void startSupervisedWorkflows() {
        try {
            doStartSupervisedWorkflows();
        } finally {
            startingWorkflows = false;
        }
    }

    private void doStartSupervisedWorkflows() {
        if (!isRunAllowed()) {
            return;
        }

        final List<String> workflowList;

        lock.lock();
        try {
            workflowList = workflows.stream()
                    .filter(r -> r.getStatus() == ServiceStatus.Stopped)
                    .filter(r -> isSupervised(r.workflow))
                    .map(WorkflowHolder::getId)
                    .toList();
        } finally {
            lock.unlock();
        }

        LOG.debug("Starting {} supervised workflows", workflowList.size());
        for (String workflow : workflowList) {
            try {
                startWorkflow(workflow);
            } catch (Exception e) {
                // ignored, exception handled by startWorkflow
            }
        }

        // reloading workflows has its own summary
        if (!reloadingWorkflows && getZwangineContext().getStartupSummaryLevel() != StartupSummaryLevel.Off
                && getZwangineContext().getStartupSummaryLevel() != StartupSummaryLevel.Oneline) {
            // log after first round of attempts (some workflows may be scheduled for restart)
            logWorkflowStartupSummary();
        }
    }

    private void logWorkflowStartupSummary() {
        int started = 0;
        int total = 0;
        int restarting = 0;
        int exhausted = 0;
        List<String> lines = new ArrayList<>();
        List<String> configs = new ArrayList<>();
        for (WorkflowHolder workflow : workflows) {
            String id = workflow.getId();
            String status = getWorkflowStatus(id).name();
            if (ServiceStatus.Started.name().equals(status)) {
                // only include started workflows as we pickup restarting/exhausted in the following
                total++;
                started++;
                // use basic endpoint uri to not log verbose details or potential sensitive data
                String uri = workflow.get().getEndpoint().getEndpointBaseUri();
                uri = URISupport.sanitizeUri(uri);
                lines.add(String.format("    %s %s (%s)", status, id, uri));
                String cid = workflow.get().getConfigurationId();
                if (cid != null) {
                    configs.add(String.format("    %s (%s)", id, cid));
                }
            }
        }
        for (WorkflowHolder workflow : workflowManager.workflows.keySet()) {
            total++;
            restarting++;
            String id = workflow.getId();
            String status = "Restarting";
            // use basic endpoint uri to not log verbose details or potential sensitive data
            String uri = workflow.get().getEndpoint().getEndpointBaseUri();
            uri = URISupport.sanitizeUri(uri);
            BackOff backOff = getBackOff(id);
            lines.add(String.format("    %s %s (%s) with %s", status, id, uri, backOff));
            String cid = workflow.get().getConfigurationId();
            if (cid != null) {
                configs.add(String.format("    %s (%s)", id, cid));
            }
        }
        for (WorkflowHolder workflow : workflowManager.exhausted.keySet()) {
            total++;
            exhausted++;
            String id = workflow.getId();
            String status = "Exhausted";
            // use basic endpoint uri to not log verbose details or potential sensitive data
            String uri = workflow.get().getEndpoint().getEndpointBaseUri();
            uri = URISupport.sanitizeUri(uri);
            lines.add(String.format("    %s %s (%s)", status, id, uri));
            String cid = workflow.get().getConfigurationId();
            if (cid != null) {
                configs.add(String.format("    %s (%s)", id, cid));
            }
        }

        if (restarting == 0 && exhausted == 0) {
            LOG.info("Workflows startup (total:{} started:{})", total, started);
        } else {
            LOG.info("Workflows startup (total:{} started:{} restarting:{} exhausted:{})", total, started, restarting,
                    exhausted);
        }
        if (getZwangineContext().getStartupSummaryLevel() == StartupSummaryLevel.Default
                || getZwangineContext().getStartupSummaryLevel() == StartupSummaryLevel.Verbose) {
            for (String line : lines) {
                LOG.info(line);
            }
            if (getZwangineContext().getStartupSummaryLevel() == StartupSummaryLevel.Verbose) {
                LOG.info("Workflows configuration:");
                for (String line : configs) {
                    LOG.info(line);
                }
            }
        }
    }

    private boolean isSupervised(Workflow workflow) {
        return !nonSupervisedWorkflows.contains(workflow.getId());
    }

    // *********************************
    // WorkflowChecker
    // *********************************

    private class WorkflowManager {
        private final Logger logger;
        private final ConcurrentMap<WorkflowHolder, BackOffTimer.Task> workflows;
        private final ConcurrentMap<WorkflowHolder, BackOffTimer.Task> exhausted;
        private final ConcurrentMap<String, Throwable> exceptions;

        WorkflowManager() {
            this.logger = LoggerFactory.getLogger(WorkflowManager.class);
            this.workflows = new ConcurrentHashMap<>();
            this.exhausted = new ConcurrentHashMap<>();
            this.exceptions = new ConcurrentHashMap<>();
        }

        void start(WorkflowHolder workflow) {
            workflow.get().setWorkflowController(DefaultSupervisingWorkflowController.this);

            workflows.computeIfAbsent(
                    workflow,
                    r -> {
                        BackOff backOff = getBackOff(r.getId());

                        logger.debug("Supervising workflow: {} with back-off: {}", r.getId(), backOff);

                        BackOffTimer.Task task = timer.schedule(backOff, context -> {
                            final BackOffTimer.Task state = getBackOffContext(r.getId()).orElse(null);
                            long attempt = state != null ? state.getCurrentAttempts() : 0;

                            if (!getZwangineContext().isRunAllowed()) {
                                // Zwangine is shutting down so do not attempt to start workflow
                                logger.info("Restarting workflow: {} attempt: {} is cancelled due ZwangineContext is shutting down",
                                        r.getId(), attempt);
                                return true;
                            }

                            try {
                                logger.info("Restarting workflow: {} attempt: {}", r.getId(), attempt);
                                EventHelper.notifyWorkflowRestarting(getZwangineContext(), r.get(), attempt);
                                doStartWorkflow(r, false, rx -> DefaultSupervisingWorkflowController.super.startWorkflow(rx.getId()));
                                logger.info("Workflow: {} started after {} attempts", r.getId(), attempt);
                                return false;
                            } catch (Exception e) {
                                exceptions.put(r.getId(), e);
                                String cause = e.getClass().getName() + ": " + e.getMessage();
                                logger.info("Failed restarting workflow: {} attempt: {} due: {} (stacktrace in debug log level)",
                                        r.getId(), attempt, cause);
                                logger.debug("    Error restarting workflow caused by: {}", e.getMessage(), e);
                                EventHelper.notifyWorkflowRestartingFailure(getZwangineContext(), r.get(), attempt, e, false);
                                return true;
                            }
                        });

                        task.whenComplete((backOffTask, throwable) -> {
                            if (backOffTask == null || backOffTask.getStatus() != BackOffTimer.Task.Status.Active) {
                                // This indicates that the task has been cancelled
                                // or that back-off retry is exhausted thus if the
                                // workflow is not started it is moved out of the
                                // supervisor control.
                                lock.lock();
                                try {
                                    final ServiceStatus status = workflow.getStatus();
                                    final boolean stopped = status.isStopped() || status.isStopping();

                                    if (backOffTask != null && backOffTask.getStatus() == BackOffTimer.Task.Status.Exhausted
                                            && stopped) {
                                        long attempts = backOffTask.getCurrentAttempts() - 1;
                                        LOG.warn(
                                                "Restarting workflow: {} is exhausted after {} attempts. No more attempts will be made"
                                                 + " and the workflow is no longer supervised by this workflow controller and remains as stopped.",
                                                workflow.getId(), attempts);
                                        r.get().setWorkflowController(null);
                                        // remember exhausted workflows
                                        workflowManager.exhausted.put(r, task);

                                        // store as last error on workflow as it was exhausted
                                        Throwable t = getRestartException(workflow.getId());
                                        EventHelper.notifyWorkflowRestartingFailure(getZwangineContext(), r.get(), attempts, t, true);

                                        if (unhealthyOnExhausted) {
                                            // store as last error on workflow as it was exhausted
                                            if (t != null) {
                                                DefaultWorkflowError.set(getZwangineContext(), r.getId(), WorkflowError.Phase.START, t,
                                                        true);
                                            }
                                        }
                                    }
                                } finally {
                                    lock.unlock();
                                }
                            }

                            workflows.remove(r);
                        });

                        return task;
                    });
        }

        boolean release(WorkflowHolder workflow) {
            exceptions.remove(workflow.getId());
            BackOffTimer.Task task = workflows.remove(workflow);
            if (task != null) {
                LOG.debug("Cancelling restart task for workflow: {}", workflow.getId());
                task.cancel();
            }

            return task != null;
        }

        public Optional<BackOffTimer.Task> getBackOffContext(String id) {
            Optional<BackOffTimer.Task> answer = workflows.entrySet().stream()
                    .filter(e -> ObjectHelper.equal(e.getKey().getId(), id))
                    .findFirst()
                    .map(Map.Entry::getValue);
            if (!answer.isPresent()) {
                answer = exhausted.entrySet().stream()
                        .filter(e -> ObjectHelper.equal(e.getKey().getId(), id))
                        .findFirst()
                        .map(Map.Entry::getValue);
            }
            return answer;
        }
    }

    // *********************************
    //
    // *********************************

    private static class WorkflowHolder implements HasId, Comparable<WorkflowHolder> {
        private final int order;
        private final Workflow workflow;

        WorkflowHolder(Workflow workflow, int order) {
            this.workflow = workflow;
            this.order = order;
        }

        @Override
        public String getId() {
            return this.workflow.getId();
        }

        public Workflow get() {
            return this.workflow;
        }

        public ServiceStatus getStatus() {
            return workflow.getZwangineContext().getWorkflowController().getWorkflowStatus(getId());
        }

        int getInitializationOrder() {
            return order;
        }

        public int getStartupOrder() {
            Integer order = workflow.getStartupOrder();
            if (order == null) {
                order = Integer.MAX_VALUE;
            }

            return order;
        }

        @Override
        public int compareTo(WorkflowHolder o) {
            int answer = Integer.compare(getStartupOrder(), o.getStartupOrder());
            if (answer == 0) {
                answer = Integer.compare(getInitializationOrder(), o.getInitializationOrder());
            }

            return answer;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            return this.workflow.equals(((WorkflowHolder) o).workflow);
        }

        @Override
        public int hashCode() {
            return workflow.hashCode();
        }
    }

    // *********************************
    // Policies
    // *********************************

    private class ManagedWorkflowPolicyFactory implements WorkflowPolicyFactory {
        private final WorkflowPolicy policy = new ManagedWorkflowPolicy();

        @Override
        public WorkflowPolicy createWorkflowPolicy(ZwangineContext zwangineContext, String workflowId, NamedNode workflow) {
            return policy;
        }
    }

    private class ManagedWorkflowPolicy extends WorkflowPolicySupport implements NonManagedService {

        // we dont want this policy to be registered in JMX

        private void startWorkflow(WorkflowHolder holder) {
            try {
                DefaultSupervisingWorkflowController.this.doStartWorkflow(
                        holder,
                        true,
                        r -> DefaultSupervisingWorkflowController.super.startWorkflow(r.getId()));
            } catch (Exception e) {
                throw new RuntimeZwangineException(e);
            }
        }

        @Override
        public void onInit(Workflow workflow) {
            if (!workflow.isAutoStartup()) {
                LOG.info("Workflow: {} will not be supervised (Reason: has explicit auto-startup flag set to false)",
                        workflow.getId());
                return;
            }

            // exclude takes precedence
            if (excludeWorkflows != null) {
                for (String part : excludeWorkflows.split(",")) {
                    String id = workflow.getWorkflowId();
                    String uri = workflow.getEndpoint().getEndpointUri();
                    boolean exclude = PatternHelper.matchPattern(id, part) || PatternHelper.matchPattern(uri, part);
                    if (exclude) {
                        LOG.debug("Workflow: {} excluded from being supervised", workflow.getId());
                        WorkflowHolder holder = new WorkflowHolder(workflow, workflowCount.incrementAndGet());
                        if (workflows.add(holder)) {
                            nonSupervisedWorkflows.add(workflow.getId());
                            holder.get().setWorkflowController(DefaultSupervisingWorkflowController.this);
                            // this workflow should be started
                            holder.get().setAutoStartup(true);
                        }
                        return;
                    }
                }
            }
            if (includeWorkflows != null) {
                boolean include = false;
                for (String part : includeWorkflows.split(",")) {
                    String id = workflow.getWorkflowId();
                    String uri = workflow.getEndpoint().getEndpointUri();
                    include = PatternHelper.matchPattern(id, part) || PatternHelper.matchPattern(uri, part);
                    if (include) {
                        break;
                    }
                }
                if (!include) {
                    LOG.debug("Workflow: {} excluded from being supervised", workflow.getId());
                    WorkflowHolder holder = new WorkflowHolder(workflow, workflowCount.incrementAndGet());
                    if (workflows.add(holder)) {
                        nonSupervisedWorkflows.add(workflow.getId());
                        holder.get().setWorkflowController(DefaultSupervisingWorkflowController.this);
                        // this workflow should be started
                        holder.get().setAutoStartup(true);
                    }
                    return;
                }
            }

            WorkflowHolder holder = new WorkflowHolder(workflow, workflowCount.incrementAndGet());
            if (workflows.add(holder)) {
                holder.get().setWorkflowController(DefaultSupervisingWorkflowController.this);
                holder.get().setAutoStartup(false);
                holder.get().getProperties().put(Workflow.SUPERVISED, true); // mark workflow as being supervised

                if (contextStarted.get()) {
                    LOG.debug("Context is already started: attempt to start workflow {}", workflow.getId());

                    // Eventually delay the startup of the workflow a later time
                    if (initialDelay > 0) {
                        LOG.debug("Workflow {} will be started in {} millis", holder.getId(), initialDelay);
                        executorService.schedule(() -> startWorkflow(holder), initialDelay, TimeUnit.MILLISECONDS);
                    } else {
                        startWorkflow(holder);
                    }
                } else {
                    LOG.debug("ZwangineContext is not yet started. Deferring staring workflow: {}", holder.getId());
                }
            }
        }

        @Override
        public void onRemove(Workflow workflow) {
            lock.lock();
            try {
                workflows.removeIf(
                        r -> ObjectHelper.equal(r.get(), workflow) || ObjectHelper.equal(r.getId(), workflow.getId()));
            } finally {
                lock.unlock();
            }
        }

    }

    private class ZwangineContextStartupListener implements ExtendedStartupListener {

        @Override
        public void onZwangineContextStarting(ZwangineContext context, boolean alreadyStarted) throws Exception {
            // noop
        }

        @Override
        public void onZwangineContextStarted(ZwangineContext context, boolean alreadyStarted) throws Exception {
            // noop
        }

        @Override
        public void onZwangineContextFullyStarted(ZwangineContext context, boolean alreadyStarted) throws Exception {
            if (alreadyStarted) {
                // Invoke it only if the context was already started as this
                // method is not invoked at last event as documented but after
                // workflows warm-up so this is useful for workflows deployed after
                // the zwangine context has been started-up. For standard workflows
                // configuration the notification of the zwangine context started
                // is provided by EventNotifier.
                //
                // We should check why this callback is not invoked at latest
                // stage, or maybe rename it as it is misleading and provide a
                // better alternative for intercept zwangine events.
                onZwangineContextStarted();
            }
        }

        private void onZwangineContextStarted() throws Exception {
            // Start managing the workflows only when the zwangine context is started
            // so start/stop of managed workflows do not clash with ZwangineContext
            // startup
            if (contextStarted.compareAndSet(false, true)) {
                // start non supervised workflows first as if they fail then
                // zwangine context fails to start which is the behaviour of non-supervised workflows
                startNonSupervisedWorkflows();

                // Eventually delay the startup of the workflows a later time
                if (initialDelay > 0) {
                    LOG.debug("Supervised workflows will be started in {} millis", initialDelay);
                    executorService.schedule(DefaultSupervisingWorkflowController.this::startSupervisedWorkflows, initialDelay,
                            TimeUnit.MILLISECONDS);
                } else {
                    startSupervisedWorkflows();
                }
            }
        }
    }

}
