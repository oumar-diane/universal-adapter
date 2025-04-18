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

import org.zenithblox.Endpoint;
import org.zenithblox.Predicate;
import org.zenithblox.Processor;

/**
 * This is an endpoint when sending to it, is intercepted and is workflowd in a detour, with the following flow: before,
 * send to original endpoint (can be skipped), after (optional).
 */
public interface InterceptSendToEndpoint extends Endpoint {

    /**
     * The original endpoint which was intercepted.
     */
    Endpoint getOriginalEndpoint();

    /**
     * Optional predicate that must match to trigger this interceptor.
     */
    Predicate getOnWhen();

    /**
     * Optional predicate that must match to trigger this interceptor.
     */
    void setOnWhen(Predicate onWhen);

    /**
     * The processor for routing in a detour before sending to the original endpoint.
     */
    Processor getBefore();

    /**
     * Sets the processor for routing in a detour before sending to the original endpoint.
     */
    void setBefore(Processor before);

    /**
     * The processor (optional) for routing after sending to the original endpoint.
     */
    Processor getAfter();

    /**
     * Sets the processor (optional) for routing after sending to the original endpoint.
     */
    void setAfter(Processor after);

    /**
     * Whether to skip sending to the original endpoint.
     */
    boolean isSkip();

}
