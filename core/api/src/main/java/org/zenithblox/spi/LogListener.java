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
package org.zenithblox.spi;

import org.zenithblox.Exchange;
import org.zenithblox.LoggingLevel;

/**
 * An event listener SPI for logging. Listeners are registered into {@link org.zenithblox.processor.LogProcessor} and
 * {@link org.zenithblox.processor.ZwangineLogProcessor} so that the logging events are delivered for both of Log
 * Component and Log EIP.
 */
public interface LogListener {

    /**
     * Invoked right before Log component or Log EIP logs. Note that {@link ZwangineLogger} holds the {@link LoggingLevel}
     * and {@link org.slf4j.Marker}. The listener can check {@link ZwangineLogger#getLevel()} to see in which log level
     * this is going to be logged.
     *
     * @param  exchange    zwangine exchange
     * @param  zwangineLogger {@link ZwangineLogger}
     * @param  message     log message
     * @return             log message, possibly enriched by the listener
     */
    String onLog(Exchange exchange, ZwangineLogger zwangineLogger, String message);

}
