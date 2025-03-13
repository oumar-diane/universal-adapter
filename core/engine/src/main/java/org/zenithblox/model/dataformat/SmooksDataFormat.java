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
 * Transform and bind XML as well as non-XML data, including EDI, CSV, JSON, and YAML using Smooks.
 */
@Metadata(firstVersion = "4.9.0", label = "dataformat,transformation", title = "Smooks")
public class SmooksDataFormat extends DataFormatDefinition {

    @Metadata(required = true)
    private String smooksConfig;

    public SmooksDataFormat() {
        super("smooks");
    }

    protected SmooksDataFormat(SmooksDataFormat source) {
        super(source);
        this.smooksConfig = source.smooksConfig;
    }

    private SmooksDataFormat(Builder builder) {
        this();
        this.smooksConfig = builder.smooksConfig;
    }

    @Override
    public SmooksDataFormat copyDefinition() {
        return new SmooksDataFormat(this);
    }

    /**
     * Path to the Smooks configuration file.
     */
    public void setSmooksConfig(String smooksConfig) {
        this.smooksConfig = smooksConfig;
    }

    public String getSmooksConfig() {
        return smooksConfig;
    }

    /**
     * {@code Builder} is a specific builder for {@link SmooksDataFormat}.
     */
    public static class Builder implements DataFormatBuilder<SmooksDataFormat> {

        private String smooksConfig;

        /**
         * Path to the Smooks configuration file.
         */
        public Builder smooksConfig(String smooksConfig) {
            this.smooksConfig = smooksConfig;
            return this;
        }

        @Override
        public SmooksDataFormat end() {
            return new SmooksDataFormat(this);
        }
    }
}
