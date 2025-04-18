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

/**
 * An advice (before and after) to execute cross-cutting functionality in the Zwangine routing engine.
 * <p/>
 * The Zwangine routing engine will execute the {@link #before(org.zenithblox.Exchange)} and
 * {@link #after(org.zenithblox.Exchange, Object)} methods during routing in correct order.
 *
 * @param <T>
 * @see       org.zenithblox.impl.engine.ZwangineInternalProcessor
 */
public interface ZwangineInternalProcessorAdvice<T> {

    /**
     * Callback executed before processing a step in the workflow.
     *
     * @param  exchange  the current exchange
     * @return           any state to keep and provide as data to the {@link #after(org.zenithblox.Exchange, Object)}
     *                   method, or use <tt>null</tt> for no state.
     * @throws Exception is thrown if error during the call.
     */
    T before(Exchange exchange) throws Exception;

    /**
     * Callback executed after processing a step in the workflow.
     *
     * @param  exchange  the current exchange
     * @param  data      the state, if any, returned in the {@link #before(org.zenithblox.Exchange)} method.
     * @throws Exception is thrown if error during the call.
     */
    void after(Exchange exchange, T data) throws Exception;

    /**
     * Whether this advice has state or not.
     */
    default boolean hasState() {
        return true;
    }

}
