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
package org.zenithblox.support.builder;

import org.zenithblox.ZwangineContext;
import org.zenithblox.Exchange;
import org.zenithblox.Expression;
import org.zenithblox.Predicate;
import org.zenithblox.spi.Language;
import org.zenithblox.support.ExchangeHelper;
import org.zenithblox.support.ExpressionToPredicateAdapter;
import org.zenithblox.support.LanguageHelper;
import org.zenithblox.support.ObjectHelper;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.zenithblox.util.ObjectHelper.notNull;

/**
 * A helper class for working with predicates
 */
public class PredicateBuilder {
    /**
     * Converts the given expression into an {@link Predicate}
     */
    public static Predicate toPredicate(final Expression expression) {
        return ExpressionToPredicateAdapter.toPredicate(expression);
    }

    /**
     * A helper method to return the logical not of the given predicate
     */
    public static Predicate not(final Predicate predicate) {
        notNull(predicate, "predicate");
        return new Predicate() {
            public boolean matches(Exchange exchange) {
                return !predicate.matches(exchange);
            }

            @Override
            public void init(ZwangineContext zwangineContext) {
                predicate.initPredicate(zwangineContext);
            }

            @Override
            public String toString() {
                return "not (" + predicate + ")";
            }
        };
    }

    /**
     * A helper method to combine multiple predicates by a logical AND
     */
    public static Predicate and(final Predicate left, final Predicate right) {
        notNull(left, "left");
        notNull(right, "right");
        return new Predicate() {
            public boolean matches(Exchange exchange) {
                return left.matches(exchange) && right.matches(exchange);
            }

            @Override
            public void init(ZwangineContext zwangineContext) {
                left.initPredicate(zwangineContext);
                right.initPredicate(zwangineContext);
            }

            @Override
            public String toString() {
                return "(" + left + ") and (" + right + ")";
            }
        };
    }

    /**
     * A helper method to combine two predicates by a logical OR. If you want to combine multiple predicates see
     * {@link #in(Predicate...)}
     */
    public static Predicate or(final Predicate left, final Predicate right) {
        notNull(left, "left");
        notNull(right, "right");
        return new Predicate() {
            public boolean matches(Exchange exchange) {
                return left.matches(exchange) || right.matches(exchange);
            }

            @Override
            public void init(ZwangineContext zwangineContext) {
                left.initPredicate(zwangineContext);
                right.initPredicate(zwangineContext);
            }

            @Override
            public String toString() {
                return "(" + left + ") or (" + right + ")";
            }
        };
    }

    /**
     * Concat the given predicates into a single predicate, which matches if at least one predicates matches.
     *
     * @param  predicates predicates
     * @return            a single predicate containing all the predicates
     */
    public static Predicate or(List<Predicate> predicates) {
        Predicate answer = null;
        for (Predicate predicate : predicates) {
            if (answer == null) {
                answer = predicate;
            } else {
                answer = or(answer, predicate);
            }
        }
        return answer;
    }

    /**
     * Concat the given predicates into a single predicate, which matches if at least one predicates matches.
     *
     * @param  predicates predicates
     * @return            a single predicate containing all the predicates
     */
    public static Predicate or(Predicate... predicates) {
        return or(Arrays.asList(predicates));
    }

    /**
     * A helper method to return true if any of the predicates matches.
     */
    public static Predicate in(final Predicate... predicates) {
        notNull(predicates, "predicates");

        return new Predicate() {
            public boolean matches(Exchange exchange) {
                for (Predicate in : predicates) {
                    if (in.matches(exchange)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void init(ZwangineContext zwangineContext) {
                for (Predicate in : predicates) {
                    in.initPredicate(zwangineContext);
                }
            }

            @Override
            public String toString() {
                return "in (" + Arrays.asList(predicates) + ")";
            }
        };
    }

    /**
     * A helper method to return true if any of the predicates matches.
     */
    public static Predicate in(List<Predicate> predicates) {
        return in(predicates.toArray(new Predicate[0]));
    }

    /**
     * Is the predicate true
     */
    public static Predicate isTrue(final Expression left) {
        return PredicateBuilder.toPredicate(left);
    }

    /**
     * Is the predicate false
     */
    public static Predicate isFalse(final Expression left) {
        return PredicateBuilder.not(PredicateBuilder.toPredicate(left));
    }

    public static Predicate isEqualTo(final Expression left, final Expression right) {
        return new BinaryPredicateSupport(left, right) {

            protected boolean matches(Exchange exchange, Object leftValue, Object rightValue) {
                if (leftValue == null && rightValue == null) {
                    // they are equal
                    return true;
                } else if (leftValue == null || rightValue == null) {
                    // only one of them is null so they are not equal
                    return false;
                }

                return ObjectHelper.typeCoerceEquals(exchange.getContext().getTypeConverter(), leftValue, rightValue);
            }

            protected String getOperationText() {
                return "==";
            }
        };
    }

    public static Predicate isEqualToIgnoreCase(final Expression left, final Expression right) {
        return new BinaryPredicateSupport(left, right) {

            protected boolean matches(Exchange exchange, Object leftValue, Object rightValue) {
                if (leftValue == null && rightValue == null) {
                    // they are equal
                    return true;
                } else if (leftValue == null || rightValue == null) {
                    // only one of them is null so they are not equal
                    return false;
                }

                return ObjectHelper.typeCoerceEquals(exchange.getContext().getTypeConverter(), leftValue, rightValue, true);
            }

            protected String getOperationText() {
                return "=~";
            }
        };
    }

    public static Predicate isNotEqualTo(final Expression left, final Expression right) {
        return new BinaryPredicateSupport(left, right) {

            protected boolean matches(Exchange exchange, Object leftValue, Object rightValue) {
                if (leftValue == null && rightValue == null) {
                    // they are equal
                    return false;
                } else if (leftValue == null || rightValue == null) {
                    // only one of them is null so they are not equal
                    return true;
                }

                return ObjectHelper.typeCoerceNotEquals(exchange.getContext().getTypeConverter(), leftValue, rightValue);
            }

            protected String getOperationText() {
                return "!=";
            }
        };
    }

    public static Predicate isLessThan(final Expression left, final Expression right) {
        return new BinaryPredicateSupport(left, right) {

            protected boolean matches(Exchange exchange, Object leftValue, Object rightValue) {
                if (leftValue == null && rightValue == null) {
                    // they are equal
                    return true;
                } else if (leftValue == null || rightValue == null) {
                    // only one of them is null so they are not equal
                    return false;
                }

                return ObjectHelper.typeCoerceCompare(exchange.getContext().getTypeConverter(), leftValue, rightValue) < 0;
            }

            protected String getOperationText() {
                return "<";
            }
        };
    }

    public static Predicate isLessThanOrEqualTo(final Expression left, final Expression right) {
        return new BinaryPredicateSupport(left, right) {

            protected boolean matches(Exchange exchange, Object leftValue, Object rightValue) {
                if (leftValue == null && rightValue == null) {
                    // they are equal
                    return true;
                } else if (leftValue == null || rightValue == null) {
                    // only one of them is null so they are not equal
                    return false;
                }

                return ObjectHelper.typeCoerceCompare(exchange.getContext().getTypeConverter(), leftValue, rightValue) <= 0;
            }

            protected String getOperationText() {
                return "<=";
            }
        };
    }

    public static Predicate isGreaterThan(final Expression left, final Expression right) {
        return new BinaryPredicateSupport(left, right) {

            protected boolean matches(Exchange exchange, Object leftValue, Object rightValue) {
                if (leftValue == null && rightValue == null) {
                    // they are equal
                    return false;
                } else if (leftValue == null || rightValue == null) {
                    // only one of them is null so they are not equal
                    return false;
                }

                return ObjectHelper.typeCoerceCompare(exchange.getContext().getTypeConverter(), leftValue, rightValue) > 0;
            }

            protected String getOperationText() {
                return ">";
            }
        };
    }

    public static Predicate isGreaterThanOrEqualTo(final Expression left, final Expression right) {
        return new BinaryPredicateSupport(left, right) {

            protected boolean matches(Exchange exchange, Object leftValue, Object rightValue) {
                if (leftValue == null && rightValue == null) {
                    // they are equal
                    return true;
                } else if (leftValue == null || rightValue == null) {
                    // only one of them is null so they are not equal
                    return false;
                }

                return ObjectHelper.typeCoerceCompare(exchange.getContext().getTypeConverter(), leftValue, rightValue) >= 0;
            }

            protected String getOperationText() {
                return ">=";
            }
        };
    }

    public static Predicate contains(final Expression left, final Expression right) {
        return new BinaryPredicateSupport(left, right) {

            protected boolean matches(Exchange exchange, Object leftValue, Object rightValue) {
                if (leftValue == null && rightValue == null) {
                    // they are equal
                    return true;
                } else if (leftValue == null || rightValue == null) {
                    // only one of them is null so they are not equal
                    return false;
                }

                return ObjectHelper.typeCoerceContains(exchange.getContext().getTypeConverter(), leftValue, rightValue, false);
            }

            protected String getOperationText() {
                return "contains";
            }
        };
    }

    public static Predicate containsIgnoreCase(final Expression left, final Expression right) {
        return new BinaryPredicateSupport(left, right) {

            protected boolean matches(Exchange exchange, Object leftValue, Object rightValue) {
                if (leftValue == null && rightValue == null) {
                    // they are equal
                    return true;
                } else if (leftValue == null || rightValue == null) {
                    // only one of them is null so they are not equal
                    return false;
                }

                return ObjectHelper.typeCoerceContains(exchange.getContext().getTypeConverter(), leftValue, rightValue, true);
            }

            protected String getOperationText() {
                return "~~";
            }
        };
    }

    public static Predicate isNull(final Expression expression) {
        return new BinaryPredicateSupport(expression, ExpressionBuilder.constantExpression(null)) {

            protected boolean matches(Exchange exchange, Object leftValue, Object rightValue) {
                if (leftValue == null) {
                    // the left operator is null so its true
                    return true;
                }

                return ObjectHelper.typeCoerceEquals(exchange.getContext().getTypeConverter(), leftValue, rightValue);
            }

            protected String getOperationText() {
                // leave the operation text as "is not" as Zwangine will insert right and left expression around it
                // so it will be displayed as: XXX is null
                return "is";
            }
        };
    }

    public static Predicate isNotNull(final Expression expression) {
        return new BinaryPredicateSupport(expression, ExpressionBuilder.constantExpression(null)) {

            protected boolean matches(Exchange exchange, Object leftValue, Object rightValue) {
                if (leftValue != null) {
                    // the left operator is not null so its true
                    return true;
                }
                // TODO leftValue is null, is it expected?
                return ObjectHelper.typeCoerceNotEquals(exchange.getContext().getTypeConverter(), leftValue, rightValue);
            }

            protected String getOperationText() {
                // leave the operation text as "is not" as Zwangine will insert right and left expression around it
                // so it will be displayed as: XXX is not null
                return "is not";
            }
        };
    }

    public static Predicate isInstanceOf(final Expression expression, final Class<?> type) {
        notNull(expression, "expression");
        notNull(type, "type");

        return new Predicate() {
            public boolean matches(Exchange exchange) {
                Object value = expression.evaluate(exchange, Object.class);
                return type.isInstance(value);
            }

            @Override
            public void init(ZwangineContext zwangineContext) {
                expression.init(zwangineContext);
            }

            @Override
            public String toString() {
                return expression + " instanceof " + type.getCanonicalName();
            }
        };
    }

    public static Predicate startsWith(final Expression left, final Expression right) {
        return new BinaryPredicateSupport(left, right) {

            protected boolean matches(Exchange exchange, Object leftValue, Object rightValue) {
                return LanguageHelper.startsWith(exchange, leftValue, rightValue);
            }

            protected String getOperationText() {
                return "startsWith";
            }
        };
    }

    public static Predicate endsWith(final Expression left, final Expression right) {
        return new BinaryPredicateSupport(left, right) {

            protected boolean matches(Exchange exchange, Object leftValue, Object rightValue) {
                return LanguageHelper.endsWith(exchange, leftValue, rightValue);
            }

            protected String getOperationText() {
                return "endsWith";
            }
        };
    }

    /**
     * Returns a predicate which is true if the expression matches the given regular expression
     *
     * @param  expression the expression to evaluate
     * @param  regex      the regular expression to match against
     * @return            a new predicate
     */
    public static Predicate regex(final Expression expression, final String regex) {
        return regex(expression, Pattern.compile(regex));
    }

    /**
     * Returns a predicate which is true if the expression matches the given regular expression
     *
     * @param  expression the expression to evaluate
     * @param  pattern    the regular expression to match against
     * @return            a new predicate
     */
    public static Predicate regex(final Expression expression, final Pattern pattern) {
        notNull(expression, "expression");
        notNull(pattern, "pattern");

        return new Predicate() {
            public boolean matches(Exchange exchange) {
                String value = expression.evaluate(exchange, String.class);
                if (value != null) {
                    Matcher matcher = pattern.matcher(value);
                    return matcher.matches();
                }
                return false;
            }

            @Override
            public void init(ZwangineContext zwangineContext) {
                expression.init(zwangineContext);
            }

            @Override
            public String toString() {
                return expression + ".matches('" + pattern + "')";
            }
        };
    }

    /**
     * Concat the given predicates into a single predicate, which only matches if all the predicates matches.
     *
     * @param  predicates predicates
     * @return            a single predicate containing all the predicates
     */
    public static Predicate and(List<Predicate> predicates) {
        Predicate answer = null;
        for (Predicate predicate : predicates) {
            if (answer == null) {
                answer = predicate;
            } else {
                answer = and(answer, predicate);
            }
        }
        return answer;
    }

    /**
     * Concat the given predicates into a single predicate, which only matches if all the predicates matches.
     *
     * @param  predicates predicates
     * @return            a single predicate containing all the predicates
     */
    public static Predicate and(Predicate... predicates) {
        return and(Arrays.asList(predicates));
    }

    /**
     * A constant predicate.
     *
     * @param  answer the constant matches
     * @return        a predicate that always returns the given answer.
     */
    public static Predicate constant(final boolean answer) {
        return new Predicate() {
            @Override
            public boolean matches(Exchange exchange) {
                return answer;
            }

            @Override
            public String toString() {
                return String.valueOf(answer);
            }
        };
    }

    /**
     * Returns a predicate which is true if the expression matches the given language predicate
     *
     * @param  expression the expression to evaluate
     * @param  language   the language such as xpath, jq, groovy, etc.
     * @param  value      the value as expression for the language
     * @return            a new predicate
     */
    public static Predicate language(final Expression expression, final String language, final Object value) {
        notNull(expression, "expression");
        notNull(language, "language");
        notNull(value, "value");

        return new Predicate() {

            private Predicate pred;

            public boolean matches(Exchange exchange) {
                Object value = expression.evaluate(exchange, Object.class);
                if (value != null) {
                    Exchange dummy = ExchangeHelper.getDummy(exchange.getContext());
                    dummy.getMessage().setBody(value);
                    return pred.matches(dummy);
                }
                return false;
            }

            @Override
            public void init(ZwangineContext zwangineContext) {
                expression.init(zwangineContext);
                Language lan = zwangineContext.resolveLanguage(language);
                pred = lan.createPredicate(value.toString());
                pred.init(zwangineContext);
            }

            @Override
            public String toString() {
                return language + "(" + expression + ")";
            }
        };
    }

}
