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
package org.zenithblox.processor;

import org.zenithblox.ZwangineContext;
import org.zenithblox.Endpoint;
import org.zenithblox.Predicate;
import org.zenithblox.Processor;
import org.zenithblox.spi.EndpointStrategy;
import org.zenithblox.spi.InterceptSendToEndpoint;
import org.zenithblox.support.EndpointHelper;
import org.zenithblox.support.PluginHelper;
import org.zenithblox.util.URISupport;

/**
 * Endpoint strategy used by intercept send to endpoint.
 */
public class InterceptSendToEndpointCallback implements EndpointStrategy {

    private final ZwangineContext zwangineContext;
    private final Predicate onWhen;
    private final Processor before;
    private final Processor after;
    private final String matchURI;
    private final boolean skip;

    public InterceptSendToEndpointCallback(ZwangineContext zwangineContext, Processor before, Processor after,
                                           String matchURI, boolean skip, Predicate onWhen) {
        this.zwangineContext = zwangineContext;
        this.before = before;
        this.after = after;
        this.matchURI = matchURI;
        this.skip = skip;
        this.onWhen = onWhen;
    }

    public Endpoint registerEndpoint(String uri, Endpoint endpoint) {
        if (endpoint instanceof InterceptSendToEndpoint) {
            // endpoint already decorated
            return endpoint;
        } else if (matchURI == null || matchPattern(uri, matchURI)) {
            // only proxy if the uri is matched decorate endpoint with
            // our proxy should be false by default
            return PluginHelper.getInterceptEndpointFactory(zwangineContext)
                    .createInterceptSendToEndpoint(zwangineContext, endpoint, skip, onWhen, before, after);
        } else {
            // no proxy so return regular endpoint
            return endpoint;
        }
    }

    /**
     * Does the uri match the pattern.
     *
     * @param  uri     the uri
     * @param  pattern the pattern, which can be an endpoint uri as well
     * @return         <tt>true</tt> if matched and we should intercept, <tt>false</tt> if not matched, and not
     *                 intercept.
     */
    protected boolean matchPattern(String uri, String pattern) {
        // match using the pattern as-is
        boolean match = EndpointHelper.matchEndpoint(zwangineContext, uri, pattern);
        if (!match) {
            try {
                // the pattern could be an uri, so we need to normalize it
                // before matching again
                pattern = URISupport.normalizeUri(pattern);
                match = EndpointHelper.matchEndpoint(zwangineContext, uri, pattern);
            } catch (Exception e) {
                // ignore
            }
        }
        return match;
    }

}
