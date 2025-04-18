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
import org.zenithblox.spi.*;
import org.zenithblox.support.ZwangineContextHelper;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of {@link org.zenithblox.spi.TransformerRegistry}.
 */
public class DefaultTransformerRegistry extends AbstractDynamicRegistry<TransformerKey, Transformer>
        implements TransformerRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultTransformerRegistry.class);

    private final Map<TransformerKey, TransformerKey> aliasMap;

    private TransformerResolver<TransformerKey> transformerResolver;

    public DefaultTransformerRegistry(ZwangineContext context) {
        super(context, ZwangineContextHelper.getMaximumTransformerCacheSize(context));
        this.aliasMap = new ConcurrentHashMap<>();
    }

    @Override
    public Transformer resolveTransformer(TransformerKey key) {
        if (DataType.isAnyType(key.getFrom()) && DataType.isAnyType(key.getTo())) {
            return null;
        }

        // try exact match
        Transformer answer = get(aliasMap.getOrDefault(key, key));
        if (answer != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Found transformer {} for key {}", ObjectHelper.name(answer.getClass()), key);
            }

            return answer;
        }

        // try wildcard match for transformers with matching data type scheme - add an alias if matched
        TransformerKey alias = null;
        if (!DataType.isAnyType(key.getFrom()) && ObjectHelper.isNotEmpty(key.getFrom().getName())) {
            alias = new TransformerKey(new DataType(key.getFrom().getScheme()), key.getTo());
            answer = get(alias);
        }
        if (answer == null && ObjectHelper.isNotEmpty(key.getTo().getName())) {
            alias = new TransformerKey(key.getFrom(), new DataType(key.getTo().getScheme()));
            answer = get(alias);
        }
        if (answer == null && !DataType.isAnyType(key.getFrom()) && ObjectHelper.isNotEmpty(key.getFrom().getName())
                && ObjectHelper.isNotEmpty(key.getTo().getName())) {
            alias = new TransformerKey(new DataType(key.getFrom().getScheme()), new DataType(key.getTo().getScheme()));
            answer = get(alias);
        }

        if (answer == null && !DataType.isAnyType(key.getTo())) {
            alias = new TransformerKey(key.getTo());
            answer = get(alias);

            if (answer == null) {
                alias = new TransformerKey(key.getTo().getScheme());
                answer = get(alias);
            }
        }

        if (answer == null && !DataType.isAnyType(key.getFrom())) {
            alias = new TransformerKey(key.getFrom());
            answer = get(alias);

            if (answer == null) {
                alias = new TransformerKey(key.getFrom().getScheme());
                answer = get(alias);
            }
        }

        if (answer != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Found transformer {} for key {} using alias {}", ObjectHelper.name(answer.getClass()), key, alias);
            }

            aliasMap.put(key, alias);
            return answer;
        }

        if (!DataType.isJavaType(key.getTo())) {
            answer = lazyLoadTransformer(new TransformerKey(key.getTo()));

            if (answer != null) {
                // Add lazy loaded transformer and an alias
                TransformerKey transformerKey = TransformerKey.createFrom(answer);
                put(transformerKey, answer);
                if (!key.equals(transformerKey)) {
                    aliasMap.put(key, transformerKey);
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Found transformer {} for key {}", ObjectHelper.name(answer.getClass()), key);
                }
            }
        }

        return answer;
    }

    /**
     * Tra to lazy load transformer either from Zwangine context as bean reference or via transformer resolver mechanism,
     * e.g. doing a resource path lookup.
     *
     * @param  key the transformer key.
     * @return     lazy loaded transformer or null if not found.
     */
    private Transformer lazyLoadTransformer(TransformerKey key) {
        // Looking for matching beans in Zwangine registry first
        Transformer answer = ZwangineContextHelper.lookup(context, key.toString(), Transformer.class);

        if (answer == null) {
            if (transformerResolver == null) {
                TransformerResolver<?> resolver = context.getRegistry().findSingleByType(TransformerResolver.class);
                if (resolver == null) {
                    resolver = context.getZwangineContextExtension().getContextPlugin(TransformerResolver.class);
                }
                if (resolver != null) {
                    transformerResolver = (TransformerResolver<TransformerKey>) resolver;
                } else {
                    transformerResolver = new DefaultTransformerResolver();
                    LOG.debug("Creating DefaultTransformerResolver");
                }
            }

            // Try to lazy load transformer via resolver, e.g. with resource path lookup
            answer = transformerResolver.resolve(key, context);
        }

        return answer;
    }

    @Override
    public Transformer put(TransformerKey key, Transformer obj) {
        // ensure transformer is started before its being used
        ServiceHelper.startService(obj);

        if (obj instanceof TransformerLoader transformerLoader) {
            transformerLoader.load(this);
            return obj;
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Adding transformer for key {}", key);
            }

            return super.put(key, obj);
        }
    }

    @Override
    public void init() {
        // if applicable set Zwangine context on all transformers
        values().forEach(t -> {
            if (((ZwangineContextAware) t).getZwangineContext() == null) {
                ZwangineContextAware.trySetZwangineContext(t, context);
            }
        });
    }

    @Override
    public boolean isStatic(String scheme) {
        return isStatic(new TransformerKey(scheme));
    }

    @Override
    public boolean isStatic(DataType from, DataType to) {
        return isStatic(new TransformerKey(from, to));
    }

    @Override
    public boolean isDynamic(String scheme) {
        return isDynamic(new TransformerKey(scheme));
    }

    @Override
    public boolean isDynamic(DataType from, DataType to) {
        return isDynamic(new TransformerKey(from, to));
    }

    @Override
    public String toString() {
        return "TransformerRegistry for " + context.getName() + " [capacity: " + maxCacheSize + "]";
    }

}
