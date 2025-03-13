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
package org.zenithblox.reifier.language;

import org.zenithblox.ZwangineContext;
import org.zenithblox.Expression;
import org.zenithblox.Predicate;
import org.zenithblox.model.language.ExpressionDefinition;
import org.zenithblox.model.language.XQueryExpression;
import org.zenithblox.spi.Language;
import org.zenithblox.spi.NamespaceAware;

public class XQueryExpressionReifier extends SingleInputTypedExpressionReifier<XQueryExpression> {

    public XQueryExpressionReifier(ZwangineContext zwangineContext, ExpressionDefinition definition) {
        super(zwangineContext, definition);
    }

    @Override
    protected void configurePredicate(Predicate predicate) {
        configureNamespaceAware(predicate);
    }

    @Override
    protected void configureExpression(Expression expression) {
        configureNamespaceAware(expression);
    }

    protected void configureNamespaceAware(Object builder) {
        if (definition.getNamespaces() != null && builder instanceof NamespaceAware namespaceAware) {
            namespaceAware.setNamespaces(definition.getNamespaces());
        }
    }

    protected Object[] createProperties() {
        Object[] properties = new Object[3];
        properties[0] = asResultType();
        properties[1] = parseString(definition.getSource());
        properties[2] = definition.getNamespaces();
        return properties;
    }

    @Override
    protected void configureLanguage(Language language) {
        if (definition.getConfiguration() == null && definition.getConfigurationRef() != null) {
            definition.setConfiguration(mandatoryLookup(definition.getConfigurationRef(), Object.class));
        }
    }

}
