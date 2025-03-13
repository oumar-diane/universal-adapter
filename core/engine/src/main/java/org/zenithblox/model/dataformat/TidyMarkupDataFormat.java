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
package org.zenithblox.model.dataformat;

import org.zenithblox.builder.DataFormatBuilder;
import org.zenithblox.model.DataFormatDefinition;
import org.zenithblox.spi.Metadata;
import org.w3c.dom.Node;

/**
 * Parse (potentially invalid) HTML into valid HTML or DOM.
 */
@Metadata(firstVersion = "2.0.0", label = "dataformat,transformation", title = "TidyMarkup")
public class TidyMarkupDataFormat extends DataFormatDefinition {

    private Class<?> dataObjectType;

    @Metadata(defaultValue = "org.w3c.dom.Node", enums = "org.w3c.dom.Node,java.lang.String")
    private String dataObjectTypeName;
    @Metadata(javaType = "java.lang.Boolean")
    private String omitXmlDeclaration;

    public TidyMarkupDataFormat() {
        super("tidyMarkup");
        this.setDataObjectType(Node.class);
    }

    protected TidyMarkupDataFormat(TidyMarkupDataFormat source) {
        super(source);
        this.dataObjectType = source.dataObjectType;
        this.dataObjectTypeName = source.dataObjectTypeName;
        this.omitXmlDeclaration = source.omitXmlDeclaration;
    }

    @Override
    public TidyMarkupDataFormat copyDefinition() {
        return new TidyMarkupDataFormat(this);
    }

    public TidyMarkupDataFormat(Class<?> dataObjectType) {
        this();
        if (!dataObjectType.isAssignableFrom(String.class) && !dataObjectType.isAssignableFrom(Node.class)) {
            throw new IllegalArgumentException(
                    "TidyMarkupDataFormat only supports returning a String or a org.w3c.dom.Node object");
        }
        this.setDataObjectType(dataObjectType);
    }

    private TidyMarkupDataFormat(Builder builder) {
        this();
        this.dataObjectType = builder.dataObjectType;
        this.dataObjectTypeName = builder.dataObjectTypeName;
        this.omitXmlDeclaration = builder.omitXmlDeclaration;
    }

    /**
     * What data type to unmarshal as, can either be org.w3c.dom.Node or java.lang.String.
     * <p/>
     * Is by default org.w3c.dom.Node
     */
    public void setDataObjectType(Class<?> dataObjectType) {
        this.dataObjectType = dataObjectType;
    }

    public Class<?> getDataObjectType() {
        return dataObjectType;
    }

    public String getDataObjectTypeName() {
        return dataObjectTypeName;
    }

    /**
     * What data type to unmarshal as, can either be org.w3c.dom.Node or java.lang.String.
     * <p/>
     * Is by default org.w3c.dom.Node
     */
    public void setDataObjectTypeName(String dataObjectTypeName) {
        this.dataObjectTypeName = dataObjectTypeName;
    }

    public String getOmitXmlDeclaration() {
        return omitXmlDeclaration;
    }

    /**
     * When returning a String, do we omit the XML declaration in the top.
     */
    public void setOmitXmlDeclaration(String omitXmlDeclaration) {
        this.omitXmlDeclaration = omitXmlDeclaration;
    }

    /**
     * {@code Builder} is a specific builder for {@link TidyMarkupDataFormat}.
     */
    public static class Builder implements DataFormatBuilder<TidyMarkupDataFormat> {

        private Class<?> dataObjectType;
        private String dataObjectTypeName;
        private String omitXmlDeclaration;

        /**
         * What data type to unmarshal as, can either be org.w3c.dom.Node or java.lang.String.
         * <p/>
         * Is by default org.w3c.dom.Node
         */
        public Builder dataObjectType(Class<?> dataObjectType) {
            this.dataObjectType = dataObjectType;
            return this;
        }

        /**
         * What data type to unmarshal as, can either be org.w3c.dom.Node or java.lang.String.
         * <p/>
         * Is by default org.w3c.dom.Node
         */
        public Builder dataObjectTypeName(String dataObjectTypeName) {
            this.dataObjectTypeName = dataObjectTypeName;
            return this;
        }

        /**
         * When returning a String, do we omit the XML declaration in the top.
         */
        public Builder omitXmlDeclaration(String omitXmlDeclaration) {
            this.omitXmlDeclaration = omitXmlDeclaration;
            return this;
        }

        /**
         * When returning a String, do we omit the XML declaration in the top.
         */
        public Builder omitXmlDeclaration(boolean omitXmlDeclaration) {
            this.omitXmlDeclaration = Boolean.toString(omitXmlDeclaration);
            return this;
        }

        @Override
        public TidyMarkupDataFormat end() {
            return new TidyMarkupDataFormat(this);
        }
    }
}
