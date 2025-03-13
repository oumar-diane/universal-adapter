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
package org.zenithblox.processor.aggregate;

import org.zenithblox.AggregationStrategy;
import org.zenithblox.Exchange;

import java.util.function.BiFunction;

/**
 * An {@link AggregationStrategy} that adapts to a {@link BiFunction}.
 * <p/>
 * This allows end users to use {@link BiFunction} for the aggregation logic, instead of having to implement the Zwangine
 * API {@link AggregationStrategy}.
 * <p/>
 * This is supported for example by zwangine-joor that makes it possible to write a {@link BiFunction} as a lambda script
 * that can be compiled and used by Zwangine.
 */
public class AggregationStrategyBiFunctionAdapter implements AggregationStrategy {

    private final BiFunction<Exchange, Exchange, Object> function;
    private boolean allowNullOldExchange;
    private boolean allowNullNewExchange;

    public AggregationStrategyBiFunctionAdapter(BiFunction<Exchange, Exchange, Object> function) {
        this.function = function;
    }

    public boolean isAllowNullOldExchange() {
        return allowNullOldExchange;
    }

    public void setAllowNullOldExchange(boolean allowNullOldExchange) {
        this.allowNullOldExchange = allowNullOldExchange;
    }

    public boolean isAllowNullNewExchange() {
        return allowNullNewExchange;
    }

    public void setAllowNullNewExchange(boolean allowNullNewExchange) {
        this.allowNullNewExchange = allowNullNewExchange;
    }

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (!allowNullOldExchange && oldExchange == null) {
            return newExchange;
        }
        if (!allowNullNewExchange && newExchange == null) {
            return oldExchange;
        }

        try {
            Object out = function.apply(oldExchange, newExchange);
            if (out != null && !(out instanceof Exchange)) {
                if (oldExchange != null) {
                    oldExchange.getIn().setBody(out);
                } else {
                    newExchange.getIn().setBody(out);
                }
            }
        } catch (Exception e) {
            if (oldExchange != null) {
                oldExchange.setException(e);
            } else {
                newExchange.setException(e);
            }
        }
        return oldExchange != null ? oldExchange : newExchange;
    }
}
