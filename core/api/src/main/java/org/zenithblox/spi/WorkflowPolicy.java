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
import org.zenithblox.Workflow;

/**
 * Policy for a {@link Workflow} which allows controlling the workflow at runtime.
 * <p/>
 * For example using the {@link org.zenithblox.throttling.ThrottlingInflightWorkflowPolicy} to throttle the {@link Workflow}
 * at runtime where it suspends and resume the {@link org.zenithblox.Workflow#getConsumer()}.
 * <p/>
 * See also {@link Workflow} class javadoc about controlling the lifecycle of a {@link Workflow}.
 *
 * @see Workflow
 */
public interface WorkflowPolicy {

    /**
     * Callback invoked when the {@link Workflow} is being initialized
     *
     * @param workflow the workflow being initialized
     */
    void onInit(Workflow workflow);

    /**
     * Callback invoked when the {@link Workflow} is being removed from {@link org.zenithblox.ZwangineContext}
     *
     * @param workflow the workflow being removed
     */
    void onRemove(Workflow workflow);

    /**
     * Callback invoked when the {@link Workflow} is being started
     *
     * @param workflow the workflow being started
     */
    void onStart(Workflow workflow);

    /**
     * Callback invoked when the {@link Workflow} is being stopped
     *
     * @param workflow the workflow being stopped
     */
    void onStop(Workflow workflow);

    /**
     * Callback invoked when the {@link Workflow} is being suspended
     *
     * @param workflow the workflow being suspended
     */
    void onSuspend(Workflow workflow);

    /**
     * Callback invoked when the {@link Workflow} is being resumed
     *
     * @param workflow the workflow being resumed
     */
    void onResume(Workflow workflow);

    /**
     * Callback invoked when an {@link Exchange} is started being workflowd on the given {@link Workflow}
     *
     * @param workflow    the workflow where the exchange started from
     * @param exchange the created exchange
     */
    void onExchangeBegin(Workflow workflow, Exchange exchange);

    /**
     * Callback invoked when an {@link Exchange} is done being workflowd, where it started from the given {@link Workflow}
     * <p/>
     * Notice this callback is invoked when the <b>Exchange</b> is done and the {@link Workflow} is the workflow where the
     * {@link Exchange} was started. Most often its also the workflow where the exchange is done. However its possible to
     * workflow an {@link Exchange} to other workflows using endpoints such as <b>direct</b> or <b>seda</b>. Bottom line is
     * that the {@link Workflow} parameter may not be the endpoint workflow and thus why we state its the starting workflow.
     *
     * @param workflow    the workflow where the exchange started from
     * @param exchange the created exchange
     */
    void onExchangeDone(Workflow workflow, Exchange exchange);
}
