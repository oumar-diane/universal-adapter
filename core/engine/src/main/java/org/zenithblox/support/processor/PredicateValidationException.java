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
package org.zenithblox.support.processor;

import org.zenithblox.Exchange;
import org.zenithblox.Predicate;
import org.zenithblox.ValidationException;

import java.io.Serial;

/**
 * A predicate validation exception occurred
 */
public class PredicateValidationException extends ValidationException {

    private static final @Serial long serialVersionUID = 5767438583860347105L;

    private final Predicate predicate;

    public PredicateValidationException(Exchange exchange, Predicate predicate) {
        super(exchange, buildMessage(predicate, exchange));
        this.predicate = predicate;
    }

    protected static String buildMessage(Predicate predicate, Exchange exchange) {
        StringBuilder builder = new StringBuilder(256);

        builder.append("Validation failed for Predicate[");
        builder.append(predicate.toString());
        builder.append("]");
        return builder.toString();
    }

    public Predicate getPredicate() {
        return predicate;
    }
}
