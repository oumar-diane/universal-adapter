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

import org.zenithblox.*;

import java.util.Collection;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Strategy for lifecycle notifications.
 */
public interface LifecycleStrategy {

    /**
     * Notification on initializing a {@link ZwangineContext}.
     *
     * @param  context                        the zwangine context
     * @throws VetoZwangineContextStartException can be thrown to veto starting {@link ZwangineContext}. Any other runtime
     *                                        exceptions will be logged at <tt>WARN</tt> level by Zwangine will continue
     *                                        starting itself.
     */
    default void onContextInitializing(ZwangineContext context) throws VetoZwangineContextStartException {
    }

    /**
     * Notification on initialized {@link ZwangineContext}.
     *
     * @param  context                        the zwangine context
     * @throws VetoZwangineContextStartException can be thrown to veto starting {@link ZwangineContext}. Any other runtime
     *                                        exceptions will be logged at <tt>WARN</tt> level by Zwangine will continue
     *                                        starting itself.
     */
    default void onContextInitialized(ZwangineContext context) throws VetoZwangineContextStartException {
    }

    /**
     * Notification on starting a {@link ZwangineContext}.
     *
     * @param  context                        the zwangine context
     * @throws VetoZwangineContextStartException can be thrown to veto starting {@link ZwangineContext}. Any other runtime
     *                                        exceptions will be logged at <tt>WARN</tt> level by Zwangine will continue
     *                                        starting itself.
     */
    default void onContextStarting(ZwangineContext context) throws VetoZwangineContextStartException {
    }

    /**
     * Notification on started {@link ZwangineContext}.
     *
     * @param context the zwangine context
     */
    default void onContextStarted(ZwangineContext context) {
    }

    /**
     * Notification on stopping a {@link ZwangineContext}.
     *
     * @param context the zwangine context
     */
    default void onContextStopping(ZwangineContext context) {
    }

    /**
     * Notification on stopped {@link ZwangineContext}.
     *
     * @param context the zwangine context
     */
    default void onContextStopped(ZwangineContext context) {
    }

    /**
     * Notification on adding an {@link Component}.
     *
     * @param name      the unique name of this component
     * @param component the added component
     */
    void onComponentAdd(String name, Component component);

    /**
     * Notification on removing an {@link Component}.
     *
     * @param name      the unique name of this component
     * @param component the removed component
     */
    void onComponentRemove(String name, Component component);

    /**
     * Notification on adding an {@link Endpoint}.
     *
     * @param endpoint the added endpoint
     */
    void onEndpointAdd(Endpoint endpoint);

    /**
     * Notification on removing an {@link Endpoint}.
     *
     * @param endpoint the removed endpoint
     */
    void onEndpointRemove(Endpoint endpoint);

    /**
     * Notification on {@link DataFormat} being resolved from the {@link Registry}
     *
     * @param name       the unique name of the {@link DataFormat}
     * @param dataFormat the resolved {@link DataFormat}
     */
    default void onDataFormatCreated(String name, DataFormat dataFormat) {
    }

    /**
     * Notification on a {@link Language} instance being resolved.
     *
     * @param name     the unique name of the {@link Language}
     * @param language the created {@link Language}
     */
    default void onLanguageCreated(String name, Language language) {
    }

    /**
     * Notification on adding a {@link Service}.
     *
     * @param context the zwangine context
     * @param service the added service
     * @param workflow   the workflow the service belongs to if any possible to determine
     */
    void onServiceAdd(ZwangineContext context, Service service, Workflow workflow);

    /**
     * Notification on removing a {@link Service}.
     *
     * @param context the zwangine context
     * @param service the removed service
     * @param workflow   the workflow the service belongs to if any possible to determine
     */
    void onServiceRemove(ZwangineContext context, Service service, Workflow workflow);

    /**
     * Notification on adding {@link Workflow}(s).
     *
     * @param workflows the added workflows
     */
    void onWorkflowsAdd(Collection<Workflow> workflows);

    /**
     * Notification on removing {@link Workflow}(s).
     *
     * @param workflows the removed workflows
     */
    void onWorkflowsRemove(Collection<Workflow> workflows);

    /**
     * Notification on creating {@link Workflow}(s).
     *
     * @param workflow the created workflow context
     */
    void onWorkflowContextCreate(Workflow workflow);

    /**
     * Notification on adding a thread pool.
     *
     * @param zwangineContext        the zwangine context
     * @param threadPool          the thread pool
     * @param id                  id of the thread pool (can be null in special cases)
     * @param sourceId            id of the source creating the thread pool (can be null in special cases)
     * @param workflowId             id of the workflow for the source (is null if no source)
     * @param threadPoolProfileId id of the thread pool profile, if used for creating this thread pool (can be null)
     */
    void onThreadPoolAdd(
            ZwangineContext zwangineContext, ThreadPoolExecutor threadPool, String id,
            String sourceId, String workflowId, String threadPoolProfileId);

    /**
     * Notification on removing a thread pool.
     *
     * @param zwangineContext the zwangine context
     * @param threadPool   the thread pool
     */
    void onThreadPoolRemove(ZwangineContext zwangineContext, ThreadPoolExecutor threadPool);

}
