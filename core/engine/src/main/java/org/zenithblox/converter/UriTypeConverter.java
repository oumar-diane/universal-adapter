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
package org.zenithblox.converter;

import org.zenithblox.Converter;
import org.zenithblox.TypeConversionException;
import org.zenithblox.TypeConverter;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * A {@link TypeConverter} that converts to and from {@link URI}s.
 */
@Converter(generateBulkLoader = true)
public final class UriTypeConverter {

    private UriTypeConverter() {
        // utility class
    }

    @Converter(order = 1)
    public static String toString(final URI value) {
        return value.toString();
    }

    @Converter(order = 2)
    public static URI toUri(final CharSequence value) {
        final String stringValue = String.valueOf(value);

        try {
            return new URI(stringValue);
        } catch (final URISyntaxException e) {
            throw new TypeConversionException(value, URI.class, e);
        }
    }

}
