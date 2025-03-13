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

/**
 * Pluggable condition that must be accepted before Zwangine can continue starting up.
 *
 * This can be used to let Zwangine wait for a specific file to be present, an environment-variable, or some other custom
 * conditions.
 */
public interface StartupCondition {

    /**
     * The name of condition used for logging purposes.
     */
    default String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * Optional logging message to log before waiting for the condition
     */
    default String getWaitMessage() {
        return null;
    }

    /**
     * Optional logging message to log if condition was not meet.
     */
    default String getFailureMessage() {
        return null;
    }

    /**
     * Checks if the condition is accepted
     *
     * @param  zwangineContext the Zwangine context (is not fully initialized)
     * @return              true to continue, false to stop and fail.
     */
    boolean canContinue(ZwangineContext zwangineContext) throws Exception;
}
