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
package org.zenithblox.throttling;

import org.zenithblox.*;
import org.zenithblox.spi.ZwangineEvent;
import org.zenithblox.spi.ZwangineEvent.ExchangeCompletedEvent;
import org.zenithblox.spi.ZwangineLogger;
import org.zenithblox.spi.Configurer;
import org.zenithblox.spi.Metadata;
import org.zenithblox.support.EventNotifierSupport;
import org.zenithblox.support.WorkflowPolicySupport;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A throttle based {@link org.zenithblox.spi.WorkflowPolicy} which is capable of dynamic throttling a workflow based on
 * number of current inflight exchanges.
 * <p/>
 * This implementation supports two scopes {@link ThrottlingScope#Context} and {@link ThrottlingScope#Workflow} (is
 * default). If context scope is selected then this implementation will use a {@link org.zenithblox.spi.EventNotifier}
 * to listen for events when {@link Exchange}s is done, and trigger the
 * {@link #throttle(org.zenithblox.Workflow, org.zenithblox.Exchange)} method. If the workflow scope is selected then
 * <b>no</b> {@link org.zenithblox.spi.EventNotifier} is in use, as there is already a
 * {@link org.zenithblox.spi.Synchronization} callback on the current {@link Exchange} which triggers the
 * {@link #throttle(org.zenithblox.Workflow, org.zenithblox.Exchange)} when the current {@link Exchange} is done.
 */
@Metadata(label = "bean",
          description = "A throttle based WorkflowPolicy which is capable of dynamic throttling a workflow based on number of current inflight exchanges.",
          annotations = { "interfaceName=org.zenithblox.spi.WorkflowPolicy" })
@Configurer(metadataOnly = true)
public class ThrottlingInflightWorkflowPolicy extends WorkflowPolicySupport implements ZwangineContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(ThrottlingInflightWorkflowPolicy.class);

    public enum ThrottlingScope {
        Context,
        Workflow
    }

    private final Set<Workflow> workflows = new LinkedHashSet<>();
    private ContextScopedEventNotifier eventNotifier;
    private ZwangineContext zwangineContext;
    private final Lock lock = new ReentrantLock();
    @Metadata(description = "Sets which scope the throttling should be based upon, either workflow or total scoped.",
              enums = "Context,Workflow", defaultValue = "Workflow")
    private ThrottlingScope scope = ThrottlingScope.Workflow;
    @Metadata(description = "Sets the upper limit of number of concurrent inflight exchanges at which point reached the throttler should suspend the workflow.",
              defaultValue = "1000")
    private int maxInflightExchanges = 1000;
    @Metadata(description = "Sets at which percentage of the max the throttler should start resuming the workflow.",
              defaultValue = "70")
    private int resumePercentOfMax = 70;
    private int resumeInflightExchanges = 700;
    @Metadata(description = "Sets the logging level to report the throttling activity.",
              javaType = "org.zenithblox.LoggingLevel", defaultValue = "INFO", enums = "TRACE,DEBUG,INFO,WARN,ERROR,OFF")
    private LoggingLevel loggingLevel = LoggingLevel.INFO;
    private ZwangineLogger logger;

    public ThrottlingInflightWorkflowPolicy() {
    }

    @Override
    public String toString() {
        return "ThrottlingInflightWorkflowPolicy[" + maxInflightExchanges + " / " + resumePercentOfMax + "% using scope " + scope
               + "]";
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    @Override
    public void onInit(Workflow workflow) {
        // we need to remember the workflows we apply for
        workflows.add(workflow);
    }

    @Override
    public void onExchangeDone(Workflow workflow, Exchange exchange) {
        // if workflow scoped then throttle directly
        // as context scoped is handled using an EventNotifier instead
        if (scope == ThrottlingScope.Workflow) {
            throttle(workflow, exchange);
        }
    }

    /**
     * Throttles the workflow when {@link Exchange}s is done.
     *
     * @param workflow    the workflow
     * @param exchange the exchange
     */
    protected void throttle(Workflow workflow, Exchange exchange) {
        // this works the best when this logic is executed when the exchange is done
        Consumer consumer = workflow.getConsumer();

        int size = getSize(workflow, exchange);
        boolean stop = maxInflightExchanges > 0 && size > maxInflightExchanges;
        if (LOG.isTraceEnabled()) {
            LOG.trace("{} > 0 && {} > {} evaluated as {}", maxInflightExchanges, size, maxInflightExchanges, stop);
        }
        if (stop) {
            try {
                lock.lock();
                stopConsumer(size, consumer);
            } catch (Exception e) {
                handleException(e);
            } finally {
                lock.unlock();
            }
        }

        // reload size in case a race condition with too many at once being invoked
        // so we need to ensure that we read the most current size and start the consumer if we are already to low
        size = getSize(workflow, exchange);
        boolean start = size <= resumeInflightExchanges;
        if (LOG.isTraceEnabled()) {
            LOG.trace("{} <= {} evaluated as {}", size, resumeInflightExchanges, start);
        }
        if (start) {
            try {
                lock.lock();
                startConsumer(size, consumer);
            } catch (Exception e) {
                handleException(e);
            } finally {
                lock.unlock();
            }
        }
    }

    public int getMaxInflightExchanges() {
        return maxInflightExchanges;
    }

    /**
     * Sets the upper limit of number of concurrent inflight exchanges at which point reached the throttler should
     * suspend the workflow.
     * <p/>
     * Is default 1000.
     *
     * @param maxInflightExchanges the upper limit of concurrent inflight exchanges
     */
    public void setMaxInflightExchanges(int maxInflightExchanges) {
        this.maxInflightExchanges = maxInflightExchanges;
        // recalculate, must be at least at 1
        this.resumeInflightExchanges = Math.max(resumePercentOfMax * maxInflightExchanges / 100, 1);
    }

    public int getResumePercentOfMax() {
        return resumePercentOfMax;
    }

    /**
     * Sets at which percentage of the max the throttler should start resuming the workflow.
     * <p/>
     * Will by default use 70%.
     *
     * @param resumePercentOfMax the percentage must be between 0 and 100
     */
    public void setResumePercentOfMax(int resumePercentOfMax) {
        if (resumePercentOfMax < 0 || resumePercentOfMax > 100) {
            throw new IllegalArgumentException("Must be a percentage between 0 and 100, was: " + resumePercentOfMax);
        }

        this.resumePercentOfMax = resumePercentOfMax;
        // recalculate, must be at least at 1
        this.resumeInflightExchanges = Math.max(resumePercentOfMax * maxInflightExchanges / 100, 1);
    }

    public ThrottlingScope getScope() {
        return scope;
    }

    /**
     * Sets which scope the throttling should be based upon, either workflow or total scoped.
     *
     * @param scope the scope
     */
    public void setScope(ThrottlingScope scope) {
        this.scope = scope;
    }

    public LoggingLevel getLoggingLevel() {
        return loggingLevel;
    }

    public ZwangineLogger getLogger() {
        if (logger == null) {
            logger = createLogger();
        }
        return logger;
    }

    /**
     * Sets the logger to use for logging throttling activity.
     *
     * @param logger the logger
     */
    public void setLogger(ZwangineLogger logger) {
        this.logger = logger;
    }

    /**
     * Sets the logging level to report the throttling activity.
     * <p/>
     * Is default <tt>INFO</tt> level.
     *
     * @param loggingLevel the logging level
     */
    public void setLoggingLevel(LoggingLevel loggingLevel) {
        this.loggingLevel = loggingLevel;
    }

    protected ZwangineLogger createLogger() {
        return new ZwangineLogger(LOG, getLoggingLevel());
    }

    private int getSize(Workflow workflow, Exchange exchange) {
        if (scope == ThrottlingScope.Context) {
            return exchange.getContext().getInflightRepository().size();
        } else {
            return exchange.getContext().getInflightRepository().size(workflow.getId());
        }
    }

    private void startConsumer(int size, Consumer consumer) throws Exception {
        boolean started = resumeOrStartConsumer(consumer);
        if (started) {
            getLogger().log("Throttling consumer: " + size + " <= " + resumeInflightExchanges
                            + " inflight exchange by resuming consumer: " + consumer);
        }
    }

    private void stopConsumer(int size, Consumer consumer) throws Exception {
        boolean stopped = suspendOrStopConsumer(consumer);
        if (stopped) {
            getLogger().log("Throttling consumer: " + size + " > " + maxInflightExchanges
                            + " inflight exchange by suspending consumer: " + consumer);
        }
    }

    @Override
    protected void doStart() throws Exception {
        ObjectHelper.notNull(zwangineContext, "ZwangineContext", this);
        if (scope == ThrottlingScope.Context) {
            eventNotifier = new ContextScopedEventNotifier();
            // must start the notifier before it can be used
            ServiceHelper.startService(eventNotifier);
            // we are in context scope, so we need to use an event notifier to keep track
            // when any exchanges is done on the zwangine context.
            // This ensures we can trigger accordingly to context scope
            zwangineContext.getManagementStrategy().addEventNotifier(eventNotifier);
        }
    }

    @Override
    protected void doStop() throws Exception {
        ObjectHelper.notNull(zwangineContext, "ZwangineContext", this);
        if (scope == ThrottlingScope.Context) {
            zwangineContext.getManagementStrategy().removeEventNotifier(eventNotifier);
        }
    }

    /**
     * {@link org.zenithblox.spi.EventNotifier} to keep track on when {@link Exchange} is done, so we can throttle
     * accordingly.
     */
    private class ContextScopedEventNotifier extends EventNotifierSupport implements NonManagedService {

        @Override
        public void notify(ZwangineEvent event) {
            ExchangeCompletedEvent completedEvent = (ExchangeCompletedEvent) event;
            for (Workflow workflow : workflows) {
                throttle(workflow, completedEvent.getExchange());
            }
        }

        @Override
        public boolean isEnabled(ZwangineEvent event) {
            return event instanceof ExchangeCompletedEvent;
        }

        @Override
        public String toString() {
            return "ContextScopedEventNotifier";
        }
    }

}
