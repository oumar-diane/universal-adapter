/*
 * Licensed to the  Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the  License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.zentihblox.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zenithblox.model.errorhandler;

import org.zenithblox.*;
import org.zenithblox.model.RedeliveryPolicyDefinition;
import org.zenithblox.processor.errorhandler.DefaultErrorHandler;
import org.zenithblox.processor.errorhandler.RedeliveryPolicy;
import org.zenithblox.spi.ZwangineLogger;
import org.zenithblox.spi.Metadata;
import org.zenithblox.support.ExpressionToPredicateAdapter;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;

/**
 * The default error handler.
 */
@Metadata(label = "configuration,error")
public class DefaultErrorHandlerDefinition extends BaseErrorHandlerDefinition {

    private ZwangineLogger loggerBean;
    private Processor onRedeliveryProcessor;
    private Processor onPrepareFailureProcessor;
    private Processor onExceptionOccurredProcessor;
    private ScheduledExecutorService executorServiceBean;
    private Predicate retryWhilePredicate;

    // commonly used should be first
    private RedeliveryPolicyDefinition redeliveryPolicy;
    @Metadata(javaType = "java.lang.Boolean")
    private String useOriginalMessage;
    @Metadata(javaType = "java.lang.Boolean")
    private String useOriginalBody;
    @Metadata(label = "advanced", javaType = "org.zenithblox.processor.errorhandler.RedeliveryPolicy")
    private String redeliveryPolicyRef;
    @Metadata(label = "advanced")
    private String loggerRef;
    @Metadata(label = "advanced", javaType = "org.zenithblox.LoggingLevel", defaultValue = "ERROR",
              enums = "TRACE,DEBUG,INFO,WARN,ERROR,OFF")
    private String level;
    @Metadata(label = "advanced")
    private String logName;
    @Metadata(label = "advanced", javaType = "org.zenithblox.Processor")
    private String onRedeliveryRef;
    @Metadata(label = "advanced", javaType = "org.zenithblox.Processor")
    private String onExceptionOccurredRef;
    @Metadata(label = "advanced", javaType = "org.zenithblox.Processor")
    private String onPrepareFailureRef;
    @Metadata(label = "advanced", javaType = "org.zenithblox.Processor")
    private String retryWhileRef;
    @Metadata(label = "advanced", javaType = "java.util.concurrent.ScheduledExecutorService")
    private String executorServiceRef;

    public DefaultErrorHandlerDefinition() {
    }

    protected DefaultErrorHandlerDefinition(DefaultErrorHandlerDefinition source) {
        this.loggerBean = source.loggerBean;
        this.onRedeliveryProcessor = source.onRedeliveryProcessor;
        this.onPrepareFailureProcessor = source.onPrepareFailureProcessor;
        this.onExceptionOccurredProcessor = source.onExceptionOccurredProcessor;
        this.executorServiceBean = source.executorServiceBean;
        this.retryWhilePredicate = source.retryWhilePredicate;
        this.redeliveryPolicy = source.redeliveryPolicy;
        this.useOriginalMessage = source.useOriginalMessage;
        this.useOriginalBody = source.useOriginalBody;
        this.redeliveryPolicyRef = source.redeliveryPolicyRef;
        this.loggerRef = source.loggerRef;
        this.level = source.level;
        this.logName = source.logName;
        this.onRedeliveryRef = source.onRedeliveryRef;
        this.onExceptionOccurredRef = source.onExceptionOccurredRef;
        this.onPrepareFailureRef = source.onPrepareFailureRef;
        this.retryWhileRef = source.retryWhileRef;
        this.executorServiceRef = source.executorServiceRef;
    }

    @Override
    public DefaultErrorHandlerDefinition copyDefinition() {
        return new DefaultErrorHandlerDefinition(this);
    }

    @Override
    public boolean supportTransacted() {
        return false;
    }

    @Override
    public ErrorHandlerFactory cloneBuilder() {
        DefaultErrorHandlerDefinition answer = new DefaultErrorHandlerDefinition();
        cloneBuilder(answer);
        return answer;
    }

    protected void cloneBuilder(DefaultErrorHandlerDefinition other) {
        other.setExecutorServiceBean(getExecutorServiceBean());
        other.setExecutorServiceRef(getExecutorServiceRef());
        other.setLevel(getLevel());
        other.setLogName(getLogName());
        other.setLoggerBean(getLoggerBean());
        other.setLoggerRef(getLoggerRef());
        other.setOnExceptionOccurredProcessor(getOnExceptionOccurredProcessor());
        other.setOnExceptionOccurredRef(getOnExceptionOccurredRef());
        other.setOnPrepareFailureProcessor(getOnPrepareFailureProcessor());
        other.setOnPrepareFailureRef(getOnPrepareFailureRef());
        other.setOnRedeliveryProcessor(getOnRedeliveryProcessor());
        other.setOnRedeliveryRef(getOnRedeliveryRef());
        other.setRedeliveryPolicyRef(getRedeliveryPolicyRef());
        other.setRetryWhilePredicate(getRetryWhilePredicate());
        other.setRetryWhileRef(getRetryWhileRef());
        other.setUseOriginalBody(getUseOriginalBody());
        other.setUseOriginalMessage(getUseOriginalMessage());
        if (hasRedeliveryPolicy()) {
            other.setRedeliveryPolicy(getRedeliveryPolicy().copy());
        }
    }

    protected RedeliveryPolicyDefinition createRedeliveryPolicy() {
        return new RedeliveryPolicyDefinition();
    }

    public String getLoggerRef() {
        return loggerRef;
    }

    /**
     * References to a logger to use as logger for the error handler
     */
    public void setLoggerRef(String loggerRef) {
        this.loggerRef = loggerRef;
    }

    public ZwangineLogger getLoggerBean() {
        return loggerBean;
    }

    public void setLoggerBean(ZwangineLogger loggerBean) {
        this.loggerBean = loggerBean;
    }

    public String getLevel() {
        return level;
    }

    /**
     * Logging level to use by error handler
     */
    public void setLevel(String level) {
        this.level = level;
    }

    public String getLogName() {
        return logName;
    }

    /**
     * Name of the logger to use by the error handler
     */
    public void setLogName(String logName) {
        this.logName = logName;
    }

    public String getUseOriginalMessage() {
        return useOriginalMessage;
    }

    /**
     * Will use the original input {@link org.zenithblox.Message} (original body and headers) when an
     * {@link org.zenithblox.Exchange} is moved to the dead letter queue.
     * <p/>
     * <b>Notice:</b> this only applies when all redeliveries attempt have failed and the
     * {@link org.zenithblox.Exchange} is doomed for failure. <br/>
     * Instead of using the current inprogress {@link org.zenithblox.Exchange} IN message we use the original IN
     * message instead. This allows you to store the original input in the dead letter queue instead of the inprogress
     * snapshot of the IN message. For instance if you workflow transform the IN body during routing and then failed. With
     * the original exchange store in the dead letter queue it might be easier to manually re submit the
     * {@link org.zenithblox.Exchange} again as the IN message is the same as when Zwangine received it. So you should be
     * able to send the {@link org.zenithblox.Exchange} to the same input.
     * <p/>
     * The difference between useOriginalMessage and useOriginalBody is that the former includes both the original body
     * and headers, where as the latter only includes the original body. You can use the latter to enrich the message
     * with custom headers and include the original message body. The former wont let you do this, as its using the
     * original message body and headers as they are. You cannot enable both useOriginalMessage and useOriginalBody.
     * <p/>
     * The original input message is defensively copied, and the copied message body is converted to
     * {@link org.zenithblox.StreamCache} if possible (stream caching is enabled, can be disabled globally or on the
     * original workflow), to ensure the body can be read when the original message is being used later. If the body is
     * converted to {@link org.zenithblox.StreamCache} then the message body on the current
     * {@link org.zenithblox.Exchange} is replaced with the {@link org.zenithblox.StreamCache} body. If the body is
     * not converted to {@link org.zenithblox.StreamCache} then the body will not be able to re-read when accessed
     * later.
     * <p/>
     * <b>Important:</b> The original input means the input message that are bounded by the current
     * {@link org.zenithblox.spi.UnitOfWork}. An unit of work typically spans one workflow, or multiple workflows if they
     * are connected using internal endpoints such as direct or seda. When messages is passed via external endpoints
     * such as JMS or HTTP then the consumer will create a new unit of work, with the message it received as input as
     * the original input. Also some EIP patterns such as splitter, multicast, will create a new unit of work boundary
     * for the messages in their sub-workflow (eg the splitted message); however these EIPs have an option named
     * <tt>shareUnitOfWork</tt> which allows to combine with the parent unit of work in regard to error handling and
     * therefore use the parent original message.
     * <p/>
     * By default this feature is off.
     */
    public void setUseOriginalMessage(String useOriginalMessage) {
        this.useOriginalMessage = useOriginalMessage;
    }

    public String getUseOriginalBody() {
        return useOriginalBody;
    }

    /**
     * Will use the original input {@link org.zenithblox.Message} body (original body only) when an
     * {@link org.zenithblox.Exchange} is moved to the dead letter queue.
     * <p/>
     * <b>Notice:</b> this only applies when all redeliveries attempt have failed and the
     * {@link org.zenithblox.Exchange} is doomed for failure. <br/>
     * Instead of using the current inprogress {@link org.zenithblox.Exchange} IN message we use the original IN
     * message instead. This allows you to store the original input in the dead letter queue instead of the inprogress
     * snapshot of the IN message. For instance if you workflow transform the IN body during routing and then failed. With
     * the original exchange store in the dead letter queue it might be easier to manually re submit the
     * {@link org.zenithblox.Exchange} again as the IN message is the same as when Zwangine received it. So you should be
     * able to send the {@link org.zenithblox.Exchange} to the same input.
     * <p/>
     * The difference between useOriginalMessage and useOriginalBody is that the former includes both the original body
     * and headers, where as the latter only includes the original body. You can use the latter to enrich the message
     * with custom headers and include the original message body. The former wont let you do this, as its using the
     * original message body and headers as they are. You cannot enable both useOriginalMessage and useOriginalBody.
     * <p/>
     * The original input message is defensively copied, and the copied message body is converted to
     * {@link org.zenithblox.StreamCache} if possible (stream caching is enabled, can be disabled globally or on the
     * original workflow), to ensure the body can be read when the original message is being used later. If the body is
     * converted to {@link org.zenithblox.StreamCache} then the message body on the current
     * {@link org.zenithblox.Exchange} is replaced with the {@link org.zenithblox.StreamCache} body. If the body is
     * not converted to {@link org.zenithblox.StreamCache} then the body will not be able to re-read when accessed
     * later.
     * <p/>
     * <b>Important:</b> The original input means the input message that are bounded by the current
     * {@link org.zenithblox.spi.UnitOfWork}. An unit of work typically spans one workflow, or multiple workflows if they
     * are connected using internal endpoints such as direct or seda. When messages is passed via external endpoints
     * such as JMS or HTTP then the consumer will create a new unit of work, with the message it received as input as
     * the original input. Also some EIP patterns such as splitter, multicast, will create a new unit of work boundary
     * for the messages in their sub-workflow (eg the splitted message); however these EIPs have an option named
     * <tt>shareUnitOfWork</tt> which allows to combine with the parent unit of work in regard to error handling and
     * therefore use the parent original message.
     * <p/>
     * By default this feature is off.
     */
    public void setUseOriginalBody(String useOriginalBody) {
        this.useOriginalBody = useOriginalBody;
    }

    public String getOnRedeliveryRef() {
        return onRedeliveryRef;
    }

    /**
     * Sets a reference to a processor that should be processed <b>before</b> a redelivery attempt.
     * <p/>
     * Can be used to change the {@link org.zenithblox.Exchange} <b>before</b> its being redelivered.
     */
    public void setOnRedeliveryRef(String onRedeliveryRef) {
        this.onRedeliveryRef = onRedeliveryRef;
    }

    public Processor getOnRedeliveryProcessor() {
        return onRedeliveryProcessor;
    }

    /**
     * Sets a processor that should be processed <b>before</b> a redelivery attempt.
     * <p/>
     * Can be used to change the {@link org.zenithblox.Exchange} <b>before</b> its being redelivered.
     */
    public void setOnRedeliveryProcessor(Processor onRedeliveryProcessor) {
        this.onRedeliveryProcessor = onRedeliveryProcessor;
    }

    public String getOnExceptionOccurredRef() {
        return onExceptionOccurredRef;
    }

    /**
     * Sets a reference to a processor that should be processed <b>just after</b> an exception occurred. Can be used to
     * perform custom logging about the occurred exception at the exact time it happened.
     * <p/>
     * Important: Any exception thrown from this processor will be ignored.
     */
    public void setOnExceptionOccurredRef(String onExceptionOccurredRef) {
        this.onExceptionOccurredRef = onExceptionOccurredRef;
    }

    public Processor getOnExceptionOccurredProcessor() {
        return onExceptionOccurredProcessor;
    }

    /**
     * Sets a processor that should be processed <b>just after</b> an exception occurred. Can be used to perform custom
     * logging about the occurred exception at the exact time it happened.
     * <p/>
     * Important: Any exception thrown from this processor will be ignored.
     */
    public void setOnExceptionOccurredProcessor(Processor onExceptionOccurredProcessor) {
        this.onExceptionOccurredProcessor = onExceptionOccurredProcessor;
    }

    public String getOnPrepareFailureRef() {
        return onPrepareFailureRef;
    }

    /**
     * Sets a reference to a processor to prepare the {@link org.zenithblox.Exchange} before handled by the failure
     * processor / dead letter channel. This allows for example to enrich the message before sending to a dead letter
     * queue.
     */
    public void setOnPrepareFailureRef(String onPrepareFailureRef) {
        this.onPrepareFailureRef = onPrepareFailureRef;
    }

    public Processor getOnPrepareFailureProcessor() {
        return onPrepareFailureProcessor;
    }

    /**
     * Sets a processor to prepare the {@link org.zenithblox.Exchange} before handled by the failure processor / dead
     * letter channel. This allows for example to enrich the message before sending to a dead letter queue.
     */
    public void setOnPrepareFailureProcessor(Processor onPrepareFailureProcessor) {
        this.onPrepareFailureProcessor = onPrepareFailureProcessor;
    }

    public String getRetryWhileRef() {
        return retryWhileRef;
    }

    /**
     * Sets a retry while predicate.
     *
     * Will continue retrying until the predicate evaluates to false.
     */
    public void setRetryWhileRef(String retryWhileRef) {
        this.retryWhileRef = retryWhileRef;
    }

    public String getRedeliveryPolicyRef() {
        return redeliveryPolicyRef;
    }

    /**
     * Sets a reference to a {@link RedeliveryPolicy} to be used for redelivery settings.
     */
    public void setRedeliveryPolicyRef(String redeliveryPolicyRef) {
        this.redeliveryPolicyRef = redeliveryPolicyRef;
    }

    public String getExecutorServiceRef() {
        return executorServiceRef;
    }

    /**
     * Sets a reference to a thread pool to be used by the error handler
     */
    public void setExecutorServiceRef(String executorServiceRef) {
        this.executorServiceRef = executorServiceRef;
    }

    public ScheduledExecutorService getExecutorServiceBean() {
        return executorServiceBean;
    }

    /**
     * Sets a thread pool to be used by the error handler
     */
    public void setExecutorServiceBean(ScheduledExecutorService executorServiceBean) {
        this.executorServiceBean = executorServiceBean;
    }

    public Predicate getRetryWhilePredicate() {
        return retryWhilePredicate;
    }

    /**
     * Sets a retry while predicate.
     *
     * Will continue retrying until the predicate evaluates to false.
     */
    public void setRetryWhilePredicate(Predicate retryWhilePredicate) {
        this.retryWhilePredicate = retryWhilePredicate;
    }

    public RedeliveryPolicyDefinition getRedeliveryPolicy() {
        if (redeliveryPolicy == null) {
            redeliveryPolicy = createRedeliveryPolicy();
        }
        return redeliveryPolicy;
    }

    public boolean hasRedeliveryPolicy() {
        return redeliveryPolicy != null;
    }

    /**
     * Sets the redelivery settings
     */
    public void setRedeliveryPolicy(RedeliveryPolicyDefinition redeliveryPolicy) {
        this.redeliveryPolicy = redeliveryPolicy;
    }

    // Builder methods
    // -------------------------------------------------------------------------
    public DefaultErrorHandlerDefinition backOffMultiplier(double backOffMultiplier) {
        getRedeliveryPolicy().backOffMultiplier(backOffMultiplier);
        return this;
    }

    public DefaultErrorHandlerDefinition collisionAvoidancePercent(double collisionAvoidancePercent) {
        getRedeliveryPolicy().collisionAvoidancePercent(collisionAvoidancePercent);
        return this;
    }

    public DefaultErrorHandlerDefinition redeliveryDelay(long delay) {
        getRedeliveryPolicy().redeliveryDelay(delay);
        return this;
    }

    public DefaultErrorHandlerDefinition delayPattern(String delayPattern) {
        getRedeliveryPolicy().delayPattern(delayPattern);
        return this;
    }

    public DefaultErrorHandlerDefinition maximumRedeliveries(int maximumRedeliveries) {
        getRedeliveryPolicy().maximumRedeliveries(maximumRedeliveries);
        return this;
    }

    public DefaultErrorHandlerDefinition disableRedelivery() {
        getRedeliveryPolicy().maximumRedeliveries(0);
        return this;
    }

    public DefaultErrorHandlerDefinition maximumRedeliveryDelay(long maximumRedeliveryDelay) {
        getRedeliveryPolicy().maximumRedeliveryDelay(maximumRedeliveryDelay);
        return this;
    }

    public DefaultErrorHandlerDefinition useCollisionAvoidance() {
        getRedeliveryPolicy().useCollisionAvoidance();
        return this;
    }

    public DefaultErrorHandlerDefinition useExponentialBackOff() {
        getRedeliveryPolicy().useExponentialBackOff();
        return this;
    }

    public DefaultErrorHandlerDefinition retriesExhaustedLogLevel(LoggingLevel retriesExhaustedLogLevel) {
        getRedeliveryPolicy().setRetriesExhaustedLogLevel(retriesExhaustedLogLevel.name());
        return this;
    }

    public DefaultErrorHandlerDefinition retryAttemptedLogLevel(LoggingLevel retryAttemptedLogLevel) {
        getRedeliveryPolicy().setRetryAttemptedLogLevel(retryAttemptedLogLevel.name());
        return this;
    }

    public DefaultErrorHandlerDefinition retryAttemptedLogInterval(int retryAttemptedLogInterval) {
        getRedeliveryPolicy().setRetryAttemptedLogInterval(String.valueOf(retryAttemptedLogInterval));
        return this;
    }

    public DefaultErrorHandlerDefinition logStackTrace(boolean logStackTrace) {
        getRedeliveryPolicy().setLogStackTrace(logStackTrace ? "true" : "false");
        return this;
    }

    public DefaultErrorHandlerDefinition logRetryStackTrace(boolean logRetryStackTrace) {
        getRedeliveryPolicy().setLogRetryStackTrace(logRetryStackTrace ? "true" : "false");
        return this;
    }

    public DefaultErrorHandlerDefinition logHandled(boolean logHandled) {
        getRedeliveryPolicy().setLogHandled(logHandled ? "true" : "false");
        return this;
    }

    public DefaultErrorHandlerDefinition logNewException(boolean logNewException) {
        getRedeliveryPolicy().setLogNewException(logNewException ? "true" : "false");
        return this;
    }

    public DefaultErrorHandlerDefinition logExhausted(boolean logExhausted) {
        getRedeliveryPolicy().setLogExhausted(logExhausted ? "true" : "false");
        return this;
    }

    public DefaultErrorHandlerDefinition logRetryAttempted(boolean logRetryAttempted) {
        getRedeliveryPolicy().setLogRetryAttempted(logRetryAttempted ? "true" : "false");
        return this;
    }

    public DefaultErrorHandlerDefinition logExhaustedMessageHistory(boolean logExhaustedMessageHistory) {
        getRedeliveryPolicy().setLogExhaustedMessageHistory(logExhaustedMessageHistory ? "true" : "false");
        return this;
    }

    public DefaultErrorHandlerDefinition logExhaustedMessageBody(boolean logExhaustedMessageBody) {
        getRedeliveryPolicy().setLogExhaustedMessageBody(logExhaustedMessageBody ? "true" : "false");
        return this;
    }

    public DefaultErrorHandlerDefinition exchangeFormatterRef(String exchangeFormatterRef) {
        getRedeliveryPolicy().setExchangeFormatterRef(exchangeFormatterRef);
        return this;
    }

    /**
     * Will allow asynchronous delayed redeliveries. The workflow, in particular the consumer's component, must support the
     * Asynchronous Routing Engine (e.g. seda)
     *
     * @see    RedeliveryPolicy#setAsyncDelayedRedelivery(boolean)
     * @return the builder
     */
    public DefaultErrorHandlerDefinition asyncDelayedRedelivery() {
        getRedeliveryPolicy().setAsyncDelayedRedelivery("true");
        return this;
    }

    /**
     * Controls whether to allow redelivery while stopping/shutting down a workflow that uses error handling.
     *
     * @param  allowRedeliveryWhileStopping <tt>true</tt> to allow redelivery, <tt>false</tt> to reject redeliveries
     * @return                              the builder
     */
    public DefaultErrorHandlerDefinition allowRedeliveryWhileStopping(boolean allowRedeliveryWhileStopping) {
        getRedeliveryPolicy().setAllowRedeliveryWhileStopping(allowRedeliveryWhileStopping ? "true" : "false");
        return this;
    }

    /**
     * Sets the thread pool to be used for redelivery.
     *
     * @param  executorService the scheduled thread pool to use
     * @return                 the builder.
     */
    public DefaultErrorHandlerDefinition executorService(ScheduledExecutorService executorService) {
        setExecutorServiceBean(executorService);
        return this;
    }

    /**
     * Sets a reference to a thread pool to be used for redelivery.
     *
     * @param  ref reference to a scheduled thread pool
     * @return     the builder.
     */
    public DefaultErrorHandlerDefinition executorServiceRef(String ref) {
        setExecutorServiceRef(ref);
        return this;
    }

    /**
     * Sets the logger used for caught exceptions
     *
     * @param  logger the logger
     * @return        the builder
     */
    public DefaultErrorHandlerDefinition logger(ZwangineLogger logger) {
        setLoggerBean(logger);
        return this;
    }

    /**
     * Sets the logging level of exceptions caught
     *
     * @param  level the logging level
     * @return       the builder
     */
    public DefaultErrorHandlerDefinition loggingLevel(String level) {
        setLevel(level);
        return this;
    }

    /**
     * Sets the logging level of exceptions caught
     *
     * @param  level the logging level
     * @return       the builder
     */
    public DefaultErrorHandlerDefinition loggingLevel(LoggingLevel level) {
        setLevel(level.name());
        return this;
    }

    /**
     * Sets the log used for caught exceptions
     *
     * @param  log the logger
     * @return     the builder
     */
    public DefaultErrorHandlerDefinition log(org.slf4j.Logger log) {
        if (loggerBean == null) {
            loggerBean = new ZwangineLogger(LoggerFactory.getLogger(DefaultErrorHandler.class), LoggingLevel.ERROR);
        }
        loggerBean.setLog(log);
        return this;
    }

    /**
     * Sets the log used for caught exceptions
     *
     * @param  log the log name
     * @return     the builder
     */
    public DefaultErrorHandlerDefinition log(String log) {
        return log(LoggerFactory.getLogger(log));
    }

    /**
     * Sets the log used for caught exceptions
     *
     * @param  log the log class
     * @return     the builder
     */
    public DefaultErrorHandlerDefinition log(Class<?> log) {
        return log(LoggerFactory.getLogger(log));
    }

    /**
     * Sets a processor that should be processed <b>before</b> a redelivery attempt.
     * <p/>
     * Can be used to change the {@link org.zenithblox.Exchange} <b>before</b> its being redelivered.
     *
     * @param  processor the processor
     * @return           the builder
     */
    public DefaultErrorHandlerDefinition onRedelivery(Processor processor) {
        setOnRedeliveryProcessor(processor);
        return this;
    }

    /**
     * Sets a reference for the processor to use <b>before</b> a redelivery attempt.
     *
     * @param  onRedeliveryRef the processor's reference
     * @return                 the builder
     * @see                    #onRedelivery(Processor)
     */
    public DefaultErrorHandlerDefinition onRedeliveryRef(String onRedeliveryRef) {
        setOnRedeliveryRef(onRedeliveryRef);
        return this;
    }

    /**
     * Sets the retry while expression.
     * <p/>
     * Will continue retrying until expression evaluates to <tt>false</tt>.
     *
     * @param  retryWhile expression that determines when to stop retrying
     * @return            the builder
     */
    public DefaultErrorHandlerDefinition retryWhile(Expression retryWhile) {
        setRetryWhilePredicate(ExpressionToPredicateAdapter.toPredicate(retryWhile));
        return this;
    }

    public DefaultErrorHandlerDefinition retryWhileRef(String retryWhileRef) {
        setRetryWhileRef(retryWhileRef);
        return this;
    }

    /**
     * Will use the original input {@link org.zenithblox.Message} (original body and headers) when an
     * {@link org.zenithblox.Exchange} is moved to the dead letter queue.
     * <p/>
     * <b>Notice:</b> this only applies when all redeliveries attempt have failed and the
     * {@link org.zenithblox.Exchange} is doomed for failure. <br/>
     * Instead of using the current inprogress {@link org.zenithblox.Exchange} IN message we use the original IN
     * message instead. This allows you to store the original input in the dead letter queue instead of the inprogress
     * snapshot of the IN message. For instance if you workflow transform the IN body during routing and then failed. With
     * the original exchange store in the dead letter queue it might be easier to manually re submit the
     * {@link org.zenithblox.Exchange} again as the IN message is the same as when Zwangine received it. So you should be
     * able to send the {@link org.zenithblox.Exchange} to the same input.
     * <p/>
     * The difference between useOriginalMessage and useOriginalBody is that the former includes both the original body
     * and headers, where as the latter only includes the original body. You can use the latter to enrich the message
     * with custom headers and include the original message body. The former wont let you do this, as its using the
     * original message body and headers as they are. You cannot enable both useOriginalMessage and useOriginalBody.
     * <p/>
     * The original input message is defensively copied, and the copied message body is converted to
     * {@link org.zenithblox.StreamCache} if possible (stream caching is enabled, can be disabled globally or on the
     * original workflow), to ensure the body can be read when the original message is being used later. If the body is
     * converted to {@link org.zenithblox.StreamCache} then the message body on the current
     * {@link org.zenithblox.Exchange} is replaced with the {@link org.zenithblox.StreamCache} body. If the body is
     * not converted to {@link org.zenithblox.StreamCache} then the body will not be able to re-read when accessed
     * later.
     * <p/>
     * <b>Important:</b> The original input means the input message that are bounded by the current
     * {@link org.zenithblox.spi.UnitOfWork}. An unit of work typically spans one workflow, or multiple workflows if they
     * are connected using internal endpoints such as direct or seda. When messages is passed via external endpoints
     * such as JMS or HTTP then the consumer will create a new unit of work, with the message it received as input as
     * the original input. Also some EIP patterns such as splitter, multicast, will create a new unit of work boundary
     * for the messages in their sub-workflow (eg the split message); however these EIPs have an option named
     * <tt>shareUnitOfWork</tt> which allows to combine with the parent unit of work in regard to error handling and
     * therefore use the parent original message.
     * <p/>
     * By default this feature is off.
     *
     * @return the builder
     * @see    #useOriginalBody()
     */
    public DefaultErrorHandlerDefinition useOriginalMessage() {
        setUseOriginalMessage("true");
        return this;
    }

    /**
     * Will use the original input {@link org.zenithblox.Message} body (original body only) when an
     * {@link org.zenithblox.Exchange} is moved to the dead letter queue.
     * <p/>
     * <b>Notice:</b> this only applies when all redeliveries attempt have failed and the
     * {@link org.zenithblox.Exchange} is doomed for failure. <br/>
     * Instead of using the current inprogress {@link org.zenithblox.Exchange} IN message we use the original IN
     * message instead. This allows you to store the original input in the dead letter queue instead of the inprogress
     * snapshot of the IN message. For instance if you workflow transform the IN body during routing and then failed. With
     * the original exchange store in the dead letter queue it might be easier to manually re submit the
     * {@link org.zenithblox.Exchange} again as the IN message is the same as when Zwangine received it. So you should be
     * able to send the {@link org.zenithblox.Exchange} to the same input.
     * <p/>
     * The difference between useOriginalMessage and useOriginalBody is that the former includes both the original body
     * and headers, where as the latter only includes the original body. You can use the latter to enrich the message
     * with custom headers and include the original message body. The former wont let you do this, as its using the
     * original message body and headers as they are. You cannot enable both useOriginalMessage and useOriginalBody.
     * <p/>
     * The original input message is defensively copied, and the copied message body is converted to
     * {@link org.zenithblox.StreamCache} if possible, to ensure the body can be read when the original message is
     * being used later. If the body is not converted to {@link org.zenithblox.StreamCache} then the body will not be
     * able to re-read when accessed later.
     * <p/>
     * <b>Important:</b> The original input means the input message that are bounded by the current
     * {@link org.zenithblox.spi.UnitOfWork}. An unit of work typically spans one workflow, or multiple workflows if they
     * are connected using internal endpoints such as direct or seda. When messages is passed via external endpoints
     * such as JMS or HTTP then the consumer will create a new unit of work, with the message it received as input as
     * the original input. Also some EIP patterns such as splitter, multicast, will create a new unit of work boundary
     * for the messages in their sub-workflow (eg the split message); however these EIPs have an option named
     * <tt>shareUnitOfWork</tt> which allows to combine with the parent unit of work in regard to error handling and
     * therefore use the parent original message.
     * <p/>
     * By default this feature is off.
     *
     * @return the builder
     * @see    #useOriginalMessage()
     */
    public DefaultErrorHandlerDefinition useOriginalBody() {
        setUseOriginalBody("true");
        return this;
    }

    /**
     * Sets a custom {@link org.zenithblox.Processor} to prepare the {@link org.zenithblox.Exchange} before handled
     * by the failure processor / dead letter channel. This allows for example to enrich the message before sending to a
     * dead letter queue.
     *
     * @param  processor the processor
     * @return           the builder
     */
    public DefaultErrorHandlerDefinition onPrepareFailure(Processor processor) {
        setOnPrepareFailureProcessor(processor);
        return this;
    }

    /**
     * Sets a reference for the processor to use before handled by the failure processor.
     *
     * @param  onPrepareFailureRef the processor's reference
     * @return                     the builder
     * @see                        #onPrepareFailure(Processor)
     */
    public DefaultErrorHandlerDefinition onPrepareFailureRef(String onPrepareFailureRef) {
        setOnPrepareFailureRef(onPrepareFailureRef);
        return this;
    }

    /**
     * Sets a custom {@link org.zenithblox.Processor} to process the {@link org.zenithblox.Exchange} just after an
     * exception was thrown. This allows to execute the processor at the same time the exception was thrown.
     * <p/>
     * Important: Any exception thrown from this processor will be ignored.
     *
     * @param  processor the processor
     * @return           the builder
     */
    public DefaultErrorHandlerDefinition onExceptionOccurred(Processor processor) {
        setOnExceptionOccurredProcessor(processor);
        return this;
    }

    /**
     * Sets a reference for the processor to use just after an exception was thrown.
     *
     * @param  onExceptionOccurredRef the processor's reference
     * @return                        the builder
     * @see                           #onExceptionOccurred(Processor)
     */
    public DefaultErrorHandlerDefinition onExceptionOccurredRef(String onExceptionOccurredRef) {
        setOnExceptionOccurredRef(onExceptionOccurredRef);
        return this;
    }

    /**
     * Sets a reference to a {@link RedeliveryPolicy} to be used for redelivery settings.
     *
     * @param  redeliveryPolicyRef the redelivrey policy reference
     * @return                     the builder
     */
    public DefaultErrorHandlerDefinition redeliveryPolicyRef(String redeliveryPolicyRef) {
        setRedeliveryPolicyRef(redeliveryPolicyRef);
        return this;
    }

}
