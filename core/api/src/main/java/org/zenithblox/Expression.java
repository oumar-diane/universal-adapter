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
package org.zenithblox;

/**
 * An <a href="http://zwangine.zwangine.org/expression.html">expression</a> provides a plugin strategy for evaluating
 * expressions on a message exchange.
 *
 * An expression should be thread-safe and be able to evaluate concurrently by different threads with different
 * exchanges.
 *
 * Any initialization logic should be done by the {@link #init(ZwangineContext)} method which allows to prepare the
 * expressions such as wiring in resources, pre-parsing and what else.
 *
 * @see StaticExpression
 */
public interface Expression {

    /**
     * Returns the value of the expression on the given exchange
     *
     * @param  exchange the message exchange on which to evaluate the expression
     * @param  type     the expected type of the evaluation result
     * @return          the value of the expression
     */
    <T> T evaluate(Exchange exchange, Class<T> type);

    /**
     * Initialize the expression with the given zwangine context
     *
     * @param context the zwangine context
     */
    default void init(ZwangineContext context) {
    }

}
