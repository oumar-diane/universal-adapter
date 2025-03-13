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
package org.zenithblox.language.csimple;

import org.zenithblox.ZwangineContext;
import org.zenithblox.Exchange;
import org.zenithblox.ExpressionEvaluationException;
import org.zenithblox.util.ObjectHelper;

/**
 * Base class for source code generateed csimple expressions.
 */
public abstract class CSimpleSupport implements CSimpleExpression, CSimpleMethod {

    @Override
    public <T> T evaluate(Exchange exchange, Class<T> type) {
        final ZwangineContext context = exchange.getContext();
        final Object body = exchange.getIn().getBody();
        Object out;
        try {
            out = evaluate(context, exchange, exchange.getIn(), body);
        } catch (Exception e) {
            throw new ExpressionEvaluationException(this, exchange, e);
        }
        return context.getTypeConverter().convertTo(type, exchange, out);
    }

    @Override
    public boolean matches(Exchange exchange) {
        Object body = exchange.getIn().getBody();
        Object out;
        try {
            out = evaluate(exchange.getContext(), exchange, exchange.getIn(), body);
        } catch (Exception e) {
            throw new ExpressionEvaluationException(this, exchange, e);
        }
        return ObjectHelper.evaluateValuePredicate(out);
    }

}
