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
import org.zenithblox.model.dataformat.MimeMultipartDataFormat;

import java.util.Map;

public class MimeMultipartDataFormatReifier extends DataFormatReifier<MimeMultipartDataFormat> {

    public MimeMultipartDataFormatReifier(ZwangineContext zwangineContext, DataFormatDefinition definition) {
        super(zwangineContext, (MimeMultipartDataFormat) definition);
    }

    @Override
    protected void prepareDataFormatConfig(Map<String, Object> properties) {
        properties.put("multipartSubType", definition.getMultipartSubType());
        properties.put("multipartWithoutAttachment", definition.getMultipartWithoutAttachment());
        properties.put("headersInline", definition.getHeadersInline());
        properties.put("includeHeaders", definition.getIncludeHeaders());
        properties.put("binaryContent", definition.getBinaryContent());
    }

}
