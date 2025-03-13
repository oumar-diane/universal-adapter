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

package org.zenithblox.spi;

import org.zenithblox.ZwangineContext;
import org.zenithblox.util.StringHelper;

import java.util.Locale;

/**
 * Resolves data type transformers from given transformer key. This represents the opportunity to lazy load transformers
 * via factory finder discovery mechanism.
 */
@FunctionalInterface
public interface TransformerResolver<K> {

    /**
     * Attempts to resolve the transformer for the given key. Usually uses the factory finder URI to resolve the
     * transformer by its name derived from the given key. Transformer names may use scheme and name as a combination in
     * order to resolve component specific transformers. Usually implements a fallback resolving mechanism when no
     * matching transformer is found (e.g. search for generic Zwangine transformers just using the name).
     *
     * @param  key          the transformer key.
     * @param  zwangineContext the current Zwangine context.
     * @return              data type transformer resolved via URI factory finder or null if not found.
     */
    Transformer resolve(K key, ZwangineContext zwangineContext);

    /**
     * Normalize transformer key to conform with factory finder resource path. Replaces all non supported characters
     * such as slashes and colons to dashes. Automatically removes the default scheme prefix as it should not be part of
     * the resource path.
     *
     * @param  key the transformer key
     * @return     normalized String representation of the key
     */
    default String normalize(K key) {
        String keyString = key.toString();
        keyString = StringHelper.after(keyString, DataType.DEFAULT_SCHEME + ":", keyString);
        return StringHelper.sanitize(keyString).toLowerCase(Locale.US);
    }

}
