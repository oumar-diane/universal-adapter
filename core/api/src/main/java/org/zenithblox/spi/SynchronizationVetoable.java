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
 * A vetoable {@link org.zenithblox.spi.Synchronization}.
 * <p/>
 * When using {@link org.zenithblox.spi.Synchronization} they are normally executed when the
 * {@link Exchange} complete at the end. If the {@link Exchange} is processed
 * asynchronously the {@link org.zenithblox.spi.Synchronization} will be handed over to the next thread. This ensures
 * for example the file consumer will delete the processed file at the very end, when the
 * {@link Exchange} has been completed successfully.
 * <p/>
 * However there may be situations where you do not want to handover certain
 * {@link org.zenithblox.spi.Synchronization}, such as when doing asynchronously request/reply over SEDA or VM
 * endpoints.
 */
public interface SynchronizationVetoable extends Synchronization {

    /**
     * Whether or not handover this synchronization is allowed.
     * <p/>
     * For example when an {@link Exchange} is being workflowd from one thread to another thread, such as
     * using request/reply over SEDA
     *
     * @return <tt>true</tt> to allow handover, <tt>false</tt> to deny.
     */
    boolean allowHandover();

    /**
     * A method to perform optional housekeeping when a Synchronization is being handed over.
     *
     * @param target The Exchange to which the synchronizations are being transferred.
     */
    void beforeHandover(Exchange target);
}
