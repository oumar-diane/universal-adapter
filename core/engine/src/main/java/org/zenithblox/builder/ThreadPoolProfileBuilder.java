/*
 * Licensed to the  Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the  License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.zentihblox.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zenithblox.builder;

import org.zenithblox.spi.ThreadPoolProfile;
import org.zenithblox.util.concurrent.ThreadPoolRejectedPolicy;

import java.util.concurrent.TimeUnit;

/**
 * Builder to build {@link org.zenithblox.spi.ThreadPoolProfile}.
 * <p/>
 * Use the {@link #build()} method when done setting up the profile.
 */
public class ThreadPoolProfileBuilder {
    private final ThreadPoolProfile profile;

    public ThreadPoolProfileBuilder(String id) {
        this.profile = new ThreadPoolProfile(id);
    }

    public ThreadPoolProfileBuilder(String id, ThreadPoolProfile origProfile) {
        this.profile = origProfile.clone();
        this.profile.setId(id);
    }

    public ThreadPoolProfileBuilder defaultProfile(Boolean defaultProfile) {
        if (defaultProfile != null) {
            this.profile.setDefaultProfile(defaultProfile);
        }
        return this;
    }

    public ThreadPoolProfileBuilder poolSize(Integer poolSize) {
        if (poolSize != null) {
            profile.setPoolSize(poolSize);
        }
        return this;
    }

    public ThreadPoolProfileBuilder maxPoolSize(Integer maxPoolSize) {
        if (maxPoolSize != null) {
            profile.setMaxPoolSize(maxPoolSize);
        }
        return this;
    }

    public ThreadPoolProfileBuilder keepAliveTime(Long keepAliveTime, TimeUnit timeUnit) {
        if (keepAliveTime != null) {
            profile.setKeepAliveTime(keepAliveTime);
        }
        if (timeUnit != null) {
            profile.setTimeUnit(timeUnit);
        }
        return this;
    }

    public ThreadPoolProfileBuilder keepAliveTime(Long keepAliveTime) {
        if (keepAliveTime != null) {
            profile.setKeepAliveTime(keepAliveTime);
        }
        return this;
    }

    public ThreadPoolProfileBuilder maxQueueSize(Integer maxQueueSize) {
        if (maxQueueSize != null) {
            profile.setMaxQueueSize(maxQueueSize);
        }
        return this;
    }

    public ThreadPoolProfileBuilder allowCoreThreadTimeOut(Boolean allowCoreThreadTimeOut) {
        if (allowCoreThreadTimeOut != null) {
            profile.setAllowCoreThreadTimeOut(allowCoreThreadTimeOut);
        }
        return this;
    }

    public ThreadPoolProfileBuilder rejectedPolicy(ThreadPoolRejectedPolicy rejectedPolicy) {
        if (rejectedPolicy != null) {
            profile.setRejectedPolicy(rejectedPolicy);
        }
        return this;
    }

    /**
     * Builds the thread pool profile
     *
     * @return the thread pool profile
     */
    public ThreadPoolProfile build() {
        return profile;
    }

}
