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

/**
 * Lifecycle API for {@link ZwangineContext}.
 */
public interface ZwangineContextLifecycle extends AutoCloseable {

    /**
     * Starts the {@link ZwangineContext} (<b>important:</b> the start method is not blocked, see more details in the
     * {@link Main} documentation for running Zwangine Standalone).
     * <p/>
     * See more details at the class-level javadoc at {@link ZwangineContext}.
     *
     * @throws RuntimeZwangineException is thrown if starting failed
     */
    void start();

    /**
     * Stop and shutdown the {@link ZwangineContext} (will stop all workflows/components/endpoints etc and clear internal
     * state/cache).
     * <p/>
     * See more details at the class-level javadoc at {@link ZwangineContext}.
     *
     * @throws RuntimeZwangineException is thrown if stopping failed
     */
    void stop();

    /**
     * Whether the ZwangineContext is started
     *
     * @return true if this ZwangineContext has been started
     */
    boolean isStarted();

    /**
     * Whether the ZwangineContext is starting
     *
     * @return true if this ZwangineContext is being started
     */
    boolean isStarting();

    /**
     * Whether the ZwangineContext is stopping
     *
     * @return true if this ZwangineContext is in the process of stopping
     */
    boolean isStopping();

    /**
     * Whether the ZwangineContext is stopped
     *
     * @return true if this ZwangineContext is stopped
     */
    boolean isStopped();

    /**
     * Whether the ZwangineContext is suspending
     *
     * @return true if this ZwangineContext is in the process of suspending
     */
    boolean isSuspending();

    /**
     * Whether the ZwangineContext is suspended
     *
     * @return true if this ZwangineContext is suspended
     */
    boolean isSuspended();

    /**
     * Helper methods so the ZwangineContext knows if it should keep running. Returns <tt>false</tt> if the ZwangineContext is
     * being stopped or is stopped.
     *
     * @return <tt>true</tt> if the ZwangineContext should continue to run.
     */
    boolean isRunAllowed();

    /**
     * Builds the ZwangineContext.
     *
     * This phase is intended for frameworks or runtimes that are capable of performing build-time optimizations such as
     * with zwangine-quarkus.
     */
    void build();

    /**
     * Initializes the ZwangineContext.
     */
    void init();

    /**
     * Suspends the ZwangineContext.
     */
    void suspend();

    /**
     * Resumes the ZwangineContext.
     */
    void resume();

    /**
     * Shutdown the ZwangineContext, which means it cannot be started again.
     * <p/>
     * See more details at the class-level javadoc at {@link ZwangineContext}.
     */
    void shutdown();

    /**
     * Closes (Shutdown) the ZwangineContext, which means it cannot be started again.
     *
     * @throws Exception is thrown if shutdown failed
     */
    void close() throws Exception;

    /**
     * Get the status of this ZwangineContext
     *
     * @return the status
     */
    ServiceStatus getStatus();

}
