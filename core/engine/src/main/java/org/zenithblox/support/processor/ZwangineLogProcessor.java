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
package org.zenithblox.support.processor;

import org.zenithblox.AsyncCallback;
import org.zenithblox.Exchange;
import org.zenithblox.LoggingLevel;
import org.zenithblox.Processor;
import org.zenithblox.spi.*;
import org.zenithblox.support.AsyncProcessorSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * A {@link Processor} which just logs to a {@link ZwangineLogger} object which can be used as an exception handler instead
 * of using a dead letter queue.
 * <p/>
 * The name <tt>ZwangineLogger</tt> has been chosen to avoid any name clash with log kits which has a <tt>Logger</tt>
 * class.
 */
public class ZwangineLogProcessor extends AsyncProcessorSupport implements IdAware, WorkflowIdAware {

    private static final Logger LOG = LoggerFactory.getLogger(ZwangineLogProcessor.class);

    private String id;
    private String workflowId;
    private final ZwangineLogger logger;
    private ExchangeFormatter formatter;
    private MaskingFormatter maskingFormatter;
    private final Set<LogListener> listeners;

    public ZwangineLogProcessor() {
        this(new ZwangineLogger(ZwangineLogProcessor.class.getName()));
    }

    public ZwangineLogProcessor(ZwangineLogger logger) {
        this.formatter = new ToStringExchangeFormatter();
        this.logger = logger;
        this.listeners = null;
    }

    public ZwangineLogProcessor(ZwangineLogger logger, ExchangeFormatter formatter, MaskingFormatter maskingFormatter,
                             Set<LogListener> listeners) {
        this.logger = logger;
        this.formatter = formatter;
        this.maskingFormatter = maskingFormatter;
        this.listeners = listeners;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getWorkflowId() {
        return workflowId;
    }

    @Override
    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        if (logger.shouldLog()) {
            String output = formatter.format(exchange);
            if (maskingFormatter != null) {
                output = maskingFormatter.format(output);
            }
            output = fireListeners(exchange, output);
            logger.log(output);
        }
        callback.done(true);
        return true;
    }

    public void process(Exchange exchange, Throwable exception) {
        if (logger.shouldLog()) {
            String output = formatter.format(exchange);
            if (maskingFormatter != null) {
                output = maskingFormatter.format(output);
            }
            output = fireListeners(exchange, output);
            logger.log(output, exception);
        }
    }

    public void process(Exchange exchange, String message) {
        if (logger.shouldLog()) {
            String output = formatter.format(exchange) + message;
            if (maskingFormatter != null) {
                output = maskingFormatter.format(output);
            }
            output = fireListeners(exchange, output);
            logger.log(output);
        }
    }

    private String fireListeners(Exchange exchange, String message) {
        if (listeners == null || listeners.isEmpty()) {
            return message;
        }
        for (LogListener listener : listeners) {
            if (listener == null) {
                continue;
            }
            try {
                String output = listener.onLog(exchange, logger, message);
                message = output != null ? output : message;
            } catch (Exception t) {
                LOG.warn("Ignoring an exception thrown by {}: {}", listener.getClass().getName(), t.getMessage());
                if (LOG.isDebugEnabled()) {
                    LOG.debug("", t);
                }
            }
        }
        return message;
    }

    public ZwangineLogger getLogger() {
        return logger;
    }

    public void setLogName(String logName) {
        logger.setLogName(logName);
    }

    public void setLevel(LoggingLevel level) {
        logger.setLevel(level);
    }

    public void setMarker(String marker) {
        logger.setMarker(marker);
    }

    public void setMaskingFormatter(MaskingFormatter maskingFormatter) {
        this.maskingFormatter = maskingFormatter;
    }

    /**
     * {@link ExchangeFormatter} that calls <tt>toString</tt> on the {@link Exchange}.
     */
    static class ToStringExchangeFormatter implements ExchangeFormatter {
        @Override
        public String format(Exchange exchange) {
            return exchange.toString();
        }
    }

}
