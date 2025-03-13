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
import org.zenithblox.Endpoint;
import org.zenithblox.spi.EndpointRegistry;
import org.zenithblox.spi.NormalizedEndpointUri;
import org.zenithblox.support.ZwangineContextHelper;
import org.zenithblox.support.NormalizedUri;

import java.util.Map;

/**
 * Default implementation of {@link org.zenithblox.spi.EndpointRegistry}
 */
public class DefaultEndpointRegistry extends AbstractDynamicRegistry<NormalizedEndpointUri, Endpoint>
        implements EndpointRegistry {

    public DefaultEndpointRegistry(ZwangineContext context) {
        super(context, ZwangineContextHelper.getMaximumEndpointCacheSize(context));
    }

    public DefaultEndpointRegistry(ZwangineContext context, Map<NormalizedEndpointUri, Endpoint> endpoints) {
        this(context);
        if (!context.isStarted()) {
            // optimize to put all into the static map as we are not started
            staticMap.putAll(endpoints);
        } else {
            putAll(endpoints);
        }
    }

    @Override
    public boolean isStatic(String key) {
        return isStatic(NormalizedUri.newNormalizedUri(key, false));
    }

    @Override
    public boolean isDynamic(String key) {
        return isDynamic(NormalizedUri.newNormalizedUri(key, false));
    }

    @Override
    public boolean containsKey(Object key) {
        if (key instanceof String str) {
            key = NormalizedUri.newNormalizedUri(str, false);
        }
        return super.containsKey(key);
    }

    @Override
    public String toString() {
        return "EndpointRegistry for " + context.getName() + " [capacity: " + maxCacheSize + "]";
    }
}
