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
package org.zenithblox.language.property;

import org.zenithblox.Expression;
import org.zenithblox.IsSingleton;
import org.zenithblox.Predicate;
import org.zenithblox.spi.Language;
import org.zenithblox.support.ExpressionToPredicateAdapter;
import org.zenithblox.support.builder.ExpressionBuilder;

/**
 * A language for exchange property expressions.
 */
@org.zenithblox.spi.annotations.Language("exchangeProperty")
public class ExchangePropertyLanguage implements Language, IsSingleton {

    public static Expression exchangeProperty(String propertyName) {
        return ExpressionBuilder.exchangePropertyExpression(propertyName);
    }

    @Override
    public Predicate createPredicate(String expression) {
        return ExpressionToPredicateAdapter.toPredicate(createExpression(expression));
    }

    @Override
    public Expression createExpression(String expression) {
        return ExchangePropertyLanguage.exchangeProperty(expression);
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
