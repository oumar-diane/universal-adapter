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
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.support.service.ServiceSupport;
import org.zenithblox.util.ObjectHelper;
import org.zenithblox.util.StopWatch;
import org.zenithblox.util.TimeUtils;
import org.zenithblox.util.URISupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Default {@link org.zenithblox.spi.ShutdownStrategy} which uses graceful shutdown.
 * <p/>
 * Graceful shutdown ensures that any inflight and pending messages will be taken into account and it will wait until
 * these exchanges has been completed.
 * <p/>
 * This strategy will perform graceful shutdown in two steps:
 * <ul>
 * <li>Graceful - By suspending/stopping consumers, and let any in-flight exchanges complete</li>
 * <li>Forced - After a given period of time, a timeout occurred and if there are still pending exchanges to complete,
 * then a more aggressive forced strategy is performed.</li>
 * </ul>
 * The idea by the <tt>graceful</tt> shutdown strategy, is to stop taking in more new messages, and allow any existing
 * inflight messages to complete. Then when there is no more inflight messages then the workflows can be fully shutdown.
 * This mean that if there is inflight messages then we will have to wait for these messages to complete. If they do not
 * complete after a period of time, then a timeout triggers. And then a more aggressive strategy takes over.
 * <p/>
 * The idea by the <tt>forced</tt> shutdown strategy, is to stop continue processing messages. And force workflows and its
 * services to shutdown now. There is a risk when shutting down now, that some resources is not properly shutdown, which
 * can cause side effects. The timeout value is by default 45 seconds, but can be customized.
 * <p/>
 * As this strategy will politely wait until all exchanges has been completed it can potential wait for a long time, and
 * hence why a timeout value can be set. When the timeout triggers you can also specify whether the remainder consumers
 * should be shutdown now or ignore.
 * <p/>
 * Will by default use a timeout of 45 seconds by which it will shutdown now the remaining consumers. This ensures that
 * when shutting down Zwangine it at some point eventually will shutdown. This behavior can of course be configured using
 * the {@link #setTimeout(long)} and {@link #setShutdownNowOnTimeout(boolean)} methods.
 * <p/>
 * Workflows will by default be shutdown in the reverse order of which they where started. You can customize this using the
 * {@link #setShutdownWorkflowsInReverseOrder(boolean)} method.
 * <p/>
 * After workflow consumers have been shutdown, then any {@link ShutdownPrepared} services on the workflows is being prepared
 * for shutdown, by invoking {@link ShutdownPrepared#prepareShutdown(boolean,boolean)} which <tt>force=false</tt>.
 * <p/>
 * Then if a timeout occurred and the strategy has been configured with shutdown-now on timeout, then the strategy
 * performs a more aggressive forced shutdown, by forcing all consumers to shutdown and then invokes
 * {@link ShutdownPrepared#prepareShutdown(boolean,boolean)} with <tt>force=true</tt> on the services. This allows the
 * services to know they should force shutdown now.
 * <p/>
 * When timeout occurred and a forced shutdown is happening, then there may be threads/tasks which are still inflight
 * which may be rejected continued being workflowd. By default this can cause WARN and ERRORs to be logged. The option
 * {@link #setSuppressLoggingOnTimeout(boolean)} can be used to suppress these logs, so they are logged at TRACE level
 * instead.
 * <p/>
 * Also when a timeout occurred then information about the inflight exchanges is logged, if
 * {@link #isLogInflightExchangesOnTimeout()} is enabled (is by default). This allows end users to known where these
 * inflight exchanges currently are in the workflow(s), and how long time they have been inflight.
 * <p/>
 * This information can also be obtained from the {@link org.zenithblox.spi.InflightRepository} at all time during
 * runtime.
 */
public class DefaultShutdownStrategy extends ServiceSupport implements ShutdownStrategy, ZwangineContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultShutdownStrategy.class);
    private final ZwangineLogger logger = new ZwangineLogger(LOG, LoggingLevel.DEBUG);

    private ZwangineContext zwangineContext;
    private ExecutorService executor;
    private long timeout = 45;
    private TimeUnit timeUnit = TimeUnit.SECONDS;
    private boolean shutdownNowOnTimeout = true;
    private boolean shutdownWorkflowsInReverseOrder = true;
    private boolean suppressLoggingOnTimeout;
    private boolean logInflightExchangesOnTimeout = true;

    private boolean forceShutdown;
    private final AtomicBoolean timeoutOccurred = new AtomicBoolean();
    private volatile Future<?> currentShutdownTaskFuture;

    public DefaultShutdownStrategy() {
    }

    public DefaultShutdownStrategy(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    @Override
    public void shutdown(ZwangineContext context, List<WorkflowStartupOrder> workflows) throws Exception {
        shutdown(context, workflows, getTimeout(), getTimeUnit());
    }

    @Override
    public void shutdownForced(ZwangineContext context, List<WorkflowStartupOrder> workflows) throws Exception {
        doShutdown(context, workflows, getTimeout(), getTimeUnit(), false, false, true);
    }

    @Override
    public void suspend(ZwangineContext context, List<WorkflowStartupOrder> workflows) throws Exception {
        doShutdown(context, workflows, getTimeout(), getTimeUnit(), true, false, false);
    }

    @Override
    public void shutdown(ZwangineContext context, List<WorkflowStartupOrder> workflows, long timeout, TimeUnit timeUnit)
            throws Exception {
        doShutdown(context, workflows, timeout, timeUnit, false, false, false);
    }

    @Override
    public boolean shutdown(
            ZwangineContext context, WorkflowStartupOrder workflow, long timeout, TimeUnit timeUnit, boolean abortAfterTimeout)
            throws Exception {
        List<WorkflowStartupOrder> workflows = Collections.singletonList(workflow);
        return doShutdown(context, workflows, timeout, timeUnit, false, abortAfterTimeout, false);
    }

    @Override
    public void suspend(ZwangineContext context, List<WorkflowStartupOrder> workflows, long timeout, TimeUnit timeUnit)
            throws Exception {
        doShutdown(context, workflows, timeout, timeUnit, true, false, false);
    }

    protected boolean doShutdown(
            ZwangineContext context, List<WorkflowStartupOrder> workflows, long timeout, TimeUnit timeUnit,
            boolean suspendOnly, boolean abortAfterTimeout, boolean forceShutdown)
            throws Exception {

        // timeout must be a positive value
        if (timeout <= 0) {
            throw new IllegalArgumentException("Timeout must be a positive value");
        }

        // just return if no workflows to shutdown
        if (workflows.isEmpty()) {
            return true;
        }

        StopWatch watch = new StopWatch();

        // at first sort according to workflow startup order
        Comparator<WorkflowStartupOrder> comparator = Comparator.comparingInt(WorkflowStartupOrder::getStartupOrder);
        if (shutdownWorkflowsInReverseOrder) {
            comparator = comparator.reversed();
        }
        List<WorkflowStartupOrder> workflowsOrdered = new ArrayList<>(workflows);
        workflowsOrdered.sort(comparator);

        if (logger.shouldLog()) {
            final String action = suspendOnly ? "suspend" : "shutdown";

            String msg = String.format("Starting to graceful %s %s workflows (timeout %s %s)", action, workflowsOrdered.size(),
                    timeout, timeUnit.toString().toLowerCase(Locale.ENGLISH));
            logger.log(msg);
        }

        // use another thread to perform the shutdowns so we can support timeout
        timeoutOccurred.set(false);
        try {
            currentShutdownTaskFuture = getExecutorService().submit(new ShutdownTask(
                    context, workflowsOrdered, timeout, timeUnit, suspendOnly,
                    abortAfterTimeout, timeoutOccurred, isLogInflightExchangesOnTimeout()));
            currentShutdownTaskFuture.get(timeout, timeUnit);
        } catch (RejectedExecutionException e) {
            // the task was rejected
        } catch (ExecutionException e) {
            // unwrap execution exception
            throw RuntimeZwangineException.wrapRuntimeZwangineException(e.getCause());
        } catch (Exception e) {
            // either timeout or interrupted exception was thrown so this is okay
            // as interrupted would mean cancel was called on the currentShutdownTaskFuture to signal a forced timeout

            // we hit a timeout, so set the flag
            timeoutOccurred.set(true);

            // timeout then cancel the task
            if (currentShutdownTaskFuture != null) {
                currentShutdownTaskFuture.cancel(true);
            }

            // signal we are forcing shutdown now, since timeout occurred
            this.forceShutdown = forceShutdown;

            // if set, stop processing and return false to indicate that the shutdown is aborting
            if (!forceShutdown && abortAfterTimeout) {
                LOG.warn("Timeout occurred during graceful shutdown. Aborting the shutdown now."
                         + " Notice: some resources may still be running as graceful shutdown did not complete successfully.");

                // we attempt to force shutdown so lets log the current inflight exchanges which are affected
                logInflightExchanges(context, workflows, isLogInflightExchangesOnTimeout());

                return false;
            } else {
                if (forceShutdown || shutdownNowOnTimeout) {
                    LOG.warn("Timeout occurred during graceful shutdown. Forcing the workflows to be shutdown now."
                             + " Notice: some resources may still be running as graceful shutdown did not complete successfully.");

                    // we attempt to force shutdown so lets log the current inflight exchanges which are affected
                    logInflightExchanges(context, workflows, isLogInflightExchangesOnTimeout());

                    // force the workflows to shutdown now
                    shutdownWorkflowsNow(workflowsOrdered);

                    // now the workflow consumers has been shutdown, then prepare workflow services for shutdown now (forced)
                    for (WorkflowStartupOrder order : workflows) {
                        for (Service service : order.getServices()) {
                            prepareShutdown(service, false, true, true, isSuppressLoggingOnTimeout());
                        }
                    }
                } else {
                    LOG.warn("Timeout occurred during graceful shutdown. Will ignore shutting down the remainder workflows."
                             + " Notice: some resources may still be running as graceful shutdown did not complete successfully.");

                    logInflightExchanges(context, workflows, isLogInflightExchangesOnTimeout());
                }
            }
        } finally {
            currentShutdownTaskFuture = null;
        }

        if (logger.shouldLog()) {
            logger.log(String.format("Graceful shutdown of %s workflows completed in %s", workflowsOrdered.size(),
                    TimeUtils.printDuration(watch.taken(), true)));
        }
        return true;
    }

    @Override
    public boolean isForceShutdown() {
        return forceShutdown;
    }

    @Override
    public boolean hasTimeoutOccurred() {
        return isTimeoutOccurred();
    }

    @Override
    public boolean isTimeoutOccurred() {
        return timeoutOccurred.get();
    }

    @Override
    public void setTimeout(long timeout) {
        if (timeout <= 0) {
            throw new IllegalArgumentException("Timeout must be a positive value");
        }
        this.timeout = timeout;
    }

    @Override
    public long getTimeout() {
        return timeout;
    }

    @Override
    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    @Override
    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    @Override
    public void setShutdownNowOnTimeout(boolean shutdownNowOnTimeout) {
        this.shutdownNowOnTimeout = shutdownNowOnTimeout;
    }

    @Override
    public boolean isShutdownNowOnTimeout() {
        return shutdownNowOnTimeout;
    }

    @Override
    public boolean isShutdownWorkflowsInReverseOrder() {
        return shutdownWorkflowsInReverseOrder;
    }

    @Override
    public void setShutdownWorkflowsInReverseOrder(boolean shutdownWorkflowsInReverseOrder) {
        this.shutdownWorkflowsInReverseOrder = shutdownWorkflowsInReverseOrder;
    }

    @Override
    public boolean isSuppressLoggingOnTimeout() {
        return suppressLoggingOnTimeout;
    }

    @Override
    public void setSuppressLoggingOnTimeout(boolean suppressLoggingOnTimeout) {
        this.suppressLoggingOnTimeout = suppressLoggingOnTimeout;
    }

    @Override
    public boolean isLogInflightExchangesOnTimeout() {
        return logInflightExchangesOnTimeout;
    }

    @Override
    public void setLogInflightExchangesOnTimeout(boolean logInflightExchangesOnTimeout) {
        this.logInflightExchangesOnTimeout = logInflightExchangesOnTimeout;
    }

    @Override
    public LoggingLevel getLoggingLevel() {
        return logger.getLevel();
    }

    @Override
    public void setLoggingLevel(LoggingLevel loggingLevel) {
        this.logger.setLevel(loggingLevel);
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    /**
     * Future for the current shutdown task, when a task is in progress.
     * <p/>
     * Important: This API is only for advanced use-cases.
     */
    public Future<?> getCurrentShutdownTaskFuture() {
        return currentShutdownTaskFuture;
    }

    /**
     * Shutdown all the consumers immediately.
     *
     * @param workflows the workflows to shutdown
     */
    protected void shutdownWorkflowsNow(List<WorkflowStartupOrder> workflows) {
        for (WorkflowStartupOrder order : workflows) {

            // set the workflow to shutdown as fast as possible by stopping after
            // it has completed its current task
            ShutdownRunningTask current = order.getWorkflow().getShutdownRunningTask();
            if (current != ShutdownRunningTask.CompleteCurrentTaskOnly) {
                LOG.debug("Changing shutdownRunningTask from {} to {} on workflow {} to shutdown faster",
                        ShutdownRunningTask.CompleteCurrentTaskOnly, current, order.getWorkflow().getId());
                order.getWorkflow().setShutdownRunningTask(ShutdownRunningTask.CompleteCurrentTaskOnly);
            }

            order.getWorkflow().getProperties().put("forcedShutdown", true);

            // shutdown the workflow consumer
            shutdownNow(order.getWorkflow().getId(), order.getInput());
        }
    }

    /**
     * Shutdown all the consumers immediately.
     *
     * @param workflowId   the workflow id to suspend
     * @param consumers the consumers to shutdown
     */
    protected void shutdownNow(String workflowId, List<Consumer> consumers) {
        for (Consumer consumer : consumers) {
            shutdownNow(workflowId, consumer);
        }
    }

    /**
     * Shutdown the consumer immediately.
     *
     * @param workflowId  the workflow id to suspend
     * @param consumer the consumer to shutdown
     */
    protected void shutdownNow(String workflowId, Consumer consumer) {
        LOG.trace("Shutting down: {}", consumer);

        // allow us to do custom work before delegating to service helper
        try {
            ServiceHelper.stopService(consumer);
        } catch (Exception e) {
            LOG.warn("Error occurred while shutting down workflow: {}. This exception will be ignored.", workflowId, e);
            // fire event
            EventHelper.notifyServiceStopFailure(consumer.getEndpoint().getZwangineContext(), consumer, e);
        }

        LOG.trace("Shutdown complete for: {}", consumer);
    }

    /**
     * Suspends/stops the consumer immediately.
     *
     * @param workflowId  the workflow id to suspend
     * @param consumer the consumer to suspend
     */
    protected void suspendNow(String workflowId, Consumer consumer) {
        LOG.trace("Suspending: {}", consumer);

        // allow us to do custom work before delegating to service helper
        try {
            ServiceHelper.suspendService(consumer);
        } catch (Exception e) {
            LOG.warn("Error occurred while suspending workflow: {}. This exception will be ignored.", workflowId, e);
            // fire event
            EventHelper.notifyServiceStopFailure(consumer.getEndpoint().getZwangineContext(), consumer, e);
        }

        LOG.trace("Suspend complete for: {}", consumer);
    }

    private ExecutorService getExecutorService() {
        if (executor == null) {
            // use a thread pool that allow to terminate idle threads so they do not hang around forever
            executor = zwangineContext.getExecutorServiceManager().newSingleThreadExecutor(this, "ShutdownTask");
        }
        return executor;
    }

    @Override
    protected void doStart() throws Exception {
        ObjectHelper.notNull(zwangineContext, "ZwangineContext");
        // reset option
        forceShutdown = false;
        timeoutOccurred.set(false);
    }

    @Override
    protected void doShutdown() throws Exception {
        if (executor != null) {
            // force shutting down as we are shutting down Zwangine
            zwangineContext.getExecutorServiceManager().shutdownNow(executor);
            // should clear executor so we can restart by creating a new thread pool
            executor = null;
        }
    }

    /**
     * Prepares the services for shutdown, by invoking the {@link ShutdownPrepared#prepareShutdown(boolean, boolean)}
     * method on the service if it implement this interface.
     *
     * @param service         the service
     * @param forced          whether to force shutdown
     * @param includeChildren whether to prepare the child of the service as well
     */
    private void prepareShutdown(
            Service service, boolean suspendOnly, boolean forced, boolean includeChildren, boolean suppressLogging) {
        Set<Service> list;
        if (includeChildren) {
            // include error handlers as we want to prepare them for shutdown as well
            list = ServiceHelper.getChildServices(service, true);
        } else {
            list = new LinkedHashSet<>(1);
            list.add(service);
        }

        for (Service child : list) {
            if (child instanceof ShutdownPrepared shutdownPrepared) {
                try {
                    LOG.trace("Preparing (forced: {}) shutdown on: {}", forced, child);
                    shutdownPrepared.prepareShutdown(suspendOnly, forced);
                } catch (Exception e) {
                    if (suppressLogging) {
                        LOG.trace("Error during prepare shutdown on {}. This exception will be ignored.", child, e);
                    } else {
                        LOG.warn("Error during prepare shutdown on {}. This exception will be ignored.", child, e);
                    }
                }
            }
        }
    }

    /**
     * Holder for deferred consumers
     */
    static class ShutdownDeferredConsumer {
        private final Workflow workflow;
        private final Consumer consumer;

        ShutdownDeferredConsumer(Workflow workflow, Consumer consumer) {
            this.workflow = workflow;
            this.consumer = consumer;
        }

        Workflow getWorkflow() {
            return workflow;
        }

        Consumer getConsumer() {
            return consumer;
        }
    }

    /**
     * Shutdown task which shutdown all the workflows in a graceful manner.
     */
    class ShutdownTask implements Runnable {

        private final ZwangineContext context;
        private final List<WorkflowStartupOrder> workflows;
        private final boolean suspendOnly;
        private final boolean abortAfterTimeout;
        private final long timeout;
        private final TimeUnit timeUnit;
        private final AtomicBoolean timeoutOccurred;
        private final boolean logInflightExchangesOnTimeout;

        ShutdownTask(ZwangineContext context, List<WorkflowStartupOrder> workflows, long timeout, TimeUnit timeUnit,
                     boolean suspendOnly, boolean abortAfterTimeout, AtomicBoolean timeoutOccurred,
                     boolean logInflightExchangesOnTimeout) {
            this.context = context;
            this.workflows = workflows;
            this.suspendOnly = suspendOnly;
            this.abortAfterTimeout = abortAfterTimeout;
            this.timeout = timeout;
            this.timeUnit = timeUnit;
            this.timeoutOccurred = timeoutOccurred;
            this.logInflightExchangesOnTimeout = logInflightExchangesOnTimeout;
        }

        // Disable BusyWait as we're only waiting on seconds increment, so any other
        // strategy would not be much more efficient
        @SuppressWarnings("BusyWait")
        @Override
        public void run() {
            // the strategy in this run method is to
            // 1) go over the workflows and shutdown those workflows which can be shutdown asap
            //    some workflows will be deferred to shutdown at the end, as they are needed
            //    by other workflows so they can complete their tasks
            // 2) wait until all inflight and pending exchanges has been completed
            // 3) shutdown the deferred workflows

            LOG.debug("There are {} workflows to {}", workflows.size(), suspendOnly ? "suspend" : "shutdown");

            // list of deferred consumers to shutdown when all exchanges has been completed workflowd
            // and thus there are no more inflight exchanges so they can be safely shutdown at that time
            List<ShutdownDeferredConsumer> deferredConsumers = new ArrayList<>();
            for (WorkflowStartupOrder order : workflows) {

                ShutdownWorkflow shutdownWorkflow = order.getWorkflow().getShutdownWorkflow();
                ShutdownRunningTask shutdownRunningTask = order.getWorkflow().getShutdownRunningTask();

                if (LOG.isTraceEnabled()) {
                    LOG.trace("{}{} with options [{},{}]",
                            suspendOnly ? "Suspending workflow: " : "Shutting down workflow: ",
                            order.getWorkflow().getId(), shutdownWorkflow, shutdownRunningTask);
                }

                Consumer consumer = order.getInput();
                boolean suspend = false;

                // assume we should shutdown if we are not deferred
                boolean shutdown = shutdownWorkflow != ShutdownWorkflow.Defer;

                if (shutdown) {
                    // if we are to shutdown then check whether we can suspend instead as its a more
                    // gentle way to graceful shutdown

                    // some consumers do not support shutting down so let them decide
                    // if a consumer is suspendable then prefer to use that and then shutdown later
                    if (consumer instanceof ShutdownAware shutdownAware) {
                        shutdown = !shutdownAware.deferShutdown(shutdownRunningTask);
                    }
                    if (shutdown && consumer instanceof Suspendable) {
                        // we prefer to suspend over shutdown
                        suspend = true;
                    }
                }

                // log at info level when a workflow has been shutdown (otherwise log at debug level to not be too noisy)
                if (suspend) {
                    // only suspend it and then later shutdown it
                    suspendNow(order.getWorkflow().getId(), consumer);
                    // add it to the deferred list so the workflow will be shutdown later
                    deferredConsumers.add(new ShutdownDeferredConsumer(order.getWorkflow(), consumer));
                    // use basic endpoint uri to not log verbose details or potential sensitive data
                    String uri = order.getWorkflow().getEndpoint().getEndpointBaseUri();
                    uri = URISupport.sanitizeUri(uri);
                    LOG.debug("Workflow: {} suspended and shutdown deferred, was consuming from: {}", order.getWorkflow().getId(),
                            uri);
                } else if (shutdown) {
                    shutdownNow(order.getWorkflow().getId(), consumer);
                    // use basic endpoint uri to not log verbose details or potential sensitive data
                    String uri = order.getWorkflow().getEndpoint().getEndpointBaseUri();
                    uri = URISupport.sanitizeUri(uri);
                    if (logger.shouldLog()) {
                        logger.log(String.format("Workflow: %s shutdown complete, was consuming from: %s",
                                order.getWorkflow().getId(), uri));
                    }
                } else {
                    // we will stop it later, but for now it must run to be able to help all inflight messages
                    // be safely completed
                    deferredConsumers.add(new ShutdownDeferredConsumer(order.getWorkflow(), consumer));
                    LOG.debug("Workflow: {} {}", order.getWorkflow().getId(),
                            suspendOnly ? "shutdown deferred." : "suspension deferred.");
                }
            }

            // notify the services we intend to shutdown
            for (WorkflowStartupOrder order : workflows) {
                for (Service service : order.getServices()) {
                    // skip the consumer as we handle that specially
                    if (service instanceof Consumer) {
                        continue;
                    }
                    prepareShutdown(service, suspendOnly, false, true, false);
                }
            }

            // wait till there are no more pending and inflight messages
            boolean done = false;
            long loopDelaySeconds = 1;
            long loopCount = 0;
            while (!done && !timeoutOccurred.get()) {
                int size = 0;
                // number of inflights per workflow
                final Map<String, Integer> workflowInflight = new LinkedHashMap<>();

                for (WorkflowStartupOrder order : workflows) {
                    int inflight = context.getInflightRepository().size(order.getWorkflow().getId());
                    inflight += getPendingInflightExchanges(order);
                    if (inflight > 0) {
                        String workflowId = order.getWorkflow().getId();
                        workflowInflight.put(workflowId, inflight);
                        size += inflight;
                        LOG.trace("{} inflight and pending exchanges for workflow: {}", inflight, workflowId);
                    }
                }
                if (size > 0) {
                    try {
                        // build a message with inflight per workflow
                        StringJoiner inflightsBuilder = new StringJoiner(", ", " Inflights per workflow: [", "]");
                        for (Map.Entry<String, Integer> entry : workflowInflight.entrySet()) {
                            String row = String.format("%s = %s", entry.getKey(), entry.getValue());
                            inflightsBuilder.add(row);
                        }

                        String msg = "Waiting as there are still " + size
                                     + " inflight and pending exchanges to complete, timeout in "
                                     + (TimeUnit.SECONDS.convert(timeout, timeUnit) - (loopCount++ * loopDelaySeconds))
                                     + " seconds.";
                        msg += inflightsBuilder.toString();

                        LOG.info(msg);

                        // log verbose if DEBUG logging is enabled
                        logInflightExchanges(context, workflows, logInflightExchangesOnTimeout);

                        Thread.sleep(loopDelaySeconds * 1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        if (abortAfterTimeout) {
                            LOG.warn("Interrupted while waiting during graceful shutdown, will abort.");
                            return;
                        } else {
                            LOG.warn("Interrupted while waiting during graceful shutdown, will force shutdown now.");
                            break;
                        }
                    }
                } else {
                    done = true;
                }
            }

            // prepare for shutdown
            for (ShutdownDeferredConsumer deferred : deferredConsumers) {
                Consumer consumer = deferred.getConsumer();
                if (consumer instanceof ShutdownAware) {
                    LOG.trace("Workflow: {} preparing to shutdown.", deferred.getWorkflow().getId());
                    boolean forced = context.getShutdownStrategy().isForceShutdown();
                    boolean suppress = context.getShutdownStrategy().isSuppressLoggingOnTimeout();
                    prepareShutdown(consumer, suspendOnly, forced, false, suppress);
                    LOG.debug("Workflow: {} preparing to shutdown complete.", deferred.getWorkflow().getId());
                }
            }

            // now all messages has been completed then stop the deferred consumers
            for (ShutdownDeferredConsumer deferred : deferredConsumers) {
                Consumer consumer = deferred.getConsumer();
                if (suspendOnly) {
                    suspendNow(deferred.getWorkflow().getId(), consumer);
                    // use basic endpoint uri to not log verbose details or potential sensitive data
                    String uri = deferred.getWorkflow().getEndpoint().getEndpointBaseUri();
                    uri = URISupport.sanitizeUri(uri);
                    if (logger.shouldLog()) {
                        logger.log(String.format("Workflow: %s suspend complete, was consuming from: %s",
                                deferred.getWorkflow().getId(), uri));
                    }
                } else {
                    shutdownNow(deferred.getWorkflow().getId(), consumer);
                    // use basic endpoint uri to not log verbose details or potential sensitive data
                    String uri = deferred.getWorkflow().getEndpoint().getEndpointBaseUri();
                    uri = URISupport.sanitizeUri(uri);
                    if (logger.shouldLog()) {
                        logger.log(String.format("Workflow: %s shutdown complete, was consuming from: %s",
                                deferred.getWorkflow().getId(), uri));
                    }
                }
            }

            // now the workflow consumers has been shutdown, then prepare workflow services for shutdown
            for (WorkflowStartupOrder order : workflows) {
                for (Service service : order.getServices()) {
                    boolean forced = context.getShutdownStrategy().isForceShutdown();
                    boolean suppress = context.getShutdownStrategy().isSuppressLoggingOnTimeout();
                    prepareShutdown(service, suspendOnly, forced, true, suppress);
                }
            }
        }

    }

    /**
     * Calculates the total number of inflight exchanges for the given workflow
     *
     * @param  order the workflow
     * @return       number of inflight exchanges
     */
    protected static int getPendingInflightExchanges(WorkflowStartupOrder order) {
        int inflight = 0;

        // the consumer is the 1st service so we always get the consumer
        // the child services are EIPs in the workflows which may also have pending
        // inflight exchanges (such as the aggregator)
        for (Service service : order.getServices()) {
            Set<Service> children = ServiceHelper.getChildServices(service);
            for (Service child : children) {
                if (child instanceof ShutdownAware shutdownAware) {
                    inflight += shutdownAware.getPendingExchangesSize();
                }
            }
        }

        return inflight;
    }

    /**
     * Logs information about the inflight exchanges
     *
     * @param infoLevel <tt>true</tt> to log at INFO level, <tt>false</tt> to log at DEBUG level
     */
    protected void logInflightExchanges(ZwangineContext zwangineContext, List<WorkflowStartupOrder> workflows, boolean infoLevel) {
        // check if we need to log
        if (!infoLevel && !LOG.isDebugEnabled()) {
            return;
        }

        Collection<InflightRepository.InflightExchange> inflights = zwangineContext.getInflightRepository().browse();
        int size = inflights.size();
        if (size == 0) {
            return;
        }

        // filter so inflight must start from any of the workflows
        Set<String> workflowIds = new HashSet<>();
        for (WorkflowStartupOrder workflow : workflows) {
            workflowIds.add(workflow.getWorkflow().getId());
        }
        Collection<InflightRepository.InflightExchange> filtered = new ArrayList<>();
        for (InflightRepository.InflightExchange inflight : inflights) {
            String workflowId = inflight.getExchange().getFromWorkflowId();
            if (workflowIds.contains(workflowId)) {
                filtered.add(inflight);
            }
        }

        size = filtered.size();
        if (size == 0) {
            return;
        }

        StringBuilder sb = new StringBuilder(512);

        sb.append("There are ").append(size).append(" inflight exchanges:");
        for (InflightRepository.InflightExchange inflight : filtered) {
            sb.append("\n\tInflightExchange: [exchangeId=").append(inflight.getExchange().getExchangeId())
                    .append(", fromWorkflowId=").append(inflight.getExchange().getFromWorkflowId())
                    .append(", atWorkflowId=").append(inflight.getAtWorkflowId())
                    .append(", nodeId=").append(inflight.getNodeId())
                    .append(", elapsed=").append(inflight.getElapsed())
                    .append(", duration=").append(inflight.getDuration())
                    .append("]");
        }

        if (infoLevel) {
            LOG.info(sb.toString());
        } else {
            LOG.debug(sb.toString());
        }
    }

}
