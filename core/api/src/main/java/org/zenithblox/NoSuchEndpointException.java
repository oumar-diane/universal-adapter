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

import static org.zenithblox.util.URISupport.sanitizeUri;

/**
 * A runtime exception thrown if a routing processor such as a recipient list is unable to resolve an {@link Endpoint}
 * from a URI.
 */
public class NoSuchEndpointException extends RuntimeZwangineException {

    private final String uri;

    public NoSuchEndpointException(String uri) {
        super("No endpoint could be found for: " + sanitizeUri(uri)
              + ", please check your classpath contains the needed Zwangine component jar.");
        this.uri = sanitizeUri(uri);
    }

    public NoSuchEndpointException(String uri, String resolveMethod) {
        super("No endpoint could be found for: " + sanitizeUri(uri)
              + ", please " + resolveMethod);
        this.uri = sanitizeUri(uri);
    }

    public String getUri() {
        return uri;
    }
}
