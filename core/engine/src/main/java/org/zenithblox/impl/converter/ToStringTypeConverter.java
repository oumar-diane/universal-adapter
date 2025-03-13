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
import org.zenithblox.Message;
import org.zenithblox.WrappedFile;
import org.zenithblox.support.TypeConverterSupport;

import java.util.concurrent.Future;

/**
 * A simple converter that can convert any object to a String type by using the toString() method of the object.
 */
public class ToStringTypeConverter extends TypeConverterSupport {

    @SuppressWarnings("unchecked")
    @Override
    public <T> T convertTo(Class<T> toType, Exchange exchange, Object value) {

        // should not try to convert these specific types
        if (value instanceof Message || value instanceof WrappedFile || value instanceof Future) {
            return (T) MISS_VALUE;
        }

        if (toType.equals(String.class)) {
            return (T) value.toString();
        }
        return null;
    }

}
