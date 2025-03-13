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
package org.zenithblox.builder;

import org.zenithblox.Endpoint;
import org.zenithblox.LoggingLevel;
import org.zenithblox.model.errorhandler.DeadLetterChannelConfiguration;
import org.zenithblox.model.errorhandler.DeadLetterChannelProperties;
import org.zenithblox.model.errorhandler.DefaultErrorHandlerConfiguration;
import org.zenithblox.processor.errorhandler.DeadLetterChannel;
import org.zenithblox.spi.ZwangineLogger;
import org.slf4j.LoggerFactory;

/**
 * Legacy error handler for XML DSL in zwangine-spring-xml
 */
public class LegacyDeadLetterChannelBuilder extends LegacyDefaultErrorHandlerBuilder implements DeadLetterChannelProperties {

    public LegacyDeadLetterChannelBuilder() {
        // no-arg constructor used by Spring DSL
    }

    public LegacyDeadLetterChannelBuilder(Endpoint deadLetter) {
        setDeadLetterUri(deadLetter.getEndpointUri());
        // DLC do not log exhausted by default
        getRedeliveryPolicy().setLogExhausted(false);
    }

    public LegacyDeadLetterChannelBuilder(String uri) {
        setDeadLetterUri(uri);
        // DLC do not log exhausted by default
        getRedeliveryPolicy().setLogExhausted(false);
    }

    @Override
    DefaultErrorHandlerConfiguration createConfiguration() {
        return new DeadLetterChannelConfiguration();
    }

    @Override
    public LegacyErrorHandlerBuilder cloneBuilder() {
        LegacyDeadLetterChannelBuilder answer = new LegacyDeadLetterChannelBuilder();
        super.cloneBuilder(answer);
        return answer;
    }

    // Properties
    // -------------------------------------------------------------------------

    @Override
    protected ZwangineLogger createLogger() {
        return new ZwangineLogger(LoggerFactory.getLogger(DeadLetterChannel.class), LoggingLevel.ERROR);
    }

    @Override
    public String toString() {
        return "DeadLetterChannelBuilder(" + getDeadLetterUri() + ")";
    }
}
