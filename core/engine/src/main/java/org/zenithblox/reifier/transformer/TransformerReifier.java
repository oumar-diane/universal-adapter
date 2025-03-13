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
package org.zenithblox.reifier.transformer;

import org.zenithblox.ZwangineContext;
import org.zenithblox.model.transformer.*;
import org.zenithblox.reifier.AbstractReifier;
import org.zenithblox.spi.ReifierStrategy;
import org.zenithblox.spi.Transformer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public abstract class TransformerReifier<T> extends AbstractReifier {

    // for custom reifiers
    private static final Map<Class<?>, BiFunction<ZwangineContext, TransformerDefinition, TransformerReifier<? extends TransformerDefinition>>> TRANSFORMERS
            = new HashMap<>(0);

    protected final T definition;

    public TransformerReifier(ZwangineContext zwangineContext, T definition) {
        super(zwangineContext);
        this.definition = definition;
    }

    public static void registerReifier(
            Class<?> processorClass,
            BiFunction<ZwangineContext, TransformerDefinition, TransformerReifier<? extends TransformerDefinition>> creator) {
        if (TRANSFORMERS.isEmpty()) {
            ReifierStrategy.addReifierClearer(TransformerReifier::clearReifiers);
        }
        TRANSFORMERS.put(processorClass, creator);
    }

    public static TransformerReifier<? extends TransformerDefinition> reifier(
            ZwangineContext zwangineContext, TransformerDefinition definition) {

        TransformerReifier<? extends TransformerDefinition> answer = null;
        if (!TRANSFORMERS.isEmpty()) {
            // custom take precedence
            BiFunction<ZwangineContext, TransformerDefinition, TransformerReifier<? extends TransformerDefinition>> reifier
                    = TRANSFORMERS.get(definition.getClass());
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

    private static TransformerReifier<? extends TransformerDefinition> coreReifier(
            ZwangineContext zwangineContext, TransformerDefinition definition) {
        if (definition instanceof CustomTransformerDefinition) {
            return new CustomTransformerReifier(zwangineContext, definition);
        } else if (definition instanceof DataFormatTransformerDefinition) {
            return new DataFormatTransformerReifier(zwangineContext, definition);
        } else if (definition instanceof EndpointTransformerDefinition) {
            return new EndpointTransformerReifier(zwangineContext, definition);
        } else if (definition instanceof LoadTransformerDefinition) {
            return new LoadTransformerReifier(zwangineContext, definition);
        }
        return null;
    }

    public static void clearReifiers() {
        TRANSFORMERS.clear();
    }

    public Transformer createTransformer() {
        return doCreateTransformer();
    }

    protected abstract Transformer doCreateTransformer();

}
