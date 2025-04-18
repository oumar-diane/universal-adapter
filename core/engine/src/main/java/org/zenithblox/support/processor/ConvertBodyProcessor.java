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
import org.zenithblox.support.AsyncCallbackToCompletableFutureAdapter;
import org.zenithblox.support.DefaultMessage;
import org.zenithblox.support.ExchangeHelper;
import org.zenithblox.support.service.ServiceSupport;
import org.zenithblox.util.IOHelper;
import org.zenithblox.util.ObjectHelper;

import java.util.concurrent.CompletableFuture;

/**
 * A processor which converts the payload of the input message to be of the given type
 * <p/>
 * If the conversion fails an {@link org.zenithblox.InvalidPayloadException} is thrown.
 */
public class ConvertBodyProcessor extends ServiceSupport implements AsyncProcessor, IdAware, WorkflowIdAware {
    private String id;
    private String workflowId;
    private final Class<?> type;
    private final String charset;
    private final boolean mandatory;

    public ConvertBodyProcessor(Class<?> type) {
        ObjectHelper.notNull(type, "type", this);
        this.type = type;
        this.charset = null;
        this.mandatory = true;
    }

    public ConvertBodyProcessor(Class<?> type, String charset) {
        ObjectHelper.notNull(type, "type", this);
        this.type = type;
        this.charset = IOHelper.normalizeCharset(charset);
        this.mandatory = true;
    }

    public ConvertBodyProcessor(Class<?> type, String charset, boolean mandatory) {
        ObjectHelper.notNull(type, "type", this);
        this.type = type;
        this.charset = IOHelper.normalizeCharset(charset);
        this.mandatory = mandatory;
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
        Message old = exchange.getMessage();

        if (old.getBody() == null) {
            // only convert if there is a body
            return;
        }

        if (exchange.getException() != null) {
            // do not convert if an exception has been thrown as if we attempt to convert and it also fails with a new
            // exception then it will override the existing exception
            return;
        }

        String originalCharsetName = null;
        if (charset != null) {
            originalCharsetName = exchange.getProperty(ExchangePropertyKey.CHARSET_NAME, String.class);
            // override existing charset with configured charset as that is what the user
            // have explicit configured and expects to be used
            exchange.setProperty(ExchangePropertyKey.CHARSET_NAME, charset);
        }
        // use mandatory conversion
        Object value;
        if (mandatory) {
            value = old.getMandatoryBody(type);
        } else {
            value = old.getBody(type);
        }

        // create a new message container so we do not drag specialized message objects along
        // but that is only needed if the old message is a specialized message
        boolean copyNeeded = !(old.getClass().equals(DefaultMessage.class));

        if (copyNeeded) {
            Message msg = new DefaultMessage(exchange.getContext());
            msg.copyFromWithNewBody(old, value);

            // replace message on exchange
            ExchangeHelper.replaceMessage(exchange, msg, false);
        } else {
            // no copy needed so set replace value directly
            old.setBody(value);
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

    public Class<?> getType() {
        return type;
    }

    public String getCharset() {
        return charset;
    }
}
