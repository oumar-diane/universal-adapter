/*
 * Licensed to the  Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the  License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.zwangine.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zenithblox.support;

import org.zenithblox.*;
import org.zenithblox.spi.PropertyConfigurer;
import org.zenithblox.spi.PropertyConfigurerAware;
import org.zenithblox.util.ObjectHelper;

/**
 * To adapt {@link org.zenithblox.Expression} as a {@link Predicate}
 */
public final class ExpressionToPredicateAdapter implements Predicate, ZwangineContextAware, PropertyConfigurerAware {
    private final Expression expression;

    public ExpressionToPredicateAdapter(Expression expression) {
        this.expression = expression;
    }

    @Override
    public boolean matches(Exchange exchange) {
        if (expression instanceof Predicate predicate) {
            return predicate.matches(exchange);
        } else {
            Object value = expression.evaluate(exchange, Object.class);
            return ObjectHelper.evaluateValuePredicate(value);
        }
    }

    @Override
    public String toString() {
        return expression.toString();
    }

    /**
     * Converts the given expression into an {@link Predicate}
     */
    public static Predicate toPredicate(final Expression expression) {
        if (expression instanceof Predicate predicate) {
            return predicate;
        }
        return new ExpressionToPredicateAdapter(expression);
    }

    @Override
    public void init(ZwangineContext context) {
        expression.init(context);
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        ZwangineContextAware.trySetZwangineContext(expression, zwangineContext);
    }

    @Override
    public ZwangineContext getZwangineContext() {
        if (expression instanceof ZwangineContextAware zwangineContext) {
            return zwangineContext.getZwangineContext();
        } else {
            return null;
        }
    }

    @Override
    public PropertyConfigurer getPropertyConfigurer(Object instance) {
        if (expression instanceof PropertyConfigurer propertyConfigurer) {
            return propertyConfigurer;
        } else if (expression instanceof PropertyConfigurerAware propertyConfigurer) {
            return propertyConfigurer.getPropertyConfigurer(expression);
        } else {
            return null;
        }
    }
}
