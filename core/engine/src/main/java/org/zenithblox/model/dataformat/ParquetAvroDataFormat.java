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
 * Parquet Avro serialization and de-serialization.
 */
@Metadata(firstVersion = "4.0.0", label = "dataformat,transformation,file", title = "Parquet File")
public class ParquetAvroDataFormat extends DataFormatDefinition {

    @Metadata(defaultValue = "GZIP", enums = "UNCOMPRESSED,SNAPPY,GZIP,LZO,BROTLI,LZ4,ZSTD,LZ4_RAW")
    private String compressionCodecName;
    private Class<?> unmarshalType;
    private String unmarshalTypeName;
    @Metadata(javaType = "java.lang.Boolean")
    private String lazyLoad;

    public ParquetAvroDataFormat() {
        super("parquetAvro");
    }

    protected ParquetAvroDataFormat(ParquetAvroDataFormat source) {
        super(source);
        this.compressionCodecName = source.compressionCodecName;
        this.unmarshalTypeName = source.unmarshalTypeName;
        this.unmarshalType = source.unmarshalType;
        this.lazyLoad = source.lazyLoad;
    }

    public ParquetAvroDataFormat(String unmarshalTypeName) {
        this();
        setUnmarshalTypeName(unmarshalTypeName);
    }

    public ParquetAvroDataFormat(Class<?> unmarshalType) {
        this();
        this.unmarshalType = unmarshalType;
    }

    public ParquetAvroDataFormat(boolean lazyLoad) {
        this();
        setLazyLoad(Boolean.toString(lazyLoad));
    }

    private ParquetAvroDataFormat(Builder builder) {
        this();
        this.compressionCodecName = builder.compressionCodecName;
        this.unmarshalTypeName = builder.unmarshalTypeName;
        this.unmarshalType = builder.unmarshalType;
        this.lazyLoad = builder.lazyLoad;
    }

    @Override
    public ParquetAvroDataFormat copyDefinition() {
        return new ParquetAvroDataFormat(this);
    }

    /**
     * Compression codec to use when marshalling.
     */
    public void setCompressionCodecName(String compressionCodecName) {
        this.compressionCodecName = compressionCodecName;
    }

    public String getCompressionCodecName() {
        return compressionCodecName;
    }

    public Class<?> getUnmarshalType() {
        return unmarshalType;
    }

    /**
     * Class to use when unmarshalling.
     */
    public void setUnmarshalType(Class<?> unmarshalType) {
        this.unmarshalType = unmarshalType;
    }

    /**
     * Class to use when (un)marshalling. If omitted, parquet files are converted into Avro's GenericRecords for
     * unmarshalling and input objects are assumed as GenericRecords for marshalling.
     */
    public void setUnmarshalTypeName(String unmarshalTypeName) {
        this.unmarshalTypeName = unmarshalTypeName;
    }

    public String getUnmarshalTypeName() {
        return unmarshalTypeName;
    }

    public String getLazyLoad() {
        return lazyLoad;
    }

    /**
     * Whether the unmarshalling should produce an iterator of records or read all the records at once.
     */
    public void setLazyLoad(String lazyLoad) {
        this.lazyLoad = lazyLoad;
    }

    /**
     * {@code Builder} is a specific builder for {@link ParquetAvroDataFormat}.
     */
    public static class Builder implements DataFormatBuilder<ParquetAvroDataFormat> {

        private String compressionCodecName;
        private Class<?> unmarshalType;
        private String unmarshalTypeName;
        private String lazyLoad;

        /**
         * Compression codec to use when marshalling.
         */
        public Builder compressionCodecName(String compressionCodecName) {
            this.compressionCodecName = compressionCodecName;
            return this;
        }

        /**
         * Class to use when unmarshalling.
         */
        public Builder unmarshalTypeName(String unmarshalTypeName) {
            this.unmarshalTypeName = unmarshalTypeName;
            return this;
        }

        /**
         * Class to use when unmarshalling.
         */
        public Builder unmarshalType(Class<?> unmarshalType) {
            this.unmarshalType = unmarshalType;
            return this;
        }

        /**
         * Whether the unmarshalling should produce an iterator of records or read all the records at once.
         */
        public Builder lazyLoad(String lazyLoad) {
            this.lazyLoad = lazyLoad;
            return this;
        }

        /**
         * Whether the unmarshalling should produce an iterator of records or read all the records at once.
         */
        public Builder lazyLoad(boolean lazyLoad) {
            this.lazyLoad = Boolean.toString(lazyLoad);
            return this;
        }

        @Override
        public ParquetAvroDataFormat end() {
            return new ParquetAvroDataFormat(this);
        }
    }

}
