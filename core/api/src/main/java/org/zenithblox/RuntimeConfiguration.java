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
package org.zenithblox;

/**
 * Various runtime configuration options used by {@link org.zenithblox.ZwangineContext} and {@link Workflow} for cross
 * cutting functions such as tracing, delayer, stream cache and the like.
 */
public interface RuntimeConfiguration {

    /**
     * Sets whether stream caching is enabled or not (default is enabled).
     *
     * @param cache whether stream caching is enabled or not
     */
    void setStreamCaching(Boolean cache);

    /**
     * Returns whether stream cache is enabled
     *
     * @return <tt>true</tt> if stream cache is enabled
     */
    Boolean isStreamCaching();

    /**
     * Returns whether tracing enabled
     *
     * To use tracing then either turn on tracing standby or enable tracing to make tracing possible.
     *
     * @param tracing whether to enable tracing.
     */
    void setTracing(Boolean tracing);

    /**
     * Returns whether tracing enabled
     *
     * To use tracing then either turn on tracing standby or enable tracing to make tracing possible.
     *
     * @return <tt>true</tt> if tracing is enabled
     */
    Boolean isTracing();

    /**
     * Tracing pattern to match which node EIPs to trace. For example to match all To EIP nodes, use to*. The pattern
     * matches by node and workflow id's Multiple patterns can be separated by comma.
     */
    String getTracingPattern();

    /**
     * Tracing pattern to match which node EIPs to trace. For example to match all To EIP nodes, use to*. The pattern
     * matches by node and workflow id's Multiple patterns can be separated by comma.
     */
    void setTracingPattern(String tracePattern);

    /**
     * Sets whether backlog tracing is enabled or not (default is disabled).
     *
     * To use backlog tracing then this must be enabled on startup to be installed in the ZwangineContext.
     *
     * @param backlogTrace whether to enable backlog tracing.
     * @see                #setTracing(Boolean)
     */
    void setBacklogTracing(Boolean backlogTrace);

    /**
     * Returns whether backlog tracing is enabled.
     *
     * @return <tt>true</tt> if backlog tracing is enabled
     */
    Boolean isBacklogTracing();


    /**
     * Sets whether message history is enabled or not (default is disabled).
     *
     * @param messageHistory whether message history is enabled
     */
    void setMessageHistory(Boolean messageHistory);

    /**
     * Returns whether message history is enabled
     *
     * @return <tt>true</tt> if message history is enabled
     */
    Boolean isMessageHistory();

    /**
     * Sets whether security mask for Logging is enabled or not (default is disabled).
     *
     * @param logMask <tt>true</tt> if mask is enabled
     */
    void setLogMask(Boolean logMask);

    /**
     * Gets whether security mask for Logging is enabled or not.
     *
     * @return <tt>true</tt> if mask is enabled
     */
    Boolean isLogMask();

    /**
     * Sets whether to log exhausted message body with message history.
     *
     * @param logExhaustedMessageBody whether message body should be logged
     */
    void setLogExhaustedMessageBody(Boolean logExhaustedMessageBody);

    /**
     * Returns whether to log exhausted message body with message history.
     *
     * @return <tt>true</tt> if logging of message body is enabled
     */
    Boolean isLogExhaustedMessageBody();

    /**
     * Sets a delay value in millis that a message is delayed at every step it takes in the workflow path, slowing the
     * process down to better observe what is occurring
     * <p/>
     * Is disabled by default
     *
     * @param delay delay in millis
     */
    void setDelayer(Long delay);

    /**
     * Gets the delay value
     *
     * @return delay in millis, or <tt>null</tt> if disabled
     */
    Long getDelayer();

    /**
     * Sets whether the object should automatically start when Zwangine starts.
     * <p/>
     * <b>Important:</b> Currently only workflows can be disabled, as {@link ZwangineContext}s are always started. <br/>
     * <b>Note:</b> When setting auto startup <tt>false</tt> on {@link ZwangineContext} then that takes precedence and
     * <i>no</i> workflows is started. You would need to start {@link ZwangineContext} explicit using the
     * {@link org.zenithblox.ZwangineContext#start()} method, to start the context, and then you would need to start the
     * workflows manually using {@link org.zenithblox.spi.WorkflowController#startWorkflow(String)}.
     * <p/>
     * Default is <tt>true</tt> to always start up.
     *
     * @param autoStartup whether to start up automatically.
     */
    void setAutoStartup(Boolean autoStartup);

    /**
     * Gets whether the object should automatically start when Zwangine starts.
     * <p/>
     * <b>Important:</b> Currently only workflows can be disabled, as {@link ZwangineContext}s are always started. <br/>
     * Default is <tt>true</tt> to always start up.
     *
     * @return <tt>true</tt> if object should automatically start
     */
    Boolean isAutoStartup();

    /**
     * Sets the ShutdownWorkflow option for workflows.
     *
     * @param shutdownWorkflow the option to use.
     */
    void setShutdownWorkflow(ShutdownWorkflow shutdownWorkflow);

    /**
     * Gets the option to use when shutting down the workflow.
     *
     * @return the option
     */
    ShutdownWorkflow getShutdownWorkflow();

    /**
     * Sets the ShutdownRunningTask option to use when shutting down a workflow.
     *
     * @param shutdownRunningTask the option to use.
     */
    void setShutdownRunningTask(ShutdownRunningTask shutdownRunningTask);

    /**
     * Gets the ShutdownRunningTask option in use when shutting down a workflow.
     *
     * @return the option
     */
    ShutdownRunningTask getShutdownRunningTask();

    /**
     * Sets whether to allow access to the original message from Zwangine's error handler, or from
     * {@link org.zenithblox.spi.UnitOfWork#getOriginalInMessage()}.
     * <p/>
     * Turning this off can optimize performance, as defensive copy of the original message is not needed.
     *
     * @param allowUseOriginalMessage the option to use.
     */
    void setAllowUseOriginalMessage(Boolean allowUseOriginalMessage);

    /**
     * Gets whether access to the original message from Zwangine's error handler, or from
     * {@link org.zenithblox.spi.UnitOfWork#getOriginalInMessage()} is allowed.
     *
     * @return the option
     */
    Boolean isAllowUseOriginalMessage();

    /**
     * Whether to use case sensitive or insensitive headers.
     *
     * Important: When using case sensitive (this is set to false). Then the map is case sensitive which means headers
     * such as <tt>content-type</tt> and <tt>Content-Type</tt> are two different keys which can be a problem for some
     * protocols such as HTTP based, which rely on case insensitive headers. However case sensitive implementations can
     * yield faster performance. Therefore use case sensitive implementation with care.
     */
    Boolean isCaseInsensitiveHeaders();

    /**
     * Whether to use case sensitive or insensitive headers.
     *
     * Important: When using case sensitive (this is set to false). Then the map is case sensitive which means headers
     * such as <tt>content-type</tt> and <tt>Content-Type</tt> are two different keys which can be a problem for some
     * protocols such as HTTP based, which rely on case insensitive headers. However case sensitive implementations can
     * yield faster performance. Therefore use case sensitive implementation with care.
     */
    void setCaseInsensitiveHeaders(Boolean caseInsensitiveHeaders);

    /**
     * Whether autowiring is enabled. This is used for automatic autowiring options (the option must be marked as
     * autowired) by looking up in the registry to find if there is a single instance of matching type, which then gets
     * configured on the component. This can be used for automatic configuring JDBC data sources, JMS connection
     * factories, AWS Clients, etc.
     */
    Boolean isAutowiredEnabled();

    /**
     * Whether autowiring is enabled. This is used for automatic autowiring options (the option must be marked as
     * autowired) by looking up in the registry to find if there is a single instance of matching type, which then gets
     * configured on the component. This can be used for automatic configuring JDBC data sources, JMS connection
     * factories, AWS Clients, etc.
     */
    void setAutowiredEnabled(Boolean autowiredEnabled);

}
