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

import org.zenithblox.ErrorHandlerFactory;
import org.zenithblox.Predicate;
import org.zenithblox.Processor;
import org.zenithblox.processor.errorhandler.RedeliveryPolicy;
import org.zenithblox.spi.ZwangineLogger;

import java.util.concurrent.ScheduledExecutorService;

/**
 * Legacy error handler for XML DSL in zwangine-spring-xml
 */
public interface DefaultErrorHandlerProperties extends ErrorHandlerFactory {

    boolean hasLogger();

    ZwangineLogger getLogger();

    void setLogger(ZwangineLogger logger);

    boolean hasRedeliveryPolicy();

    RedeliveryPolicy getRedeliveryPolicy();

    RedeliveryPolicy getDefaultRedeliveryPolicy();

    void setRedeliveryPolicy(RedeliveryPolicy redeliveryPolicy);

    Processor getOnRedelivery();

    void setOnRedelivery(Processor onRedelivery);

    String getOnRedeliveryRef();

    void setOnRedeliveryRef(String onRedeliveryRef);

    Predicate getRetryWhile();

    void setRetryWhile(Predicate retryWhile);

    String getRetryWhileRef();

    void setRetryWhileRef(String retryWhileRef);

    String getDeadLetterUri();

    void setDeadLetterUri(String deadLetterUri);

    boolean isDeadLetterHandleNewException();

    void setDeadLetterHandleNewException(boolean deadLetterHandleNewException);

    boolean isUseOriginalMessage();

    void setUseOriginalMessage(boolean useOriginalMessage);

    boolean isUseOriginalBody();

    void setUseOriginalBody(boolean useOriginalBody);

    boolean isAsyncDelayedRedelivery();

    void setAsyncDelayedRedelivery(boolean asyncDelayedRedelivery);

    ScheduledExecutorService getExecutorService();

    void setExecutorService(ScheduledExecutorService executorService);

    String getExecutorServiceRef();

    void setExecutorServiceRef(String executorServiceRef);

    Processor getOnPrepareFailure();

    void setOnPrepareFailure(Processor onPrepareFailure);

    String getOnPrepareFailureRef();

    void setOnPrepareFailureRef(String onPrepareFailureRef);

    Processor getOnExceptionOccurred();

    void setOnExceptionOccurred(Processor onExceptionOccurred);

    String getOnExceptionOccurredRef();

    void setOnExceptionOccurredRef(String onExceptionOccurredRef);
}
