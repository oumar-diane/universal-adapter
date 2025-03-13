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
 * Marshall and unmarshall FHIR objects to/from JSON.
 */
@Metadata(firstVersion = "2.21.0", label = "dataformat,transformation,health,json", title = "FHIR JSon")
public class FhirJsonDataFormat extends FhirDataformat {

    public FhirJsonDataFormat() {
        super("fhirJson");
    }

    protected FhirJsonDataFormat(FhirJsonDataFormat source) {
        super(source);
    }

    @Override
    public FhirJsonDataFormat copyDefinition() {
        return new FhirJsonDataFormat(this);
    }

    private FhirJsonDataFormat(Builder builder) {
        super("fhirJson", builder);
    }

    /**
     * {@code Builder} is a specific builder for {@link FhirJsonDataFormat}.
     */
    public static class Builder extends AbstractBuilder<Builder, FhirJsonDataFormat> {

        @Override
        public FhirJsonDataFormat end() {
            return new FhirJsonDataFormat(this);
        }
    }
}
