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
package org.zenithblox.processor;

import org.zenithblox.AsyncCallback;
import org.zenithblox.Exchange;
import org.zenithblox.Expression;
import org.zenithblox.Traceable;
import org.zenithblox.spi.*;
import org.zenithblox.support.AsyncProcessorSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * A processor which evaluates an {@link Expression} and logs it.
 */
public class LogProcessor extends AsyncProcessorSupport implements Traceable, IdAware, WorkflowIdAware {

    private static final Logger LOG = LoggerFactory.getLogger(LogProcessor.class);

    private String id;
    private String workflowId;
    private final Expression expression;
    private final String message;
    private final ZwangineLogger logger;
    private final MaskingFormatter formatter;
    private final Set<LogListener> listeners;

    public LogProcessor(Expression expression, ZwangineLogger logger, MaskingFormatter formatter, Set<LogListener> listeners) {
        this.expression = expression;
        this.message = null;
        this.logger = logger;
        this.formatter = formatter;
        this.listeners = listeners;
    }

    public LogProcessor(String message, ZwangineLogger logger, MaskingFormatter formatter, Set<LogListener> listeners) {
        this.expression = null;
        this.message = message;
        this.logger = logger;
        this.formatter = formatter;
        this.listeners = listeners;
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        if (logger.shouldLog()) {
            try {
                String msg;
                if (expression != null) {
                    msg = expression.evaluate(exchange, String.class);
                } else {
                    msg = message;
                }
                if (formatter != null) {
                    msg = formatter.format(msg);
                }
                if (listeners != null && !listeners.isEmpty()) {
                    msg = fireListeners(exchange, msg);
                }
                logger.doLog(msg);
            } catch (Exception e) {
                exchange.setException(e);
            }
        }
        callback.done(true);
        return true;
    }

    private String fireListeners(Exchange exchange, String message) {
        for (LogListener listener : listeners) {
            if (listener == null) {
                continue;
            }
            try {
                String output = listener.onLog(exchange, logger, message);
                message = output != null ? output : message;
            } catch (Exception t) {
                LOG.warn("Ignoring an exception: {} thrown by: {} caused by: {}", t.getClass().getName(),
                        listener.getClass().getName(), t.getMessage());
                if (LOG.isDebugEnabled()) {
                    LOG.debug("", t);
                }
            }
        }
        return message;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public String getTraceLabel() {
        if (expression != null) {
            return "log[" + expression + "]";
        } else {
            return "log[" + message + "]";
        }
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

    public String getMessage() {
        return message;
    }

    public Expression getExpression() {
        return expression;
    }

    public ZwangineLogger getLogger() {
        return logger;
    }

    public MaskingFormatter getLogFormatter() {
        return formatter;
    }

}
