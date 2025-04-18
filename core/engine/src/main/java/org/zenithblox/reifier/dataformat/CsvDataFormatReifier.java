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
package org.zenithblox.reifier.dataformat;

import org.zenithblox.ZwangineContext;
import org.zenithblox.model.DataFormatDefinition;
import org.zenithblox.model.dataformat.CsvDataFormat;

import java.util.Map;
import java.util.StringJoiner;

public class CsvDataFormatReifier extends DataFormatReifier<CsvDataFormat> {

    public CsvDataFormatReifier(ZwangineContext zwangineContext, DataFormatDefinition definition) {
        super(zwangineContext, (CsvDataFormat) definition);
    }

    @Override
    protected void prepareDataFormatConfig(Map<String, Object> properties) {
        properties.put("format", asRef(definition.getFormatRef()));
        properties.put("formatName", definition.getFormatName());
        properties.put("commentMarkerDisabled", definition.getCommentMarkerDisabled());
        properties.put("commentMarker", definition.getCommentMarker());
        properties.put("delimiter", definition.getDelimiter());
        properties.put("escapeDisabled", definition.getEscapeDisabled());
        properties.put("escape", definition.getEscape());
        properties.put("headerDisabled", definition.getHeaderDisabled());
        // in the model header is a List<String> however it should ideally have
        // just been a comma separated String so its configurable in uris
        // so we join the List into a String in the reifier so the configurer can
        // use the value as-is
        if (definition.getHeader() != null && !definition.getHeader().isEmpty()) {
            StringJoiner sj = new StringJoiner(",");
            for (String s : definition.getHeader()) {
                sj.add(s);
            }
            properties.put("header", sj.toString());
        }
        properties.put("allowMissingColumnNames", definition.getAllowMissingColumnNames());
        properties.put("ignoreEmptyLines", definition.getIgnoreEmptyLines());
        properties.put("ignoreSurroundingSpaces", definition.getIgnoreSurroundingSpaces());
        properties.put("nullStringDisabled", definition.getNullStringDisabled());
        properties.put("nullString", definition.getNullString());
        properties.put("quoteDisabled", definition.getQuoteDisabled());
        properties.put("quote", definition.getQuote());
        properties.put("recordSeparatorDisabled", definition.getRecordSeparatorDisabled());
        properties.put("recordSeparator", definition.getRecordSeparator());
        properties.put("skipHeaderRecord", definition.getSkipHeaderRecord());
        properties.put("quoteMode", definition.getQuoteMode());
        properties.put("trim", definition.getTrim());
        properties.put("ignoreHeaderCase", definition.getIgnoreHeaderCase());
        properties.put("trailingDelimiter", definition.getTrailingDelimiter());
        properties.put("lazyLoad", definition.getLazyLoad());
        properties.put("useMaps", definition.getUseMaps());
        properties.put("useOrderedMaps", definition.getUseOrderedMaps());
        properties.put("captureHeaderRecord", definition.getCaptureHeaderRecord());
        properties.put("recordConverter", asRef(definition.getRecordConverterRef()));
        properties.put("marshallerFactory", asRef(definition.getMarshallerFactoryRef()));
    }

}
