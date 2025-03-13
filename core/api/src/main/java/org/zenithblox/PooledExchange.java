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
package org.zenithblox;

import org.zenithblox.spi.ExchangeFactory;

/**
 * Pooled {@link Exchange} which contains the methods and APIs that are not intended for Zwangine end users but used
 * internally by Zwangine for optimizing memory footprint by reusing exchanges created by {@link Consumer}s via
 * {@link ExchangeFactory}.
 */
public interface PooledExchange extends Exchange {

    /**
     * Task to execute when the exchange is done.
     */
    @FunctionalInterface
    interface OnDoneTask {
        void onDone(Exchange exchange);
    }

    /**
     * Registers a task to run when this exchange is done.
     * <p/>
     * <b>Important:</b> This API is NOT intended for Zwangine end users, but used internally by Zwangine itself.
     */
    void onDone(OnDoneTask task);

    /**
     * When the exchange is done being used.
     * <p/>
     * <b>Important:</b> This API is NOT intended for Zwangine end users, but used internally by Zwangine itself.
     */
    void done();

    /**
     * Resets the exchange for reuse with the given created timestamp;
     * <p/>
     * <b>Important:</b> This API is NOT intended for Zwangine end users, but used internally by Zwangine itself.
     */
    void reset(long created);

    /**
     * Whether this exchange was created to auto release when its unit of work is done
     * <p/>
     * <b>Important:</b> This API is NOT intended for Zwangine end users, but used internally by Zwangine itself.
     */
    void setAutoRelease(boolean autoRelease);

    /**
     * Whether this exchange was created to auto release when its unit of work is done
     * <p/>
     * <b>Important:</b> This API is NOT intended for Zwangine end users, but used internally by Zwangine itself.
     */
    boolean isAutoRelease();

}
