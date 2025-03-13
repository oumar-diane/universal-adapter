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

package org.zenithblox.impl.engine;

import org.zenithblox.ZwangineContext;
import org.zenithblox.ZwangineContextAware;
import org.zenithblox.spi.Transformer;
import org.zenithblox.spi.TransformerKey;
import org.zenithblox.spi.TransformerResolver;
import org.zenithblox.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * The default implementation of {@link org.zenithblox.spi.TransformerResolver} which tries to find components by
 * using the URI scheme prefix and searching for a file of the URI scheme name in the
 * <b>META-INF/services.org.zenithblox/transformer/</b> directory on the classpath.
 */
public class DefaultTransformerResolver implements TransformerResolver<TransformerKey> {

    public static final String DATA_TYPE_TRANSFORMER_RESOURCE_PATH = "META-INF/services.org.zenithblox/transformer/";

    private static final Logger LOG = LoggerFactory.getLogger(DefaultTransformerResolver.class);

    @Override
    public Transformer resolve(TransformerKey key, ZwangineContext context) {
        String normalizedKey = normalize(key);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Resolving data type transformer for key {} via: {}{}", key, DATA_TYPE_TRANSFORMER_RESOURCE_PATH,
                    normalizedKey);
        }

        Optional<Transformer> transformer = findTransformer(normalizedKey, context);
        if (LOG.isDebugEnabled() && transformer.isPresent()) {
            LOG.debug("Found data type transformer for key {} via type: {} via: {}{}", key,
                    ObjectHelper.name(transformer.getClass()), DATA_TYPE_TRANSFORMER_RESOURCE_PATH, normalizedKey);
        }

        transformer.ifPresent(t -> ZwangineContextAware.trySetZwangineContext(t, context));

        return transformer.orElse(null);
    }

    private Optional<Transformer> findTransformer(String key, ZwangineContext context) {
        return context.getZwangineContextExtension()
                .getBootstrapFactoryFinder(DATA_TYPE_TRANSFORMER_RESOURCE_PATH)
                .newInstance(key, Transformer.class);
    }
}
