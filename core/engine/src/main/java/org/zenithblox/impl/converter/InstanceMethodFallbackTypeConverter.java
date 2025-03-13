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
package org.zenithblox.impl.converter;

import org.zenithblox.Exchange;
import org.zenithblox.RuntimeZwangineException;
import org.zenithblox.spi.TypeConverterRegistry;
import org.zenithblox.support.ObjectHelper;
import org.zenithblox.support.TypeConverterSupport;

import java.lang.reflect.Method;

/**
 * A {@link org.zenithblox.TypeConverter} implementation which instantiates an object so that an instance method can
 * be used as a fallback type converter
 */
public class InstanceMethodFallbackTypeConverter extends TypeConverterSupport {
    private final CachingInjector<?> injector;
    private final Method method;
    private final boolean useExchange;
    private final TypeConverterRegistry registry;
    private final boolean allowNull;

    public InstanceMethodFallbackTypeConverter(CachingInjector<?> injector, Method method, TypeConverterRegistry registry,
                                               boolean allowNull) {
        this.injector = injector;
        this.method = method;
        this.useExchange = method.getParameterCount() == 4;
        this.registry = registry;
        this.allowNull = allowNull;
    }

    @Override
    public String toString() {
        return "InstanceMethodFallbackTypeConverter: " + method;
    }

    @Override
    public boolean allowNull() {
        return allowNull;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T convertTo(Class<T> type, Exchange exchange, Object value) {
        Object instance = injector.newInstance();
        if (instance == null) {
            throw new RuntimeZwangineException("Could not instantiate an instance of: " + type.getCanonicalName());
        }
        return useExchange
                ? (T) ObjectHelper.invokeMethod(method, instance, type, exchange, value, registry) : (T) ObjectHelper
                        .invokeMethod(method, instance, type, value, registry);
    }

}
