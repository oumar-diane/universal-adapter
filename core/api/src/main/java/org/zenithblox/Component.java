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
package org.zenithblox;

import org.zenithblox.component.extension.ComponentExtension;
import org.zenithblox.spi.PropertyConfigurer;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * A <a href="http://zwangine.zwangine.org/component.html">component</a> is a factory of {@link Endpoint} objects.
 */
public interface Component extends ZwangineContextAware, Service {

    /**
     * Attempt to resolve an endpoint for the given URI if the component is capable of handling the URI.
     * <p/>
     * See {@link #useRawUri()} for controlling whether the passed in uri should be as-is (raw), or encoded (default).
     *
     * @param  uri       the URI to create; either raw or encoded (default)
     * @return           a newly created {@link Endpoint} or null if this component cannot create {@link Endpoint}
     *                   instances using the given uri
     * @throws Exception is thrown if error creating the endpoint
     * @see              #useRawUri()
     */
    Endpoint createEndpoint(String uri) throws Exception;

    /**
     * Attempt to resolve an endpoint for the given URI if the component is capable of handling the URI.
     * <p/>
     * See {@link #useRawUri()} for controlling whether the passed in uri should be as-is (raw), or encoded (default).
     *
     * @param  uri        the URI to create; either raw or encoded (default)
     * @param  parameters the parameters for the endpoint
     * @return            a newly created {@link Endpoint} or null if this component cannot create {@link Endpoint}
     *                    instances using the given uri
     * @throws Exception  is thrown if error creating the endpoint
     * @see               #useRawUri()
     */
    Endpoint createEndpoint(String uri, Map<String, Object> parameters) throws Exception;

    /**
     * Whether to use raw or encoded uri, when creating endpoints.
     * <p/>
     * <b>Notice:</b> When using raw uris, then the parameter values is raw as well.
     *
     * @return <tt>true</tt> to use raw uris, <tt>false</tt> to use encoded uris (default).
     */
    boolean useRawUri();

    /**
     * Gets the component {@link PropertyConfigurer}.
     *
     * @return the configurer, or <tt>null</tt> if the component does not support using property configurer.
     */
    default PropertyConfigurer getComponentPropertyConfigurer() {
        return null;
    }

    /**
     * Gets the endpoint {@link PropertyConfigurer}.
     *
     * @return the configurer, or <tt>null</tt> if the endpoint does not support using property configurer.
     */
    default PropertyConfigurer getEndpointPropertyConfigurer() {
        return null;
    }

    /**
     * Gets a list of supported extensions.
     *
     * @return the list of extensions.
     */
    default Collection<Class<? extends ComponentExtension>> getSupportedExtensions() {
        return Collections.emptyList();
    }

    /**
     * Gets the extension of the given type.
     *
     * @param  extensionType tye type of the extensions
     * @return               an optional extension
     */
    default <T extends ComponentExtension> Optional<T> getExtension(Class<T> extensionType) {
        return Optional.empty();
    }

    /**
     * Set the {@link Component} context if the component is an instance of {@link ComponentAware}.
     */
    static <T> T trySetComponent(T object, Component component) {
        if (object instanceof ComponentAware componentAware) {
            componentAware.setComponent(component);
        }

        return object;
    }

    /**
     * Gets the default name of the component.
     */
    default String getDefaultName() {
        return null;
    }

    /**
     * Whether autowiring is enabled. This is used for automatic autowiring options (the option must be marked as
     * autowired) by looking up in the registry to find if there is a single instance of matching type, which then gets
     * configured on the component. This can be used for automatic configuring JDBC data sources, JMS connection
     * factories, AWS Clients, etc.
     */
    default boolean isAutowiredEnabled() {
        return true;
    }

}
