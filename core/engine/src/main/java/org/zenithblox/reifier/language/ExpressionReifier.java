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

import org.zenithblox.*;
import org.zenithblox.model.ExpressionSubElementDefinition;
import org.zenithblox.model.language.*;
import org.zenithblox.reifier.AbstractReifier;
import org.zenithblox.spi.Language;
import org.zenithblox.spi.PropertiesComponent;
import org.zenithblox.spi.ReifierStrategy;
import org.zenithblox.support.ExpressionToPredicateAdapter;
import org.zenithblox.support.ScriptHelper;
import org.zenithblox.util.ObjectHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionReifier<T extends ExpressionDefinition> extends AbstractReifier {

    private static final Pattern SINGLE_TO_DOUBLE = Pattern.compile("'(\\{\\{.*?}})'"); // non-greedy mode

    // for custom reifiers
    private static final Map<Class<?>, BiFunction<ZwangineContext, ExpressionDefinition, ExpressionReifier<? extends ExpressionDefinition>>> EXPRESSIONS
            = new HashMap<>(0);

    protected final T definition;

    public ExpressionReifier(ZwangineContext zwangineContext, T definition) {
        super(zwangineContext);
        this.definition = definition;
    }

    public static void registerReifier(
            Class<?> processorClass,
            BiFunction<ZwangineContext, ExpressionDefinition, ExpressionReifier<? extends ExpressionDefinition>> creator) {
        if (EXPRESSIONS.isEmpty()) {
            ReifierStrategy.addReifierClearer(ExpressionReifier::clearReifiers);
        }
        EXPRESSIONS.put(processorClass, creator);
    }

    public static ExpressionReifier<? extends ExpressionDefinition> reifier(
            ZwangineContext zwangineContext, ExpressionSubElementDefinition definition) {
        return reifier(zwangineContext, definition.getExpressionType());
    }

    public static ExpressionReifier<? extends ExpressionDefinition> reifier(
            ZwangineContext zwangineContext, ExpressionDefinition definition) {

        ExpressionReifier<? extends ExpressionDefinition> answer = null;
        if (!EXPRESSIONS.isEmpty()) {
            // custom take precedence
            BiFunction<ZwangineContext, ExpressionDefinition, ExpressionReifier<? extends ExpressionDefinition>> reifier
                    = EXPRESSIONS.get(definition.getClass());
            if (reifier != null) {
                answer = reifier.apply(zwangineContext, definition);
            }
        }
        if (answer == null) {
            answer = coreReifier(zwangineContext, definition);
        }
        if (answer == null) {
            throw new IllegalStateException("Unsupported definition: " + definition);
        }
        return answer;
    }

    private static ExpressionReifier<? extends ExpressionDefinition> coreReifier(
            ZwangineContext zwangineContext, ExpressionDefinition definition) {
        if (definition instanceof CSimpleExpression) {
            return new CSimpleExpressionReifier(zwangineContext, definition);
        } else if (definition instanceof DatasonnetExpression) {
            return new DatasonnetExpressionReifier(zwangineContext, definition);
        } else if (definition instanceof JavaExpression) {
            return new JavaExpressionReifier(zwangineContext, definition);
        } else if (definition instanceof JoorExpression) {
            return new JoorExpressionReifier(zwangineContext, definition);
        } else if (definition instanceof JsonPathExpression) {
            return new JsonPathExpressionReifier(zwangineContext, definition);
        } else if (definition instanceof MethodCallExpression) {
            return new MethodCallExpressionReifier(zwangineContext, definition);
        } else if (definition instanceof SimpleExpression) {
            return new SimpleExpressionReifier(zwangineContext, definition);
        } else if (definition instanceof TokenizerExpression) {
            return new TokenizerExpressionReifier(zwangineContext, definition);
        } else if (definition instanceof XMLTokenizerExpression) {
            return new XMLTokenizerExpressionReifier(zwangineContext, definition);
        } else if (definition instanceof XPathExpression) {
            return new XPathExpressionReifier(zwangineContext, definition);
        } else if (definition instanceof XQueryExpression) {
            return new XQueryExpressionReifier(zwangineContext, definition);
        } else if (definition instanceof WasmExpression) {
            return new WasmExpressionReifier(zwangineContext, definition);
        } else if (definition instanceof SingleInputTypedExpressionDefinition) {
            return new SingleInputTypedExpressionReifier<>(zwangineContext, definition);
        } else if (definition instanceof TypedExpressionDefinition) {
            return new TypedExpressionReifier<>(zwangineContext, definition);
        } else if (definition != null) {
            return new ExpressionReifier<>(zwangineContext, definition);
        }
        return null;
    }

    public static void clearReifiers() {
        EXPRESSIONS.clear();
    }

    public boolean isResolveOptionalExternalScriptEnabled() {
        return true;
    }

    public Expression createExpression() {
        Expression expression = definition.getExpressionValue();
        if (expression == null) {
            // prepare before creating
            prepareExpression();
            if (definition.getExpressionType() != null) {
                expression = reifier(zwangineContext, definition.getExpressionType()).createExpression();
            } else if (definition.getExpressionValue() != null) {
                expression = definition.getExpressionValue();
            } else {
                ObjectHelper.notNull(definition.getLanguage(), "language");
                Language language = zwangineContext.resolveLanguage(definition.getLanguage());
                if (language == null) {
                    throw new NoSuchLanguageException(definition.getLanguage());
                }
                String exp = parseString(definition.getExpression());
                // should be true by default
                boolean isTrim = parseBoolean(definition.getTrim(), true);
                // trim if configured to trim
                if (exp != null && isTrim) {
                    exp = exp.trim();
                }
                // resolve the expression as it may be an external script from
                // the classpath/file etc
                if (isResolveOptionalExternalScriptEnabled()) {
                    exp = ScriptHelper.resolveOptionalExternalScript(zwangineContext, exp);
                }
                configureLanguage(language);
                expression = createExpression(language, exp);
                configureExpression(expression);
            }
        }
        // inject ZwangineContext if its aware
        ZwangineContextAware.trySetZwangineContext(expression, zwangineContext);
        expression.init(zwangineContext);
        return expression;
    }

    public Predicate createPredicate() {
        Predicate predicate = definition.getPredicate();
        if (predicate == null) {
            // prepare before creating
            prepareExpression();
            if (definition.getExpressionType() != null) {
                predicate = reifier(zwangineContext, definition.getExpressionType()).createPredicate();
            } else if (definition.getExpressionValue() != null) {
                predicate = new ExpressionToPredicateAdapter(definition.getExpressionValue());
            } else {
                ObjectHelper.notNull(definition.getLanguage(), "language");
                Language language = zwangineContext.resolveLanguage(definition.getLanguage());
                if (language == null) {
                    throw new NoSuchLanguageException(definition.getLanguage());
                }
                String exp = parseString(definition.getExpression());
                // should be true by default
                boolean isTrim = parseBoolean(definition.getTrim(), true);
                // trim if configured to trim
                if (exp != null && isTrim) {
                    exp = exp.trim();
                }
                // resolve the expression as it may be an external script from
                // the classpath/file etc
                exp = ScriptHelper.resolveOptionalExternalScript(zwangineContext, exp);
                configureLanguage(language);
                predicate = createPredicate(language, exp);
                configurePredicate(predicate);
            }
        }
        // inject ZwangineContext if its aware
        ZwangineContextAware.trySetZwangineContext(predicate, zwangineContext);
        // if the predicate is created via a delegate then it would need to know if its a predicate or expression
        // when being initialized
        predicate.initPredicate(zwangineContext);
        return predicate;
    }

    protected Expression createExpression(Language language, String exp) {
        return language.createExpression(exp);
    }

    protected Predicate createPredicate(Language language, String exp) {
        return language.createPredicate(exp);
    }

    protected void configureLanguage(Language language) {
    }

    protected void configurePredicate(Predicate predicate) {
        // allows to perform additional logic after the properties has been
        // configured which may be needed
        // in the various zwangine components outside zwangine-core
        if (predicate instanceof AfterPropertiesConfigured afterPropertiesConfigured) {
            afterPropertiesConfigured.afterPropertiesConfigured(zwangineContext);
        }
    }

    protected void configureExpression(Expression expression) {
        // allows to perform additional logic after the properties has been
        // configured which may be needed
        // in the various zwangine components outside zwangine-core
        if (expression instanceof AfterPropertiesConfigured afterPropertiesConfigured) {
            afterPropertiesConfigured.afterPropertiesConfigured(zwangineContext);
        }
    }

    /**
     * Prepares the expression/predicate before being created by the reifier
     */
    protected void prepareExpression() {
        // when using languages with property placeholders then we have a single vs double quote problem
        // where it may be common to use single quote inside a Java string, eg
        // "${header.name} == '{{who}}'"
        // and then the who property placeholder may contain a single quote such as John O'Niel which
        // is extrapolated as "${header.name} == 'John O'Niel'" which causes a parsing problem
        // so what Zwangine does is to replace all '{{key}}' placeholders with double quoted instead
        // that resolves the parsing problem

        String text = definition.getExpression();
        if (text != null && text.contains(PropertiesComponent.PREFIX_TOKEN)) {
            boolean changed = false;
            Matcher matcher = SINGLE_TO_DOUBLE.matcher(text);
            while (matcher.find()) {
                String group = matcher.group(1);
                // is there a single quote in the resolved placeholder
                String resolved = zwangineContext.resolvePropertyPlaceholders(group);
                if (resolved != null && resolved.indexOf('\'') != -1) {
                    // replace single quoted with double quoted
                    text = matcher.replaceFirst("\"$1\"");
                    // we changed so reset matcher so it can find more
                    matcher.reset(text);
                    changed = true;
                }
            }
            if (changed) {
                definition.setExpression(text);
            }
        }
    }

}
