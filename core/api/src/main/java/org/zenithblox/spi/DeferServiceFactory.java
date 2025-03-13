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
import org.zenithblox.Producer;

/**
 * Factory to create services such as {@link Producer}s and defer starting the created service, until
 * {@link org.zenithblox.ZwangineContext} has been started.
 */
public interface DeferServiceFactory {

    /**
     * Service factory key.
     */
    String FACTORY = "defer-service-factory";

    /**
     * Creates the {@link Producer} which is deferred started until {@link org.zenithblox.ZwangineContext} is being
     * started.
     * <p/>
     * When the producer is started, it re-lookup the endpoint to capture any changes such as the endpoint has been
     * intercepted. This allows the producer to react and send messages to the updated endpoint.
     *
     * @param  endpoint  the endpoint
     * @return           the producer which will be deferred started until {@link org.zenithblox.ZwangineContext} has
     *                   been started
     * @throws Exception can be thrown if there is an error starting the producer
     */
    Producer createProducer(Endpoint endpoint) throws Exception;

}
