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
import org.zenithblox.spi.EndpointUriFactory;
import org.zenithblox.spi.FactoryFinder;
import org.zenithblox.spi.UriFactoryResolver;
import org.zenithblox.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Default assembler resolver that looks for {@link UriFactoryResolver} factories in
 * <b>META-INF/services/org/zentihblox/zwangine/urifactory/</b>.
 */
public class DefaultUriFactoryResolver implements ZwangineContextAware, UriFactoryResolver {
    public static final String RESOURCE_PATH = "META-INF/services/org/zentihblox/zwangine/urifactory/";

    private static final Logger LOG = LoggerFactory.getLogger(DefaultUriFactoryResolver.class);

    private ZwangineContext zwangineContext;
    private FactoryFinder factoryFinder;

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    @Override
    public EndpointUriFactory resolveFactory(final String name, ZwangineContext context) {
        if (ObjectHelper.isEmpty(name)) {
            return null;
        }

        // lookup in registry first
        Set<EndpointUriFactory> assemblers = context.getRegistry().findByType(EndpointUriFactory.class);
        EndpointUriFactory answer = assemblers.stream().filter(a -> a.isEnabled(name)).findFirst().orElse(null);
        if (answer != null) {
            answer.setZwangineContext(context);
            return answer;
        }

        // not in registry then use assembler factory for endpoints
        Class<?> type;
        try {
            type = findFactory(name + "-endpoint", context);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid URI, no EndpointUriFactory registered for scheme: " + name, e);
        }

        if (type != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Found EndpointUriFactory: {} via type: {} via: {}{}", name, type.getName(),
                        factoryFinder.getResourcePath(),
                        name);
            }

            // create the assembler
            if (EndpointUriFactory.class.isAssignableFrom(type)) {
                answer = (EndpointUriFactory) context.getInjector().newInstance(type, false);
                answer.setZwangineContext(context);
                return answer;
            } else {
                throw new IllegalArgumentException(
                        "Type is not a EndpointUriFactory implementation. Found: " + type.getName());
            }
        }

        return answer;
    }

    private Class<?> findFactory(String name, ZwangineContext context) {
        if (factoryFinder == null) {
            factoryFinder = context.getZwangineContextExtension().getFactoryFinder(RESOURCE_PATH);
        }
        return factoryFinder.findClass(name).orElse(null);
    }

}
