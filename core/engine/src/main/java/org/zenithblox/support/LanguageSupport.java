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
package org.zenithblox.support;

import org.zenithblox.*;
import org.zenithblox.spi.Language;
import org.zenithblox.util.IOHelper;
import org.zenithblox.util.TimeUtils;

import java.io.InputStream;
import java.util.List;

/**
 * Base language for {@link Language} implementations.
 */
public abstract class LanguageSupport implements Language, IsSingleton, ZwangineContextAware {

    public static final String RESOURCE = "resource:";

    private static final String[] SIMPLE_FUNCTION_START = new String[] { "${", "$simple{" };

    private ZwangineContext zwangineContext;

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    /**
     * Loads the resource if the given expression is referring to an external resource by using the syntax
     * <tt>resource:scheme:uri<tt>.
     * If the expression is not referring to a resource, then its returned as is.
     * <p/>
     * For example <tt>resource:classpath:mygroovy.groovy</tt> to refer to a groovy script on the classpath.
     *
     * @param  expression                       the expression
     * @return                                  the expression
     * @throws ExpressionIllegalSyntaxException is thrown if error loading the resource
     */
    protected String loadResource(String expression) throws ExpressionIllegalSyntaxException {
        // we can only load static resources (if they are dynamic then simple will load them on-demand)
        if (zwangineContext != null && expression != null && isStaticResource(expression)) {
            String uri = expression.substring(RESOURCE.length());
            InputStream is = null;
            try {
                is = ResourceHelper.resolveMandatoryResourceAsInputStream(zwangineContext, uri);
                expression = zwangineContext.getTypeConverter().mandatoryConvertTo(String.class, is);
            } catch (Exception e) {
                throw new ExpressionIllegalSyntaxException(expression, e);
            } finally {
                IOHelper.close(is);
            }
        }
        return expression;
    }

    /**
     * Does the expression refer to a static resource.
     */
    protected boolean isStaticResource(String expression) {
        return expression.startsWith(RESOURCE) && !hasSimpleFunction(expression);
    }

    /**
     * Does the expression refer to a dynamic resource which uses simple functions.
     */
    protected boolean isDynamicResource(String expression) {
        return expression.startsWith(RESOURCE) && hasSimpleFunction(expression);
    }

    /**
     * Does the expression include a simple function.
     *
     * @param  expression the expression
     * @return            <tt>true</tt> if one or more simple function is included in the expression
     */
    public static boolean hasSimpleFunction(String expression) {
        if (expression != null) {
            return expression.contains(SIMPLE_FUNCTION_START[0]) || expression.contains(SIMPLE_FUNCTION_START[1]);
        }
        return false;
    }

    /**
     * Converts the property to the expected type
     *
     * @param  type         the expected type
     * @param  properties   the options (optimized as object array with hardcoded positions for properties)
     * @param  index        index of the property
     * @param  defaultValue optional default value
     * @return              the value converted to the expected type
     */
    protected <T> T property(Class<T> type, Object[] properties, int index, Object defaultValue) {
        Object value = properties == null || index >= properties.length ? null : properties[index];
        if (value == null) {
            value = defaultValue;
        }

        if (zwangineContext != null && value instanceof String str) {
            value = getZwangineContext().resolvePropertyPlaceholders(str);
        }

        // if the type is not string based and the value is a bean reference, then we need to lookup
        // the bean from the registry
        if (value instanceof String && String.class != type) {
            String text = value.toString();

            if (zwangineContext != null && EndpointHelper.isReferenceParameter(text)) {
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
            if (!text.equalsIgnoreCase("true") && !text.equalsIgnoreCase("false")) {
                throw new IllegalArgumentException(
                        "Cannot convert the String value: " + value + " to type: " + type
                                                   + " as the value is not true or false");
            }
        }
        if (value == null) {
            return null;
        }
        if (zwangineContext != null) {
            return zwangineContext.getTypeConverter().convertTo(type, value);
        } else {
            return (T) value;
        }
    }

}
