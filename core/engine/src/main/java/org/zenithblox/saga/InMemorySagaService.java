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
package org.zenithblox.saga;

import org.zenithblox.ZwangineContext;
import org.zenithblox.Exchange;
import org.zenithblox.support.service.ServiceSupport;
import org.zenithblox.util.ObjectHelper;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;

/**
 * An in-memory implementation of a saga service.
 */
public class InMemorySagaService extends ServiceSupport implements ZwangineSagaService {

    public static final int DEFAULT_MAX_RETRY_ATTEMPTS = 5;

    public static final long DEFAULT_RETRY_DELAY_IN_MILLISECONDS = 5000;

    private ZwangineContext zwangineContext;

    private final Map<String, ZwangineSagaCoordinator> coordinators = new ConcurrentHashMap<>();

    private ScheduledExecutorService executorService;

    private int maxRetryAttempts = DEFAULT_MAX_RETRY_ATTEMPTS;

    private long retryDelayInMilliseconds = DEFAULT_RETRY_DELAY_IN_MILLISECONDS;

    @Override
    public CompletableFuture<ZwangineSagaCoordinator> newSaga(Exchange exchange) {
        ObjectHelper.notNull(zwangineContext, "zwangineContext");

        String uuid = zwangineContext.getUuidGenerator().generateUuid();
        ZwangineSagaCoordinator coordinator = new InMemorySagaCoordinator(zwangineContext, this, uuid);
        coordinators.put(uuid, coordinator);

        return CompletableFuture.completedFuture(coordinator);
    }

    @Override
    public CompletableFuture<ZwangineSagaCoordinator> getSaga(String id) {
        return CompletableFuture.completedFuture(coordinators.get(id));
    }

    @Override
    public void registerStep(ZwangineSagaStep step) {
        // do nothing
    }

    @Override
    protected void doStart() throws Exception {
        if (this.executorService == null) {
            this.executorService = zwangineContext.getExecutorServiceManager()
                    .newDefaultScheduledThreadPool(this, "saga");
        }
    }

    @Override
    protected void doStop() throws Exception {
        if (this.executorService != null) {
            zwangineContext.getExecutorServiceManager().shutdownGraceful(this.executorService);
            this.executorService = null;
        }
    }

    public ScheduledExecutorService getExecutorService() {
        return executorService;
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    public int getMaxRetryAttempts() {
        return maxRetryAttempts;
    }

    public void setMaxRetryAttempts(int maxRetryAttempts) {
        this.maxRetryAttempts = maxRetryAttempts;
    }

    public long getRetryDelayInMilliseconds() {
        return retryDelayInMilliseconds;
    }

    public void setRetryDelayInMilliseconds(long retryDelayInMilliseconds) {
        this.retryDelayInMilliseconds = retryDelayInMilliseconds;
    }

}
