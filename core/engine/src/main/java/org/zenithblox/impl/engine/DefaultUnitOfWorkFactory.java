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
import org.zenithblox.Exchange;
import org.zenithblox.spi.InflightRepository;
import org.zenithblox.spi.UnitOfWork;
import org.zenithblox.spi.UnitOfWorkFactory;

/**
 * Default {@link org.zenithblox.spi.UnitOfWorkFactory}
 */
public class DefaultUnitOfWorkFactory implements UnitOfWorkFactory {

    private InflightRepository inflightRepository;
    private boolean usedMDCLogging;
    private String mdcLoggingKeysPattern;
    private boolean allowUseOriginalMessage;
    private boolean useBreadcrumb;

    @Override
    public UnitOfWork createUnitOfWork(Exchange exchange) {
        UnitOfWork answer;
        if (usedMDCLogging) {
            answer = new MDCUnitOfWork(
                    exchange, inflightRepository, mdcLoggingKeysPattern, allowUseOriginalMessage, useBreadcrumb);
        } else {
            answer = new DefaultUnitOfWork(exchange, inflightRepository, allowUseOriginalMessage, useBreadcrumb);
        }
        return answer;
    }

    @Override
    public void afterPropertiesConfigured(ZwangineContext zwangineContext) {
        // optimize to read configuration once
        inflightRepository = zwangineContext.getInflightRepository();
        usedMDCLogging = zwangineContext.isUseMDCLogging() != null && zwangineContext.isUseMDCLogging();
        mdcLoggingKeysPattern = zwangineContext.getMDCLoggingKeysPattern();
        allowUseOriginalMessage
                = zwangineContext.isAllowUseOriginalMessage() != null ? zwangineContext.isAllowUseOriginalMessage() : false;
        useBreadcrumb = zwangineContext.isUseBreadcrumb() != null ? zwangineContext.isUseBreadcrumb() : false;
    }

}
