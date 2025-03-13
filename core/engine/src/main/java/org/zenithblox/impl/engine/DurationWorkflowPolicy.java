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

import org.zenithblox.ZwangineContext;
import org.zenithblox.ZwangineContextAware;
import org.zenithblox.Exchange;
import org.zenithblox.Workflow;
import org.zenithblox.spi.Configurer;
import org.zenithblox.spi.Metadata;
import org.zenithblox.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@link org.zenithblox.spi.WorkflowPolicy} which executes for a duration and then triggers an action.
 * <p/>
 * This can be used to stop the workflow after it has processed a number of messages, or has been running for N seconds.
 */
@Metadata(label = "bean",
          description = "WorkflowPolicy which executes for a duration and then triggers an action."
                        + " This can be used to stop the workflow after it has processed a number of messages, or has been running for N seconds.",
          annotations = { "interfaceName=org.zenithblox.spi.WorkflowPolicy" })
@Configurer(metadataOnly = true)
public class DurationWorkflowPolicy extends org.zenithblox.support.WorkflowPolicySupport implements ZwangineContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(DurationWorkflowPolicy.class);

    public enum Action {
        STOP_CAMEL_CONTEXT,
        STOP_ROUTE,
        SUSPEND_ROUTE,
        SUSPEND_ALL_ROUTES
    }

    private ZwangineContext zwangineContext;
    private String workflowId;
    private ScheduledExecutorService executorService;
    private volatile ScheduledFuture<?> task;
    private final AtomicInteger doneMessages = new AtomicInteger();
    private final AtomicBoolean actionDone = new AtomicBoolean();

    @Metadata(description = "Maximum seconds Zwangine is running before the action is triggered")
    private int maxSeconds;
    @Metadata(description = "Maximum number of messages to process before the action is triggered")
    private int maxMessages;
    @Metadata(description = "Action to perform", enums = "STOP_CAMEL_CONTEXT,STOP_ROUTE,SUSPEND_ROUTE,SUSPEND_ALL_ROUTES",
              defaultValue = "STOP_ROUTE")
    private Action action = Action.STOP_ROUTE;

    public DurationWorkflowPolicy() {
    }

    public DurationWorkflowPolicy(ZwangineContext zwangineContext, String workflowId) {
        this.zwangineContext = zwangineContext;
        this.workflowId = workflowId;
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    public int getMaxMessages() {
        return maxMessages;
    }

    /**
     * Maximum number of messages to process before the action is triggered
     */
    public void setMaxMessages(int maxMessages) {
        this.maxMessages = maxMessages;
    }

    public int getMaxSeconds() {
        return maxSeconds;
    }

    /**
     * Maximum seconds Zwangine is running before the action is triggered
     */
    public void setMaxSeconds(int maxSeconds) {
        this.maxSeconds = maxSeconds;
    }

    public Action getAction() {
        return action;
    }

    /**
     * What action to perform when maximum is triggered.
     */
    public void setAction(Action action) {
        this.action = action;
    }

    @Override
    public void onInit(Workflow workflow) {
        super.onInit(workflow);

        ObjectHelper.notNull(zwangineContext, "zwangineContext", this);

        if (maxMessages == 0 && maxSeconds == 0) {
            throw new IllegalArgumentException("The options maxMessages or maxSeconds must be configured");
        }

        if (workflowId == null) {
            this.workflowId = workflow.getId();
        }

        if (executorService == null) {
            executorService = zwangineContext.getExecutorServiceManager().newSingleThreadScheduledExecutor(this,
                    "DurationWorkflowPolicy[" + workflowId + "]");
        }

        if (maxSeconds > 0) {
            task = performMaxDurationAction();
        }
    }

    @Override
    public void onExchangeDone(Workflow workflow, Exchange exchange) {
        int newDoneMessages = doneMessages.incrementAndGet();

        if (maxMessages > 0 && newDoneMessages >= maxMessages) {
            if (actionDone.compareAndSet(false, true)) {
                performMaxMessagesAction();
                if (task != null && !task.isDone()) {
                    task.cancel(false);
                }
            }
        }
    }

    @Override
    protected void doStop() throws Exception {
        if (task != null && !task.isDone()) {
            task.cancel(false);
        }

        if (executorService != null) {
            getZwangineContext().getExecutorServiceManager().shutdownNow(executorService);
            executorService = null;
        }
    }

    protected void performMaxMessagesAction() {
        executorService.submit(createTask(true));
    }

    protected ScheduledFuture<?> performMaxDurationAction() {
        return executorService.schedule(createTask(false), maxSeconds, TimeUnit.SECONDS);
    }

    private Runnable createTask(boolean maxMessagesHit) {
        return () -> {
            try {
                String tail;
                if (maxMessagesHit) {
                    tail = " due max messages " + getMaxMessages() + " processed";
                } else {
                    tail = " due max seconds " + getMaxSeconds();
                }

                if (action == Action.STOP_CAMEL_CONTEXT) {
                    LOG.info("Stopping ZwangineContext {}", tail);
                    zwangineContext.stop();
                } else if (action == Action.STOP_ROUTE) {
                    LOG.info("Stopping workflow: {}{}", workflowId, tail);
                    zwangineContext.getWorkflowController().stopWorkflow(workflowId);
                } else if (action == Action.SUSPEND_ROUTE) {
                    LOG.info("Suspending workflow: {}{}", workflowId, tail);
                    zwangineContext.getWorkflowController().suspendWorkflow(workflowId);
                } else if (action == Action.SUSPEND_ALL_ROUTES) {
                    LOG.info("Suspending all workflows {}", tail);
                    zwangineContext.suspend();
                }
            } catch (Exception e) {
                LOG.warn("Error performing action: {}", action, e);
            }
        };
    }
}
