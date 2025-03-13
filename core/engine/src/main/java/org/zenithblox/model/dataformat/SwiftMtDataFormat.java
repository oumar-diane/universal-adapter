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
 * Encode and decode SWIFT MT messages.
 */
@Metadata(firstVersion = "3.20.0", label = "dataformat,transformation,swift", title = "SWIFT MT")
public class SwiftMtDataFormat extends DataFormatDefinition {

    @Metadata(label = "advanced", javaType = "java.lang.Boolean")
    private String writeInJson;

    public SwiftMtDataFormat() {
        super("swiftMt");
    }

    protected SwiftMtDataFormat(SwiftMtDataFormat source) {
        super(source);
        this.writeInJson = source.writeInJson;
    }

    public SwiftMtDataFormat(String writeInJson) {
        this();
        this.writeInJson = writeInJson;
    }

    private SwiftMtDataFormat(Builder builder) {
        this();
        this.writeInJson = builder.writeInJson;
    }

    @Override
    public SwiftMtDataFormat copyDefinition() {
        return new SwiftMtDataFormat(this);
    }

    public String getWriteInJson() {
        return writeInJson;
    }

    /**
     * The flag indicating that messages must be marshalled in a JSON format.
     *
     * @param writeInJson {@code true} if messages must be marshalled in a JSON format, {@code false} otherwise.
     */
    public void setWriteInJson(String writeInJson) {
        this.writeInJson = writeInJson;
    }

    /**
     * {@code Builder} is a specific builder for {@link SwiftMtDataFormat}.
     */
    public static class Builder implements DataFormatBuilder<SwiftMtDataFormat> {

        private String writeInJson;

        /**
         * The flag indicating that messages must be marshalled in a JSON format.
         *
         * @param writeInJson {@code true} if messages must be marshalled in a JSON format, {@code false} otherwise.
         */
        public Builder writeInJson(String writeInJson) {
            this.writeInJson = writeInJson;
            return this;
        }

        /**
         * The flag indicating that messages must be marshalled in a JSON format.
         *
         * @param writeInJson {@code true} if messages must be marshalled in a JSON format, {@code false} otherwise.
         */
        public Builder writeInJson(boolean writeInJson) {
            this.writeInJson = Boolean.toString(writeInJson);
            return this;
        }

        @Override
        public SwiftMtDataFormat end() {
            return new SwiftMtDataFormat(this);
        }
    }
}
