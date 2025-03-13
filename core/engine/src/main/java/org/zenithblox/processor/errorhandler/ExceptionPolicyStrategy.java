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
package org.zenithblox.processor.errorhandler;

import org.zenithblox.Exchange;

import java.util.Set;

/**
 * A strategy to determine which {@link ExceptionPolicyKey} should handle the thrown exception.
 *
 * To use a custom implementation, then register the custom {@link ExceptionPolicyStrategy} instance to the Zwangine
 * {@link org.zenithblox.spi.Registry}.
 *
 * @see DefaultExceptionPolicyStrategy for the DefaultExceptionPolicy
 */
public interface ExceptionPolicyStrategy {

    /**
     * Resolves the {@link ExceptionPolicyKey} that should handle the thrown exception.
     *
     * @param  exceptionPolicies the configured exception policies to resolve from
     * @param  exchange          the exchange
     * @param  exception         the exception that was thrown
     * @return                   the resolved exception type to handle this exception, <tt>null</tt> if none found.
     */
    ExceptionPolicyKey getExceptionPolicy(Set<ExceptionPolicyKey> exceptionPolicies, Exchange exchange, Throwable exception);

}
