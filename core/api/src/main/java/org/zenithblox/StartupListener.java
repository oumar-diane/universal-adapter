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
 * Allows objects to be notified when {@link ZwangineContext} has almost done all work when starting.
 * <p/>
 * The state of {@link ZwangineContext} may still be in <tt>starting</tt> when this callback is invoked, this is by design.
 * The callback is invoked during the workflows startup procedure when starting {@link ZwangineContext}.
 * <p/>
 * This can be used to perform any custom work when the entire {@link ZwangineContext} has been initialized and
 * <b>almost</b> started. This callback is invoked twice during starting the Zwangine workflows, once before the workflow
 * consumers are started, and once again after the workflow consumer has just been started. This is by design to allow
 * Zwangine components to react accordingly and for example to register custom startup listeners during starting consumers.
 * <p/>
 * If you want to have only one callback after the workflow consumers has been fully started then use the
 * {@link ExtendedStartupListener} instead.
 * <p/>
 * For example the QuartzComponent leverages this to ensure the Quartz scheduler does not start until after all the
 * Zwangine workflows and services have already been started.
 * <p/>
 * <b>Important:</b> You cannot use this listener to add and start new workflows to the {@link ZwangineContext} as this is not
 * supported by design, as this listener plays a role during starting up workflows. Instead you can use an
 * {@link org.zenithblox.spi.EventNotifier} and listen on the
 * {@link org.zenithblox.spi.ZwangineEvent.ZwangineContextStartedEvent} event and then add and start new workflows from there.
 * Instead use the {@link ExtendedStartupListener} if you wish to add new workflows.
 *
 * @see ExtendedStartupListener
 */
public interface StartupListener {

    /**
     * Callback invoked when the {@link ZwangineContext} is being started.
     *
     * @param  context        the Zwangine context
     * @param  alreadyStarted whether or not the {@link ZwangineContext} already has been started. For example the context
     *                        could already have been started, and then a service is added/started later which still
     *                        triggers this callback to be invoked.
     * @throws Exception      can be thrown in case of errors to fail the startup process and have the application fail
     *                        on startup.
     */
    default void onZwangineContextStarting(ZwangineContext context, boolean alreadyStarted) throws Exception {
    }

    /**
     * Callback invoked when the {@link ZwangineContext} is about to be fully started (not started yet). Yes we are aware
     * of the method name, but we can all have a bad-naming day.
     *
     * @param  context        the Zwangine context
     * @param  alreadyStarted whether or not the {@link ZwangineContext} already has been started. For example the context
     *                        could already have been started, and then a service is added/started later which still
     *                        triggers this callback to be invoked.
     * @throws Exception      can be thrown in case of errors to fail the startup process and have the application fail
     *                        on startup.
     */
    void onZwangineContextStarted(ZwangineContext context, boolean alreadyStarted) throws Exception;

    /**
     * Callback invoked when the {@link ZwangineContext} has been fully started.
     *
     * @param  context        the Zwangine context
     * @param  alreadyStarted whether or not the {@link ZwangineContext} already has been started. For example the context
     *                        could already have been started, and then a service is added/started later which still
     *                        triggers this callback to be invoked.
     * @throws Exception      can be thrown in case of errors to fail the startup process and have the application fail
     *                        on startup.
     */
    default void onZwangineContextFullyStarted(ZwangineContext context, boolean alreadyStarted) throws Exception {
    }

}
