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
 * Converts the message data received from the wire into a format that  Zwangine processors can consume
 */
@Metadata(label = "eip,dataformat,transformation")
public class UnmarshalDefinition extends NoOutputDefinition<UnmarshalDefinition> implements DataFormatDefinitionAware {

    private DataFormatDefinition dataFormatType;
    private String variableSend;
    private String variableReceive;
    @Metadata(label = "advanced", javaType = "java.lang.Boolean", defaultValue = "false")
    private String allowNullBody;

    public UnmarshalDefinition() {
    }

    protected UnmarshalDefinition(UnmarshalDefinition source) {
        super(source);
        this.variableSend = source.variableSend;
        this.variableReceive = source.variableReceive;
        this.allowNullBody = source.allowNullBody;
        this.dataFormatType = source.dataFormatType != null ? source.dataFormatType.copyDefinition() : null;
    }

    public UnmarshalDefinition(DataFormatDefinition dataFormatType) {
        this.dataFormatType = dataFormatType;
    }

    @Override
    public UnmarshalDefinition copyDefinition() {
        return new UnmarshalDefinition(this);
    }

    @Override
    public String toString() {
        return "Unmarshal[" + description() + "]";
    }

    protected String description() {
        if (dataFormatType != null) {
            return dataFormatType.toString();
        } else {
            return "";
        }
    }

    @Override
    public String getShortName() {
        return "unmarshal";
    }

    @Override
    public String getLabel() {
        return "unmarshal[" + description() + "]";
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

    public String getAllowNullBody() {
        return allowNullBody;
    }

    /**
     * Indicates whether {@code null} is allowed as value of a body to unmarshall.
     */
    public void setAllowNullBody(String allowNullBody) {
        this.allowNullBody = allowNullBody;
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
    public UnmarshalDefinition variableReceive(String variableReceive) {
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
    public UnmarshalDefinition variableSend(String variableSend) {
        setVariableSend(variableSend);
        return this;
    }

    /**
     * Indicates whether {@code null} is allowed as value of a body to unmarshall.
     *
     * @param  allowNullBody {@code true} if {@code null} is allowed as value of a body to unmarshall, {@code false}
     *                       otherwise
     * @return               the builder
     */
    public UnmarshalDefinition allowNullBody(boolean allowNullBody) {
        setAllowNullBody(Boolean.toString(allowNullBody));
        return this;
    }
}
