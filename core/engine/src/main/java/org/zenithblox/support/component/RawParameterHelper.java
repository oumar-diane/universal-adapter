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
import org.zenithblox.support.ExchangeHelper;
import org.zenithblox.util.URISupport;

import java.util.Map;

public final class RawParameterHelper {

    private RawParameterHelper() {
    }

    /**
     * Traverses the given parameters, and resolve any parameter values which uses the RAW token syntax:
     * <tt>key=RAW(value)</tt>. This method will then remove the RAW tokens, and replace the content of the value, with
     * just the value.
     *
     * The value can refer to a simple expression using $simple{xxx} syntax which allows to refer to passwords defined
     * as ENV variables etc.
     *
     * @param zwangineContext the zwangine context
     * @param parameters   the uri parameters
     */
    public static void resolveRawParameterValues(ZwangineContext zwangineContext, Map<String, Object> parameters) {
        URISupport.resolveRawParameterValues(parameters, s -> {
            if (s != null && s.contains("$simple{")) {
                Exchange dummy = ExchangeHelper.getDummy(zwangineContext);
                s = zwangineContext.resolveLanguage("simple").createExpression(s).evaluate(dummy, String.class);
            }
            return s;
        });
    }
}
