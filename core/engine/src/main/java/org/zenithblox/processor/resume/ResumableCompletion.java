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

package org.zenithblox.processor.resume;

import org.zenithblox.Exchange;
import org.zenithblox.LoggingLevel;
import org.zenithblox.resume.Resumable;
import org.zenithblox.resume.ResumeStrategy;
import org.zenithblox.spi.ZwangineLogger;
import org.zenithblox.spi.Synchronization;
import org.zenithblox.support.ExchangeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResumableCompletion implements Synchronization {
    private static final Logger LOG = LoggerFactory.getLogger(ResumableCompletion.class);

    private final ResumeStrategy resumeStrategy;
    private final LoggingLevel loggingLevel;
    private final boolean intermittent;

    public ResumableCompletion(ResumeStrategy resumeStrategy, LoggingLevel loggingLevel, boolean intermittent) {
        this.resumeStrategy = resumeStrategy;
        this.loggingLevel = loggingLevel;
        this.intermittent = intermittent;
    }

    @Override
    public void onComplete(Exchange exchange) {
        if (ExchangeHelper.isFailureHandled(exchange)) {
            return;
        }

        Object offset = ExchangeHelper.getResultMessage(exchange).getHeader(Exchange.OFFSET);

        if (offset instanceof Resumable resumable) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Processing the resumable: {}", resumable.getOffsetKey());
                LOG.trace("Processing the resumable of type: {}", resumable.getLastOffset().getValue());
            }

            try {
                resumeStrategy.updateLastOffset(resumable);
            } catch (Exception e) {
                LOG.error("Unable to update the offset: {}", e.getMessage(), e);
            }

        } else {
            if (!intermittent) {
                exchange.setException(new NoOffsetException(exchange));
                LOG.warn("Cannot update the last offset because it's not available");
            }
        }
    }

    @Override
    public void onFailure(Exchange exchange) {
        Exception e = exchange.getException();
        Object resObj = exchange.getMessage().getHeader(Exchange.OFFSET);

        if (resObj instanceof Resumable resumable) {
            String logMessage = String.format(
                    "Skipping offset update with address '%s' and offset value '%s' due to failure in processing: %s",
                    resumable.getOffsetKey(), resumable.getLastOffset().getValue(), e.getMessage());

            if (LOG.isDebugEnabled() || ZwangineLogger.shouldLog(LOG, loggingLevel)) {
                ZwangineLogger.log(LOG, LoggingLevel.DEBUG, logMessage, e);
            } else {
                logMessage += " (stacktrace available in DEBUG logging level)";

                ZwangineLogger.log(LOG, loggingLevel, logMessage);
            }
        } else {
            String logMessage = String.format("Skipping offset update of '%s' due to failure in processing: %s",
                    resObj == null ? "type null" : "unspecified type", e.getMessage());

            if (LOG.isDebugEnabled() || ZwangineLogger.shouldLog(LOG, loggingLevel)) {
                ZwangineLogger.log(LOG, LoggingLevel.DEBUG, logMessage, e);
            } else {
                logMessage += " (stacktrace available in DEBUG logging level)";

                ZwangineLogger.log(LOG, loggingLevel, logMessage);
            }
        }
    }
}
