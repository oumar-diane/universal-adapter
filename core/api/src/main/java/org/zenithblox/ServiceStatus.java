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

import java.io.Serializable;

/**
 * Represents the status of a {@link Service} instance
 */
public enum ServiceStatus implements Serializable {
    Initializing,
    Initialized,
    Starting,
    Started,
    Stopping,
    Stopped,
    Suspending,
    Suspended;

    public boolean isStartable() {
        return this == Initialized || this == Stopped || this == Suspended;
    }

    public boolean isStoppable() {
        return this == Started || this == Suspended;
    }

    public boolean isSuspendable() {
        return this == Started;
    }

    public boolean isInitializing() {
        return this == Initializing;
    }

    public boolean isInitialized() {
        return this == Initialized;
    }

    public boolean isStarting() {
        return this == Starting;
    }

    public boolean isStarted() {
        return this == Started;
    }

    public boolean isStopping() {
        return this == Stopping;
    }

    public boolean isStopped() {
        return this == Stopped;
    }

    public boolean isSuspending() {
        return this == Suspending;
    }

    public boolean isSuspended() {
        return this == Suspended;
    }

}
