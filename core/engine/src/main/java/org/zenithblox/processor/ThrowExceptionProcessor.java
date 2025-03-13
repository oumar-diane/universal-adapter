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

import org.zenithblox.*;
import org.zenithblox.spi.IdAware;
import org.zenithblox.spi.WorkflowIdAware;
import org.zenithblox.support.AsyncProcessorSupport;
import org.zenithblox.util.ObjectHelper;

import java.lang.reflect.Constructor;

/**
 * The processor which sets an {@link Exception} on the {@link Exchange}
 */
public class ThrowExceptionProcessor extends AsyncProcessorSupport
        implements Traceable, IdAware, WorkflowIdAware, ZwangineContextAware {
    private String id;
    private String workflowId;
    private ZwangineContext zwangineContext;
    private Expression simple;
    private final Exception exception;
    private final Class<? extends Exception> type;
    private final String message;

    public ThrowExceptionProcessor(Exception exception) {
        this(exception, null, null);
    }

    public ThrowExceptionProcessor(Exception exception, Class<? extends Exception> type, String message) {
        this.exception = exception;
        this.type = type;
        this.message = message;
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        Exception cause = exception;

        try {
            if (message != null && type != null) {
                // create the message using simple language so it can be dynamic
                String text = simple.evaluate(exchange, String.class);
                // create a new exception of that type, and provide the message as
                Constructor<?> constructor = type.getConstructor(String.class);
                cause = (Exception) constructor.newInstance(text);
                exchange.setException(cause);
            } else if (cause == null && type != null) {
                // create a new exception of that type using its default constructor
                Constructor<?> constructor = type.getDeclaredConstructor();
                cause = (Exception) constructor.newInstance();
                exchange.setException(cause);
            } else {
                exchange.setException(cause);
            }
        } catch (Exception e) {
            Class<? extends Exception> exceptionClass = exception != null ? exception.getClass() : type;
            exchange.setException(
                    new ZwangineExchangeException("Error creating new instance of " + exceptionClass, exchange, e));
        }

        callback.done(true);
        return true;
    }

    @Override
    public String getTraceLabel() {
        String className = this.exception == null ? this.type.getSimpleName() : this.exception.getClass().getSimpleName();
        return "throwException[" + className + "]";
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

    public Exception getException() {
        return exception;
    }

    public Class<? extends Exception> getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    protected void doInit() throws Exception {
        ObjectHelper.notNull(zwangineContext, "zwangineContext", this);

        if (message != null) {
            simple = zwangineContext.resolveLanguage("simple").createExpression(message);
        }
    }

}
