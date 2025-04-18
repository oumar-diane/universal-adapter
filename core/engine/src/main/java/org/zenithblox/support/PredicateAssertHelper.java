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

import org.zenithblox.BinaryPredicate;
import org.zenithblox.Exchange;
import org.zenithblox.Predicate;
import org.zenithblox.util.ObjectHelper;

/**
 * A helper for doing {@link Predicate} assertions.
 */
public final class PredicateAssertHelper {

    private PredicateAssertHelper() {
        // Utility class
    }

    public static void assertMatches(Predicate predicate, String text, Exchange exchange) {
        ObjectHelper.notNull(predicate, "predicate");
        ObjectHelper.notNull(exchange, "exchange");

        if (predicate instanceof BinaryPredicate eval) {
            // with binary evaluations as we can get more detailed information
            String evalText = eval.matchesReturningFailureMessage(exchange);
            if (evalText != null) {
                throw new AssertionError(text + predicate + " evaluated as: " + evalText + " on " + exchange);
            }
        } else {
            doAssertMatches(predicate, text, exchange);
        }
    }

    private static void doAssertMatches(Predicate predicate, String text, Exchange exchange) {
        if (!predicate.matches(exchange)) {
            if (text == null) {
                throw new AssertionError(predicate + " on " + exchange);
            } else {
                throw new AssertionError(text + predicate + " on " + exchange);
            }
        }
    }

}
