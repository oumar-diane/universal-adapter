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
import org.zenithblox.NoSuchBeanException;
import org.zenithblox.NoTypeConversionAvailableException;
import org.zenithblox.RuntimeZwangineException;
import org.zenithblox.support.EndpointHelper;
import org.zenithblox.util.TimeUtils;

import java.util.List;

/**
 * Base class used by Zwangine Package Maven Plugin when it generates source code for fast property configurations via
 * {@link org.zenithblox.spi.PropertyConfigurer}.
 */
public abstract class PropertyConfigurerSupport {

    /**
     * A special magic value that are used by tooling such as zwangine-jbang export
     */
    public static final String MAGIC_VALUE = "@@ZwangineMagicValue@@";

    /**
     * Converts the property to the expected type
     *
     * @param  zwangineContext the zwangine context
     * @param  type         the expected type
     * @param  value        the value
     * @return              the value converted to the expected type
     */
    public static <T> T property(ZwangineContext zwangineContext, Class<T> type, Object value) {
        // if the type is not string based and the value is a bean reference, then we need to lookup
        // the bean from the registry
        if (value instanceof String && String.class != type) {
            String text = value.toString();

            if (EndpointHelper.isReferenceParameter(text)) {
                Object obj;
                // special for a list where we refer to beans which can be either a list or a single element
                // so use Object.class as type
                if (type == List.class) {
                    obj = EndpointHelper.resolveReferenceListParameter(zwangineContext, text, Object.class);
                } else {
                    obj = EndpointHelper.resolveReferenceParameter(zwangineContext, text, type);
                }
                if (obj == null) {
                    // no bean found so throw an exception
                    throw new NoSuchBeanException(text, type.getName());
                }
                value = obj;
            } else if (type == long.class || type == Long.class || type == int.class || type == Integer.class) {
                Object obj = null;
                // string to long/int then it may be a duration where we can convert the value to milli seconds
                // it may be a time pattern, such as 5s for 5 seconds = 5000
                try {
                    long num = TimeUtils.toMilliSeconds(text);
                    if (type == int.class || type == Integer.class) {
                        // need to cast to int
                        obj = (int) num;
                    } else {
                        obj = num;
                    }
                } catch (IllegalArgumentException e) {
                    // ignore
                }
                if (obj != null) {
                    value = obj;
                }
            }
        }

        // special for boolean values with string values as we only want to accept "true" or "false"
        if ((type == Boolean.class || type == boolean.class) && value instanceof String text) {
            if (!MAGIC_VALUE.equals(value) && !text.equalsIgnoreCase("true") && !text.equalsIgnoreCase("false")) {
                throw new IllegalArgumentException(
                        "Cannot convert the String value: " + value + " to type: " + type
                                                   + " as the value is not true or false");
            }
        }

        if (value != null) {
            try {
                if (MAGIC_VALUE.equals(value) && boolean.class == type) {
                    value = "true";
                }
                return zwangineContext.getTypeConverter().mandatoryConvertTo(type, value);
            } catch (NoTypeConversionAvailableException e) {
                throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
            }
        } else {
            return null;
        }
    }

}
