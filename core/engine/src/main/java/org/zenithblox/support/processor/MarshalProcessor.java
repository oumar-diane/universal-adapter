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
import org.zenithblox.support.builder.OutputStreamBuilder;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.util.ObjectHelper;

/**
 * Marshals the body of the incoming message using the given data
 * format</a>
 */
public class MarshalProcessor extends AsyncProcessorSupport implements Traceable, ZwangineContextAware, IdAware, WorkflowIdAware {
    private String id;
    private String workflowId;
    private ZwangineContext zwangineContext;
    private final DataFormat dataFormat;
    private String variableSend;
    private String variableReceive;

    public MarshalProcessor(DataFormat dataFormat) {
        this.dataFormat = dataFormat;
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        ObjectHelper.notNull(dataFormat, "dataFormat");

        // if stream caching is enabled then use that so we can stream accordingly
        // for example to overflow to disk for big streams
        OutputStreamBuilder osb = OutputStreamBuilder.withExchange(exchange);

        Message in = exchange.getIn();
        final Object originalBody = in.getBody();
        Object body = originalBody;
        if (variableSend != null) {
            body = ExchangeHelper.getVariable(exchange, variableSend);
        }

        // lets setup the out message before we invoke the dataFormat
        // so that it can mutate it if necessary
        Message out = exchange.getOut();
        out.copyFrom(in);

        try {
            dataFormat.marshal(exchange, body, osb);
            Object result = osb.build();
            // result should be stored in variable instead of message body
            if (variableReceive != null) {
                ExchangeHelper.setVariable(exchange, variableReceive, result);
            } else {
                out.setBody(result);
            }
        } catch (Exception e) {
            // remove OUT message, as an exception occurred
            exchange.setOut(null);
            exchange.setException(e);
        }

        callback.done(true);
        return true;
    }

    @Override
    public String toString() {
        return "Marshal[" + dataFormat + "]";
    }

    @Override
    public String getTraceLabel() {
        return "marshal[" + dataFormat + "]";
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
