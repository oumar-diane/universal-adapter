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

import org.zenithblox.TypeConverter;
import org.zenithblox.spi.TypeConverterRegistry;
import org.zenithblox.spi.TypeConvertible;

import java.util.Map;

/**
 * Converter-specific statistics
 */
interface ConverterStatistics extends TypeConverterRegistry.Statistics {

    boolean isStatisticsEnabled();

    /**
     * Increment the count of failed conversions
     */
    void incrementFailed();

    /**
     * Increment the count of noop conversions (i.e.; ones in which a type conversion was not needed)
     */
    void incrementNoop();

    /**
     * Increment the count of conversions that hit the cache
     */
    void incrementHit();

    /**
     * Increment the count of conversions that missed the cache
     */
    void incrementMiss();

    /**
     * Increment the count of total conversion attempts
     */
    void incrementAttempt();

    /**
     * Log the statistics from the converters
     *
     * @param converters    the converters cache instance
     * @param missConverter the type that represents a type conversion miss
     */
    void logMappingStatisticsMessage(Map<TypeConvertible<?, ?>, TypeConverter> converters, TypeConverter missConverter);
}
