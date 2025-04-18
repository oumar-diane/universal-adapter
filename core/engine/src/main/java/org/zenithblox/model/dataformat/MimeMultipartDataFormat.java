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
 * Marshal Zwangine messages with attachments into MIME-Multipart messages and back.
 */
@Metadata(firstVersion = "2.17.0", label = "dataformat,transformation", title = "MIME Multipart")
public class MimeMultipartDataFormat extends DataFormatDefinition {

    @Metadata(defaultValue = "mixed")
    private String multipartSubType = "mixed";
    @Metadata(javaType = "java.lang.Boolean")
    private String multipartWithoutAttachment;
    @Metadata(javaType = "java.lang.Boolean")
    private String headersInline;
    private String includeHeaders;
    @Metadata(javaType = "java.lang.Boolean")
    private String binaryContent;

    public MimeMultipartDataFormat() {
        super("mimeMultipart");
    }

    protected MimeMultipartDataFormat(MimeMultipartDataFormat source) {
        super(source);
        this.multipartSubType = source.multipartSubType;
        this.multipartWithoutAttachment = source.multipartWithoutAttachment;
        this.headersInline = source.headersInline;
        this.includeHeaders = source.includeHeaders;
        this.binaryContent = source.binaryContent;
    }

    private MimeMultipartDataFormat(Builder builder) {
        this();
        this.multipartSubType = builder.multipartSubType;
        this.multipartWithoutAttachment = builder.multipartWithoutAttachment;
        this.headersInline = builder.headersInline;
        this.includeHeaders = builder.includeHeaders;
        this.binaryContent = builder.binaryContent;
    }

    @Override
    public MimeMultipartDataFormat copyDefinition() {
        return new MimeMultipartDataFormat(this);
    }

    public String getMultipartSubType() {
        return multipartSubType;
    }

    /**
     * Specify the subtype of the MIME Multipart.
     * <p>
     * Default is mixed.
     */
    public void setMultipartSubType(String multipartSubType) {
        this.multipartSubType = multipartSubType;
    }

    public String getMultipartWithoutAttachment() {
        return multipartWithoutAttachment;
    }

    /**
     * Defines whether a message without attachment is also marshaled into a MIME Multipart (with only one body part).
     * <p>
     * Default is false.
     */
    public void setMultipartWithoutAttachment(String multipartWithoutAttachment) {
        this.multipartWithoutAttachment = multipartWithoutAttachment;
    }

    public String getHeadersInline() {
        return headersInline;
    }

    /**
     * Defines whether the MIME-Multipart headers are part of the message body (true) or are set as Zwangine headers
     * (false).
     * <p>
     * Default is false.
     */
    public void setHeadersInline(String headersInline) {
        this.headersInline = headersInline;
    }

    public String getBinaryContent() {
        return binaryContent;
    }

    /**
     * A regex that defines which Zwangine headers are also included as MIME headers into the MIME multipart. This will
     * only work if headersInline is set to true.
     * <p>
     * Default is to include no headers
     */
    public void setIncludeHeaders(String includeHeaders) {
        this.includeHeaders = includeHeaders;
    }

    public String getIncludeHeaders() {
        return includeHeaders;
    }

    /**
     * Defines whether the content of binary parts in the MIME multipart is binary (true) or Base-64 encoded (false)
     * <p>
     * Default is false.
     */
    public void setBinaryContent(String binaryContent) {
        this.binaryContent = binaryContent;
    }

    /**
     * {@code Builder} is a specific builder for {@link MimeMultipartDataFormat}.
     */
    public static class Builder implements DataFormatBuilder<MimeMultipartDataFormat> {

        private String multipartSubType = "mixed";
        private String multipartWithoutAttachment;
        private String headersInline;
        private String includeHeaders;
        private String binaryContent;

        /**
         * Specify the subtype of the MIME Multipart.
         * <p>
         * Default is mixed.
         */
        public Builder multipartSubType(String multipartSubType) {
            this.multipartSubType = multipartSubType;
            return this;
        }

        /**
         * Defines whether a message without attachment is also marshaled into a MIME Multipart (with only one body
         * part).
         * <p>
         * Default is false.
         */
        public Builder multipartWithoutAttachment(String multipartWithoutAttachment) {
            this.multipartWithoutAttachment = multipartWithoutAttachment;
            return this;
        }

        /**
         * Defines whether a message without attachment is also marshaled into a MIME Multipart (with only one body
         * part).
         * <p>
         * Default is false.
         */
        public Builder multipartWithoutAttachment(boolean multipartWithoutAttachment) {
            this.multipartWithoutAttachment = Boolean.toString(multipartWithoutAttachment);
            return this;
        }

        /**
         * Defines whether the MIME-Multipart headers are part of the message body (true) or are set as Zwangine headers
         * (false).
         * <p>
         * Default is false.
         */
        public Builder headersInline(String headersInline) {
            this.headersInline = headersInline;
            return this;
        }

        /**
         * Defines whether the MIME-Multipart headers are part of the message body (true) or are set as Zwangine headers
         * (false).
         * <p>
         * Default is false.
         */
        public Builder headersInline(boolean headersInline) {
            this.headersInline = Boolean.toString(headersInline);
            return this;
        }

        /**
         * A regex that defines which Zwangine headers are also included as MIME headers into the MIME multipart. This will
         * only work if headersInline is set to true.
         * <p>
         * Default is to include no headers
         */
        public Builder includeHeaders(String includeHeaders) {
            this.includeHeaders = includeHeaders;
            return this;
        }

        /**
         * Defines whether the content of binary parts in the MIME multipart is binary (true) or Base-64 encoded (false)
         * <p>
         * Default is false.
         */
        public Builder binaryContent(String binaryContent) {
            this.binaryContent = binaryContent;
            return this;
        }

        /**
         * Defines whether the content of binary parts in the MIME multipart is binary (true) or Base-64 encoded (false)
         * <p>
         * Default is false.
         */
        public Builder binaryContent(boolean binaryContent) {
            this.binaryContent = Boolean.toString(binaryContent);
            return this;
        }

        @Override
        public MimeMultipartDataFormat end() {
            return new MimeMultipartDataFormat(this);
        }
    }
}
