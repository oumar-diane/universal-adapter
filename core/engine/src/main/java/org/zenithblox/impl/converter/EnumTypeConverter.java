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

import org.zenithblox.ZwangineContext;
import org.zenithblox.ZwangineContextAware;
import org.zenithblox.Exchange;
import org.zenithblox.TypeConverter;
import org.zenithblox.spi.TypeConverterRegistry;
import org.zenithblox.support.TypeConverterSupport;
import org.zenithblox.util.ObjectHelper;
import org.zenithblox.util.StringHelper;

/**
 * A type converter which is used to convert from String to enum type
 */
public class EnumTypeConverter extends TypeConverterSupport implements ZwangineContextAware {

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
    public <T> T convertTo(Class<T> type, Exchange exchange, Object value) {
        return doConvertTo(type, exchange, value);
    }

    @SuppressWarnings("unchecked")
    private <T> T doConvertTo(Class<T> type, Exchange exchange, Object value) {
        if (type.isEnum()) {
            // is there a direct enum type converter
            TypeConverterRegistry tcr = zwangineContext != null ? zwangineContext.getTypeConverterRegistry() : null;
            if (tcr != null) {
                Class<?> fromType = value.getClass();
                TypeConverter tc = tcr.lookup(type, value.getClass());
                if (tc == null) {
                    // no direct converter but the enum may be a wrapper/primitive variant so try to lookup again
                    Class<?> primitiveType = ObjectHelper.convertWrapperTypeToPrimitiveType(fromType);
                    if (fromType != primitiveType) {
                        tc = tcr.lookup(type, primitiveType);
                    }
                }
                if (tc != null) {
                    return tc.convertTo(type, exchange, value);
                }
            }

            // convert to enum via its string based enum constant
            // (and trim in case there are leading/trailing white-space)
            String text = value.toString().trim();
            Class<Enum<?>> enumClass = (Class<Enum<?>>) type;

            // we want to match case insensitive for enums
            for (Enum<?> enumValue : enumClass.getEnumConstants()) {
                if (enumValue.name().equalsIgnoreCase(text)) {
                    return type.cast(enumValue);
                }
            }

            // add support for using dash or zwangine cased to common used upper cased underscore style for enum constants
            String text2 = StringHelper.asEnumConstantValue(text);
            if (!text2.equals(text)) {
                for (Enum<?> enumValue : enumClass.getEnumConstants()) {
                    if (enumValue.name().equalsIgnoreCase(text2)) {
                        return type.cast(enumValue);
                    }
                }
            }

            throw new IllegalArgumentException("Enum class " + type + " does not have any constant with value: " + text);
        }

        return null;
    }

}
