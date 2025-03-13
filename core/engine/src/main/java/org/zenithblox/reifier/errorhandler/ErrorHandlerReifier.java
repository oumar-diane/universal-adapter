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
package org.zenithblox.reifier.errorhandler;

import org.zenithblox.*;
import org.zenithblox.model.OnExceptionDefinition;
import org.zenithblox.model.RedeliveryPolicyDefinition;
import org.zenithblox.model.errorhandler.DeadLetterChannelDefinition;
import org.zenithblox.model.errorhandler.DefaultErrorHandlerDefinition;
import org.zenithblox.model.errorhandler.NoErrorHandlerDefinition;
import org.zenithblox.model.errorhandler.RefErrorHandlerDefinition;
import org.zenithblox.processor.errorhandler.*;
import org.zenithblox.processor.errorhandler.ExceptionPolicy.RedeliveryOption;
import org.zenithblox.reifier.AbstractReifier;
import org.zenithblox.spi.ErrorHandler;
import org.zenithblox.spi.Language;
import org.zenithblox.support.ZwangineContextHelper;
import org.zenithblox.support.PluginHelper;
import org.zenithblox.util.ObjectHelper;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;

public abstract class ErrorHandlerReifier<T extends ErrorHandlerFactory> extends AbstractReifier {

    private static final Map<Class<?>, BiFunction<Workflow, ErrorHandlerFactory, ErrorHandlerReifier<? extends ErrorHandlerFactory>>> ERROR_HANDLERS
            = new HashMap<>(0);

    protected final T definition;

    protected final Lock lock = new ReentrantLock();

    /**
     * Utility classes should not have a public constructor.
     */
    protected ErrorHandlerReifier(Workflow workflow, T definition) {
        super(workflow);
        this.definition = definition;
    }

    public static void registerReifier(
            Class<?> errorHandlerClass,
            BiFunction<Workflow, ErrorHandlerFactory, ErrorHandlerReifier<? extends ErrorHandlerFactory>> creator) {
        ERROR_HANDLERS.put(errorHandlerClass, creator);
    }

    public static ErrorHandlerReifier<? extends ErrorHandlerFactory> reifier(Workflow workflow, ErrorHandlerFactory definition) {
        ErrorHandlerReifier<? extends ErrorHandlerFactory> answer = null;
        if (!ERROR_HANDLERS.isEmpty()) {
            // custom take precedence
            BiFunction<Workflow, ErrorHandlerFactory, ErrorHandlerReifier<? extends ErrorHandlerFactory>> reifier
                    = ERROR_HANDLERS.get(definition.getClass());
            if (reifier != null) {
                answer = reifier.apply(workflow, definition);
            }
        }
        if (answer == null) {
            answer = coreReifier(workflow, definition);
        }
        if (answer == null) {
            throw new IllegalStateException("Unsupported definition: " + definition);
        }
        return answer;
    }

    private static ErrorHandlerReifier<? extends ErrorHandlerFactory> coreReifier(Workflow workflow, ErrorHandlerFactory definition) {
        if (definition instanceof DeadLetterChannelDefinition deadLetterChannelDefinition) {
            return new DeadLetterChannelReifier(workflow, deadLetterChannelDefinition);
        } else if (definition instanceof DefaultErrorHandlerDefinition defaultErrorHandlerDefinition) {
            return new DefaultErrorHandlerReifier(workflow, defaultErrorHandlerDefinition);
        } else if (definition instanceof NoErrorHandlerDefinition) {
            return new NoErrorHandlerReifier(workflow, definition);
        } else if (definition instanceof RefErrorHandlerDefinition) {
            return new ErrorHandlerRefReifier(workflow, definition);
        }

        return null;
    }

    public ExceptionPolicy createExceptionPolicy(OnExceptionDefinition def) {
        Predicate handled = def.getHandledPolicy();
        if (handled == null && def.getHandled() != null) {
            handled = createPredicate(def.getHandled());
        }
        Predicate continued = def.getContinuedPolicy();
        if (continued == null && def.getContinued() != null) {
            continued = createPredicate(def.getContinued());
        }
        Predicate retryWhile = def.getRetryWhilePolicy();
        if (retryWhile == null && def.getRetryWhile() != null) {
            retryWhile = createPredicate(def.getRetryWhile());
        }
        Processor onRedelivery = getProcessor(def.getOnRedelivery(), def.getOnRedeliveryRef());
        Processor onExceptionOccurred = getProcessor(def.getOnExceptionOccurred(), def.getOnExceptionOccurredRef());
        return new ExceptionPolicy(
                parseString(def.getId()), ZwangineContextHelper.getWorkflowId(def),
                parseBoolean(def.getUseOriginalMessage(), false),
                parseBoolean(def.getUseOriginalBody(), false),
                ObjectHelper.isNotEmpty(def.getOutputs()), handled,
                continued, retryWhile, onRedelivery,
                onExceptionOccurred, parseString(def.getRedeliveryPolicyRef()),
                createRedeliveryPolicyOptions(def.getRedeliveryPolicyType()), def.getExceptions());
    }

    @Deprecated
    public RedeliveryPolicy createRedeliveryPolicy(RedeliveryPolicyDefinition definition, ZwangineContext context) {
        Map<RedeliveryOption, String> options = createRedeliveryPolicyOptions(definition);
        return createRedeliveryPolicy(options, context, null);
    }

    @Deprecated
    private RedeliveryPolicy createRedeliveryPolicy(
            Map<RedeliveryOption, String> definition, ZwangineContext context, RedeliveryPolicy parentPolicy) {
        RedeliveryPolicy answer;
        if (parentPolicy != null) {
            answer = parentPolicy.copy();
        } else {
            answer = new RedeliveryPolicy();
        }
        try {
            if (definition.get(RedeliveryOption.maximumRedeliveries) != null) {
                answer.setMaximumRedeliveries(
                        ZwangineContextHelper.parseInteger(context, definition.get(RedeliveryOption.maximumRedeliveries)));
            }
            if (definition.get(RedeliveryOption.redeliveryDelay) != null) {
                answer.setRedeliveryDelay(
                        ZwangineContextHelper.parseLong(context, definition.get(RedeliveryOption.redeliveryDelay)));
            }
            if (definition.get(RedeliveryOption.asyncDelayedRedelivery) != null) {
                answer.setAsyncDelayedRedelivery(
                        ZwangineContextHelper.parseBoolean(context, definition.get(RedeliveryOption.asyncDelayedRedelivery)));
            }
            if (definition.get(RedeliveryOption.retriesExhaustedLogLevel) != null) {
                answer.setRetriesExhaustedLogLevel(ZwangineContextHelper.parse(context, LoggingLevel.class,
                        definition.get(RedeliveryOption.retriesExhaustedLogLevel)));
            }
            if (definition.get(RedeliveryOption.retryAttemptedLogLevel) != null) {
                answer.setRetryAttemptedLogLevel(ZwangineContextHelper.parse(context, LoggingLevel.class,
                        definition.get(RedeliveryOption.retryAttemptedLogLevel)));
            }
            if (definition.get(RedeliveryOption.retryAttemptedLogInterval) != null) {
                answer.setRetryAttemptedLogInterval(
                        ZwangineContextHelper.parseInteger(context, definition.get(RedeliveryOption.retryAttemptedLogInterval)));
            }
            if (definition.get(RedeliveryOption.backOffMultiplier) != null) {
                answer.setBackOffMultiplier(
                        ZwangineContextHelper.parseDouble(context, definition.get(RedeliveryOption.backOffMultiplier)));
            }
            if (definition.get(RedeliveryOption.useExponentialBackOff) != null) {
                answer.setUseExponentialBackOff(
                        ZwangineContextHelper.parseBoolean(context, definition.get(RedeliveryOption.useExponentialBackOff)));
            }
            if (definition.get(RedeliveryOption.collisionAvoidanceFactor) != null) {
                answer.setCollisionAvoidanceFactor(
                        ZwangineContextHelper.parseDouble(context, definition.get(RedeliveryOption.collisionAvoidanceFactor)));
            }
            if (definition.get(RedeliveryOption.useCollisionAvoidance) != null) {
                answer.setUseCollisionAvoidance(
                        ZwangineContextHelper.parseBoolean(context, definition.get(RedeliveryOption.useCollisionAvoidance)));
            }
            if (definition.get(RedeliveryOption.maximumRedeliveryDelay) != null) {
                answer.setMaximumRedeliveryDelay(
                        ZwangineContextHelper.parseLong(context, definition.get(RedeliveryOption.maximumRedeliveryDelay)));
            }
            if (definition.get(RedeliveryOption.logStackTrace) != null) {
                answer.setLogStackTrace(
                        ZwangineContextHelper.parseBoolean(context, definition.get(RedeliveryOption.logStackTrace)));
            }
            if (definition.get(RedeliveryOption.logRetryStackTrace) != null) {
                answer.setLogRetryStackTrace(
                        ZwangineContextHelper.parseBoolean(context, definition.get(RedeliveryOption.logRetryStackTrace)));
            }
            if (definition.get(RedeliveryOption.logHandled) != null) {
                answer.setLogHandled(ZwangineContextHelper.parseBoolean(context, definition.get(RedeliveryOption.logHandled)));
            }
            if (definition.get(RedeliveryOption.logNewException) != null) {
                answer.setLogNewException(
                        ZwangineContextHelper.parseBoolean(context, definition.get(RedeliveryOption.logNewException)));
            }
            if (definition.get(RedeliveryOption.logContinued) != null) {
                answer.setLogContinued(ZwangineContextHelper.parseBoolean(context, definition.get(RedeliveryOption.logContinued)));
            }
            if (definition.get(RedeliveryOption.logRetryAttempted) != null) {
                answer.setLogRetryAttempted(
                        ZwangineContextHelper.parseBoolean(context, definition.get(RedeliveryOption.logRetryAttempted)));
            }
            if (definition.get(RedeliveryOption.logExhausted) != null) {
                answer.setLogExhausted(ZwangineContextHelper.parseBoolean(context, definition.get(RedeliveryOption.logExhausted)));
            }
            if (definition.get(RedeliveryOption.logExhaustedMessageHistory) != null) {
                answer.setLogExhaustedMessageHistory(
                        ZwangineContextHelper.parseBoolean(context, definition.get(RedeliveryOption.logExhaustedMessageHistory)));
            }
            if (definition.get(RedeliveryOption.logExhaustedMessageBody) != null) {
                answer.setLogExhaustedMessageBody(
                        ZwangineContextHelper.parseBoolean(context, definition.get(RedeliveryOption.logExhaustedMessageBody)));
            }
            if (definition.get(RedeliveryOption.disableRedelivery) != null) {
                if (ZwangineContextHelper.parseBoolean(context, definition.get(RedeliveryOption.disableRedelivery))) {
                    answer.setMaximumRedeliveries(0);
                }
            }
            if (definition.get(RedeliveryOption.delayPattern) != null) {
                answer.setDelayPattern(ZwangineContextHelper.parseText(context, definition.get(RedeliveryOption.delayPattern)));
            }
            if (definition.get(RedeliveryOption.allowRedeliveryWhileStopping) != null) {
                answer.setAllowRedeliveryWhileStopping(ZwangineContextHelper.parseBoolean(context,
                        definition.get(RedeliveryOption.allowRedeliveryWhileStopping)));
            }
            if (definition.get(RedeliveryOption.exchangeFormatterRef) != null) {
                answer.setExchangeFormatterRef(
                        ZwangineContextHelper.parseText(context, definition.get(RedeliveryOption.exchangeFormatterRef)));
            }
        } catch (Exception e) {
            throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
        }

        return answer;
    }

    private Map<RedeliveryOption, String> createRedeliveryPolicyOptions(RedeliveryPolicyDefinition definition) {
        if (definition == null) {
            return null;
        }
        Map<RedeliveryOption, String> policy = new EnumMap<>(RedeliveryOption.class);
        setOption(policy, RedeliveryOption.maximumRedeliveries, definition.getMaximumRedeliveries());
        setOption(policy, RedeliveryOption.redeliveryDelay, definition.getRedeliveryDelay(), "1000");
        setOption(policy, RedeliveryOption.asyncDelayedRedelivery, definition.getAsyncDelayedRedelivery());
        setOption(policy, RedeliveryOption.backOffMultiplier, definition.getBackOffMultiplier(), "2");
        setOption(policy, RedeliveryOption.useExponentialBackOff, definition.getUseExponentialBackOff());
        setOption(policy, RedeliveryOption.collisionAvoidanceFactor, definition.getCollisionAvoidanceFactor(), "0.15");
        setOption(policy, RedeliveryOption.useCollisionAvoidance, definition.getUseCollisionAvoidance());
        setOption(policy, RedeliveryOption.maximumRedeliveryDelay, definition.getMaximumRedeliveryDelay(), "60000");
        setOption(policy, RedeliveryOption.retriesExhaustedLogLevel, definition.getRetriesExhaustedLogLevel(), "ERROR");
        setOption(policy, RedeliveryOption.retryAttemptedLogLevel, definition.getRetryAttemptedLogLevel(), "DEBUG");
        setOption(policy, RedeliveryOption.retryAttemptedLogInterval, definition.getRetryAttemptedLogInterval(), "1");
        setOption(policy, RedeliveryOption.logRetryAttempted, definition.getLogRetryAttempted(), "true");
        setOption(policy, RedeliveryOption.logStackTrace, definition.getLogStackTrace(), "true");
        setOption(policy, RedeliveryOption.logRetryStackTrace, definition.getLogRetryStackTrace());
        setOption(policy, RedeliveryOption.logHandled, definition.getLogHandled());
        setOption(policy, RedeliveryOption.logNewException, definition.getLogNewException(), "true");
        setOption(policy, RedeliveryOption.logContinued, definition.getLogContinued());
        setOption(policy, RedeliveryOption.logExhausted, definition.getLogExhausted(), "true");
        setOption(policy, RedeliveryOption.logExhaustedMessageHistory, definition.getLogExhaustedMessageHistory());
        setOption(policy, RedeliveryOption.logExhaustedMessageBody, definition.getLogExhaustedMessageBody());
        setOption(policy, RedeliveryOption.disableRedelivery, definition.getDisableRedelivery());
        setOption(policy, RedeliveryOption.delayPattern, definition.getDelayPattern());
        setOption(policy, RedeliveryOption.allowRedeliveryWhileStopping, definition.getAllowRedeliveryWhileStopping(), "true");
        setOption(policy, RedeliveryOption.exchangeFormatterRef, definition.getExchangeFormatterRef());
        return policy;
    }

    private void setOption(Map<RedeliveryOption, String> policy, RedeliveryOption option, String value) {
        setOption(policy, option, value, null);
    }

    private void setOption(
            Map<RedeliveryOption, String> policy, RedeliveryOption option, String value, String defaultValue) {
        if (value != null) {
            policy.put(option, parseString(value));
        } else if (defaultValue != null) {
            policy.put(option, defaultValue);
        }
    }

    public void addExceptionPolicy(ErrorHandlerSupport handlerSupport, OnExceptionDefinition exceptionType) {
        // add error handler as child service so they get lifecycle handled
        Processor errorHandler = workflow.getOnException(exceptionType.getId());
        handlerSupport.addErrorHandler(errorHandler);

        // load exception classes
        List<Class<? extends Throwable>> list;
        if (ObjectHelper.isNotEmpty(exceptionType.getExceptions())) {
            list = createExceptionClasses(exceptionType);
            for (Class<? extends Throwable> clazz : list) {
                String workflowId = null;
                // only get the workflow id, if the exception type is workflow scoped
                if (exceptionType.isWorkflowScoped()) {
                    workflowId = workflow.getWorkflowId();
                }
                Predicate when = exceptionType.getOnWhen() != null ? exceptionType.getOnWhen().getExpression() : null;
                ExceptionPolicyKey key = new ExceptionPolicyKey(workflowId, clazz, when);
                ExceptionPolicy policy = createExceptionPolicy(exceptionType);
                handlerSupport.addExceptionPolicy(key, policy);
            }
        }
    }

    protected List<Class<? extends Throwable>> createExceptionClasses(OnExceptionDefinition exceptionType) {
        List<String> list = exceptionType.getExceptions();
        List<Class<? extends Throwable>> answer = new ArrayList<>(list.size());
        for (String name : list) {
            try {
                Class<? extends Throwable> type = zwangineContext.getClassResolver().resolveMandatoryClass(name, Throwable.class);
                answer.add(type);
            } catch (ClassNotFoundException e) {
                throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
            }
        }
        return answer;
    }

    /**
     * Creates the error handler
     *
     * @param  processor the outer processor
     * @return           the error handler
     * @throws Exception is thrown if the error handler could not be created
     */
    public abstract Processor createErrorHandler(Processor processor) throws Exception;

    public void configure(ErrorHandler handler) {
        if (handler instanceof ErrorHandlerSupport handlerSupport) {
            for (NamedNode exception : workflow.getErrorHandlers(definition)) {
                addExceptionPolicy(handlerSupport, (OnExceptionDefinition) exception);
            }
        }
        if (handler instanceof RedeliveryErrorHandler redeliveryErrorHandler) {
            boolean original = redeliveryErrorHandler.isUseOriginalMessagePolicy()
                    || redeliveryErrorHandler.isUseOriginalBodyPolicy();
            if (original) {
                // ensure allow original is turned on
                workflow.setAllowUseOriginalMessage(true);
            }
        }
    }

    /**
     * Note: Not for end users - this method is used internally by zwangine-blueprint
     */
    public static RedeliveryPolicy createRedeliveryPolicy(
            RedeliveryPolicyDefinition definition, ZwangineContext context, RedeliveryPolicy parentPolicy) {
        RedeliveryPolicy answer;
        if (parentPolicy != null) {
            answer = parentPolicy.copy();
        } else {
            answer = new RedeliveryPolicy();
        }

        try {
            // copy across the properties - if they are set
            if (definition.getMaximumRedeliveries() != null) {
                answer.setMaximumRedeliveries(ZwangineContextHelper.parseInteger(context, definition.getMaximumRedeliveries()));
            }
            if (definition.getRedeliveryDelay() != null) {
                Duration duration = ZwangineContextHelper.parseDuration(context, definition.getRedeliveryDelay());
                answer.setRedeliveryDelay(duration.toMillis());
            }
            if (definition.getAsyncDelayedRedelivery() != null) {
                answer.setAsyncDelayedRedelivery(
                        ZwangineContextHelper.parseBoolean(context, definition.getAsyncDelayedRedelivery()));
            }
            if (definition.getRetriesExhaustedLogLevel() != null) {
                answer.setRetriesExhaustedLogLevel(
                        ZwangineContextHelper.parse(context, LoggingLevel.class, definition.getRetriesExhaustedLogLevel()));
            }
            if (definition.getRetryAttemptedLogLevel() != null) {
                answer.setRetryAttemptedLogLevel(
                        ZwangineContextHelper.parse(context, LoggingLevel.class, definition.getRetryAttemptedLogLevel()));
            }
            if (definition.getRetryAttemptedLogInterval() != null) {
                answer.setRetryAttemptedLogInterval(
                        ZwangineContextHelper.parseInteger(context, definition.getRetryAttemptedLogInterval()));
            }
            if (definition.getBackOffMultiplier() != null) {
                answer.setBackOffMultiplier(ZwangineContextHelper.parseDouble(context, definition.getBackOffMultiplier()));
            }
            if (definition.getUseExponentialBackOff() != null) {
                answer.setUseExponentialBackOff(
                        ZwangineContextHelper.parseBoolean(context, definition.getUseExponentialBackOff()));
            }
            if (definition.getCollisionAvoidanceFactor() != null) {
                answer.setCollisionAvoidanceFactor(
                        ZwangineContextHelper.parseDouble(context, definition.getCollisionAvoidanceFactor()));
            }
            if (definition.getUseCollisionAvoidance() != null) {
                answer.setUseCollisionAvoidance(
                        ZwangineContextHelper.parseBoolean(context, definition.getUseCollisionAvoidance()));
            }
            if (definition.getMaximumRedeliveryDelay() != null) {
                Duration duration = ZwangineContextHelper.parseDuration(context, definition.getMaximumRedeliveryDelay());
                answer.setMaximumRedeliveryDelay(duration.toMillis());
            }
            if (definition.getLogStackTrace() != null) {
                answer.setLogStackTrace(ZwangineContextHelper.parseBoolean(context, definition.getLogStackTrace()));
            }
            if (definition.getLogRetryStackTrace() != null) {
                answer.setLogRetryStackTrace(ZwangineContextHelper.parseBoolean(context, definition.getLogRetryStackTrace()));
            }
            if (definition.getLogHandled() != null) {
                answer.setLogHandled(ZwangineContextHelper.parseBoolean(context, definition.getLogHandled()));
            }
            if (definition.getLogNewException() != null) {
                answer.setLogNewException(ZwangineContextHelper.parseBoolean(context, definition.getLogNewException()));
            }
            if (definition.getLogContinued() != null) {
                answer.setLogContinued(ZwangineContextHelper.parseBoolean(context, definition.getLogContinued()));
            }
            if (definition.getLogRetryAttempted() != null) {
                answer.setLogRetryAttempted(ZwangineContextHelper.parseBoolean(context, definition.getLogRetryAttempted()));
            }
            if (definition.getLogExhausted() != null) {
                answer.setLogExhausted(ZwangineContextHelper.parseBoolean(context, definition.getLogExhausted()));
            }
            if (definition.getLogExhaustedMessageHistory() != null) {
                answer.setLogExhaustedMessageHistory(
                        ZwangineContextHelper.parseBoolean(context, definition.getLogExhaustedMessageHistory()));
            }
            if (definition.getLogExhaustedMessageBody() != null) {
                answer.setLogExhaustedMessageBody(
                        ZwangineContextHelper.parseBoolean(context, definition.getLogExhaustedMessageBody()));
            }
            if (definition.getDisableRedelivery() != null) {
                if (ZwangineContextHelper.parseBoolean(context, definition.getDisableRedelivery())) {
                    answer.setMaximumRedeliveries(0);
                }
            }
            if (definition.getDelayPattern() != null) {
                answer.setDelayPattern(ZwangineContextHelper.parseText(context, definition.getDelayPattern()));
            }
            if (definition.getAllowRedeliveryWhileStopping() != null) {
                answer.setAllowRedeliveryWhileStopping(
                        ZwangineContextHelper.parseBoolean(context, definition.getAllowRedeliveryWhileStopping()));
            }
            if (definition.getExchangeFormatterRef() != null) {
                answer.setExchangeFormatterRef(ZwangineContextHelper.parseText(context, definition.getExchangeFormatterRef()));
            }
        } catch (Exception e) {
            throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
        }

        return answer;
    }

    protected Predicate getPredicate(Predicate pred, String ref) {
        ref = parseString(ref);

        if (pred == null && ref != null) {
            // its a bean expression
            Language bean = zwangineContext.resolveLanguage("bean");
            pred = bean.createPredicate(ref);
        }
        return pred;
    }

    protected <U> U getBean(Class<U> clazz, U bean, String ref) {
        ref = parseString(ref);

        if (bean == null && ref != null) {
            bean = lookupByNameAndType(ref, clazz);
        }
        return bean;
    }

    protected Processor getProcessor(Processor processor, String ref) {
        ref = parseString(ref);

        if (processor == null) {
            processor = getBean(Processor.class, null, ref);
        }
        if (processor != null) {
            // must wrap the processor in an UoW
            processor = PluginHelper.getInternalProcessorFactory(zwangineContext)
                    .addUnitOfWorkProcessorAdvice(zwangineContext, processor, workflow);
        }
        return processor;
    }

}
