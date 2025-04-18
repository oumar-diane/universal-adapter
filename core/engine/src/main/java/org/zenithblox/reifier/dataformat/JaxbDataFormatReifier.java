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
import org.zenithblox.model.dataformat.JaxbDataFormat;

import java.util.Map;

public class JaxbDataFormatReifier extends DataFormatReifier<JaxbDataFormat> {

    public JaxbDataFormatReifier(ZwangineContext zwangineContext, DataFormatDefinition definition) {
        super(zwangineContext, (JaxbDataFormat) definition);
    }

    @Override
    protected void prepareDataFormatConfig(Map<String, Object> properties) {
        if (definition.getPrettyPrint() != null) {
            properties.put("prettyPrint", definition.getPrettyPrint());
        } else {
            // is default true
            properties.put("prettyPrint", "true");
        }
        if (definition.getObjectFactory() != null) {
            properties.put("objectFactory", definition.getObjectFactory());
        } else {
            // is default true
            properties.put("objectFactory", "true");
        }
        if (definition.getIgnoreJAXBElement() != null) {
            properties.put("ignoreJAXBElement", definition.getIgnoreJAXBElement());
        } else {
            // is default true
            properties.put("ignoreJAXBElement", "true");
        }
        properties.put("mustBeJAXBElement", definition.getMustBeJAXBElement());
        properties.put("filterNonXmlChars", definition.getFilterNonXmlChars());
        properties.put("fragment", definition.getFragment());
        properties.put("contextPath", definition.getContextPath());
        properties.put("contextPathIsClassName", definition.getContextPathIsClassName());
        properties.put("partClass", definition.getPartClass());
        properties.put("partNamespace", definition.getPartNamespace());
        properties.put("encoding", definition.getEncoding());
        properties.put("namespacePrefix", asRef(definition.getNamespacePrefixRef()));
        properties.put("schema", definition.getSchema());
        properties.put("schemaSeverityLevel", definition.getSchemaSeverityLevel());
        properties.put("xmlStreamWriterWrapper", definition.getXmlStreamWriterWrapper());
        properties.put("schemaLocation", definition.getSchemaLocation());
        properties.put("noNamespaceSchemaLocation", definition.getNoNamespaceSchemaLocation());
        properties.put("jaxbProviderProperties", definition.getJaxbProviderProperties());
        if (definition.getContentTypeHeader() != null) {
            properties.put("contentTypeHeader", definition.getContentTypeHeader());
        } else {
            // is default true
            properties.put("contentTypeHeader", "true");
        }
        properties.put("accessExternalSchemaProtocols", definition.getAccessExternalSchemaProtocols());
    }

}
