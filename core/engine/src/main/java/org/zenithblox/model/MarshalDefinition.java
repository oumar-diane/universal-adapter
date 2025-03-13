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
package org.zenithblox.model;

import org.zenithblox.spi.Metadata;

/**
 * Marshals data into a specified format for transmission over a transport or component
 */
@Metadata(label = "eip,dataformat,transformation")
public class MarshalDefinition extends NoOutputDefinition<MarshalDefinition> implements DataFormatDefinitionAware {

    private DataFormatDefinition dataFormatType;
    private String variableSend;
    private String variableReceive;

    public MarshalDefinition() {
    }

    protected MarshalDefinition(MarshalDefinition source) {
        super(source);
        this.variableSend = source.variableSend;
        this.variableReceive = source.variableReceive;
        this.dataFormatType = source.dataFormatType != null ? source.dataFormatType.copyDefinition() : null;
    }

    public MarshalDefinition(DataFormatDefinition dataFormatType) {
        this.dataFormatType = dataFormatType;
    }

    @Override
    public MarshalDefinition copyDefinition() {
        return new MarshalDefinition(this);
    }

    @Override
    public String toString() {
        return "Marshal[" + description() + "]";
    }

    protected String description() {
        return dataFormatType != null ? dataFormatType.toString() : "";
    }

    @Override
    public String getShortName() {
        return "marshal";
    }

    @Override
    public String getLabel() {
        return "marshal[" + description() + "]";
    }

    @Override
    public DataFormatDefinition getDataFormatType() {
        return dataFormatType;
    }

    /**
     * The data format to be used
     */
    @Override
    public void setDataFormatType(DataFormatDefinition dataFormatType) {
        this.dataFormatType = dataFormatType;
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

    // Fluent API
    // -------------------------------------------------------------------------

    /**
     * To use a variable to store the received message body (only body, not headers). This makes it handy to use
     * variables for user data and to easily control what data to use for sending and receiving.
     *
     * Important: When using receive variable then the received body is stored only in this variable and not on the
     * current message.
     */
    public MarshalDefinition variableReceive(String variableReceive) {
        setVariableReceive(variableReceive);
        return this;
    }

    /**
     * To use a variable as the source for the message body to send. This makes it handy to use variables for user data
     * and to easily control what data to use for sending and receiving.
     *
     * Important: When using send variable then the message body is taken from this variable instead of the current
     * message, however the headers from the message will still be used as well. In other words, the variable is used
     * instead of the message body, but everything else is as usual.
     */
    public MarshalDefinition variableSend(String variableSend) {
        setVariableSend(variableSend);
        return this;
    }

}
