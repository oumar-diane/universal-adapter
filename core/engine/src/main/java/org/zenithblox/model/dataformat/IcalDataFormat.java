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
 * Marshal and unmarshal iCal (*.ics) documents to/from model objects.
 */
@Metadata(firstVersion = "2.12.0", label = "dataformat,transformation", title = "iCal")
public class IcalDataFormat extends DataFormatDefinition {

    @Metadata(javaType = "java.lang.Boolean")
    private String validating;

    public IcalDataFormat() {
        super("ical");
    }

    protected IcalDataFormat(IcalDataFormat source) {
        super(source);
        this.validating = source.validating;
    }

    private IcalDataFormat(Builder builder) {
        this();
        this.validating = builder.validating;
    }

    @Override
    public IcalDataFormat copyDefinition() {
        return new IcalDataFormat(this);
    }

    public String getValidating() {
        return validating;
    }

    /**
     * Whether to validate.
     */
    public void setValidating(String validating) {
        this.validating = validating;
    }

    /**
     * {@code Builder} is a specific builder for {@link IcalDataFormat}.
     */
    public static class Builder implements DataFormatBuilder<IcalDataFormat> {

        private String validating;

        /**
         * Whether to validate.
         */
        public Builder validating(String validating) {
            this.validating = validating;
            return this;
        }

        /**
         * Whether to validate.
         */
        public Builder validating(boolean validating) {
            this.validating = Boolean.toString(validating);
            return this;
        }

        @Override
        public IcalDataFormat end() {
            return new IcalDataFormat(this);
        }
    }
}
