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
 * Unmarshal a CBOR payload to POJO and back.
 */
@Metadata(firstVersion = "3.0.0", label = "dataformat,transformation,json", title = "CBOR")
public class CBORDataFormat extends DataFormatDefinition {

    private Class<?> collectionType;
    private Class<?> unmarshalType;

    @Metadata(label = "advanced")
    private String objectMapper;
    @Metadata(defaultValue = "true", javaType = "java.lang.Boolean")
    private String useDefaultObjectMapper;
    private String unmarshalTypeName;
    @Metadata(label = "advanced")
    private String collectionTypeName;
    @Metadata(defaultValue = "false", javaType = "java.lang.Boolean")
    private String useList;
    @Metadata(defaultValue = "false", javaType = "java.lang.Boolean")
    private String allowUnmarshallType;
    @Metadata(defaultValue = "false", javaType = "java.lang.Boolean")
    private String prettyPrint;
    @Metadata(label = "advanced", defaultValue = "false", javaType = "java.lang.Boolean")
    private String allowJmsType;
    private String enableFeatures;
    private String disableFeatures;

    public CBORDataFormat() {
        super("cbor");
    }

    protected CBORDataFormat(CBORDataFormat source) {
        super(source);
        this.collectionType = source.collectionType;
        this.unmarshalType = source.unmarshalType;
        this.objectMapper = source.objectMapper;
        this.useDefaultObjectMapper = source.useDefaultObjectMapper;
        this.unmarshalTypeName = source.unmarshalTypeName;
        this.collectionTypeName = source.collectionTypeName;
        this.useList = source.useList;
        this.allowUnmarshallType = source.allowUnmarshallType;
        this.prettyPrint = source.prettyPrint;
        this.allowJmsType = source.allowJmsType;
        this.enableFeatures = source.enableFeatures;
        this.disableFeatures = source.disableFeatures;
    }

    private CBORDataFormat(Builder builder) {
        this();
        this.collectionType = builder.collectionType;
        this.unmarshalType = builder.unmarshalType;
        this.objectMapper = builder.objectMapper;
        this.useDefaultObjectMapper = builder.useDefaultObjectMapper;
        this.unmarshalTypeName = builder.unmarshalTypeName;
        this.collectionTypeName = builder.collectionTypeName;
        this.useList = builder.useList;
        this.allowUnmarshallType = builder.allowUnmarshallType;
        this.prettyPrint = builder.prettyPrint;
        this.allowJmsType = builder.allowJmsType;
        this.enableFeatures = builder.enableFeatures;
        this.disableFeatures = builder.disableFeatures;
    }

    @Override
    public CBORDataFormat copyDefinition() {
        return new CBORDataFormat(this);
    }

    public String getObjectMapper() {
        return objectMapper;
    }

    /**
     * Lookup and use the existing CBOR ObjectMapper with the given id when using Jackson.
     */
    public void setObjectMapper(String objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String getUseDefaultObjectMapper() {
        return useDefaultObjectMapper;
    }

    /**
     * Whether to lookup and use default Jackson CBOR ObjectMapper from the registry.
     */
    public void setUseDefaultObjectMapper(String useDefaultObjectMapper) {
        this.useDefaultObjectMapper = useDefaultObjectMapper;
    }

    public String getUnmarshalTypeName() {
        return unmarshalTypeName;
    }

    /**
     * Class name of the java type to use when unmarshalling
     */
    public void setUnmarshalTypeName(String unmarshalTypeName) {
        this.unmarshalTypeName = unmarshalTypeName;
    }

    public Class<?> getUnmarshalType() {
        return unmarshalType;
    }

    public String getPrettyPrint() {
        return prettyPrint;
    }

    /**
     * To enable pretty printing output nicely formatted.
     * <p/>
     * Is by default false.
     */
    public void setPrettyPrint(String prettyPrint) {
        this.prettyPrint = prettyPrint;
    }

    public String getAllowJmsType() {
        return allowJmsType;
    }

    /**
     * Used for JMS users to allow the JMSType header from the JMS spec to specify a FQN classname to use to unmarshal
     * to.
     */
    public void setAllowJmsType(String allowJmsType) {
        this.allowJmsType = allowJmsType;
    }

    /**
     * Class of the java type to use when unmarshalling
     */
    public void setUnmarshalType(Class<?> unmarshalType) {
        this.unmarshalType = unmarshalType;
    }

    public String getCollectionTypeName() {
        return collectionTypeName;
    }

    /**
     * Refers to a custom collection type to lookup in the registry to use. This option should rarely be used, but
     * allows to use different collection types than java.util.Collection based as default.
     */
    public void setCollectionTypeName(String collectionTypeName) {
        this.collectionTypeName = collectionTypeName;
    }

    public Class<?> getCollectionType() {
        return collectionType;
    }

    public void setCollectionType(Class<?> collectionType) {
        this.collectionType = collectionType;
    }

    public String getUseList() {
        return useList;
    }

    /**
     * To unmarshal to a List of Map or a List of Pojo.
     */
    public void setUseList(String useList) {
        this.useList = useList;
    }

    public String getAllowUnmarshallType() {
        return allowUnmarshallType;
    }

    /**
     * If enabled then Jackson CBOR is allowed to attempt to use the ZwangineCBORUnmarshalType header during the
     * unmarshalling.
     * <p/>
     * This should only be enabled when desired to be used.
     */
    public void setAllowUnmarshallType(String allowUnmarshallType) {
        this.allowUnmarshallType = allowUnmarshallType;
    }

    public String getEnableFeatures() {
        return enableFeatures;
    }

    /**
     * Set of features to enable on the Jackson <tt>com.fasterxml.jackson.databind.ObjectMapper</tt>.
     * <p/>
     * The features should be a name that matches a enum from
     * <tt>com.fasterxml.jackson.databind.SerializationFeature</tt>,
     * <tt>com.fasterxml.jackson.databind.DeserializationFeature</tt>, or
     * <tt>com.fasterxml.jackson.databind.MapperFeature</tt>
     * <p/>
     * Multiple features can be separated by comma
     */
    public void setEnableFeatures(String enableFeatures) {
        this.enableFeatures = enableFeatures;
    }

    public String getDisableFeatures() {
        return disableFeatures;
    }

    /**
     * Set of features to disable on the Jackson <tt>com.fasterxml.jackson.databind.ObjectMapper</tt>.
     * <p/>
     * The features should be a name that matches a enum from
     * <tt>com.fasterxml.jackson.databind.SerializationFeature</tt>,
     * <tt>com.fasterxml.jackson.databind.DeserializationFeature</tt>, or
     * <tt>com.fasterxml.jackson.databind.MapperFeature</tt>
     * <p/>
     * Multiple features can be separated by comma
     */
    public void setDisableFeatures(String disableFeatures) {
        this.disableFeatures = disableFeatures;
    }

    /**
     * {@code Builder} is a specific builder for {@link CBORDataFormat}.
     */
    public static class Builder implements DataFormatBuilder<CBORDataFormat> {

        private Class<?> collectionType;
        private Class<?> unmarshalType;
        private String objectMapper;
        private String useDefaultObjectMapper;
        private String unmarshalTypeName;
        private String collectionTypeName;
        private String useList;
        private String allowUnmarshallType;
        private String prettyPrint;
        private String allowJmsType;
        private String enableFeatures;
        private String disableFeatures;

        /**
         * Lookup and use the existing CBOR ObjectMapper with the given id when using Jackson.
         */
        public Builder objectMapper(String objectMapper) {
            this.objectMapper = objectMapper;
            return this;
        }

        /**
         * Whether to lookup and use default Jackson CBOR ObjectMapper from the registry.
         */
        public Builder useDefaultObjectMapper(String useDefaultObjectMapper) {
            this.useDefaultObjectMapper = useDefaultObjectMapper;
            return this;
        }

        /**
         * Whether to lookup and use default Jackson CBOR ObjectMapper from the registry.
         */
        public Builder useDefaultObjectMapper(boolean useDefaultObjectMapper) {
            this.useDefaultObjectMapper = Boolean.toString(useDefaultObjectMapper);
            return this;
        }

        /**
         * Class name of the java type to use when unmarshalling
         */
        public Builder unmarshalTypeName(String unmarshalTypeName) {
            this.unmarshalTypeName = unmarshalTypeName;
            return this;
        }

        /**
         * To enable pretty printing output nicely formatted.
         * <p/>
         * Is by default false.
         */
        public Builder prettyPrint(String prettyPrint) {
            this.prettyPrint = prettyPrint;
            return this;
        }

        /**
         * To enable pretty printing output nicely formatted.
         * <p/>
         * Is by default false.
         */
        public Builder prettyPrint(boolean prettyPrint) {
            this.prettyPrint = Boolean.toString(prettyPrint);
            return this;
        }

        /**
         * Used for JMS users to allow the JMSType header from the JMS spec to specify a FQN classname to use to
         * unmarshal to.
         */
        public Builder allowJmsType(String allowJmsType) {
            this.allowJmsType = allowJmsType;
            return this;
        }

        /**
         * Used for JMS users to allow the JMSType header from the JMS spec to specify a FQN classname to use to
         * unmarshal to.
         */
        public Builder allowJmsType(boolean allowJmsType) {
            this.allowJmsType = Boolean.toString(allowJmsType);
            return this;
        }

        /**
         * Class of the java type to use when unmarshalling
         */
        public Builder unmarshalType(Class<?> unmarshalType) {
            this.unmarshalType = unmarshalType;
            return this;
        }

        /**
         * Refers to a custom collection type to lookup in the registry to use. This option should rarely be used, but
         * allows to use different collection types than java.util.Collection based as default.
         */
        public Builder collectionTypeName(String collectionTypeName) {
            this.collectionTypeName = collectionTypeName;
            return this;
        }

        public Builder collectionType(Class<?> collectionType) {
            this.collectionType = collectionType;
            return this;
        }

        /**
         * To unmarshal to a List of Map or a List of Pojo.
         */
        public Builder useList(String useList) {
            this.useList = useList;
            return this;
        }

        /**
         * To unmarshal to a List of Map or a List of Pojo.
         */
        public Builder useList(boolean useList) {
            this.useList = Boolean.toString(useList);
            return this;
        }

        /**
         * If enabled then Jackson CBOR is allowed to attempt to use the ZwangineCBORUnmarshalType header during the
         * unmarshalling.
         * <p/>
         * This should only be enabled when desired to be used.
         */
        public Builder allowUnmarshallType(String allowUnmarshallType) {
            this.allowUnmarshallType = allowUnmarshallType;
            return this;
        }

        /**
         * If enabled then Jackson CBOR is allowed to attempt to use the ZwangineCBORUnmarshalType header during the
         * unmarshalling.
         * <p/>
         * This should only be enabled when desired to be used.
         */
        public Builder allowUnmarshallType(boolean allowUnmarshallType) {
            this.allowUnmarshallType = Boolean.toString(allowUnmarshallType);
            return this;
        }

        /**
         * Set of features to enable on the Jackson <tt>com.fasterxml.jackson.databind.ObjectMapper</tt>.
         * <p/>
         * The features should be a name that matches a enum from
         * <tt>com.fasterxml.jackson.databind.SerializationFeature</tt>,
         * <tt>com.fasterxml.jackson.databind.DeserializationFeature</tt>, or
         * <tt>com.fasterxml.jackson.databind.MapperFeature</tt>
         * <p/>
         * Multiple features can be separated by comma
         */
        public Builder enableFeatures(String enableFeatures) {
            this.enableFeatures = enableFeatures;
            return this;
        }

        /**
         * Set of features to disable on the Jackson <tt>com.fasterxml.jackson.databind.ObjectMapper</tt>.
         * <p/>
         * The features should be a name that matches a enum from
         * <tt>com.fasterxml.jackson.databind.SerializationFeature</tt>,
         * <tt>com.fasterxml.jackson.databind.DeserializationFeature</tt>, or
         * <tt>com.fasterxml.jackson.databind.MapperFeature</tt>
         * <p/>
         * Multiple features can be separated by comma
         */
        public Builder disableFeatures(String disableFeatures) {
            this.disableFeatures = disableFeatures;
            return this;
        }

        @Override
        public CBORDataFormat end() {
            return new CBORDataFormat(this);
        }
    }
}
