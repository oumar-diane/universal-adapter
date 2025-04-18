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
package org.zenithblox.clock;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * A clock abstraction used to track pass of time
 */
public interface Clock {

    /**
     * The elapsed time since the creation of the clock
     *
     * @return The elapsed time, in milliseconds, since the creation of the exchange
     */
    long elapsed();

    /**
     * The point in time the clock was created
     *
     * @return The point in time, in milliseconds, the exchange was created.
     * @see    System#currentTimeMillis()
     */
    long getCreated();

    /**
     * Get the creation date/time as with time-zone information
     *
     * @return A ZonedDateTime instance from the computed creation time
     */
    default ZonedDateTime asZonedCreationDateTime() {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(getCreated()), ZoneId.systemDefault());
    }

    /**
     * Get the creation date/time as regular Java Date instance
     *
     * @return A Date instance from the computed creation time
     */
    default Date asDate() {
        return new Date(getCreated());
    }

    /**
     * Get the elapsed duration for this clock in the standard Java Duration
     *
     * @return A new Duration instance representing the elapsed duration for this clock
     */
    default Duration asDuration() {
        return Duration.ofMillis(elapsed());
    }
}
