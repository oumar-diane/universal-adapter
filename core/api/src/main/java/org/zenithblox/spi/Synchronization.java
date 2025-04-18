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
 * Provides a hook for custom {@link org.zenithblox.Processor} or {@link org.zenithblox.Component} instances to
 * respond to completed or failed processing of an {@link Exchange} rather like Spring's
 * <a href="http://static.springframework.org/spring/docs/2.5.x/api/org/springframework/transaction/
 * support/TransactionSynchronization.html">TransactionSynchronization</a>
 */
public interface Synchronization {

    /**
     * Called when the processing of the message exchange is complete
     *
     * @param exchange the exchange being processed
     */
    void onComplete(Exchange exchange);

    /**
     * Called when the processing of the message exchange has failed for some reason. The exception which caused the
     * problem is in {@link Exchange#getException()}.
     *
     * @param exchange the exchange being processed
     */
    void onFailure(Exchange exchange);

    /**
     * Get an optional {@link SynchronizationWorkflowAware} for this synchronization
     *
     * @return An instance of {@link SynchronizationWorkflowAware} or null if unset for this synchronization
     */
    default SynchronizationWorkflowAware getWorkflowSynchronization() {
        return null;
    }

}
