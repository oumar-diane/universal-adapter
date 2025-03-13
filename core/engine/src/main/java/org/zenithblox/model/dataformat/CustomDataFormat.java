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
 * Delegate to a custom {@link org.zenithblox.spi.DataFormat} implementation via Zwangine registry.
 */
@Metadata(label = "dataformat,transformation", title = "Custom")
public class CustomDataFormat extends DataFormatDefinition {

    private String ref;

    public CustomDataFormat() {
    }

    protected CustomDataFormat(CustomDataFormat source) {
        super(source);
        this.ref = source.ref;
    }

    public CustomDataFormat(String ref) {
        this.ref = ref;
    }

    private CustomDataFormat(Builder builder) {
        this();
        this.ref = builder.ref;
    }

    @Override
    public CustomDataFormat copyDefinition() {
        return new CustomDataFormat(this);
    }

    /**
     * Reference to the custom {@link org.zenithblox.spi.DataFormat} to lookup from the Zwangine registry.
     */
    public String getRef() {
        return ref;
    }

    /**
     * Reference to the custom {@link org.zenithblox.spi.DataFormat} to lookup from the Zwangine registry.
     */
    public void setRef(String ref) {
        this.ref = ref;
    }

    @Override
    public String toString() {
        return "CustomDataFormat[" + ref + "]";
    }

    /**
     * {@code Builder} is a specific builder for {@link CustomDataFormat}.
     */
    public static class Builder implements DataFormatBuilder<CustomDataFormat> {

        private String ref;

        /**
         * Reference to the custom {@link org.zenithblox.spi.DataFormat} to lookup from the Zwangine registry.
         */
        public Builder ref(String ref) {
            this.ref = ref;
            return this;
        }

        @Override
        public CustomDataFormat end() {
            return new CustomDataFormat(this);
        }
    }
}
