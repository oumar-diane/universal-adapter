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
 * Set the expected data type of the input message. If the actual message type is different at runtime, zwangine look for a
 * required {@link Transformer} and apply if exists. If validate attribute is true then zwangine applies {@link Validator}
 * as well. Type name consists of two parts, 'scheme' and 'name' connected with ':'. For Java type 'name' is a fully
 * qualified class name. For example {@code java:java.lang.String}, {@code json:ABCOrder}. It's also possible to specify
 * only scheme part, so that it works like a wildcard. If only 'xml' is specified, all the XML message matches. It's
 * handy to add only one transformer/validator for all the transformation from/to XML.
 */
@Metadata(label = "configuration")
public class InputTypeDefinition extends OptionalIdentifiedDefinition<InputTypeDefinition> {

    @Metadata(required = true)
    private String urn;
    @Metadata(javaType = "java.lang.Boolean", defaultValue = "false")
    private String validate;

    public InputTypeDefinition() {
    }

    public InputTypeDefinition urn(String urn) {
        setUrn(urn);
        return this;
    }

    public InputTypeDefinition javaClass(Class<?> clazz) {
        setJavaClass(clazz);
        return this;
    }

    public InputTypeDefinition validate(boolean validate) {
        setValidate(Boolean.toString(validate));
        return this;
    }

    public String getUrn() {
        return urn;
    }

    /**
     * The input type URN.
     */
    public void setUrn(String urn) {
        this.urn = urn;
    }

    /**
     * Set input type via Java Class.
     */
    public void setJavaClass(Class<?> clazz) {
        this.urn = "java:" + clazz.getName();
    }

    public String getValidate() {
        return this.validate;
    }

    /**
     * Whether if validation is required for this input type.
     */
    public void setValidate(String validate) {
        this.validate = validate;
    }

    @Override
    public String toString() {
        return "inputType[" + urn + "]";
    }

    @Override
    public String getShortName() {
        return "inputType";
    }

    @Override
    public String getLabel() {
        return "inputType[" + urn + "]";
    }

}
