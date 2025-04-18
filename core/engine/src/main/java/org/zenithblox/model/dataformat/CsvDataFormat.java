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

import java.util.List;

/**
 * Handle CSV (Comma Separated Values) payloads.
 */
@Metadata(firstVersion = "1.3.0", label = "dataformat,transformation,csv", title = "CSV")
public class CsvDataFormat extends DataFormatDefinition {

    // Format options
    @Metadata(label = "advanced")
    private String formatRef;
    @Metadata(label = "advanced", enums = "DEFAULT,EXCEL,INFORMIX_UNLOAD,INFORMIX_UNLOAD_CSV,MYSQL,RFC4180",
              defaultValue = "DEFAULT")
    private String formatName;
    @Metadata(label = "advanced", javaType = "java.lang.Boolean")
    private String commentMarkerDisabled;
    @Metadata(label = "advanced")
    private String commentMarker;
    private String delimiter;
    @Metadata(label = "advanced", javaType = "java.lang.Boolean")
    private String escapeDisabled;
    @Metadata(label = "advanced")
    private String escape;
    @Metadata(javaType = "java.lang.Boolean")
    private String headerDisabled;
    private List<String> header;
    @Metadata(javaType = "java.lang.Boolean")
    private String allowMissingColumnNames;
    @Metadata(javaType = "java.lang.Boolean")
    private String ignoreEmptyLines;
    @Metadata(javaType = "java.lang.Boolean")
    private String ignoreSurroundingSpaces;
    @Metadata(label = "advanced", javaType = "java.lang.Boolean")
    private String nullStringDisabled;
    @Metadata(label = "advanced")
    private String nullString;
    @Metadata(javaType = "java.lang.Boolean")
    private String quoteDisabled;
    private String quote;
    private String recordSeparatorDisabled;
    private String recordSeparator;
    @Metadata(javaType = "java.lang.Boolean")
    private String skipHeaderRecord;
    @Metadata(enums = "ALL,ALL_NON_NULL,MINIMAL,NON_NUMERIC,NONE")
    private String quoteMode;
    @Metadata(javaType = "java.lang.Boolean")
    private String ignoreHeaderCase;
    @Metadata(javaType = "java.lang.Boolean")
    private String trim;
    @Metadata(javaType = "java.lang.Boolean")
    private String trailingDelimiter;
    @Metadata(label = "advanced")
    private String marshallerFactoryRef;

    // Unmarshall options
    @Metadata(label = "advanced", javaType = "java.lang.Boolean")
    private String lazyLoad;
    @Metadata(javaType = "java.lang.Boolean")
    private String useMaps;
    @Metadata(javaType = "java.lang.Boolean")
    private String useOrderedMaps;
    @Metadata(label = "advanced")
    private String recordConverterRef;
    @Metadata(label = "advanced", javaType = "java.lang.Boolean")
    private String captureHeaderRecord;

    public CsvDataFormat() {
        super("csv");
    }

    protected CsvDataFormat(CsvDataFormat source) {
        super(source);
        this.formatRef = source.formatRef;
        this.formatName = source.formatName;
        this.commentMarkerDisabled = source.commentMarkerDisabled;
        this.commentMarker = source.commentMarker;
        this.delimiter = source.delimiter;
        this.escapeDisabled = source.escapeDisabled;
        this.escape = source.escape;
        this.headerDisabled = source.headerDisabled;
        this.header = source.header;
        this.allowMissingColumnNames = source.allowMissingColumnNames;
        this.ignoreEmptyLines = source.ignoreEmptyLines;
        this.ignoreSurroundingSpaces = source.ignoreSurroundingSpaces;
        this.nullStringDisabled = source.nullStringDisabled;
        this.nullString = source.nullString;
        this.quoteDisabled = source.quoteDisabled;
        this.quote = source.quote;
        this.recordSeparatorDisabled = source.recordSeparatorDisabled;
        this.recordSeparator = source.recordSeparator;
        this.skipHeaderRecord = source.skipHeaderRecord;
        this.quoteMode = source.quoteMode;
        this.ignoreHeaderCase = source.ignoreHeaderCase;
        this.trim = source.trim;
        this.trailingDelimiter = source.trailingDelimiter;
        this.marshallerFactoryRef = source.marshallerFactoryRef;
        this.lazyLoad = source.lazyLoad;
        this.useMaps = source.useMaps;
        this.useOrderedMaps = source.useOrderedMaps;
        this.recordConverterRef = source.recordConverterRef;
        this.captureHeaderRecord = source.captureHeaderRecord;
    }

    public CsvDataFormat(String delimiter) {
        this();
        setDelimiter(delimiter);
    }

    public CsvDataFormat(boolean lazyLoad) {
        this();
        setLazyLoad(Boolean.toString(lazyLoad));
    }

    private CsvDataFormat(Builder builder) {
        this();
        this.formatRef = builder.formatRef;
        this.formatName = builder.formatName;
        this.commentMarkerDisabled = builder.commentMarkerDisabled;
        this.commentMarker = builder.commentMarker;
        this.delimiter = builder.delimiter;
        this.escapeDisabled = builder.escapeDisabled;
        this.escape = builder.escape;
        this.headerDisabled = builder.headerDisabled;
        this.header = builder.header;
        this.allowMissingColumnNames = builder.allowMissingColumnNames;
        this.ignoreEmptyLines = builder.ignoreEmptyLines;
        this.ignoreSurroundingSpaces = builder.ignoreSurroundingSpaces;
        this.nullStringDisabled = builder.nullStringDisabled;
        this.nullString = builder.nullString;
        this.quoteDisabled = builder.quoteDisabled;
        this.quote = builder.quote;
        this.recordSeparatorDisabled = builder.recordSeparatorDisabled;
        this.recordSeparator = builder.recordSeparator;
        this.skipHeaderRecord = builder.skipHeaderRecord;
        this.quoteMode = builder.quoteMode;
        this.ignoreHeaderCase = builder.ignoreHeaderCase;
        this.trim = builder.trim;
        this.trailingDelimiter = builder.trailingDelimiter;
        this.marshallerFactoryRef = builder.marshallerFactoryRef;
        this.lazyLoad = builder.lazyLoad;
        this.useMaps = builder.useMaps;
        this.useOrderedMaps = builder.useOrderedMaps;
        this.recordConverterRef = builder.recordConverterRef;
        this.captureHeaderRecord = builder.captureHeaderRecord;
    }

    @Override
    public CsvDataFormat copyDefinition() {
        return new CsvDataFormat(this);
    }

    /**
     * Sets the implementation of the CsvMarshallerFactory interface which is able to customize
     * marshalling/unmarshalling behavior by extending CsvMarshaller or creating it from scratch.
     *
     * @param marshallerFactoryRef the <code>CsvMarshallerFactory</code> reference.
     */
    public void setMarshallerFactoryRef(String marshallerFactoryRef) {
        this.marshallerFactoryRef = marshallerFactoryRef;
    }

    /**
     * Returns the <code>CsvMarshallerFactory</code> reference.
     *
     * @return the <code>CsvMarshallerFactory</code> or <code>null</code> if none has been specified.
     */
    public String getMarshallerFactoryRef() {
        return marshallerFactoryRef;
    }

    public String getFormatRef() {
        return formatRef;
    }

    /**
     * The reference format to use, it will be updated with the other format options, the default value is
     * CSVFormat.DEFAULT
     */
    public void setFormatRef(String formatRef) {
        this.formatRef = formatRef;
    }

    public String getFormatName() {
        return formatName;
    }

    /**
     * The name of the format to use, the default value is CSVFormat.DEFAULT
     */
    public void setFormatName(String formatName) {
        this.formatName = formatName;
    }

    public String getCommentMarkerDisabled() {
        return commentMarkerDisabled;
    }

    /**
     * Disables the comment marker of the reference format.
     */
    public void setCommentMarkerDisabled(String commentMarkerDisabled) {
        this.commentMarkerDisabled = commentMarkerDisabled;
    }

    public String getCommentMarker() {
        return commentMarker;
    }

    /**
     * Sets the comment marker of the reference format.
     */
    public void setCommentMarker(String commentMarker) {
        this.commentMarker = commentMarker;
    }

    public String getDelimiter() {
        return delimiter;
    }

    /**
     * Sets the delimiter to use.
     * <p/>
     * The default value is , (comma)
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getEscapeDisabled() {
        return escapeDisabled;
    }

    /**
     * Use for disabling using escape character
     */
    public void setEscapeDisabled(String escapeDisabled) {
        this.escapeDisabled = escapeDisabled;
    }

    public String getEscape() {
        return escape;
    }

    /**
     * Sets the escape character to use
     */
    public void setEscape(String escape) {
        this.escape = escape;
    }

    /**
     * Use for disabling headers
     */
    public String getHeaderDisabled() {
        return headerDisabled;
    }

    public void setHeaderDisabled(String headerDisabled) {
        this.headerDisabled = headerDisabled;
    }

    public List<String> getHeader() {
        return header;
    }

    /**
     * To configure the CSV headers
     */
    public void setHeader(List<String> header) {
        this.header = header;
    }

    public String getAllowMissingColumnNames() {
        return allowMissingColumnNames;
    }

    /**
     * Whether to allow missing column names.
     */
    public void setAllowMissingColumnNames(String allowMissingColumnNames) {
        this.allowMissingColumnNames = allowMissingColumnNames;
    }

    public String getIgnoreEmptyLines() {
        return ignoreEmptyLines;
    }

    /**
     * Whether to ignore empty lines.
     */
    public void setIgnoreEmptyLines(String ignoreEmptyLines) {
        this.ignoreEmptyLines = ignoreEmptyLines;
    }

    public String getIgnoreSurroundingSpaces() {
        return ignoreSurroundingSpaces;
    }

    /**
     * Whether to ignore surrounding spaces
     */
    public void setIgnoreSurroundingSpaces(String ignoreSurroundingSpaces) {
        this.ignoreSurroundingSpaces = ignoreSurroundingSpaces;
    }

    public String getNullStringDisabled() {
        return nullStringDisabled;
    }

    /**
     * Used to disable null strings
     */
    public void setNullStringDisabled(String nullStringDisabled) {
        this.nullStringDisabled = nullStringDisabled;
    }

    public String getNullString() {
        return nullString;
    }

    /**
     * Sets the null string
     */
    public void setNullString(String nullString) {
        this.nullString = nullString;
    }

    public String getQuoteDisabled() {
        return quoteDisabled;
    }

    /**
     * Used to disable quotes
     */
    public void setQuoteDisabled(String quoteDisabled) {
        this.quoteDisabled = quoteDisabled;
    }

    public String getQuote() {
        return quote;
    }

    /**
     * Sets the quote to use which by default is double-quote character
     */
    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getRecordSeparatorDisabled() {
        return recordSeparatorDisabled;
    }

    /**
     * Used for disabling record separator
     */
    public void setRecordSeparatorDisabled(String recordSeparatorDisabled) {
        this.recordSeparatorDisabled = recordSeparatorDisabled;
    }

    public String getRecordSeparator() {
        return recordSeparator;
    }

    /**
     * Sets the record separator (aka new line) which by default is new line characters (CRLF)
     */
    public void setRecordSeparator(String recordSeparator) {
        this.recordSeparator = recordSeparator;
    }

    public String getSkipHeaderRecord() {
        return skipHeaderRecord;
    }

    /**
     * Whether to skip the header record in the output
     */
    public void setSkipHeaderRecord(String skipHeaderRecord) {
        this.skipHeaderRecord = skipHeaderRecord;
    }

    public String getQuoteMode() {
        return quoteMode;
    }

    /**
     * Sets the quote mode
     */
    public void setQuoteMode(String quoteMode) {
        this.quoteMode = quoteMode;
    }

    public String getLazyLoad() {
        return lazyLoad;
    }

    /**
     * Whether the unmarshalling should produce an iterator that reads the lines on the fly or if all the lines must be
     * read at one.
     */
    public void setLazyLoad(String lazyLoad) {
        this.lazyLoad = lazyLoad;
    }

    public String getUseMaps() {
        return useMaps;
    }

    /**
     * Whether the unmarshalling should produce maps (HashMap)for the lines values instead of lists. It requires to have
     * header (either defined or collected).
     */
    public void setUseMaps(String useMaps) {
        this.useMaps = useMaps;
    }

    public String getUseOrderedMaps() {
        return useOrderedMaps;
    }

    /**
     * Whether the unmarshalling should produce ordered maps (LinkedHashMap) for the lines values instead of lists. It
     * requires to have header (either defined or collected).
     */
    public void setUseOrderedMaps(String useOrderedMaps) {
        this.useOrderedMaps = useOrderedMaps;
    }

    public String getRecordConverterRef() {
        return recordConverterRef;
    }

    /**
     * Refers to a custom <tt>CsvRecordConverter</tt> to lookup from the registry to use.
     */
    public void setRecordConverterRef(String recordConverterRef) {
        this.recordConverterRef = recordConverterRef;
    }

    /**
     * Sets whether or not to trim leading and trailing blanks.
     */
    public void setTrim(String trim) {
        this.trim = trim;
    }

    public String getTrim() {
        return trim;
    }

    /**
     * Sets whether or not to ignore case when accessing header names.
     */
    public void setIgnoreHeaderCase(String ignoreHeaderCase) {
        this.ignoreHeaderCase = ignoreHeaderCase;
    }

    public String getIgnoreHeaderCase() {
        return ignoreHeaderCase;
    }

    /**
     * Sets whether or not to add a trailing delimiter.
     */
    public void setTrailingDelimiter(String trailingDelimiter) {
        this.trailingDelimiter = trailingDelimiter;
    }

    public String getTrailingDelimiter() {
        return trailingDelimiter;
    }

    public String getCaptureHeaderRecord() {
        return captureHeaderRecord;
    }

    /**
     * Whether the unmarshalling should capture the header record and store it in the message header
     */
    public void setCaptureHeaderRecord(String captureHeaderRecord) {
        this.captureHeaderRecord = captureHeaderRecord;
    }

    /**
     * {@code Builder} is a specific builder for {@link CsvDataFormat}.
     */
    public static class Builder implements DataFormatBuilder<CsvDataFormat> {
        private String formatRef;
        private String formatName;
        private String commentMarkerDisabled;
        private String commentMarker;
        private String delimiter;
        private String escapeDisabled;
        private String escape;
        private String headerDisabled;
        private List<String> header;
        private String allowMissingColumnNames;
        private String ignoreEmptyLines;
        private String ignoreSurroundingSpaces;
        private String nullStringDisabled;
        private String nullString;
        private String quoteDisabled;
        private String quote;
        private String recordSeparatorDisabled;
        private String recordSeparator;
        private String skipHeaderRecord;
        private String quoteMode;
        private String ignoreHeaderCase;
        private String trim;
        private String trailingDelimiter;
        private String marshallerFactoryRef;
        private String lazyLoad;
        private String useMaps;
        private String useOrderedMaps;
        private String recordConverterRef;
        private String captureHeaderRecord;

        /**
         * Sets the implementation of the CsvMarshallerFactory interface which is able to customize
         * marshalling/unmarshalling behavior by extending CsvMarshaller or creating it from scratch.
         *
         * @param marshallerFactoryRef the <code>CsvMarshallerFactory</code> reference.
         */
        public Builder marshallerFactoryRef(String marshallerFactoryRef) {
            this.marshallerFactoryRef = marshallerFactoryRef;
            return this;
        }

        /**
         * The reference format to use, it will be updated with the other format options, the default value is
         * CSVFormat.DEFAULT
         */
        public Builder formatRef(String formatRef) {
            this.formatRef = formatRef;
            return this;
        }

        /**
         * The name of the format to use, the default value is CSVFormat.DEFAULT
         */
        public Builder formatName(String formatName) {
            this.formatName = formatName;
            return this;
        }

        /**
         * Disables the comment marker of the reference format.
         */
        public Builder commentMarkerDisabled(String commentMarkerDisabled) {
            this.commentMarkerDisabled = commentMarkerDisabled;
            return this;
        }

        /**
         * Disables the comment marker of the reference format.
         */
        public Builder commentMarkerDisabled(boolean commentMarkerDisabled) {
            this.commentMarkerDisabled = Boolean.toString(commentMarkerDisabled);
            return this;
        }

        /**
         * Sets the comment marker of the reference format.
         */
        public Builder commentMarker(String commentMarker) {
            this.commentMarker = commentMarker;
            return this;
        }

        /**
         * Sets the delimiter to use.
         * <p/>
         * The default value is , (comma)
         */
        public Builder delimiter(String delimiter) {
            this.delimiter = delimiter;
            return this;
        }

        /**
         * Use for disabling using escape character
         */
        public Builder escapeDisabled(String escapeDisabled) {
            this.escapeDisabled = escapeDisabled;
            return this;
        }

        /**
         * Use for disabling using escape character
         */
        public Builder escapeDisabled(boolean escapeDisabled) {
            this.escapeDisabled = Boolean.toString(escapeDisabled);
            return this;
        }

        /**
         * Sets the escape character to use
         */
        public Builder escape(String escape) {
            this.escape = escape;
            return this;
        }

        public Builder headerDisabled(String headerDisabled) {
            this.headerDisabled = headerDisabled;
            return this;
        }

        public Builder headerDisabled(boolean headerDisabled) {
            this.headerDisabled = Boolean.toString(headerDisabled);
            return this;
        }

        /**
         * To configure the CSV headers
         */
        public Builder header(List<String> header) {
            this.header = header;
            return this;
        }

        /**
         * Whether to allow missing column names.
         */
        public Builder allowMissingColumnNames(String allowMissingColumnNames) {
            this.allowMissingColumnNames = allowMissingColumnNames;
            return this;
        }

        /**
         * Whether to allow missing column names.
         */
        public Builder allowMissingColumnNames(boolean allowMissingColumnNames) {
            this.allowMissingColumnNames = Boolean.toString(allowMissingColumnNames);
            return this;
        }

        /**
         * Whether to ignore empty lines.
         */
        public Builder ignoreEmptyLines(String ignoreEmptyLines) {
            this.ignoreEmptyLines = ignoreEmptyLines;
            return this;
        }

        /**
         * Whether to ignore empty lines.
         */
        public Builder ignoreEmptyLines(boolean ignoreEmptyLines) {
            this.ignoreEmptyLines = Boolean.toString(ignoreEmptyLines);
            return this;
        }

        /**
         * Whether to ignore surrounding spaces
         */
        public Builder ignoreSurroundingSpaces(String ignoreSurroundingSpaces) {
            this.ignoreSurroundingSpaces = ignoreSurroundingSpaces;
            return this;
        }

        /**
         * Whether to ignore surrounding spaces
         */
        public Builder ignoreSurroundingSpaces(boolean ignoreSurroundingSpaces) {
            this.ignoreSurroundingSpaces = Boolean.toString(ignoreSurroundingSpaces);
            return this;
        }

        /**
         * Used to disable null strings
         */
        public Builder nullStringDisabled(String nullStringDisabled) {
            this.nullStringDisabled = nullStringDisabled;
            return this;
        }

        /**
         * Used to disable null strings
         */
        public Builder nullStringDisabled(boolean nullStringDisabled) {
            this.nullStringDisabled = Boolean.toString(nullStringDisabled);
            return this;
        }

        /**
         * Sets the null string
         */
        public Builder nullString(String nullString) {
            this.nullString = nullString;
            return this;
        }

        /**
         * Used to disable quotes
         */
        public Builder quoteDisabled(String quoteDisabled) {
            this.quoteDisabled = quoteDisabled;
            return this;
        }

        /**
         * Used to disable quotes
         */
        public Builder quoteDisabled(boolean quoteDisabled) {
            this.quoteDisabled = Boolean.toString(quoteDisabled);
            return this;
        }

        /**
         * Sets the quote which by default is "
         */
        public Builder quote(String quote) {
            this.quote = quote;
            return this;
        }

        /**
         * Used for disabling record separator
         */
        public Builder recordSeparatorDisabled(String recordSeparatorDisabled) {
            this.recordSeparatorDisabled = recordSeparatorDisabled;
            return this;
        }

        /**
         * Sets the record separator (aka new line) which by default is new line characters (CRLF)
         */
        public Builder recordSeparator(String recordSeparator) {
            this.recordSeparator = recordSeparator;
            return this;
        }

        /**
         * Whether to skip the header record in the output
         */
        public Builder skipHeaderRecord(String skipHeaderRecord) {
            this.skipHeaderRecord = skipHeaderRecord;
            return this;
        }

        /**
         * Whether to skip the header record in the output
         */
        public Builder skipHeaderRecord(boolean skipHeaderRecord) {
            this.skipHeaderRecord = Boolean.toString(skipHeaderRecord);
            return this;
        }

        /**
         * Sets the quote mode
         */
        public Builder quoteMode(String quoteMode) {
            this.quoteMode = quoteMode;
            return this;
        }

        /**
         * Whether the unmarshalling should produce an iterator that reads the lines on the fly or if all the lines must
         * be read at one.
         */
        public Builder lazyLoad(String lazyLoad) {
            this.lazyLoad = lazyLoad;
            return this;
        }

        /**
         * Whether the unmarshalling should produce an iterator that reads the lines on the fly or if all the lines must
         * be read at one.
         */
        public Builder lazyLoad(boolean lazyLoad) {
            this.lazyLoad = Boolean.toString(lazyLoad);
            return this;
        }

        /**
         * Whether the unmarshalling should produce maps (HashMap)for the lines values instead of lists. It requires to
         * have header (either defined or collected).
         */
        public Builder useMaps(String useMaps) {
            this.useMaps = useMaps;
            return this;
        }

        /**
         * Whether the unmarshalling should produce maps (HashMap)for the lines values instead of lists. It requires to
         * have header (either defined or collected).
         */
        public Builder useMaps(boolean useMaps) {
            this.useMaps = Boolean.toString(useMaps);
            return this;
        }

        /**
         * Whether the unmarshalling should produce ordered maps (LinkedHashMap) for the lines values instead of lists.
         * It requires to have header (either defined or collected).
         */
        public Builder useOrderedMaps(String useOrderedMaps) {
            this.useOrderedMaps = useOrderedMaps;
            return this;
        }

        /**
         * Whether the unmarshalling should produce ordered maps (LinkedHashMap) for the lines values instead of lists.
         * It requires to have header (either defined or collected).
         */
        public Builder useOrderedMaps(boolean useOrderedMaps) {
            this.useOrderedMaps = Boolean.toString(useOrderedMaps);
            return this;
        }

        /**
         * Refers to a custom <tt>CsvRecordConverter</tt> to lookup from the registry to use.
         */
        public Builder recordConverterRef(String recordConverterRef) {
            this.recordConverterRef = recordConverterRef;
            return this;
        }

        /**
         * Sets whether or not to trim leading and trailing blanks.
         */
        public Builder trim(String trim) {
            this.trim = trim;
            return this;
        }

        /**
         * Sets whether or not to trim leading and trailing blanks.
         */
        public Builder trim(boolean trim) {
            this.trim = Boolean.toString(trim);
            return this;
        }

        /**
         * Sets whether or not to ignore case when accessing header names.
         */
        public Builder ignoreHeaderCase(String ignoreHeaderCase) {
            this.ignoreHeaderCase = ignoreHeaderCase;
            return this;
        }

        /**
         * Sets whether or not to ignore case when accessing header names.
         */
        public Builder ignoreHeaderCase(boolean ignoreHeaderCase) {
            this.ignoreHeaderCase = Boolean.toString(ignoreHeaderCase);
            return this;
        }

        /**
         * Sets whether or not to add a trailing delimiter.
         */
        public Builder trailingDelimiter(String trailingDelimiter) {
            this.trailingDelimiter = trailingDelimiter;
            return this;
        }

        /**
         * Sets whether or not to add a trailing delimiter.
         */
        public Builder trailingDelimiter(boolean trailingDelimiter) {
            this.trailingDelimiter = Boolean.toString(trailingDelimiter);
            return this;
        }

        /**
         * Whether the unmarshalling should capture the header record and store it in the message header
         */
        public Builder captureHeaderRecord(String captureHeaderRecord) {
            this.captureHeaderRecord = captureHeaderRecord;
            return this;
        }

        /**
         * Whether the unmarshalling should capture the header record and store it in the message header
         */
        public Builder captureHeaderRecord(boolean captureHeaderRecord) {
            this.captureHeaderRecord = Boolean.toString(captureHeaderRecord);
            return this;
        }

        @Override
        public CsvDataFormat end() {
            return new CsvDataFormat(this);
        }
    }
}
