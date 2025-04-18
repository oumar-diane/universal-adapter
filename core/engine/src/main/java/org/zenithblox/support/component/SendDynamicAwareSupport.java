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
package org.zenithblox.support.component;

import org.zenithblox.ZwangineContext;
import org.zenithblox.Exchange;
import org.zenithblox.spi.EndpointUriFactory;
import org.zenithblox.spi.SendDynamicAware;
import org.zenithblox.support.service.ServiceSupport;
import org.zenithblox.util.StringHelper;
import org.zenithblox.util.URISupport;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Support class for {@link SendDynamicAware} implementations.
 */
public abstract class SendDynamicAwareSupport extends ServiceSupport implements SendDynamicAware {

    private ZwangineContext zwangineContext;
    private Set<String> knownProperties;
    private Set<String> knownPrefixes;
    private String scheme;

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    @Override
    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    @Override
    public String getScheme() {
        return scheme;
    }

    @Override
    public boolean resolveRawParameterValues() {
        return true;
    }

    @Override
    protected void doInit() throws Exception {
        if (knownProperties == null || knownPrefixes == null) {
            // optimize to eager load the list of known properties/prefixes
            EndpointUriFactory factory = getZwangineContext().getZwangineContextExtension().getEndpointUriFactory(getScheme());
            if (factory == null) {
                throw new IllegalStateException("Cannot find EndpointUriFactory for component: " + getScheme());
            }
            knownProperties = factory.propertyNames();
            knownPrefixes = factory.multiValuePrefixes();
        }
    }

    public Map<String, Object> endpointProperties(Exchange exchange, String uri) throws Exception {
        Map<String, Object> properties;
        // optimize as we know its only query parameters that can be dynamic
        Map<String, Object> map = URISupport.parseQuery(URISupport.extractQuery(uri));
        if (map != null && !map.isEmpty() && isLenientProperties()) {
            if (resolveRawParameterValues()) {
                // parameters using raw syntax: RAW(value)
                // should have the token removed, so its only the value we have in parameters, as we are about to create
                // an endpoint and want to have the parameter values without the RAW tokens
                RawParameterHelper.resolveRawParameterValues(exchange.getContext(), map);
            }
            // okay so only add the known properties as they are the non lenient properties
            properties = new LinkedHashMap<>();
            map.forEach((k, v) -> {
                boolean accept = knownProperties.contains(k);
                // we should put the key from a multi-value (prefix) in the
                // properties too, or the property may be lost
                if (!accept && !knownPrefixes.isEmpty()) {
                    accept = knownPrefixes.stream().anyMatch(k::startsWith);
                }
                if (accept) {
                    properties.put(k, v);
                }
            });
        } else {
            properties = map;
        }

        return properties;
    }

    public Map<String, Object> endpointLenientProperties(Exchange exchange, String uri) throws Exception {
        Map<String, Object> properties;
        // optimize as we know its only query parameters that can be dynamic
        Map<String, Object> map = URISupport.parseQuery(URISupport.extractQuery(uri));
        if (map != null && !map.isEmpty()) {
            if (resolveRawParameterValues()) {
                // parameters using raw syntax: RAW(value)
                // should have the token removed, so its only the value we have in parameters, as we are about to create
                // an endpoint and want to have the parameter values without the RAW tokens
                RawParameterHelper.resolveRawParameterValues(exchange.getContext(), map);
            }
            properties = new LinkedHashMap<>();
            map.forEach((k, v) -> {
                // we only accept if the key is not an existing known property
                // or that the key is not from a multi-value (prefix)
                boolean accept = !knownProperties.contains(k);
                if (accept && !knownPrefixes.isEmpty()) {
                    accept = knownPrefixes.stream().noneMatch(k::startsWith);
                }
                if (accept) {
                    properties.put(k, v.toString());
                }
            });
        } else {
            properties = map;
        }
        return properties;
    }

    public String asEndpointUri(Exchange exchange, String uri, Map<String, Object> properties) throws Exception {
        String query = URISupport.createQueryString(properties, false);
        return StringHelper.before(uri, "?", uri) + "?" + query;
    }

}
