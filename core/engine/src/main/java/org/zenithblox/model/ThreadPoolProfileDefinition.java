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
package org.zenithblox.model;

import org.zenithblox.spi.Metadata;
import org.zenithblox.util.concurrent.ThreadPoolRejectedPolicy;

import java.util.concurrent.TimeUnit;

/**
 * To configure thread pools
 */
@Metadata(label = "configuration")
public class ThreadPoolProfileDefinition extends OptionalIdentifiedDefinition<ThreadPoolProfileDefinition> {

    @Metadata(label = "advanced", javaType = "java.lang.Boolean")
    private String defaultProfile;
    @Metadata(javaType = "java.lang.Integer")
    private String poolSize;
    @Metadata(javaType = "java.lang.Integer")
    private String maxPoolSize;
    @Metadata(javaType = "java.lang.Long")
    private String keepAliveTime;
    @Metadata(label = "advanced", javaType = "java.util.concurrent.TimeUnit",
              enums = "NANOSECONDS,MICROSECONDS,MILLISECONDS,SECONDS,MINUTES,HOURS,DAYS")
    private String timeUnit;
    @Metadata(javaType = "java.lang.Integer")
    private String maxQueueSize;
    @Metadata(label = "advanced", javaType = "java.lang.Boolean")
    private String allowCoreThreadTimeOut;
    @Metadata(label = "advanced", javaType = "org.zenithblox.util.concurrent.ThreadPoolRejectedPolicy",
              enums = "Abort,CallerRuns")
    private String rejectedPolicy;

    public ThreadPoolProfileDefinition() {
    }

    @Override
    public String getShortName() {
        return "threadPoolProfile";
    }

    @Override
    public String getLabel() {
        return "ThreadPoolProfile " + getId();
    }

    public ThreadPoolProfileDefinition poolSize(int poolSize) {
        return poolSize(Integer.toString(poolSize));
    }

    public ThreadPoolProfileDefinition poolSize(String poolSize) {
        setPoolSize(poolSize);
        return this;
    }

    public ThreadPoolProfileDefinition maxPoolSize(int maxPoolSize) {
        return maxPoolSize(Integer.toString(maxPoolSize));
    }

    public ThreadPoolProfileDefinition maxPoolSize(String maxPoolSize) {
        setMaxPoolSize(maxPoolSize);
        return this;
    }

    public ThreadPoolProfileDefinition keepAliveTime(long keepAliveTime) {
        return keepAliveTime(Long.toString(keepAliveTime));
    }

    public ThreadPoolProfileDefinition keepAliveTime(String keepAliveTime) {
        setKeepAliveTime(keepAliveTime);
        return this;
    }

    public ThreadPoolProfileDefinition timeUnit(TimeUnit timeUnit) {
        return timeUnit(timeUnit.name());
    }

    public ThreadPoolProfileDefinition timeUnit(String timeUnit) {
        setTimeUnit(timeUnit);
        return this;
    }

    public ThreadPoolProfileDefinition maxQueueSize(int maxQueueSize) {
        return maxQueueSize(Integer.toString(maxQueueSize));
    }

    public ThreadPoolProfileDefinition maxQueueSize(String maxQueueSize) {
        setMaxQueueSize(maxQueueSize);
        return this;
    }

    public ThreadPoolProfileDefinition rejectedPolicy(ThreadPoolRejectedPolicy rejectedPolicy) {
        setRejectedPolicy(rejectedPolicy.name());
        return this;
    }

    public ThreadPoolProfileDefinition rejectedPolicy(String rejectedPolicy) {
        setRejectedPolicy(rejectedPolicy);
        return this;
    }

    public ThreadPoolProfileDefinition allowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
        return allowCoreThreadTimeOut(Boolean.toString(allowCoreThreadTimeOut));
    }

    public ThreadPoolProfileDefinition allowCoreThreadTimeOut(String allowCoreThreadTimeOut) {
        setAllowCoreThreadTimeOut(allowCoreThreadTimeOut);
        return this;
    }

    public String getDefaultProfile() {
        return defaultProfile;
    }

    /**
     * Whether this profile is the default thread pool profile
     */
    public void setDefaultProfile(String defaultProfile) {
        this.defaultProfile = defaultProfile;
    }

    public String getPoolSize() {
        return poolSize;
    }

    /**
     * Sets the core pool size
     */
    public void setPoolSize(String poolSize) {
        this.poolSize = poolSize;
    }

    public String getMaxPoolSize() {
        return maxPoolSize;
    }

    /**
     * Sets the maximum pool size
     */
    public void setMaxPoolSize(String maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public String getKeepAliveTime() {
        return keepAliveTime;
    }

    /**
     * Sets the keep alive time for idle threads in the pool
     */
    public void setKeepAliveTime(String keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public String getMaxQueueSize() {
        return maxQueueSize;
    }

    /**
     * Sets the maximum number of tasks in the work queue.
     * <p/>
     * Use <tt>-1</tt> or <tt>Integer.MAX_VALUE</tt> for an unbounded queue
     */
    public void setMaxQueueSize(String maxQueueSize) {
        this.maxQueueSize = maxQueueSize;
    }

    public String getAllowCoreThreadTimeOut() {
        return allowCoreThreadTimeOut;
    }

    /**
     * Whether idle core threads is allowed to timeout and therefore can shrink the pool size below the core pool size
     * <p/>
     * Is by default <tt>true</tt>
     */
    public void setAllowCoreThreadTimeOut(String allowCoreThreadTimeOut) {
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    /**
     * Sets the time unit to use for keep alive time By default SECONDS is used.
     */
    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }

    public String getRejectedPolicy() {
        return rejectedPolicy;
    }

    /**
     * Sets the handler for tasks which cannot be executed by the thread pool.
     */
    public void setRejectedPolicy(String rejectedPolicy) {
        this.rejectedPolicy = rejectedPolicy;
    }

}
