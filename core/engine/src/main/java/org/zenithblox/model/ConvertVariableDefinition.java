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
 * Converts the variable to another type
 */
@Metadata(label = "eip,transformation")
public class ConvertVariableDefinition extends NoOutputDefinition<ConvertVariableDefinition> {

    private Class<?> typeClass;

    private String name;
    private String type;
    private String toName;
    @Metadata(label = "advanced", javaType = "java.lang.Boolean", defaultValue = "true")
    private String mandatory;
    @Metadata(label = "advanced")
    private String charset;

    public ConvertVariableDefinition() {
    }

    protected ConvertVariableDefinition(ConvertVariableDefinition source) {
        super(source);
        ;
        this.typeClass = source.typeClass;
        this.name = source.name;
        this.type = source.type;
        this.toName = source.toName;
        this.mandatory = source.mandatory;
        this.charset = source.charset;
    }

    public ConvertVariableDefinition(String name, String type) {
        setName(name);
        setType(type);
    }

    public ConvertVariableDefinition(String name, Class<?> typeClass) {
        setName(name);
        setTypeClass(typeClass);
        setType(typeClass.getCanonicalName());
    }

    public ConvertVariableDefinition(String name, String toName, Class<?> typeClass) {
        setName(name);
        setToName(toName);
        setTypeClass(typeClass);
        setType(typeClass.getCanonicalName());
    }

    public ConvertVariableDefinition(String name, Class<?> typeClass, boolean mandatory) {
        setName(name);
        setTypeClass(typeClass);
        setType(typeClass.getCanonicalName());
        setMandatory(mandatory ? "true" : "false");
    }

    public ConvertVariableDefinition(String name, Class<?> typeClass, String charset) {
        setName(name);
        setTypeClass(typeClass);
        setType(typeClass.getCanonicalName());
        setCharset(charset);
    }

    @Override
    public ConvertVariableDefinition copyDefinition() {
        return new ConvertVariableDefinition(this);
    }

    @Override
    public String toString() {
        return "ConvertVariableTo[" + getName() + ": " + getType() + "]";
    }

    @Override
    public String getShortName() {
        return "convertVariableTo";
    }

    @Override
    public String getLabel() {
        return "convertVariableTo[" + getType() + "]";
    }

    /**
     * Name of variable to convert its value
     * <p/>
     * The <tt>simple</tt> language can be used to define a dynamic evaluated header name to be used. Otherwise a
     * constant name will be used.
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getToName() {
        return toName;
    }

    /**
     * To use another variable to store the result. By default, the result is stored in the same variable. This option
     * allows to use another variable.
     * <p/>
     * The <tt>simple</tt> language can be used to define a dynamic evaluated variable name to be used. Otherwise a
     * constant name will be used.
     */
    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getType() {
        return type;
    }

    /**
     * The java type to convert to
     */
    public void setType(String type) {
        this.type = type;
    }

    public Class<?> getTypeClass() {
        return typeClass;
    }

    public void setTypeClass(Class<?> typeClass) {
        this.typeClass = typeClass;
    }

    public String getCharset() {
        return charset;
    }

    /**
     * To use a specific charset when converting
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getMandatory() {
        return mandatory;
    }

    /**
     * When mandatory then the conversion must return a value (cannot be null), if this is not possible then
     * NoTypeConversionAvailableException is thrown. Setting this to false could mean conversion is not possible and the
     * value is null.
     */
    public void setMandatory(String mandatory) {
        this.mandatory = mandatory;
    }
}
