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

import org.zenithblox.ZwangineContext;
import org.zenithblox.Endpoint;
import org.zenithblox.Predicate;
import org.zenithblox.Processor;

/**
 * A factory to create {@link Endpoint} which are intercepted.
 */
public interface InterceptEndpointFactory {

    /**
     * Creates an endpoint when intercepting sending to an endpoint (detour).
     *
     * @param  zwangineContext the zwangine context
     * @param  endpoint     the endpoint to intercept
     * @param  skip         whether to skip sending to the original endpoint
     * @param  onWhen       optional predicate to trigger this interceptor
     * @param  before       the processor to execute before intercepting
     * @param  after        the processor to execute after intercepted
     * @return              the endpoint with intercepting behaviour
     */
    Endpoint createInterceptSendToEndpoint(
            ZwangineContext zwangineContext, Endpoint endpoint, boolean skip,
            Predicate onWhen, Processor before, Processor after);

}
