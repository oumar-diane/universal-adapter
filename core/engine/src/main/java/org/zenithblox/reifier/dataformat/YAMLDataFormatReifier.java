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
import org.zenithblox.model.dataformat.YAMLDataFormat;
import org.zenithblox.model.dataformat.YAMLLibrary;
import org.zenithblox.model.dataformat.YAMLTypeFilterDefinition;
import org.zenithblox.model.dataformat.YAMLTypeFilterType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YAMLDataFormatReifier extends DataFormatReifier<YAMLDataFormat> {

    public YAMLDataFormatReifier(ZwangineContext zwangineContext, DataFormatDefinition definition) {
        super(zwangineContext, (YAMLDataFormat) definition);
    }

    @Override
    protected void prepareDataFormatConfig(Map<String, Object> properties) {
        if (definition.getLibrary() == YAMLLibrary.SnakeYAML) {
            configureSnakeDataFormat(properties);
        }
    }

    protected void configureSnakeDataFormat(Map<String, Object> properties) {
        properties.put("unmarshalType", or(definition.getUnmarshalType(), definition.getUnmarshalTypeName()));
        properties.put("classLoader", definition.getClassLoader());
        if (definition.getUseApplicationContextClassLoader() != null) {
            properties.put("useApplicationContextClassLoader", definition.getUseApplicationContextClassLoader());
        } else {
            properties.put("useApplicationContextClassLoader", "true");
        }
        properties.put("prettyFlow", definition.getPrettyFlow());
        properties.put("allowAnyType", definition.getAllowAnyType());
        properties.put("typeFilterDefinitions", getTypeFilterDefinitions());
        properties.put("constructor", definition.getConstructor());
        properties.put("representer", definition.getRepresenter());
        properties.put("dumperOptions", definition.getDumperOptions());
        properties.put("resolver", definition.getResolver());
        properties.put("maxAliasesForCollections", definition.getMaxAliasesForCollections());
        properties.put("allowRecursiveKeys", definition.getAllowRecursiveKeys());
    }

    private List<String> getTypeFilterDefinitions() {
        if (definition.getTypeFilters() != null && !definition.getTypeFilters().isEmpty()) {
            List<String> typeFilterDefinitions = new ArrayList<>(definition.getTypeFilters().size());
            for (YAMLTypeFilterDefinition definition : definition.getTypeFilters()) {
                String value = parseString(definition.getValue());
                if (value != null && !value.startsWith("type") && !value.startsWith("regexp")) {
                    YAMLTypeFilterType type = parse(YAMLTypeFilterType.class, definition.getType());
                    if (type == null) {
                        type = YAMLTypeFilterType.type;
                    }
                    value = type.name() + ":" + value;
                }
                if (value != null) {
                    typeFilterDefinitions.add(value);
                }
            }
            return typeFilterDefinitions;
        } else {
            return null;
        }
    }

}
