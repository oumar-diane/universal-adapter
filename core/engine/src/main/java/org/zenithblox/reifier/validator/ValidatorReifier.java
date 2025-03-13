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
package org.zenithblox.reifier.validator;

import org.zenithblox.ZwangineContext;
import org.zenithblox.model.validator.CustomValidatorDefinition;
import org.zenithblox.model.validator.EndpointValidatorDefinition;
import org.zenithblox.model.validator.PredicateValidatorDefinition;
import org.zenithblox.model.validator.ValidatorDefinition;
import org.zenithblox.reifier.AbstractReifier;
import org.zenithblox.spi.ReifierStrategy;
import org.zenithblox.spi.Validator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public abstract class ValidatorReifier<T> extends AbstractReifier {

    // for custom reifiers
    private static final Map<Class<?>, BiFunction<ZwangineContext, ValidatorDefinition, ValidatorReifier<? extends ValidatorDefinition>>> VALIDATORS
            = new HashMap<>(0);

    protected final T definition;

    public ValidatorReifier(ZwangineContext zwangineContext, T definition) {
        super(zwangineContext);
        this.definition = definition;
    }

    public static void registerReifier(
            Class<?> processorClass,
            BiFunction<ZwangineContext, ValidatorDefinition, ValidatorReifier<? extends ValidatorDefinition>> creator) {
        if (VALIDATORS.isEmpty()) {
            ReifierStrategy.addReifierClearer(ValidatorReifier::clearReifiers);
        }
        VALIDATORS.put(processorClass, creator);
    }

    public static ValidatorReifier<? extends ValidatorDefinition> reifier(
            ZwangineContext zwangineContext, ValidatorDefinition definition) {

        ValidatorReifier<? extends ValidatorDefinition> answer = null;
        if (!VALIDATORS.isEmpty()) {
            // custom take precedence
            BiFunction<ZwangineContext, ValidatorDefinition, ValidatorReifier<? extends ValidatorDefinition>> reifier
                    = VALIDATORS.get(definition.getClass());
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

    private static ValidatorReifier<? extends ValidatorDefinition> coreReifier(
            ZwangineContext zwangineContext, ValidatorDefinition definition) {
        if (definition instanceof CustomValidatorDefinition) {
            return new CustomValidatorReifier(zwangineContext, definition);
        } else if (definition instanceof EndpointValidatorDefinition) {
            return new EndpointValidatorReifier(zwangineContext, definition);
        } else if (definition instanceof PredicateValidatorDefinition) {
            return new PredicateValidatorReifier(zwangineContext, definition);
        }
        return null;
    }

    public static void clearReifiers() {
        VALIDATORS.clear();
    }

    public Validator createValidator() {
        return doCreateValidator();
    }

    protected abstract Validator doCreateValidator();

}
