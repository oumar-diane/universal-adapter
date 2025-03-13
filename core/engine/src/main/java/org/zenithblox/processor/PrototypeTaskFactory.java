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
import org.zenithblox.support.PrototypeObjectFactorySupport;

public abstract class PrototypeTaskFactory extends PrototypeObjectFactorySupport<PooledExchangeTask>
        implements PooledExchangeTaskFactory {

    @Override
    public PooledExchangeTask acquire(Exchange exchange, AsyncCallback callback) {
        PooledExchangeTask task = create(exchange, callback);
        task.prepare(exchange, callback);
        return task;
    }

    @Override
    public PooledExchangeTask acquire() {
        throw new UnsupportedOperationException("Not in use");
    }

    @Override
    public boolean release(PooledExchangeTask task) {
        // not pooled so no need to reset task
        return true;
    }

    @Override
    public String toString() {
        return "PrototypeTaskFactory";
    }
}
