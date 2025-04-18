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
package org.zenithblox.model.loadbalancer;

import org.zenithblox.model.LoadBalancerDefinition;
import org.zenithblox.spi.Metadata;

import java.util.ArrayList;
import java.util.List;

/**
 * In case of failures the exchange will be tried on the next endpoint.
 */
@Metadata(label = "eip,routing")
public class FailoverLoadBalancerDefinition extends LoadBalancerDefinition {

    private List<Class<?>> exceptionTypes = new ArrayList<>();

    private List<String> exceptions = new ArrayList<>();
    private String roundRobin;
    private String sticky;
    @Metadata(defaultValue = "-1")
    private String maximumFailoverAttempts;
    @Metadata(label = "advanced", javaType = "java.lang.Boolean", defaultValue = "true")
    private Boolean inheritErrorHandler;

    public FailoverLoadBalancerDefinition() {
    }

    protected FailoverLoadBalancerDefinition(FailoverLoadBalancerDefinition source) {
        super(source);
        this.exceptionTypes = new ArrayList<>(source.exceptionTypes);
        this.exceptions = new ArrayList<>(source.exceptions);
        this.roundRobin = source.roundRobin;
        this.sticky = source.sticky;
        this.maximumFailoverAttempts = source.maximumFailoverAttempts;
        this.inheritErrorHandler = source.inheritErrorHandler;
    }

    @Override
    public FailoverLoadBalancerDefinition copyDefinition() {
        return new FailoverLoadBalancerDefinition(this);
    }

    public List<String> getExceptions() {
        return exceptions;
    }

    /**
     * A list of class names for specific exceptions to monitor. If no exceptions are configured then all exceptions are
     * monitored
     */
    public void setExceptions(List<String> exceptions) {
        this.exceptions = exceptions;
    }

    public List<Class<?>> getExceptionTypes() {
        return exceptionTypes;
    }

    /**
     * A list of specific exceptions to monitor. If no exceptions are configured then all exceptions are monitored
     */
    public void setExceptionTypes(List<Class<?>> exceptionTypes) {
        this.exceptionTypes = exceptionTypes;
    }

    public String getRoundRobin() {
        return roundRobin;
    }

    /**
     * Whether or not the failover load balancer should operate in round robin mode or not. If not, then it will always
     * start from the first endpoint when a new message is to be processed. In other words it restart from the top for
     * every message. If round robin is enabled, then it keeps state and will continue with the next endpoint in a round
     * robin fashion.
     * <p/>
     * You can also enable sticky mode together with round robin, if so then it will pick the last known good endpoint
     * to use when starting the load balancing (instead of using the next when starting).
     */
    public void setRoundRobin(String roundRobin) {
        this.roundRobin = roundRobin;
    }

    public String getSticky() {
        return sticky;
    }

    /**
     * Whether or not the failover load balancer should operate in sticky mode or not. If not, then it will always start
     * from the first endpoint when a new message is to be processed. In other words it restart from the top for every
     * message. If sticky is enabled, then it keeps state and will continue with the last known good endpoint.
     * <p/>
     * You can also enable sticky mode together with round robin, if so then it will pick the last known good endpoint
     * to use when starting the load balancing (instead of using the next when starting).
     */
    public void setSticky(String sticky) {
        this.sticky = sticky;
    }

    public String getMaximumFailoverAttempts() {
        return maximumFailoverAttempts;
    }

    /**
     * A value to indicate after X failover attempts we should exhaust (give up). Use -1 to indicate never give up and
     * continuously try to failover. Use 0 to never failover. And use e.g. 3 to failover at most 3 times before giving
     * up. This option can be used whether roundRobin is enabled or not.
     */
    public void setMaximumFailoverAttempts(String maximumFailoverAttempts) {
        this.maximumFailoverAttempts = maximumFailoverAttempts;
    }

    public Boolean getInheritErrorHandler() {
        return inheritErrorHandler;
    }

    /**
     * To turn off Zwangine error handling during load balancing.
     * <p/>
     * By default, Zwangine error handler will attempt calling a service, which means you can specify retires and other
     * fine-grained settings. And only when Zwangine error handler have failed all attempts, then this load balancer will
     * fail over to the next endpoint and try again. You can turn this off, and then this load balancer will fail over
     * immediately on an error.
     */
    public void setInheritErrorHandler(Boolean inheritErrorHandler) {
        this.inheritErrorHandler = inheritErrorHandler;
    }

    @Override
    public String toString() {
        return "FailoverLoadBalancer";
    }
}
