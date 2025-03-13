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

import org.zenithblox.spi.Metadata;

/**
 * Marshal and unmarshal Java objects from and to TSV (Tab-Separated Values) records using UniVocity Parsers.
 */
@Metadata(firstVersion = "2.15.0", label = "dataformat,transformation,csv", title = "uniVocity TSV")
public class UniVocityTsvDataFormat extends UniVocityAbstractDataFormat {

    @Metadata(label = "advanced", defaultValue = "\\")
    private String escapeChar;

    public UniVocityTsvDataFormat() {
        super("univocityTsv");
    }

    protected UniVocityTsvDataFormat(UniVocityTsvDataFormat source) {
        super(source);
        this.escapeChar = source.escapeChar;
    }

    private UniVocityTsvDataFormat(Builder builder) {
        super("univocityTsv", builder);
        this.escapeChar = builder.escapeChar;
    }

    @Override
    public UniVocityTsvDataFormat copyDefinition() {
        return new UniVocityTsvDataFormat(this);
    }

    public String getEscapeChar() {
        return escapeChar;
    }

    /**
     * The escape character.
     */
    public void setEscapeChar(String escapeChar) {
        this.escapeChar = escapeChar;
    }

    /**
     * {@code Builder} is a specific builder for {@link UniVocityTsvDataFormat}.
     */
    public static class Builder extends AbstractBuilder<Builder, UniVocityTsvDataFormat> {

        private String escapeChar;

        /**
         * The escape character.
         */
        public Builder escapeChar(String escapeChar) {
            this.escapeChar = escapeChar;
            return this;
        }

        @Override
        public UniVocityTsvDataFormat end() {
            return new UniVocityTsvDataFormat(this);
        }
    }
}
