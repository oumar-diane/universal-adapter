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
 * Encode and decode data using Base64.
 */
@Metadata(firstVersion = "2.11.0", label = "dataformat,transformation", title = "Base64")
public class Base64DataFormat extends DataFormatDefinition {

    @Metadata(defaultValue = "76", javaType = "java.lang.Integer")
    private String lineLength;
    @Metadata(label = "advanced")
    private String lineSeparator;
    @Metadata(label = "advanced", javaType = "java.lang.Boolean")
    private String urlSafe;

    public Base64DataFormat() {
        super("base64");
    }

    protected Base64DataFormat(Base64DataFormat source) {
        super(source);
        this.lineLength = source.lineLength;
        this.lineSeparator = source.lineSeparator;
        this.urlSafe = source.urlSafe;
    }

    private Base64DataFormat(Builder builder) {
        this();
        this.lineLength = builder.lineLength;
        this.lineSeparator = builder.lineSeparator;
        this.urlSafe = builder.urlSafe;
    }

    @Override
    public Base64DataFormat copyDefinition() {
        return new Base64DataFormat(this);
    }

    public String getLineLength() {
        return lineLength;
    }

    /**
     * To specific a maximum line length for the encoded data.
     * <p/>
     * By default 76 is used.
     */
    public void setLineLength(String lineLength) {
        this.lineLength = lineLength;
    }

    public String getLineSeparator() {
        return lineSeparator;
    }

    /**
     * The line separators to use.
     * <p/>
     * Uses new line characters (CRLF) by default.
     */
    public void setLineSeparator(String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }

    public String getUrlSafe() {
        return urlSafe;
    }

    /**
     * Instead of emitting '+' and '/' we emit '-' and '_' respectively. urlSafe is only applied to encode operations.
     * Decoding seamlessly handles both modes. Is by default false.
     */
    public void setUrlSafe(String urlSafe) {
        this.urlSafe = urlSafe;
    }

    /**
     * {@code Builder} is a specific builder for {@link Base64DataFormat}.
     */
    public static class Builder implements DataFormatBuilder<Base64DataFormat> {

        private String lineLength;
        private String lineSeparator;
        private String urlSafe;

        /**
         * To specific a maximum line length for the encoded data.
         * <p/>
         * By default 76 is used.
         */
        public Builder lineLength(String lineLength) {
            this.lineLength = lineLength;
            return this;
        }

        /**
         * To specific a maximum line length for the encoded data.
         * <p/>
         * By default 76 is used.
         */
        public Builder lineLength(int lineLength) {
            this.lineLength = Integer.toString(lineLength);
            return this;
        }

        /**
         * The line separators to use.
         * <p/>
         * Uses new line characters (CRLF) by default.
         */
        public Builder lineSeparator(String lineSeparator) {
            this.lineSeparator = lineSeparator;
            return this;
        }

        /**
         * Instead of emitting '+' and '/' we emit '-' and '_' respectively. urlSafe is only applied to encode
         * operations. Decoding seamlessly handles both modes. Is by default false.
         */
        public Builder urlSafe(String urlSafe) {
            this.urlSafe = urlSafe;
            return this;
        }

        /**
         * Instead of emitting '+' and '/' we emit '-' and '_' respectively. urlSafe is only applied to encode
         * operations. Decoding seamlessly handles both modes. Is by default false.
         */
        public Builder urlSafe(boolean urlSafe) {
            this.urlSafe = Boolean.toString(urlSafe);
            return this;
        }

        @Override
        public Base64DataFormat end() {
            return new Base64DataFormat(this);
        }
    }
}
