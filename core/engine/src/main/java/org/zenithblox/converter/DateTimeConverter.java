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

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Date and time related converters.
 */
@Converter(generateBulkLoader = true)
public final class DateTimeConverter {

    /**
     * Utility classes should not have a public constructor.
     */
    private DateTimeConverter() {
    }

    @Converter(order = 1)
    public static TimeZone toTimeZone(String s) {
        return TimeZone.getTimeZone(s);
    }

    @Converter(order = 2)
    public static Date toDate(Long l) {
        return new Date(l);
    }

    @Converter(order = 3)
    public static Long toLong(Date date) {
        return date.getTime();
    }

    @Converter(order = 4)
    public static TimeUnit toTimeUnit(String unit) {
        String match = unit.toUpperCase(Locale.ROOT).trim();
        return switch (match) {
            case "DAYS" -> TimeUnit.DAYS;
            case "HOURS" -> TimeUnit.HOURS;
            case "MINUTES" -> TimeUnit.MINUTES;
            case "SECONDS" -> TimeUnit.SECONDS;
            case "MILLISECONDS" -> TimeUnit.MILLISECONDS;
            case "NANOSECONDS" -> TimeUnit.NANOSECONDS;
            default -> throw new IllegalStateException("Unexpected value: " + unit);
        };
    }
}
