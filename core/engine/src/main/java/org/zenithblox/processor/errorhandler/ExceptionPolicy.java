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
package org.zenithblox.processor.errorhandler;

import org.zenithblox.*;
import org.zenithblox.support.ZwangineContextHelper;

import java.util.List;
import java.util.Map;

public class ExceptionPolicy {

    private final String id;
    private final String workflowId;
    private final boolean useOriginalInMessage;
    private final boolean useOriginalInBody;
    private final boolean hasOutputs;

    private final Predicate handledPolicy;
    private final Predicate continuedPolicy;
    private final Predicate retryWhilePolicy;
    private final Processor onRedelivery;
    private final Processor onExceptionOccurred;
    private final String redeliveryPolicyRef;
    private final Map<RedeliveryOption, String> redeliveryPolicy;
    private final List<String> exceptions;

    public ExceptionPolicy(String id, String workflowId, boolean useOriginalInMessage, boolean useOriginalInBody,
                           boolean hasOutputs, Predicate handledPolicy, Predicate continuedPolicy,
                           Predicate retryWhilePolicy, Processor onRedelivery, Processor onExceptionOccurred,
                           String redeliveryPolicyRef, Map<RedeliveryOption, String> redeliveryPolicy,
                           List<String> exceptions) {
        this.id = id;
        this.workflowId = workflowId;
        this.useOriginalInMessage = useOriginalInMessage;
        this.useOriginalInBody = useOriginalInBody;
        this.hasOutputs = hasOutputs;
        this.handledPolicy = handledPolicy;
        this.continuedPolicy = continuedPolicy;
        this.retryWhilePolicy = retryWhilePolicy;
        this.onRedelivery = onRedelivery;
        this.onExceptionOccurred = onExceptionOccurred;
        this.redeliveryPolicyRef = redeliveryPolicyRef;
        this.redeliveryPolicy = redeliveryPolicy;
        this.exceptions = exceptions;
    }

    public String getId() {
        return id;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public boolean isUseOriginalInMessage() {
        return useOriginalInMessage;
    }

    public boolean isUseOriginalInBody() {
        return useOriginalInBody;
    }

    public List<String> getExceptions() {
        return exceptions;
    }

    public Predicate getHandledPolicy() {
        return handledPolicy;
    }

    public Predicate getContinuedPolicy() {
        return continuedPolicy;
    }

    public Predicate getRetryWhilePolicy() {
        return retryWhilePolicy;
    }

    public Processor getOnRedelivery() {
        return onRedelivery;
    }

    public Processor getOnExceptionOccurred() {
        return onExceptionOccurred;
    }

    /**
     * Allows an exception handler to create a new redelivery policy for this exception type
     *
     * @param  context      the zwangine context
     * @param  parentPolicy the current redelivery policy, is newer <tt>null</tt>
     * @return              a newly created redelivery policy, or return the original policy if no customization is
     *                      required for this exception handler.
     */
    public RedeliveryPolicy createRedeliveryPolicy(ZwangineContext context, RedeliveryPolicy parentPolicy) {
        if (redeliveryPolicyRef != null) {
            return ZwangineContextHelper.mandatoryLookup(context, redeliveryPolicyRef, RedeliveryPolicy.class);
        } else if (redeliveryPolicy != null) {
            return createRedeliveryPolicy(redeliveryPolicy, context, parentPolicy);
        } else if (hasOutputs && parentPolicy.getMaximumRedeliveries() != 0) {
            // if we have outputs, then do not inherit parent maximumRedeliveries
            // as you would have to explicit configure maximumRedeliveries on this onException to use it
            // this is the behavior Zwangine has always had
            RedeliveryPolicy answer = parentPolicy.copy();
            answer.setMaximumRedeliveries(0);
            return answer;
        } else {
            return parentPolicy;
        }
    }

    public boolean determineIfRedeliveryIsEnabled(ZwangineContext zwangineContext) throws Exception {
        if (redeliveryPolicyRef != null) {
            // lookup in registry if ref provided
            RedeliveryPolicy policy
                    = ZwangineContextHelper.mandatoryLookup(zwangineContext, redeliveryPolicyRef, RedeliveryPolicy.class);
            if (policy.getMaximumRedeliveries() != 0) {
                // must check for != 0 as (-1 means redeliver forever)
                return true;
            }
        } else if (redeliveryPolicy != null) {
            Integer max
                    = ZwangineContextHelper.parseInteger(zwangineContext, redeliveryPolicy.get(RedeliveryOption.maximumRedeliveries));
            if (max != null && max != 0) {
                // must check for != 0 as (-1 means redeliver forever)
                return true;
            }
        }

        if (retryWhilePolicy != null) {
            return true;
        }

        return false;
    }

    public enum RedeliveryOption {
        maximumRedeliveries,
        redeliveryDelay,
        asyncDelayedRedelivery,
        backOffMultiplier,
        useExponentialBackOff,
        collisionAvoidanceFactor,
        useCollisionAvoidance,
        maximumRedeliveryDelay,
        retriesExhaustedLogLevel,
        retryAttemptedLogLevel,
        retryAttemptedLogInterval,
        logRetryAttempted,
        logStackTrace,
        logRetryStackTrace,
        logHandled,
        logNewException,
        logContinued,
        logExhausted,
        logExhaustedMessageHistory,
        logExhaustedMessageBody,
        disableRedelivery,
        delayPattern,
        allowRedeliveryWhileStopping,
        exchangeFormatterRef
    }

    private static RedeliveryPolicy createRedeliveryPolicy(
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

}
