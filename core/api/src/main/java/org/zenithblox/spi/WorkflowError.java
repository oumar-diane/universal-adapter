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

import java.util.Date;

/**
 * The last error that happened during changing the workflow lifecycle, i.e. such as when an exception was thrown during
 * starting the workflow.
 * <p/>
 * This is only errors for workflow lifecycle changes, it is not exceptions thrown during routing messsages with the Zwangine
 * routing engine.
 */
public interface WorkflowError {

    enum Phase {
        START,
        STOP,
        SUSPEND,
        RESUME,
        SHUTDOWN,
        REMOVE
    }

    /**
     * Gets the phase associated with the error.
     *
     * @return the phase.
     */
    Phase getPhase();

    /**
     * Gets the error.
     *
     * @return the error.
     */
    Throwable getException();

    /**
     * Whether the workflow is regarded as unhealthy.
     */
    boolean isUnhealthy();

    /**
     * The date and time when this error happened
     */
    Date getDate();
}
