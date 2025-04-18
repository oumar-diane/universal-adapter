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
import org.zenithblox.spi.DataFormat;
import org.zenithblox.spi.IdAware;
import org.zenithblox.spi.WorkflowIdAware;
import org.zenithblox.support.AsyncProcessorSupport;
import org.zenithblox.support.ExchangeHelper;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.util.IOHelper;
import org.zenithblox.util.ObjectHelper;

import java.io.InputStream;
import java.util.Iterator;

/**
 * Unmarshals the body of the incoming message using the given <a href="http://zwangine.zwangine.org/data-format.html">data
 * format</a>
 */
public class UnmarshalProcessor extends AsyncProcessorSupport implements Traceable, ZwangineContextAware, IdAware, WorkflowIdAware {
    private String id;
    private String workflowId;
    private ZwangineContext zwangineContext;
    private final DataFormat dataFormat;
    private final boolean allowNullBody;
    private String variableSend;
    private String variableReceive;

    public UnmarshalProcessor(DataFormat dataFormat) {
        this(dataFormat, false);
    }

    public UnmarshalProcessor(DataFormat dataFormat, boolean allowNullBody) {
        this.dataFormat = dataFormat;
        this.allowNullBody = allowNullBody;
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        ObjectHelper.notNull(dataFormat, "dataFormat");

        InputStream stream = null;
        Object result = null;
        try {
            final Message in = exchange.getIn();
            final Object originalBody = in.getBody();
            Object body = originalBody;
            if (variableSend != null) {
                body = ExchangeHelper.getVariable(exchange, variableSend);
            }
            final Message out;
            if (allowNullBody && body == null) {
                // The body is null, and it is an allowed value so let's skip the unmarshalling
                out = exchange.getOut();
            } else {
                // lets set up the out message before we invoke the dataFormat so that it can mutate it if necessary
                out = exchange.getOut();
                out.copyFrom(in);
                if (body instanceof InputStream is) {
                    stream = is;
                    result = dataFormat.unmarshal(exchange, stream);
                } else {
                    result = dataFormat.unmarshal(exchange, body);
                }
            }
            if (result instanceof Exchange) {
                if (result != exchange) {
                    // it's not allowed to return another exchange other than the one provided to dataFormat
                    throw new RuntimeZwangineException(
                            "The returned exchange " + result + " is not the same as " + exchange
                                                    + " provided to the DataFormat");
                }
            } else if (result instanceof Message msg) {
                // result should be stored in variable instead of message body
                if (variableReceive != null) {
                    Object value = msg.getBody();
                    ExchangeHelper.setVariable(exchange, variableReceive, value);
                } else {
                    // the dataformat has probably set headers, attachments, etc. so let's use it as the outbound payload
                    exchange.setOut(msg);
                }
            } else {
                // result should be stored in variable instead of message body
                if (variableReceive != null) {
                    ExchangeHelper.setVariable(exchange, variableReceive, result);
                } else {
                    out.setBody(result);
                }
            }
        } catch (Exception e) {
            // remove OUT message, as an exception occurred
            exchange.setOut(null);
            exchange.setException(e);
        } finally {
            // The Iterator will close the stream itself
            if (!(result instanceof Iterator)) {
                IOHelper.close(stream, "input stream");
            }
        }
        callback.done(true);
        return true;
    }

    @Override
    public String toString() {
        return "Unmarshal[" + dataFormat + "]";
    }

    @Override
    public String getTraceLabel() {
        return "unmarshal[" + dataFormat + "]";
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
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    public boolean isAllowNullBody() {
        return allowNullBody;
    }

    public String getVariableSend() {
        return variableSend;
    }

    public void setVariableSend(String variableSend) {
        this.variableSend = variableSend;
    }

    public String getVariableReceive() {
        return variableReceive;
    }

    public void setVariableReceive(String variableReceive) {
        this.variableReceive = variableReceive;
    }

    @Override
    protected void doStart() throws Exception {
        // inject ZwangineContext on data format
        ZwangineContextAware.trySetZwangineContext(dataFormat, zwangineContext);
        // add dataFormat as service which will also start the service
        // (false => we handle the lifecycle of the dataFormat)
        getZwangineContext().addService(dataFormat, false, true);
    }

    @Override
    protected void doStop() throws Exception {
        ServiceHelper.stopService(dataFormat);
        getZwangineContext().removeService(dataFormat);
    }

}
