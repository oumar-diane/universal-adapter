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
import org.zenithblox.model.dataformat.JsonDataFormat;
import org.zenithblox.model.dataformat.JsonLibrary;

import java.util.Map;

public class JsonDataFormatReifier extends DataFormatReifier<JsonDataFormat> {

    public JsonDataFormatReifier(ZwangineContext zwangineContext, DataFormatDefinition definition) {
        super(zwangineContext, (JsonDataFormat) definition);
    }

    @Override
    protected void prepareDataFormatConfig(Map<String, Object> properties) {
        properties.put("objectMapper", asRef(definition.getObjectMapper()));
        if (definition.getLibrary() == JsonLibrary.Jackson) {
            if (definition.getUseDefaultObjectMapper() == null) {
                // default true
                properties.put("useDefaultObjectMapper", "true");
            } else {
                properties.put("useDefaultObjectMapper", definition.getUseDefaultObjectMapper());
            }
            properties.put("autoDiscoverObjectMapper", definition.getAutoDiscoverObjectMapper());
            properties.put("jsonView", or(definition.getJsonView(), definition.getJsonViewTypeName()));
        } else {
            properties.put("jsonView", definition.getJsonView());
        }
        properties.put("unmarshalType", or(definition.getUnmarshalType(), definition.getUnmarshalTypeName()));
        properties.put("prettyPrint", definition.getPrettyPrint());
        properties.put("include", definition.getInclude());
        properties.put("allowJmsType", definition.getAllowJmsType());
        properties.put("collectionType", or(definition.getCollectionType(), definition.getCollectionTypeName()));
        properties.put("useList", definition.getUseList());
        properties.put("combineUnicodeSurrogates", definition.getCombineUnicodeSurrogates());
        properties.put("moduleClassNames", definition.getModuleClassNames());
        properties.put("moduleRefs", definition.getModuleRefs());
        properties.put("enableFeatures", definition.getEnableFeatures());
        properties.put("disableFeatures", definition.getDisableFeatures());
        properties.put("allowUnmarshallType", definition.getAllowUnmarshallType());
        if (definition.getLibrary() == JsonLibrary.Jackson) {
            properties.put("schemaResolver", asRef(definition.getSchemaResolver()));
            properties.put("autoDiscoverSchemaResolver", definition.getAutoDiscoverSchemaResolver());
            properties.put("namingStrategy", definition.getNamingStrategy());
            properties.put("timezone", definition.getTimezone());
        }
        if (definition.getLibrary() == JsonLibrary.Fastjson || definition.getLibrary() == JsonLibrary.Gson) {
            properties.put("dateFormatPattern", definition.getDateFormatPattern());
        }
    }

}
