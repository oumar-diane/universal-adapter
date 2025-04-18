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
 * Marshal and unmarshal <a href="http://jsonapi.org/format/">JSON:API resources</a> using
 * <a href="https://github.com/jasminb/jsonapi-converter">JSONAPI-Converter library</a>.
 */
@Metadata(firstVersion = "3.0.0", label = "dataformat,transformation", title = "JSonApi")
public class JsonApiDataFormat extends DataFormatDefinition {

    private String dataFormatTypes;
    private Class<?>[] dataFormatTypeClasses;
    private String mainFormatType;
    private Class<?> mainFormatTypeClass;

    public JsonApiDataFormat() {
        super("jsonApi");
    }

    protected JsonApiDataFormat(JsonApiDataFormat source) {
        super(source);
        this.dataFormatTypes = source.dataFormatTypes;
        this.dataFormatTypeClasses = source.dataFormatTypeClasses;
        this.mainFormatType = source.mainFormatType;
        this.mainFormatTypeClass = source.mainFormatTypeClass;
    }

    private JsonApiDataFormat(Builder builder) {
        this();
        this.dataFormatTypes = builder.dataFormatTypes;
        this.dataFormatTypeClasses = builder.dataFormatTypeClasses;
        this.mainFormatType = builder.mainFormatType;
        this.mainFormatTypeClass = builder.mainFormatTypeClass;
    }

    @Override
    public JsonApiDataFormat copyDefinition() {
        return new JsonApiDataFormat(this);
    }

    public String getDataFormatTypes() {
        return dataFormatTypes;
    }

    /**
     * The classes to take into account for the marshalling. Multiple classes can be separated by comma.
     */
    public void setDataFormatTypes(String dataFormatTypes) {
        this.dataFormatTypes = dataFormatTypes;
    }

    public Class<?>[] getDataFormatTypeClasses() {
        return dataFormatTypeClasses;
    }

    /**
     * The classes to take into account for the marshalling.
     */
    public void setDataFormatTypeClasses(Class<?>[] dataFormatTypeClasses) {
        this.dataFormatTypeClasses = dataFormatTypeClasses;
    }

    public String getMainFormatType() {
        return mainFormatType;
    }

    /**
     * The class to take into account while unmarshalling.
     */
    public void setMainFormatType(String mainFormatType) {
        this.mainFormatType = mainFormatType;
    }

    public Class<?> getMainFormatTypeClass() {
        return mainFormatTypeClass;
    }

    /**
     * The classes to take into account while unmarshalling.
     */
    public void setMainFormatTypeClass(Class<?> mainFormatTypeClass) {
        this.mainFormatTypeClass = mainFormatTypeClass;
    }

    /**
     * {@code Builder} is a specific builder for {@link JsonApiDataFormat}.
     */
    public static class Builder implements DataFormatBuilder<JsonApiDataFormat> {

        private String dataFormatTypes;
        private Class<?>[] dataFormatTypeClasses;
        private String mainFormatType;
        private Class<?> mainFormatTypeClass;

        /**
         * The classes to take into account for the marshalling,
         */
        public Builder dataFormatTypes(Class<?>[] dataFormatTypes) {
            this.dataFormatTypeClasses = dataFormatTypes;
            return this;
        }

        /**
         * The classes (FQN name) to take into account for the marshalling. Multiple class names can be separated by
         * comma.
         */
        public Builder dataFormatTypes(String dataFormatTypes) {
            this.dataFormatTypes = dataFormatTypes;
            return this;
        }

        /**
         * The classes to take into account while unmarshalling,
         */
        public Builder mainFormatType(Class<?> mainFormatType) {
            this.mainFormatTypeClass = mainFormatType;
            return this;
        }

        /**
         * The class (FQN name) to take into account while unmarshalling,
         */
        public Builder mainFormatType(String mainFormatType) {
            this.mainFormatType = mainFormatType;
            return this;
        }

        @Override
        public JsonApiDataFormat end() {
            return new JsonApiDataFormat(this);
        }
    }
}
