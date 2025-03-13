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
package org.zenithblox.spi;

import org.zenithblox.ZwangineContext;
import org.zenithblox.LoggingLevel;
import org.zenithblox.StaticService;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Pluggable shutdown strategy executed during shutdown of Zwangine and the active workflows.
 * <p/>
 * Shutting down workflows in a reliable and graceful manner is not a trivial task. Therefore, Zwangine provides a pluggable
 * strategy allowing 3rd party to use their own strategy if needed.
 * <p/>
 * The shutdown strategy is <b>not</b> intended for Zwangine end users to use for stopping workflows. Instead, use
 * {@link WorkflowController} via {@link ZwangineContext}.
 * <p/>
 * The key problem is to stop the input consumers for the workflows such that no new messages is coming into Zwangine. But at
 * the same time still keep the workflows running so the existing in flight exchanges can still be run to completion. On
 * top of that there are some in memory components (such as SEDA) which may have pending messages on its in memory queue
 * which we want to run to completion as well, otherwise they will get lost.
 * <p/>
 * Zwangine provides a default strategy which supports all that that can be used as inspiration for your own strategy.
 *
 * @see org.zenithblox.spi.ShutdownAware
 * @see WorkflowController
 */
public interface ShutdownStrategy extends StaticService {

    /**
     * Shutdown the workflows, forcing shutdown being more aggressive, if timeout occurred.
     * <p/>
     * This operation is used when {@link ZwangineContext} is shutting down, to ensure Zwangine will shutdown if messages
     * seems to be <i>stuck</i>.
     *
     * @param  context   the zwangine context
     * @param  workflows    the workflows, ordered by the order they were started
     * @throws Exception is thrown if error shutting down the consumers, however its preferred to avoid this
     */
    void shutdownForced(ZwangineContext context, List<WorkflowStartupOrder> workflows) throws Exception;

    /**
     * Shutdown the workflows
     *
     * @param  context   the zwangine context
     * @param  workflows    the workflows, ordered by the order they were started
     * @throws Exception is thrown if error shutting down the consumers, however its preferred to avoid this
     */
    void shutdown(ZwangineContext context, List<WorkflowStartupOrder> workflows) throws Exception;

    /**
     * Suspends the workflows
     *
     * @param  context   the zwangine context
     * @param  workflows    the workflows, ordered by the order they are started
     * @throws Exception is thrown if error suspending the consumers, however its preferred to avoid this
     */
    void suspend(ZwangineContext context, List<WorkflowStartupOrder> workflows) throws Exception;

    /**
     * Shutdown the workflows using a specified timeout instead of the default timeout values
     *
     * @param  context   the zwangine context
     * @param  workflows    the workflows, ordered by the order they are started
     * @param  timeout   timeout
     * @param  timeUnit  the unit to use
     * @throws Exception is thrown if error shutting down the consumers, however its preferred to avoid this
     */
    void shutdown(ZwangineContext context, List<WorkflowStartupOrder> workflows, long timeout, TimeUnit timeUnit) throws Exception;

    /**
     * Shutdown the workflow using a specified timeout instead of the default timeout values and supports abortAfterTimeout
     * mode
     *
     * @param  context           the zwangine context
     * @param  workflow             the workflow
     * @param  timeout           timeout
     * @param  timeUnit          the unit to use
     * @param  abortAfterTimeout should abort shutdown after timeout
     * @return                   <tt>true</tt> if the workflow is stopped before the timeout
     * @throws Exception         is thrown if error shutting down the consumer, however its preferred to avoid this
     */
    boolean shutdown(ZwangineContext context, WorkflowStartupOrder workflow, long timeout, TimeUnit timeUnit, boolean abortAfterTimeout)
            throws Exception;

    /**
     * Suspends the workflows using a specified timeout instead of the default timeout values
     *
     * @param  context   the zwangine context
     * @param  workflows    the workflows, ordered by the order they were started
     * @param  timeout   timeout
     * @param  timeUnit  the unit to use
     * @throws Exception is thrown if error suspending the consumers, however its preferred to avoid this
     */
    void suspend(ZwangineContext context, List<WorkflowStartupOrder> workflows, long timeout, TimeUnit timeUnit) throws Exception;

    /**
     * Set a timeout to wait for the shutdown to complete.
     * <p/>
     * You must set a positive value. If you want to wait (forever) then use a very high value such as
     * {@link Long#MAX_VALUE}
     * <p/>
     * The default timeout unit is <tt>SECONDS</tt>
     *
     * @throws IllegalArgumentException if the timeout value is 0 or negative
     * @param  timeout                  timeout
     */
    void setTimeout(long timeout);

    /**
     * Gets the timeout.
     * <p/>
     * The default timeout unit is <tt>SECONDS</tt>
     *
     * @return the timeout
     */
    long getTimeout();

    /**
     * Set the time unit to use
     *
     * @param timeUnit the unit to use
     */
    void setTimeUnit(TimeUnit timeUnit);

    /**
     * Gets the time unit used
     *
     * @return the time unit
     */
    TimeUnit getTimeUnit();

    /**
     * Whether Zwangine should try to suppress logging during shutdown and timeout was triggered, meaning forced shutdown
     * is happening. And during forced shutdown we want to avoid logging errors/warnings et al. in the logs as a side
     * effect of the forced timeout.
     * <p/>
     * By default this is <tt>false</tt>
     * <p/>
     * Notice the suppression is a <i>best effort</i> as there may still be some logs coming from 3rd party libraries
     * and whatnot, which Zwangine cannot control.
     *
     * @param suppressLoggingOnTimeout <tt>true</tt> to suppress logging, false to log as usual.
     */
    void setSuppressLoggingOnTimeout(boolean suppressLoggingOnTimeout);

    /**
     * Whether Zwangine should try to suppress logging during shutdown and timeout was triggered, meaning forced shutdown
     * is happening. And during forced shutdown we want to avoid logging errors/warnings et al. in the logs as a side
     * effect of the forced timeout.
     * <p/>
     * By default this is <tt>false</tt>
     * <p/>
     * Notice the suppression is a <i>best effort</i> as there may still be some logs coming from 3rd party libraries
     * and whatnot, which Zwangine cannot control.
     */
    boolean isSuppressLoggingOnTimeout();

    /**
     * Sets whether to force shutdown of all consumers when a timeout occurred and thus not all consumers was shutdown
     * within that period.
     * <p/>
     * You should have good reasons to set this option to <tt>false</tt> as it means that the workflows keep running and is
     * halted abruptly when {@link ZwangineContext} has been shutdown.
     *
     * @param shutdownNowOnTimeout <tt>true</tt> to force shutdown, <tt>false</tt> to leave them running
     */
    void setShutdownNowOnTimeout(boolean shutdownNowOnTimeout);

    /**
     * Whether to force shutdown of all consumers when a timeout occurred.
     *
     * @return force shutdown or not
     */
    boolean isShutdownNowOnTimeout();

    /**
     * Sets whether workflows should be shutdown in reverse or the same order as they were started.
     *
     * @param shutdownWorkflowsInReverseOrder <tt>true</tt> to shutdown in reverse order
     */
    void setShutdownWorkflowsInReverseOrder(boolean shutdownWorkflowsInReverseOrder);

    /**
     * Whether to shutdown workflows in reverse order than they were started.
     * <p/>
     * This option is by default set to <tt>true</tt>.
     *
     * @return <tt>true</tt> if workflows should be shutdown in reverse order.
     */
    boolean isShutdownWorkflowsInReverseOrder();

    /**
     * Sets whether to log information about the inflight {@link org.zenithblox.Exchange}s which are still running
     * during a shutdown which didn't complete without the given timeout.
     *
     * @param logInflightExchangesOnTimeout <tt>true</tt> to log information about the inflight exchanges,
     *                                      <tt>false</tt> to not log
     */
    void setLogInflightExchangesOnTimeout(boolean logInflightExchangesOnTimeout);

    /**
     * Whether to log information about the inflight {@link org.zenithblox.Exchange}s which are still running during a
     * shutdown which didn't complete without the given timeout.
     */
    boolean isLogInflightExchangesOnTimeout();

    /**
     * Whether the shutdown strategy is forcing to shutdown
     */
    boolean isForceShutdown();

    /**
     * Whether a timeout has occurred during a shutdown.
     *
     * @deprecated use {@link #isTimeoutOccurred()}
     */
    @Deprecated(since = "4.8.0")
    boolean hasTimeoutOccurred();

    /**
     * Whether a timeout has occurred during a shutdown.
     */
    default boolean isTimeoutOccurred() {
        return hasTimeoutOccurred();
    }

    /**
     * Gets the logging level used for logging shutdown activity (such as starting and stopping workflows). The default
     * logging level is DEBUG.
     */
    LoggingLevel getLoggingLevel();

    /**
     * Sets the logging level used for logging shutdown activity (such as starting and stopping workflows). The default
     * logging level is DEBUG.
     */
    void setLoggingLevel(LoggingLevel loggingLevel);

}
