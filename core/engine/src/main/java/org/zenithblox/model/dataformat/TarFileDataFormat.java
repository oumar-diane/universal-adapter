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
 * Archive files into tarballs or extract files from tarballs.
 */
@Metadata(firstVersion = "2.16.0", label = "dataformat,transformation,file", title = "Tar File")
public class TarFileDataFormat extends DataFormatDefinition {

    @Metadata(javaType = "java.lang.Boolean")
    private String usingIterator;
    @Metadata(javaType = "java.lang.Boolean")
    private String allowEmptyDirectory;
    @Metadata(javaType = "java.lang.Boolean")
    private String preservePathElements;
    @Metadata(label = "advanced", javaType = "java.lang.Long", defaultValue = "1073741824")
    private String maxDecompressedSize;

    public TarFileDataFormat() {
        super("tarFile");
    }

    protected TarFileDataFormat(TarFileDataFormat source) {
        super(source);
        this.usingIterator = source.usingIterator;
        this.allowEmptyDirectory = source.allowEmptyDirectory;
        this.preservePathElements = source.preservePathElements;
        this.maxDecompressedSize = source.maxDecompressedSize;
    }

    private TarFileDataFormat(Builder builder) {
        this.usingIterator = builder.usingIterator;
        this.allowEmptyDirectory = builder.allowEmptyDirectory;
        this.preservePathElements = builder.preservePathElements;
        this.maxDecompressedSize = builder.maxDecompressedSize;
    }

    @Override
    public TarFileDataFormat copyDefinition() {
        return new TarFileDataFormat(this);
    }

    public String getUsingIterator() {
        return usingIterator;
    }

    public String getAllowEmptyDirectory() {
        return allowEmptyDirectory;
    }

    public String getPreservePathElements() {
        return preservePathElements;
    }

    public String getMaxDecompressedSize() {
        return maxDecompressedSize;
    }

    /**
     * If the tar file has more than one entry, the setting this option to true, allows working with the splitter EIP,
     * to split the data using an iterator in a streaming mode.
     */
    public void setUsingIterator(String usingIterator) {
        this.usingIterator = usingIterator;
    }

    /**
     * If the tar file has more than one entry, setting this option to true, allows to get the iterator even if the
     * directory is empty
     */
    public void setAllowEmptyDirectory(String allowEmptyDirectory) {
        this.allowEmptyDirectory = allowEmptyDirectory;
    }

    /**
     * If the file name contains path elements, setting this option to true, allows the path to be maintained in the tar
     * file.
     */
    public void setPreservePathElements(String preservePathElements) {
        this.preservePathElements = preservePathElements;
    }

    /**
     * Set the maximum decompressed size of a tar file (in bytes). The default value if not specified corresponds to 1
     * gigabyte. An IOException will be thrown if the decompressed size exceeds this amount. Set to -1 to disable
     * setting a maximum decompressed size.
     *
     * @param maxDecompressedSize the maximum decompressed size of a tar file (in bytes)
     */
    public void setMaxDecompressedSize(String maxDecompressedSize) {
        this.maxDecompressedSize = maxDecompressedSize;
    }

    /**
     * {@code Builder} is a specific builder for {@link TarFileDataFormat}.
     */
    public static class Builder implements DataFormatBuilder<TarFileDataFormat> {

        private String usingIterator;
        private String allowEmptyDirectory;
        private String preservePathElements;
        private String maxDecompressedSize;

        /**
         * If the tar file has more than one entry, the setting this option to true, allows working with the splitter
         * EIP, to split the data using an iterator in a streaming mode.
         */
        public Builder usingIterator(String usingIterator) {
            this.usingIterator = usingIterator;
            return this;
        }

        /**
         * If the tar file has more than one entry, the setting this option to true, allows working with the splitter
         * EIP, to split the data using an iterator in a streaming mode.
         */
        public Builder usingIterator(boolean usingIterator) {
            this.usingIterator = Boolean.toString(usingIterator);
            return this;
        }

        /**
         * If the tar file has more than one entry, setting this option to true, allows to get the iterator even if the
         * directory is empty
         */
        public Builder allowEmptyDirectory(String allowEmptyDirectory) {
            this.allowEmptyDirectory = allowEmptyDirectory;
            return this;
        }

        /**
         * If the tar file has more than one entry, setting this option to true, allows to get the iterator even if the
         * directory is empty
         */
        public Builder allowEmptyDirectory(boolean allowEmptyDirectory) {
            this.allowEmptyDirectory = Boolean.toString(allowEmptyDirectory);
            return this;
        }

        /**
         * If the file name contains path elements, setting this option to true, allows the path to be maintained in the
         * tar file.
         */
        public Builder preservePathElements(String preservePathElements) {
            this.preservePathElements = preservePathElements;
            return this;
        }

        /**
         * If the file name contains path elements, setting this option to true, allows the path to be maintained in the
         * tar file.
         */
        public Builder preservePathElements(boolean preservePathElements) {
            this.preservePathElements = Boolean.toString(preservePathElements);
            return this;
        }

        /**
         * Set the maximum decompressed size of a tar file (in bytes). The default value if not specified corresponds to
         * 1 gigabyte. An IOException will be thrown if the decompressed size exceeds this amount. Set to -1 to disable
         * setting a maximum decompressed size.
         *
         * @param maxDecompressedSize the maximum decompressed size of a tar file (in bytes)
         */
        public Builder maxDecompressedSize(String maxDecompressedSize) {
            this.maxDecompressedSize = maxDecompressedSize;
            return this;
        }

        /**
         * Set the maximum decompressed size of a tar file (in bytes). The default value if not specified corresponds to
         * 1 gigabyte. An IOException will be thrown if the decompressed size exceeds this amount. Set to -1 to disable
         * setting a maximum decompressed size.
         *
         * @param maxDecompressedSize the maximum decompressed size of a tar file (in bytes)
         */
        public Builder maxDecompressedSize(long maxDecompressedSize) {
            this.maxDecompressedSize = Long.toString(maxDecompressedSize);
            return this;
        }

        @Override
        public TarFileDataFormat end() {
            return new TarFileDataFormat(this);
        }
    }
}
