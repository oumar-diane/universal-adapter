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

/**
 * Allows a {@link org.zenithblox.Service} to prepare for shutdown.
 * <p/>
 * <b>Important: </b> Implementators of this interface must be a {@link org.zenithblox.Service} as well.
 * <p/>
 * This allows {@link org.zenithblox.Processor}s to prepare for shutdown, such as when
 * {@link org.zenithblox.ZwangineContext} or a {@link org.zenithblox.Workflow} is shutting down. The
 * {@link org.zenithblox.Processor} could be a stateful EIP such as the
 * {@link org.zenithblox.processor.aggregate.AggregateProcessor}, allowing it to do custom work to prepare for
 * shutdown.
 */
public interface ShutdownPrepared {

    /**
     * Prepares for stop/shutdown.
     * <p/>
     * The {@link ShutdownStrategy} supports preparing for shutdown using two steps. First a regular preparation, where
     * the given forced parameter will be <tt>false</tt>. And if the shutdown times out, then the
     * {@link ShutdownStrategy} performs a more aggressive shutdown, calling this method a second time with
     * <tt>true</tt> for the given forced parameter. For example by graceful stopping any threads or the likes.
     * <p/>
     * In addition a service can also be suspended (not stopped), and when this happens the parameter
     * <tt>suspendOnly</tt> has the value <tt>true</tt>. This can be used to prepare the service for suspension, such as
     * marking a worker thread to skip action.
     * <p/>
     * For forced shutdown, then the service is expected to aggressively shutdown any child services, such as thread
     * pools etc. This is the last chance it has to perform such duties.
     *
     * @param suspendOnly <tt>true</tt> if the intention is to only suspend the service, and not stop/shutdown the
     *                    service.
     * @param forced      <tt>true</tt> is forcing a more aggressive shutdown, <tt>false</tt> is for preparing to
     *                    shutdown.
     */
    void prepareShutdown(boolean suspendOnly, boolean forced);

}
