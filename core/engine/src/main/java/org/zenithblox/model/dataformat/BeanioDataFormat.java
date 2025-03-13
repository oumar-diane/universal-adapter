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
 * Marshal and unmarshal Java beans to and from flat files (such as CSV, delimited, or fixed length formats).
 */
@Metadata(firstVersion = "2.10.0", label = "dataformat,transformation,csv", title = "BeanIO")
public class BeanioDataFormat extends DataFormatDefinition {

    private String mapping;
    private String streamName;
    @Metadata(javaType = "java.lang.Boolean")
    private String ignoreUnidentifiedRecords;
    @Metadata(javaType = "java.lang.Boolean")
    private String ignoreUnexpectedRecords;
    @Metadata(javaType = "java.lang.Boolean")
    private String ignoreInvalidRecords;
    @Metadata(label = "advanced")
    private String encoding;
    @Metadata(label = "advanced")
    private String beanReaderErrorHandlerType;
    @Metadata(label = "advanced", javaType = "java.lang.Boolean")
    private String unmarshalSingleObject;

    public BeanioDataFormat() {
        super("beanio");
    }

    protected BeanioDataFormat(BeanioDataFormat source) {
        super(source);
        this.mapping = source.mapping;
        this.streamName = source.streamName;
        this.ignoreUnidentifiedRecords = source.ignoreUnidentifiedRecords;
        this.ignoreUnexpectedRecords = source.ignoreUnexpectedRecords;
        this.ignoreInvalidRecords = source.ignoreInvalidRecords;
        this.encoding = source.encoding;
        this.beanReaderErrorHandlerType = source.beanReaderErrorHandlerType;
        this.unmarshalSingleObject = source.unmarshalSingleObject;
    }

    private BeanioDataFormat(Builder builder) {
        this();
        this.mapping = builder.mapping;
        this.streamName = builder.streamName;
        this.ignoreUnidentifiedRecords = builder.ignoreUnidentifiedRecords;
        this.ignoreUnexpectedRecords = builder.ignoreUnexpectedRecords;
        this.ignoreInvalidRecords = builder.ignoreInvalidRecords;
        this.encoding = builder.encoding;
        this.beanReaderErrorHandlerType = builder.beanReaderErrorHandlerType;
        this.unmarshalSingleObject = builder.unmarshalSingleObject;
    }

    @Override
    public BeanioDataFormat copyDefinition() {
        return new BeanioDataFormat(this);
    }

    public String getMapping() {
        return mapping;
    }

    /**
     * The BeanIO mapping file. Is by default loaded from the classpath. You can prefix with file:, http:, or classpath:
     * to denote from where to load the mapping file.
     */
    public void setMapping(String mapping) {
        this.mapping = mapping;
    }

    public String getStreamName() {
        return streamName;
    }

    /**
     * The name of the stream to use.
     */
    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }

    public String getIgnoreUnidentifiedRecords() {
        return ignoreUnidentifiedRecords;
    }

    /**
     * Whether to ignore unidentified records.
     */
    public void setIgnoreUnidentifiedRecords(String ignoreUnidentifiedRecords) {
        this.ignoreUnidentifiedRecords = ignoreUnidentifiedRecords;
    }

    public String getIgnoreUnexpectedRecords() {
        return ignoreUnexpectedRecords;
    }

    /**
     * Whether to ignore unexpected records.
     */
    public void setIgnoreUnexpectedRecords(String ignoreUnexpectedRecords) {
        this.ignoreUnexpectedRecords = ignoreUnexpectedRecords;
    }

    public String getIgnoreInvalidRecords() {
        return ignoreInvalidRecords;
    }

    /**
     * Whether to ignore invalid records.
     */
    public void setIgnoreInvalidRecords(String ignoreInvalidRecords) {
        this.ignoreInvalidRecords = ignoreInvalidRecords;
    }

    public String getEncoding() {
        return encoding;
    }

    /**
     * The charset to use.
     * <p/>
     * Is by default the JVM platform default charset.
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getBeanReaderErrorHandlerType() {
        return beanReaderErrorHandlerType;
    }

    /**
     * To use a custom org.zenithblox.dataformat.beanio.BeanIOErrorHandler as error handler while parsing. Configure
     * the fully qualified class name of the error handler. Notice the options ignoreUnidentifiedRecords,
     * ignoreUnexpectedRecords, and ignoreInvalidRecords may not be in use when you use a custom error handler.
     */
    public void setBeanReaderErrorHandlerType(String beanReaderErrorHandlerType) {
        this.beanReaderErrorHandlerType = beanReaderErrorHandlerType;
    }

    public String getUnmarshalSingleObject() {
        return unmarshalSingleObject;
    }

    /**
     * This options controls whether to unmarshal as a list of objects or as a single object only. The former is the
     * default mode, and the latter is only intended in special use-cases where beanio maps the Zwangine message to a
     * single POJO bean.
     */
    public void setUnmarshalSingleObject(String unmarshalSingleObject) {
        this.unmarshalSingleObject = unmarshalSingleObject;
    }

    /**
     * {@code Builder} is a specific builder for {@link BeanioDataFormat}.
     */
    public static class Builder implements DataFormatBuilder<BeanioDataFormat> {

        private String mapping;
        private String streamName;
        private String ignoreUnidentifiedRecords;
        private String ignoreUnexpectedRecords;
        private String ignoreInvalidRecords;
        private String encoding;
        private String beanReaderErrorHandlerType;
        private String unmarshalSingleObject;

        /**
         * The BeanIO mapping file. Is by default loaded from the classpath. You can prefix with file:, http:, or
         * classpath: to denote from where to load the mapping file.
         */
        public Builder mapping(String mapping) {
            this.mapping = mapping;
            return this;
        }

        /**
         * The name of the stream to use.
         */
        public Builder streamName(String streamName) {
            this.streamName = streamName;
            return this;
        }

        /**
         * Whether to ignore unidentified records.
         */
        public Builder ignoreUnidentifiedRecords(String ignoreUnidentifiedRecords) {
            this.ignoreUnidentifiedRecords = ignoreUnidentifiedRecords;
            return this;
        }

        /**
         * Whether to ignore unidentified records.
         */
        public Builder ignoreUnidentifiedRecords(boolean ignoreUnidentifiedRecords) {
            this.ignoreUnidentifiedRecords = ignoreUnidentifiedRecords ? "true" : "false";
            return this;
        }

        /**
         * Whether to ignore unexpected records.
         */
        public Builder ignoreUnexpectedRecords(String ignoreUnexpectedRecords) {
            this.ignoreUnexpectedRecords = ignoreUnexpectedRecords;
            return this;
        }

        /**
         * Whether to ignore unexpected records.
         */
        public Builder ignoreUnexpectedRecords(boolean ignoreUnexpectedRecords) {
            this.ignoreUnexpectedRecords = ignoreUnexpectedRecords ? "true" : "false";
            return this;
        }

        /**
         * Whether to ignore invalid records.
         */
        public Builder ignoreInvalidRecords(String ignoreInvalidRecords) {
            this.ignoreInvalidRecords = ignoreInvalidRecords;
            return this;
        }

        /**
         * Whether to ignore invalid records.
         */
        public Builder ignoreInvalidRecords(boolean ignoreInvalidRecords) {
            this.ignoreInvalidRecords = ignoreInvalidRecords ? "true" : "false";
            return this;
        }

        /**
         * The charset to use.
         * <p/>
         * Is by default the JVM platform default charset.
         */
        public Builder encoding(String encoding) {
            this.encoding = encoding;
            return this;
        }

        /**
         * To use a custom org.zenithblox.dataformat.beanio.BeanIOErrorHandler as error handler while parsing.
         * Configure the fully qualified class name of the error handler. Notice the options ignoreUnidentifiedRecords,
         * ignoreUnexpectedRecords, and ignoreInvalidRecords may not be in use when you use a custom error handler.
         */
        public Builder beanReaderErrorHandlerType(String beanReaderErrorHandlerType) {
            this.beanReaderErrorHandlerType = beanReaderErrorHandlerType;
            return this;
        }

        /**
         * This options controls whether to unmarshal as a list of objects or as a single object only. The former is the
         * default mode, and the latter is only intended in special use-cases where beanio maps the Zwangine message to a
         * single POJO bean.
         */
        public Builder unmarshalSingleObject(String unmarshalSingleObject) {
            this.unmarshalSingleObject = unmarshalSingleObject;
            return this;
        }

        /**
         * This options controls whether to unmarshal as a list of objects or as a single object only. The former is the
         * default mode, and the latter is only intended in special use-cases where beanio maps the Zwangine message to a
         * single POJO bean.
         */
        public Builder unmarshalSingleObject(boolean unmarshalSingleObject) {
            this.unmarshalSingleObject = unmarshalSingleObject ? "true" : "false";
            return this;
        }

        @Override
        public BeanioDataFormat end() {
            return new BeanioDataFormat(this);
        }
    }

}
