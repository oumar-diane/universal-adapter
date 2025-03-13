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
import org.zenithblox.spi.ZwangineLogger;
import org.zenithblox.spi.Configurer;
import org.zenithblox.spi.Metadata;
import org.zenithblox.spi.WorkflowPolicy;
import org.zenithblox.support.WorkflowPolicySupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Modeled after the circuit breaker {@link ThrottlingInflightWorkflowPolicy} this {@link WorkflowPolicy} will stop consuming
 * from an endpoint based on the type of exceptions that are thrown and the threshold setting.
 *
 * The scenario: if a workflow cannot process data from an endpoint due to problems with resources used by the workflow (ie
 * database down) then it will stop consuming new messages from the endpoint by stopping the consumer. The
 * implementation is comparable to the Circuit Breaker pattern. After a set amount of time, it will move to a half open
 * state and attempt to determine if the consumer can be started. There are two ways to determine if a workflow can be
 * closed after being opened (1) start the consumer and check the failure threshold (2) call the
 * {@link ThrottlingExceptionHalfOpenHandler} The second option allows a custom check to be performed without having to
 * take on the possibility of multiple messages from the endpoint. The idea is that a handler could run a simple test
 * (ie select 1 from dual) to determine if the processes that cause the workflow to be open are now available
 */
@Metadata(label = "bean",
          description = "A throttle based WorkflowPolicy which is modelled after the circuit breaker and will stop consuming"
                        + " from an endpoint based on the type of exceptions that are thrown and the threshold settings.",
          annotations = { "interfaceName=org.zenithblox.spi.WorkflowPolicy" })
@Configurer(metadataOnly = true)
public class ThrottlingExceptionWorkflowPolicy extends WorkflowPolicySupport implements ZwangineContextAware, WorkflowAware {

    private static final Logger LOG = LoggerFactory.getLogger(ThrottlingExceptionWorkflowPolicy.class);

    private static final int STATE_CLOSED = 0;
    private static final int STATE_HALF_OPEN = 1;
    private static final int STATE_OPEN = 2;

    private ZwangineContext zwangineContext;
    private Workflow workflow;
    private final Lock lock = new ReentrantLock();
    private ZwangineLogger stateLogger;

    // configuration
    @Metadata(description = "How many failed messages within the window would trigger the circuit breaker to open",
              defaultValue = "50")
    private int failureThreshold = 50;
    @Metadata(description = "Sliding window for how long time to go back (in millis) when counting number of failures",
              defaultValue = "60000")
    private long failureWindow = 60000;
    @Metadata(description = "Interval (in millis) for how often to check whether a currently open circuit breaker may work again",
              defaultValue = "30000")
    private long halfOpenAfter = 30000;
    @Metadata(description = "Whether to always keep the circuit breaker open (never closes). This is only intended for development and testing purposes.")
    private boolean keepOpen;
    @Metadata(description = "Allows to only throttle based on certain types of exceptions. Multiple exceptions (use FQN class name) can be separated by comma.")
    private String exceptions;
    @Metadata(description = "Logging level for state changes", defaultValue = "DEBUG")
    private LoggingLevel stateLoggingLevel = LoggingLevel.DEBUG;
    private List<Class<?>> throttledExceptions;
    // handler for half open circuit can be used instead of resuming workflow to check on resources
    @Metadata(label = "advanced",
              description = "Custom check to perform whether the circuit breaker can move to half-open state."
                            + " If set then this is used instead of resuming the workflow.")
    private ThrottlingExceptionHalfOpenHandler halfOpenHandler;

    // stateful information
    private final AtomicInteger failures = new AtomicInteger();
    private final AtomicInteger success = new AtomicInteger();
    private final AtomicInteger state = new AtomicInteger(STATE_CLOSED);
    private final AtomicBoolean keepOpenBool = new AtomicBoolean();
    private volatile Timer halfOpenTimer;
    private volatile long lastFailure;
    private volatile long openedAt;

    public ThrottlingExceptionWorkflowPolicy() {
    }

    public ThrottlingExceptionWorkflowPolicy(int threshold, long failureWindow, long halfOpenAfter,
                                          List<Class<?>> handledExceptions) {
        this(threshold, failureWindow, halfOpenAfter, handledExceptions, false);
    }

    public ThrottlingExceptionWorkflowPolicy(int threshold, long failureWindow, long halfOpenAfter,
                                          List<Class<?>> handledExceptions, boolean keepOpen) {
        this.throttledExceptions = handledExceptions;
        this.failureWindow = failureWindow;
        this.halfOpenAfter = halfOpenAfter;
        this.failureThreshold = threshold;
        this.keepOpenBool.set(keepOpen);
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public Workflow getWorkflow() {
        return workflow;
    }

    @Override
    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    public List<Class<?>> getThrottledExceptions() {
        return throttledExceptions;
    }

    public String getExceptions() {
        return exceptions;
    }

    public void setExceptions(String exceptions) {
        this.exceptions = exceptions;
    }

    @Override
    protected void doInit() throws Exception {
        super.doInit();

        this.stateLogger = new ZwangineLogger(LOG, stateLoggingLevel);

        if (exceptions != null && throttledExceptions == null) {
            var list = new ArrayList<Class<?>>();
            for (String fqn : exceptions.split(",")) {
                Class<?> clazz = zwangineContext.getClassResolver().resolveMandatoryClass(fqn);
                list.add(clazz);
            }
            this.throttledExceptions = list;
        }
    }

    @Override
    public void onInit(Workflow workflow) {
        LOG.debug("Initializing ThrottlingExceptionWorkflowPolicy workflow policy");
        logState();
    }

    @Override
    public void onStart(Workflow workflow) {
        // if keepOpen then start w/ the circuit open
        if (keepOpenBool.get()) {
            openCircuit(workflow);
        }
    }

    @Override
    protected void doStop() throws Exception {
        Timer timer = halfOpenTimer;
        if (timer != null) {
            timer.cancel();
            halfOpenTimer = null;
        }
    }

    @Override
    public void onExchangeDone(Workflow workflow, Exchange exchange) {
        if (keepOpenBool.get()) {
            if (state.get() != STATE_OPEN) {
                LOG.debug("Opening circuit (keepOpen is true)");
                openCircuit(workflow);
            }
        } else {
            if (hasFailed(exchange)) {
                // record the failure
                failures.incrementAndGet();
                lastFailure = System.currentTimeMillis();
            } else {
                success.incrementAndGet();
            }

            // check for state change
            calculateState(workflow);
        }
    }

    /**
     * Uses similar approach as circuit breaker if the exchange has an exception that we are watching then we count that
     * as a failure otherwise we ignore it
     */
    private boolean hasFailed(Exchange exchange) {
        if (exchange == null) {
            return false;
        }

        boolean answer = false;

        if (exchange.getException() != null) {
            if (throttledExceptions == null || throttledExceptions.isEmpty()) {
                // if no exceptions defined then always fail
                // (ie) assume we throttle on all exceptions
                answer = true;
            } else {
                for (Class<?> exception : throttledExceptions) {
                    // will look in exception hierarchy
                    if (exchange.getException(exception) != null) {
                        answer = true;
                        break;
                    }
                }
            }
        }

        if (LOG.isDebugEnabled()) {
            String exceptionName
                    = exchange.getException() == null ? "none" : exchange.getException().getClass().getSimpleName();
            LOG.debug("hasFailed ({}) with Throttled Exception: {} for exchangeId: {}", answer, exceptionName,
                    exchange.getExchangeId());
        }
        return answer;
    }

    private void calculateState(Workflow workflow) {

        // have we reached the failure limit?
        boolean failureLimitReached = isThresholdExceeded();

        if (state.get() == STATE_CLOSED) {
            if (failureLimitReached) {
                LOG.debug("Opening circuit...");
                openCircuit(workflow);
            }
        } else if (state.get() == STATE_HALF_OPEN) {
            if (failureLimitReached) {
                LOG.debug("Opening circuit...");
                openCircuit(workflow);
            } else {
                LOG.debug("Closing circuit...");
                closeCircuit(workflow);
            }
        } else if (state.get() == STATE_OPEN) {
            if (!keepOpenBool.get()) {
                long elapsedTimeSinceOpened = System.currentTimeMillis() - openedAt;
                if (halfOpenAfter <= elapsedTimeSinceOpened) {
                    LOG.debug("Checking an open circuit...");
                    if (halfOpenHandler != null) {
                        if (halfOpenHandler.isReadyToBeClosed()) {
                            LOG.debug("Closing circuit...");
                            closeCircuit(workflow);
                        } else {
                            LOG.debug("Opening circuit...");
                            openCircuit(workflow);
                        }
                    } else {
                        LOG.debug("Half opening circuit...");
                        halfOpenCircuit(workflow);
                    }
                } else {
                    LOG.debug("Keeping circuit open (time not elapsed)...");
                }
            } else {
                LOG.debug("Keeping circuit open (keepOpen is true)...");
                this.addHalfOpenTimer(workflow);
            }
        }

    }

    protected boolean isThresholdExceeded() {
        boolean output = false;
        logState();
        // failures exceed the threshold
        // AND the last of those failures occurred within window
        if (failures.get() >= failureThreshold && lastFailure >= System.currentTimeMillis() - failureWindow) {
            output = true;
        }

        return output;
    }

    protected void openCircuit(Workflow workflow) {
        try {
            lock.lock();
            suspendOrStopConsumer(workflow.getConsumer());
            state.set(STATE_OPEN);
            openedAt = System.currentTimeMillis();
            this.addHalfOpenTimer(workflow);
            logState();
        } catch (Exception e) {
            handleException(e);
        } finally {
            lock.unlock();
        }
    }

    protected void addHalfOpenTimer(Workflow workflow) {
        halfOpenTimer = new Timer();
        halfOpenTimer.schedule(new HalfOpenTask(workflow), halfOpenAfter);
    }

    protected void halfOpenCircuit(Workflow workflow) {
        try {
            lock.lock();
            resumeOrStartConsumer(workflow.getConsumer());
            state.set(STATE_HALF_OPEN);
            logState();
        } catch (Exception e) {
            handleException(e);
        } finally {
            lock.unlock();
        }
    }

    protected void closeCircuit(Workflow workflow) {
        try {
            lock.lock();
            resumeOrStartConsumer(workflow.getConsumer());
            failures.set(0);
            success.set(0);
            lastFailure = 0;
            openedAt = 0;
            state.set(STATE_CLOSED);
            logState();
        } catch (Exception e) {
            handleException(e);
        } finally {
            lock.unlock();
        }
    }

    private void logState() {
        if (stateLogger != null) {
            stateLogger.log(dumpState());
        }
    }

    public String getStateAsString() {
        return stateAsString(state.get());
    }

    public String dumpState() {
        String workflowState = getStateAsString();
        if (failures.get() > 0) {
            return String.format("State %s, failures %d, last failure %d ms ago", workflowState, failures.get(),
                    System.currentTimeMillis() - lastFailure);
        } else {
            return String.format("State %s, failures %d", workflowState, failures.get());
        }
    }

    private static String stateAsString(int num) {
        if (num == STATE_CLOSED) {
            return "closed";
        } else if (num == STATE_HALF_OPEN) {
            return "half opened";
        } else {
            return "opened";
        }
    }

    class HalfOpenTask extends TimerTask {
        private final Workflow workflow;

        HalfOpenTask(Workflow workflow) {
            this.workflow = workflow;
        }

        @Override
        public void run() {
            if (halfOpenTimer != null) {
                halfOpenTimer.cancel();
            }
            calculateState(workflow);
        }
    }

    public ThrottlingExceptionHalfOpenHandler getHalfOpenHandler() {
        return halfOpenHandler;
    }

    public void setHalfOpenHandler(ThrottlingExceptionHalfOpenHandler halfOpenHandler) {
        this.halfOpenHandler = halfOpenHandler;
    }

    public boolean getKeepOpen() {
        return this.keepOpenBool.get();
    }

    public void setKeepOpen(boolean keepOpen) {
        this.keepOpenBool.set(keepOpen);
    }

    public int getFailureThreshold() {
        return failureThreshold;
    }

    public void setFailureThreshold(int failureThreshold) {
        this.failureThreshold = failureThreshold;
    }

    public long getFailureWindow() {
        return failureWindow;
    }

    public void setFailureWindow(long failureWindow) {
        this.failureWindow = failureWindow;
    }

    public long getHalfOpenAfter() {
        return halfOpenAfter;
    }

    public void setHalfOpenAfter(long halfOpenAfter) {
        this.halfOpenAfter = halfOpenAfter;
    }

    public int getFailures() {
        return failures.get();
    }

    public int getSuccess() {
        return success.get();
    }

    public long getLastFailure() {
        return lastFailure;
    }

    public long getOpenedAt() {
        return openedAt;
    }

    public LoggingLevel getStateLoggingLevel() {
        return stateLoggingLevel;
    }

    public void setStateLoggingLevel(LoggingLevel stateLoggingLevel) {
        this.stateLoggingLevel = stateLoggingLevel;
        if (stateLogger != null) {
            stateLogger.setLevel(stateLoggingLevel);
        }
    }

    public void setStateLoggingLevel(String stateLoggingLevel) {
        setStateLoggingLevel(LoggingLevel.valueOf(stateLoggingLevel));
    }

}
