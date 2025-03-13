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
package org.zenithblox.model;

import org.zenithblox.Expression;
import org.zenithblox.builder.ExpressionBuilder;
import org.zenithblox.spi.Metadata;

import java.util.*;
import java.util.Map.Entry;

/**
 * Allows setting multiple headers on the message at the same time.
 */
@Metadata(label = "eip,transformation")
public class SetHeadersDefinition extends ProcessorDefinition<SetHeadersDefinition> {

    /**
     * This is provided to support XML and YAML DSL
     */
    private List<SetHeaderDefinition> headers = new ArrayList<>();

    public SetHeadersDefinition() {
    }

    protected SetHeadersDefinition(SetHeadersDefinition source) {
        super(source);
        this.headers = ProcessorDefinitionHelper.deepCopyDefinitions(source.headers);
    }

    @Override
    public SetHeadersDefinition copyDefinition() {
        return new SetHeadersDefinition(this);
    }

    /**
     * Allow setting multiple headers using a single expression.
     */
    public SetHeadersDefinition(Object... headerNamesAndExprs) {
        createSetHeaderDefinitions(headerNamesAndExprs);
    }

    private void createSetHeaderDefinitions(Object[] headerNamesAndExprs) {
        if (headerNamesAndExprs.length == 1 && headerNamesAndExprs[0] instanceof Map) {
            createHeadersFromMap((Map<?, ?>) headerNamesAndExprs[0]);
        } else if (headerNamesAndExprs.length % 2 != 0) {
            throw new IllegalArgumentException("Must be a Map or have an even number of arguments!");
        } else {
            for (int i = 0; i < headerNamesAndExprs.length; i += 2) {
                addHeader(headerNamesAndExprs[i], headerNamesAndExprs[i + 1]);
            }
        }
    }

    private void addHeader(Object key, Object value) {
        if (!(key instanceof String)) {
            throw new IllegalArgumentException("Keys must be Strings");
        }
        if (!(value instanceof Expression)) {
            // Assume it's a constant of some kind
            value = ExpressionBuilder.constantExpression(value);
        }
        headers.add(new SetHeaderDefinition((String) key, (Expression) value));
    }

    private void createHeadersFromMap(Map<?, ?> headerMap) {
        for (Entry<?, ?> entry : headerMap.entrySet()) {
            addHeader(entry.getKey(), entry.getValue());
        }
    }

    public List<SetHeaderDefinition> getHeaders() {
        return headers;
    }

    public void setHeaders(List<SetHeaderDefinition> headers) {
        this.headers = headers;
    }

    @Override
    public String getLabel() {
        return "setHeaders[" + getHeaderNames() + "]";
    }

    private String getHeaderNames() {
        StringJoiner sb = new StringJoiner(",");
        for (SetHeaderDefinition def : headers) {
            sb.add(def.getName());
        }
        return sb.toString();
    }

    @Override
    public String getShortName() {
        return "setHeaders";
    }

    @Override
    public List<ProcessorDefinition<?>> getOutputs() {
        return Collections.emptyList();
    }

}
