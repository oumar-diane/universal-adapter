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

import org.zenithblox.Consumer;
import org.zenithblox.Endpoint;

/**
 * Strategy for a {@link org.zenithblox.PollingConsumer} when polling an {@link Endpoint}.
 * <p/>
 * This pluggable strategy allows to plugin different implementations what to do, most noticeable what to do in case the
 * polling goes wrong. This can be handled in the
 * {@link #rollback(Consumer , Endpoint , int, Exception) rollback} method.
 */
public interface PollingConsumerPollStrategy {

    /**
     * Called when poll is about to begin
     *
     * @param  consumer the consumer
     * @param  endpoint the endpoint being consumed
     * @return          <tt>true</tt> to begin polling, or <tt>false</tt> to skip polling this time.
     */
    boolean begin(Consumer consumer, Endpoint endpoint);

    /**
     * Called when poll is completed successfully
     *
     * @param consumer       the consumer
     * @param endpoint       the endpoint being consumed
     * @param polledMessages number of messages polled, will be <tt>0</tt> if no message was polled at all.
     */
    void commit(Consumer consumer, Endpoint endpoint, int polledMessages);

    /**
     * Called when poll failed
     *
     * @param  consumer     the consumer
     * @param  endpoint     the endpoint being consumed
     * @param  retryCounter current retry attempt, starting from 0.
     * @param  cause        the caused exception
     * @throws Exception    can be used to rethrow the caused exception. Notice that thrown an exception will terminate
     *                      the scheduler and thus Zwangine will not trigger again. So if you want to let the scheduler to
     *                      continue to run then do <b>not</b> throw any exception from this method.
     * @return              whether to retry immediately or not. Return <tt>false</tt> to ignore the problem,
     *                      <tt>true</tt> to try immediately again
     */
    boolean rollback(Consumer consumer, Endpoint endpoint, int retryCounter, Exception cause) throws Exception;

}
