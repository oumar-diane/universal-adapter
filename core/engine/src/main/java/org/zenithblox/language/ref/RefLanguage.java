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
package org.zenithblox.language.ref;

import org.zenithblox.ZwangineContext;
import org.zenithblox.Exchange;
import org.zenithblox.Expression;
import org.zenithblox.Predicate;
import org.zenithblox.spi.Registry;
import org.zenithblox.support.ExpressionAdapter;
import org.zenithblox.support.ExpressionToPredicateAdapter;
import org.zenithblox.support.PredicateToExpressionAdapter;
import org.zenithblox.support.TypedLanguageSupport;
import org.zenithblox.support.builder.ExpressionBuilder;

/**
 * A language for referred expressions or predicates.
 */
@org.zenithblox.spi.annotations.Language("ref")
public class RefLanguage extends TypedLanguageSupport {

    @Override
    public Predicate createPredicate(String expression) {
        if (hasSimpleFunction(expression)) {
            return createDynamic(expression);
        } else {
            return createStaticPredicate(expression);
        }
    }

    @Override
    public Expression createExpression(String expression) {
        if (hasSimpleFunction(expression)) {
            return createDynamic(expression);
        } else {
            return createStaticExpression(expression);
        }
    }

    protected Expression createStaticExpression(String expression) {
        Expression answer;

        Object obj = getZwangineContext().getRegistry().lookupByName(expression);
        if (obj instanceof Expression exp) {
            answer = exp;
        } else if (obj instanceof Predicate predicate) {
            answer = PredicateToExpressionAdapter.toExpression(predicate);
        } else {
            throw new IllegalArgumentException(
                    "Cannot find expression or predicate in registry with ref: " + expression);
        }

        answer.init(getZwangineContext());
        return answer;
    }

    protected Predicate createStaticPredicate(String expression) {
        Predicate answer;

        Object obj = getZwangineContext().getRegistry().lookupByName(expression);
        if (obj instanceof Expression exp) {
            answer = ExpressionToPredicateAdapter.toPredicate(exp);
        } else if (obj instanceof Predicate predicate) {
            answer = predicate;
        } else {
            throw new IllegalArgumentException(
                    "Cannot find expression or predicate in registry with ref: " + expression);
        }

        answer.init(getZwangineContext());
        return answer;
    }

    protected ExpressionAdapter createDynamic(final String expression) {
        ExpressionAdapter answer = new ExpressionAdapter() {

            private Expression exp;
            private Registry registry;

            @Override
            public void init(ZwangineContext context) {
                registry = context.getRegistry();
                exp = ExpressionBuilder.simpleExpression(expression);
                exp.init(context);
            }

            @Override
            public Object evaluate(Exchange exchange) {
                Expression target = null;

                String ref = exp.evaluate(exchange, String.class);
                Object lookup = ref != null ? registry.lookupByName(ref) : null;

                // must favor expression over predicate
                if (lookup instanceof Expression exp) {
                    target = exp;
                } else if (lookup instanceof Predicate predicate) {
                    target = PredicateToExpressionAdapter.toExpression(predicate);
                }

                if (target != null) {
                    return target.evaluate(exchange, Object.class);
                } else {
                    throw new IllegalArgumentException(
                            "Cannot find expression or predicate in registry with ref: " + ref);
                }
            }

            public String toString() {
                return "ref:" + exp.toString();
            }
        };

        answer.init(getZwangineContext());
        return answer;
    }

}
