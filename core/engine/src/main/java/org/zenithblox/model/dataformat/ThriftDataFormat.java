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

/**
 * Serialize and deserialize messages using  Thrift binary data format.
 */
@Metadata(firstVersion = "2.20.0", label = "dataformat,transformation", title = "Thrift")
public class ThriftDataFormat extends DataFormatDefinition implements ContentTypeHeaderAware {

    private Object defaultInstance;

    private String instanceClass;
    @Metadata(enums = "binary,json,sjson", defaultValue = "binary")
    private String contentTypeFormat;
    @Metadata(javaType = "java.lang.Boolean", defaultValue = "true",
              description = "Whether the data format should set the Content-Type header with the type from the data format."
                            + " For example application/xml for data formats marshalling to XML, or application/json for data formats marshalling to JSON")
    private String contentTypeHeader;

    public ThriftDataFormat() {
        super("thrift");
    }

    protected ThriftDataFormat(ThriftDataFormat builder) {
        super(builder);
        this.defaultInstance = builder.defaultInstance;
        this.instanceClass = builder.instanceClass;
        this.contentTypeFormat = builder.contentTypeFormat;
        this.contentTypeHeader = builder.contentTypeHeader;
    }

    public ThriftDataFormat(String instanceClass) {
        this();
        setInstanceClass(instanceClass);
    }

    public ThriftDataFormat(String instanceClass, String contentTypeFormat) {
        this();
        setInstanceClass(instanceClass);
        setContentTypeFormat(contentTypeFormat);
    }

    private ThriftDataFormat(Builder builder) {
        this();
        this.defaultInstance = builder.defaultInstance;
        this.instanceClass = builder.instanceClass;
        this.contentTypeFormat = builder.contentTypeFormat;
        this.contentTypeHeader = builder.contentTypeHeader;
    }

    @Override
    public ThriftDataFormat copyDefinition() {
        return new ThriftDataFormat(this);
    }

    public String getInstanceClass() {
        return instanceClass;
    }

    /**
     * Name of class to use when unmarshalling
     */
    public void setInstanceClass(String instanceClass) {
        this.instanceClass = instanceClass;
    }

    /**
     * Defines a content type format in which thrift message will be serialized/deserialized from(to) the Java been. The
     * format can either be native or json for either native binary thrift, json or simple json fields representation.
     * The default value is binary.
     */
    public void setContentTypeFormat(String contentTypeFormat) {
        this.contentTypeFormat = contentTypeFormat;
    }

    public String getContentTypeFormat() {
        return contentTypeFormat;
    }

    public String getContentTypeHeader() {
        return contentTypeHeader;
    }

    public void setContentTypeHeader(String contentTypeHeader) {
        this.contentTypeHeader = contentTypeHeader;
    }

    public Object getDefaultInstance() {
        return defaultInstance;
    }

    public void setDefaultInstance(Object defaultInstance) {
        this.defaultInstance = defaultInstance;
    }

    /**
     * {@code Builder} is a specific builder for {@link ThriftDataFormat}.
     */
    public static class Builder implements DataFormatBuilder<ThriftDataFormat> {

        private Object defaultInstance;
        private String instanceClass;
        private String contentTypeFormat;
        private String contentTypeHeader;

        /**
         * Name of class to use when unmarshalling
         */
        public Builder instanceClass(String instanceClass) {
            this.instanceClass = instanceClass;
            return this;
        }

        /**
         * Defines a content type format in which thrift message will be serialized/deserialized from(to) the Java been.
         * The format can either be native or json for either native binary thrift, json or simple json fields
         * representation. The default value is binary.
         */
        public Builder contentTypeFormat(String contentTypeFormat) {
            this.contentTypeFormat = contentTypeFormat;
            return this;
        }

        public Builder contentTypeHeader(String contentTypeHeader) {
            this.contentTypeHeader = contentTypeHeader;
            return this;
        }

        public Builder contentTypeHeader(boolean contentTypeHeader) {
            this.contentTypeHeader = Boolean.toString(contentTypeHeader);
            return this;
        }

        public Builder defaultInstance(Object defaultInstance) {
            this.defaultInstance = defaultInstance;
            return this;
        }

        @Override
        public ThriftDataFormat end() {
            return new ThriftDataFormat(this);
        }
    }
}
