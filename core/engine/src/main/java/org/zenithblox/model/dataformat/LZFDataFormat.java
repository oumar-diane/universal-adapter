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
 * Compress and decompress streams using LZF deflate algorithm.
 */
@Metadata(firstVersion = "2.17.0", label = "dataformat,transformation", title = "LZF Deflate Compression")
public class LZFDataFormat extends DataFormatDefinition {

    @Metadata(javaType = "java.lang.Boolean")
    private String usingParallelCompression;

    public LZFDataFormat() {
        super("lzf");
    }

    protected LZFDataFormat(LZFDataFormat source) {
        super(source);
        this.usingParallelCompression = source.usingParallelCompression;
    }

    private LZFDataFormat(Builder builder) {
        this();
        this.usingParallelCompression = builder.usingParallelCompression;
    }

    @Override
    public LZFDataFormat copyDefinition() {
        return new LZFDataFormat(this);
    }

    public String getUsingParallelCompression() {
        return usingParallelCompression;
    }

    /**
     * Enable encoding (compress) using multiple processing cores.
     */
    public void setUsingParallelCompression(String usingParallelCompression) {
        this.usingParallelCompression = usingParallelCompression;
    }

    /**
     * {@code Builder} is a specific builder for {@link LZFDataFormat}.
     */
    public static class Builder implements DataFormatBuilder<LZFDataFormat> {

        private String usingParallelCompression;

        /**
         * Enable encoding (compress) using multiple processing cores.
         */
        public Builder usingParallelCompression(String usingParallelCompression) {
            this.usingParallelCompression = usingParallelCompression;
            return this;
        }

        /**
         * Enable encoding (compress) using multiple processing cores.
         */
        public Builder usingParallelCompression(boolean usingParallelCompression) {
            this.usingParallelCompression = Boolean.toString(usingParallelCompression);
            return this;
        }

        @Override
        public LZFDataFormat end() {
            return new LZFDataFormat(this);
        }
    }
}
