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
 * Compress and decompress streams using <code>java.util.zip.Deflater</code> and <code>java.util.zip.Inflater</code>.
 */
@Metadata(firstVersion = "2.12.0", label = "dataformat,transformation", title = "Zip Deflater")
public class ZipDeflaterDataFormat extends DataFormatDefinition {

    @Metadata(javaType = "java.lang.Integer", defaultValue = "-1", enums = "-1,0,1,2,3,4,5,6,7,8,9")
    private String compressionLevel;

    public ZipDeflaterDataFormat() {
        super("zipDeflater");
    }

    protected ZipDeflaterDataFormat(ZipDeflaterDataFormat source) {
        super(source);
        this.compressionLevel = source.compressionLevel;
    }

    private ZipDeflaterDataFormat(Builder builder) {
        this();
        this.compressionLevel = builder.compressionLevel;
    }

    @Override
    public ZipDeflaterDataFormat copyDefinition() {
        return new ZipDeflaterDataFormat(this);
    }

    public String getCompressionLevel() {
        return compressionLevel;
    }

    /**
     * To specify a specific compression between 0-9. -1 is default compression, 0 is no compression, and 9 is the best
     * compression.
     */
    public void setCompressionLevel(String compressionLevel) {
        this.compressionLevel = compressionLevel;
    }

    /**
     * {@code Builder} is a specific builder for {@link ZipDeflaterDataFormat}.
     */
    public static class Builder implements DataFormatBuilder<ZipDeflaterDataFormat> {

        private String compressionLevel;

        /**
         * To specify a specific compression between 0-9. -1 is default compression, 0 is no compression, and 9 is the
         * best compression.
         */
        public Builder compressionLevel(String compressionLevel) {
            this.compressionLevel = compressionLevel;
            return this;
        }

        /**
         * To specify a specific compression between 0-9. -1 is default compression, 0 is no compression, and 9 is the
         * best compression.
         */
        public Builder compressionLevel(int compressionLevel) {
            this.compressionLevel = Integer.toString(compressionLevel);
            return this;
        }

        @Override
        public ZipDeflaterDataFormat end() {
            return new ZipDeflaterDataFormat(this);
        }
    }
}
