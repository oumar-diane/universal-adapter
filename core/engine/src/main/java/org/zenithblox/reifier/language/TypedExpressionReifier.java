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
import org.zenithblox.RuntimeZwangineException;
import org.zenithblox.model.language.ExpressionDefinition;
import org.zenithblox.model.language.TypedExpressionDefinition;
import org.zenithblox.spi.Language;

/**
 * {@code TypedExpressionReifier} is a specific reifier for expressions for which a result type can be provided.
 *
 * @param <T> the type of expression
 */
class TypedExpressionReifier<T extends TypedExpressionDefinition> extends ExpressionReifier<T> {

    @SuppressWarnings("unchecked")
    TypedExpressionReifier(ZwangineContext zwangineContext, ExpressionDefinition definition) {
        super(zwangineContext, (T) definition);
    }

    @Override
    protected Expression createExpression(Language language, String exp) {
        return language.createExpression(exp, createProperties());
    }

    @Override
    protected Predicate createPredicate(Language language, String exp) {
        return language.createPredicate(exp, createProperties());
    }

    protected Object[] createProperties() {
        Object[] properties = new Object[1];
        properties[0] = asResultType();
        return properties;
    }

    protected Class<?> asResultType() {
        if (definition.getResultType() == null && definition.getResultTypeName() != null) {
            try {
                return zwangineContext.getClassResolver().resolveMandatoryClass(parseString(definition.getResultTypeName()));
            } catch (ClassNotFoundException e) {
                throw RuntimeZwangineException.wrapRuntimeException(e);
            }
        }
        return definition.getResultType();
    }

    @Override
    protected void configureLanguage(Language language) {
        asResultType();
    }
}
