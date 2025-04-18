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
 * Unmarshal unstructured data to objects using Logstash based Grok patterns.
 */
@Metadata(label = "dataformat,transformation", title = "Grok", firstVersion = "3.0.0")
public class GrokDataFormat extends DataFormatDefinition {

    @Metadata
    private String pattern;
    @Metadata(javaType = "java.lang.Boolean")
    private String flattened;
    @Metadata(defaultValue = "true", javaType = "java.lang.Boolean")
    private String allowMultipleMatchesPerLine;
    @Metadata(javaType = "java.lang.Boolean")
    private String namedOnly;

    public GrokDataFormat() {
        super("grok");
    }

    protected GrokDataFormat(GrokDataFormat source) {
        super(source);
        this.pattern = source.pattern;
        this.flattened = source.flattened;
        this.allowMultipleMatchesPerLine = source.allowMultipleMatchesPerLine;
        this.namedOnly = source.namedOnly;
    }

    private GrokDataFormat(Builder builder) {
        this();
        this.pattern = builder.pattern;
        this.flattened = builder.flattened;
        this.allowMultipleMatchesPerLine = builder.allowMultipleMatchesPerLine;
        this.namedOnly = builder.namedOnly;
    }

    @Override
    public GrokDataFormat copyDefinition() {
        return new GrokDataFormat(this);
    }

    public String getPattern() {
        return pattern;
    }

    /**
     * The grok pattern to match lines of input
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getFlattened() {
        return flattened;
    }

    /**
     * Turns on flattened mode. In flattened mode the exception is thrown when there are multiple pattern matches with
     * same key.
     */
    public void setFlattened(String flattened) {
        this.flattened = flattened;
    }

    public String getAllowMultipleMatchesPerLine() {
        return allowMultipleMatchesPerLine;
    }

    /**
     * If false, every line of input is matched for pattern only once. Otherwise the line can be scanned multiple times
     * when non-terminal pattern is used.
     */
    public void setAllowMultipleMatchesPerLine(String allowMultipleMatchesPerLine) {
        this.allowMultipleMatchesPerLine = allowMultipleMatchesPerLine;
    }

    public String getNamedOnly() {
        return namedOnly;
    }

    /**
     * Whether to capture named expressions only or not (i.e. %{IP:ip} but not ${IP})
     */
    public void setNamedOnly(String namedOnly) {
        this.namedOnly = namedOnly;
    }

    @Override
    public String toString() {
        return "GrokDataFormat[" + pattern + ']';
    }

    /**
     * {@code Builder} is a specific builder for {@link GrokDataFormat}.
     */
    public static class Builder implements DataFormatBuilder<GrokDataFormat> {

        private String pattern;
        private String flattened;
        private String allowMultipleMatchesPerLine;
        private String namedOnly;

        /**
         * The grok pattern to match lines of input
         */
        public Builder pattern(String pattern) {
            this.pattern = pattern;
            return this;
        }

        /**
         * Turns on flattened mode. In flattened mode the exception is thrown when there are multiple pattern matches
         * with same key.
         */
        public Builder flattened(String flattened) {
            this.flattened = flattened;
            return this;
        }

        /**
         * Turns on flattened mode. In flattened mode the exception is thrown when there are multiple pattern matches
         * with same key.
         */
        public Builder flattened(boolean flattened) {
            this.flattened = Boolean.toString(flattened);
            return this;
        }

        /**
         * If false, every line of input is matched for pattern only once. Otherwise the line can be scanned multiple
         * times when non-terminal pattern is used.
         */
        public Builder allowMultipleMatchesPerLine(String allowMultipleMatchesPerLine) {
            this.allowMultipleMatchesPerLine = allowMultipleMatchesPerLine;
            return this;
        }

        /**
         * If false, every line of input is matched for pattern only once. Otherwise the line can be scanned multiple
         * times when non-terminal pattern is used.
         */
        public Builder allowMultipleMatchesPerLine(boolean allowMultipleMatchesPerLine) {
            this.allowMultipleMatchesPerLine = Boolean.toString(allowMultipleMatchesPerLine);
            return this;
        }

        /**
         * Whether to capture named expressions only or not (i.e. %{IP:ip} but not ${IP})
         */
        public Builder namedOnly(String namedOnly) {
            this.namedOnly = namedOnly;
            return this;
        }

        /**
         * Whether to capture named expressions only or not (i.e. %{IP:ip} but not ${IP})
         */
        public Builder namedOnly(boolean namedOnly) {
            this.namedOnly = Boolean.toString(namedOnly);
            return this;
        }

        @Override
        public GrokDataFormat end() {
            return new GrokDataFormat(this);
        }
    }
}
