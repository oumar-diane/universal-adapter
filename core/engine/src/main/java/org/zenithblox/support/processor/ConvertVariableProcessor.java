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

import org.zenithblox.*;
import org.zenithblox.spi.IdAware;
import org.zenithblox.spi.WorkflowIdAware;
import org.zenithblox.spi.VariableRepository;
import org.zenithblox.spi.VariableRepositoryFactory;
import org.zenithblox.support.AsyncCallbackToCompletableFutureAdapter;
import org.zenithblox.support.service.ServiceSupport;
import org.zenithblox.util.IOHelper;
import org.zenithblox.util.ObjectHelper;
import org.zenithblox.util.StringHelper;

import java.util.concurrent.CompletableFuture;

/**
 * A processor which converts the variable to be of the given type
 */
public class ConvertVariableProcessor extends ServiceSupport
        implements AsyncProcessor, IdAware, WorkflowIdAware, ZwangineContextAware {
    private ZwangineContext zwangineContext;
    private VariableRepositoryFactory factory;
    private String id;
    private String workflowId;
    private final String name;
    private final Expression variableName;
    private final String toName;
    private final Expression toVariableName;
    private final Class<?> type;
    private final String charset;
    private final boolean mandatory;

    public ConvertVariableProcessor(String name, Expression variableName, String toName, Expression toVariableName,
                                    Class<?> type, String charset, boolean mandatory) {
        ObjectHelper.notNull(variableName, "variableName");
        ObjectHelper.notNull(type, "type", this);
        this.name = name;
        this.variableName = variableName;
        this.toName = toName;
        this.toVariableName = toVariableName;
        this.type = type;
        this.charset = IOHelper.normalizeCharset(charset);
        this.mandatory = mandatory;
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
    public void process(Exchange exchange) throws Exception {
        // what is the variable name
        String name = variableName.evaluate(exchange, String.class);
        String key = name;
        String targetName = toVariableName != null ? toVariableName.evaluate(exchange, String.class) : name;

        VariableRepository repo = null;
        Object value;
        String id = StringHelper.before(key, ":");
        if (id != null) {
            key = StringHelper.after(key, ":");
            repo = factory.getVariableRepository(id);
            if (repo == null) {
                throw new IllegalArgumentException("VariableRepository with id: " + id + " does not exist");
            }
            value = repo.getVariable(key);
        } else {
            value = exchange.getVariable(key);
        }

        if (value == null && mandatory) {
            throw new NoSuchVariableException(exchange, name);
        } else if (value == null) {
            // only convert if there is a variable
            return;
        }

        String originalCharsetName = null;
        if (charset != null) {
            originalCharsetName = exchange.getProperty(ExchangePropertyKey.CHARSET_NAME, String.class);
            // override existing charset with configured charset as that is what the user
            // have explicit configured and expects to be used
            exchange.setProperty(ExchangePropertyKey.CHARSET_NAME, charset);
        }
        if (mandatory) {
            value = exchange.getContext().getTypeConverter().mandatoryConvertTo(type, exchange, value);
        } else {
            value = exchange.getContext().getTypeConverter().convertTo(type, exchange, value);
        }

        if (repo != null) {
            repo.setVariable(targetName, value);
        } else {
            exchange.setVariable(targetName, value);
        }

        // remove or restore charset when we are done as we should not propagate that,
        // as that can lead to double converting later on
        if (charset != null) {
            if (originalCharsetName != null && !originalCharsetName.isEmpty()) {
                exchange.setProperty(ExchangePropertyKey.CHARSET_NAME, originalCharsetName);
            } else {
                exchange.removeProperty(ExchangePropertyKey.CHARSET_NAME);
            }
        }
    }

    @Override
    public CompletableFuture<Exchange> processAsync(Exchange exchange) {
        AsyncCallbackToCompletableFutureAdapter<Exchange> callback = new AsyncCallbackToCompletableFutureAdapter<>(exchange);
        process(exchange, callback);
        return callback.getFuture();
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        try {
            process(exchange);
        } catch (Exception e) {
            exchange.setException(e);
        }
        callback.done(true);
        return true;
    }

    public String getName() {
        return name;
    }

    public String getToName() {
        return toName;
    }

    public Class<?> getType() {
        return type;
    }

    public String getCharset() {
        return charset;
    }

    @Override
    protected void doBuild() throws Exception {
        ObjectHelper.notNull(zwangineContext, "zwangineContext");
        factory = zwangineContext.getZwangineContextExtension().getContextPlugin(VariableRepositoryFactory.class);
    }
}
