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
package org.zenithblox.processor;

import org.zenithblox.AsyncCallback;
import org.zenithblox.Exchange;
import org.zenithblox.spi.PooledObjectFactory;

/**
 * Factory to create {@link PooledExchangeTask}.
 *
 * @see PooledExchangeTask
 */
public interface PooledExchangeTaskFactory extends PooledObjectFactory<PooledExchangeTask> {

    /**
     * Creates a new task to use for processing the exchange.
     *
     * @param  exchange the current exchange
     * @param  callback the callback for the exchange
     * @return          the task
     */
    PooledExchangeTask create(Exchange exchange, AsyncCallback callback);

    /**
     * Attempts to acquire a pooled task to use for processing the exchange, if not possible then a new task is created.
     *
     * @param  exchange the current exchange
     * @param  callback the callback for the exchange
     * @return          the task
     */
    PooledExchangeTask acquire(Exchange exchange, AsyncCallback callback);

    /**
     * Releases the task after its done being used
     *
     * @param  task the task
     * @return      true if the task was released, and false if the task failed to be released or no space in pool, and
     *              the task was discarded.
     */
    boolean release(PooledExchangeTask task);
}
