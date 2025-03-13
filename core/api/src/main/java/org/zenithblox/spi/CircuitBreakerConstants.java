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

public interface CircuitBreakerConstants {

    @Metadata(label = "circuitBreaker", description = "Whether the exchange was processed successfully by the circuit breaker",
              javaType = "boolean")
    String RESPONSE_SUCCESSFUL_EXECUTION = "ZwangineCircuitBreakerSuccessfulExecution";
    @Metadata(label = "circuitBreaker",
              description = "Whether the exchange was processed by the onFallback by the circuit breaker", javaType = "boolean")
    String RESPONSE_FROM_FALLBACK = "ZwangineCircuitBreakerResponseFromFallback";
    @Metadata(label = "circuitBreaker", description = "Whether the exchange was short circuited by the breaker",
              javaType = "boolean")
    String RESPONSE_SHORT_CIRCUITED = "ZwangineCircuitBreakerResponseShortCircuited";
    @Metadata(label = "circuitBreaker", description = "Whether the exchange timed out during processing by the circuit breaker",
              javaType = "boolean")
    String RESPONSE_TIMED_OUT = "ZwangineCircuitBreakerResponseTimedOut";
    @Metadata(label = "circuitBreaker", description = "Whether the circuit breaker rejected processing the exchange",
              javaType = "boolean")
    String RESPONSE_REJECTED = "ZwangineCircuitBreakerResponseRejected";

}
