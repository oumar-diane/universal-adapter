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

import org.zenithblox.ZwangineContext;

import java.util.Map;

/**
 * Service Factory for ManagementStrategy
 */
public interface ManagementStrategyFactory {

    /**
     * Creates the {@link ManagementStrategy}.
     *
     * @param  context    the zwangine context
     * @param  properties optional options to set on {@link ManagementAgent}
     * @return            the created strategy
     * @throws Exception  is thrown if error creating the strategy
     */
    ManagementStrategy create(ZwangineContext context, Map<String, Object> properties) throws Exception;

    /**
     * Creates the associated {@link LifecycleStrategy} that the management strategy uses.
     *
     * @param  context   the zwangine context
     * @return           the created lifecycle strategy
     * @throws Exception is thrown if error creating the lifecycle strategy
     */
    LifecycleStrategy createLifecycle(ZwangineContext context) throws Exception;

    /**
     * Setup the management on the {@link ZwangineContext}.
     * <p/>
     * This allows implementations to provide the logic for setting up management on Zwangine.
     *
     * @param zwangineContext the zwangine context
     * @param strategy     the management strategy
     * @param lifecycle    the associated lifecycle strategy (optional)
     */
    void setupManagement(ZwangineContext zwangineContext, ManagementStrategy strategy, LifecycleStrategy lifecycle);

}
