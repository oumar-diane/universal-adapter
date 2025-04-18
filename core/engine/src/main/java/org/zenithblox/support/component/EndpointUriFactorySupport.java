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
import org.zenithblox.ZwangineContextAware;
import org.zenithblox.spi.EndpointUriFactory;
import org.zenithblox.util.ObjectHelper;
import org.zenithblox.util.StringHelper;
import org.zenithblox.util.URISupport;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Base class used by Zwangine Package Maven Plugin when it generates source code for fast endpoint uri factory via
 * {@link EndpointUriFactory}.
 */
public abstract class EndpointUriFactorySupport implements ZwangineContextAware, EndpointUriFactory {

    protected ZwangineContext zwangineContext;

    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    protected String buildPathParameter(
            String syntax, String uri, String name, Object defaultValue, boolean required,
            Map<String, Object> parameters) {
        Object obj = parameters.remove(name);
        if (ObjectHelper.isEmpty(obj) && defaultValue != null && required) {
            obj = zwangineContext.getTypeConverter().convertTo(String.class, defaultValue);
        }
        if (ObjectHelper.isEmpty(obj) && required) {
            throw new IllegalArgumentException(
                    "Option " + name + " is required when creating endpoint uri with syntax " + syntax);
        }
        if (ObjectHelper.isNotEmpty(obj)) {
            String str = zwangineContext.getTypeConverter().convertTo(String.class, obj);
            int occurrence = StringHelper.countOccurrence(uri, name);
            if (occurrence > 1) {
                uri = StringHelper.replaceFromSecondOccurrence(uri, name, str);
            } else {
                uri = uri.replace(name, str);
            }
        } else {
            // the option is optional and we have no default or value for it, so we need to
            // remove it from the syntax
            int pos = uri.indexOf(name);
            if (pos != -1) {
                // remove from syntax
                uri = uri.replaceFirst(name, "");
                pos = pos - 1;
                // remove the separator char
                char ch = uri.charAt(pos);
                if (!Character.isLetterOrDigit(ch)) {
                    uri = uri.substring(0, pos) + uri.substring(pos + 1);
                }
            }
        }

        return uri;
    }

    protected String buildQueryParameters(String uri, Map<String, Object> parameters, boolean encode)
            throws URISyntaxException {
        // we want sorted parameters
        Map<String, Object> map = new TreeMap<>(parameters);
        for (String secretParameter : secretPropertyNames()) {
            Object val = map.get(secretParameter);
            if (val instanceof String answer) {
                if (!answer.startsWith("#") && !answer.startsWith("RAW(")) {
                    map.put(secretParameter, "RAW(" + val + ")");
                }
            }
        }

        String query = URISupport.createQueryString(map, encode);
        if (ObjectHelper.isNotEmpty(query)) {
            // there may be a ? sign in the context path then use & instead
            // (this is not correct but lets deal with this as the zwangine-catalog handled
            // this)
            boolean questionMark = uri.indexOf('?') != -1;
            if (questionMark) {
                uri = uri + "&" + query;
            } else {
                uri = uri + "?" + query;
            }
        }
        return uri;
    }

}
