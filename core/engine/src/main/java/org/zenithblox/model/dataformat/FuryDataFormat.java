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
 * Serialize and deserialize messages using  Fury
 */
@Metadata(firstVersion = "4.9.0", label = "dataformat,transformation", title = "Fury")
public class FuryDataFormat extends DataFormatDefinition {
    private Class<?> unmarshalType;

    @Metadata(description = "Class of the java type to use when unmarshalling")
    private String unmarshalTypeName;

    @Metadata(label = "advanced", description = "Whether to require register classes", defaultValue = "true",
              javaType = "java.lang.Boolean")
    private String requireClassRegistration;

    @Metadata(label = "advanced", description = "Whether to use the threadsafe fury", defaultValue = "true",
              javaType = "java.lang.Boolean")
    private String threadSafe;

    @Metadata(label = "advanced", description = "Whether to auto-discover Fury from the registry", defaultValue = "true",
              javaType = "java.lang.Boolean")
    private String allowAutoWiredFury;

    public FuryDataFormat() {
        super("fury");
    }

    public FuryDataFormat(FuryDataFormat source) {
        super(source);
        this.unmarshalType = source.unmarshalType;
        this.unmarshalTypeName = source.unmarshalTypeName;
        this.requireClassRegistration = source.requireClassRegistration;
        this.threadSafe = source.threadSafe;
        this.allowAutoWiredFury = source.allowAutoWiredFury;
    }

    private FuryDataFormat(Builder builder) {
        this.unmarshalType = builder.unmarshalType;
        this.unmarshalTypeName = builder.unmarshalTypeName;
        this.requireClassRegistration = builder.requireClassRegistration;
        this.threadSafe = builder.threadSafe;
        this.allowAutoWiredFury = builder.allowAutoWiredFury;
    }

    @Override
    public FuryDataFormat copyDefinition() {
        return new FuryDataFormat(this);
    }

    public Class<?> getUnmarshalType() {
        return unmarshalType;
    }

    public void setUnmarshalType(final Class<?> unmarshalType) {
        this.unmarshalType = unmarshalType;
    }

    public String getUnmarshalTypeName() {
        return unmarshalTypeName;
    }

    public void setUnmarshalTypeName(final String unmarshalTypeName) {
        this.unmarshalTypeName = unmarshalTypeName;
    }

    public String getRequireClassRegistration() {
        return requireClassRegistration;
    }

    public void setRequireClassRegistration(String requireClassRegistration) {
        this.requireClassRegistration = requireClassRegistration;
    }

    public String getThreadSafe() {
        return threadSafe;
    }

    public void setThreadSafe(String threadSafe) {
        this.threadSafe = threadSafe;
    }

    public String getAllowAutoWiredFury() {
        return allowAutoWiredFury;
    }

    public void setAllowAutoWiredFury(String allowAutoWiredFury) {
        this.allowAutoWiredFury = allowAutoWiredFury;
    }

    /**
     * {@code Builder} is a specific builder for {@link FuryDataFormat}.
     */
    public static class Builder implements DataFormatBuilder<FuryDataFormat> {
        private Class<?> unmarshalType;
        private String unmarshalTypeName;
        private String requireClassRegistration;
        private String threadSafe;
        private String allowAutoWiredFury;

        public Builder unmarshalType(Class<?> value) {
            this.unmarshalType = value;
            return this;
        }

        public Builder unmarshalTypeName(String value) {
            this.unmarshalTypeName = value;
            return this;
        }

        public Builder requireClassRegistration(String value) {
            this.requireClassRegistration = value;
            return this;
        }

        public Builder threadSafe(String value) {
            this.threadSafe = value;
            return this;
        }

        public Builder allowAutoWiredFury(String value) {
            this.allowAutoWiredFury = value;
            return this;
        }

        @Override
        public FuryDataFormat end() {
            return new FuryDataFormat(this);
        }
    }
}
