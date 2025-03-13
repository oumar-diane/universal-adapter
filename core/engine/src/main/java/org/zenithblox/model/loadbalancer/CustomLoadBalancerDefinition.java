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
import org.zenithblox.processor.loadbalancer.LoadBalancer;
import org.zenithblox.spi.Metadata;

/**
 * To use a custom load balancer implementation.
 */
@Metadata(label = "eip,routing")
public class CustomLoadBalancerDefinition extends LoadBalancerDefinition {

    private LoadBalancer loadBalancer;

    private String ref;

    public CustomLoadBalancerDefinition() {
    }

    protected CustomLoadBalancerDefinition(CustomLoadBalancerDefinition source) {
        super(source);
        this.loadBalancer = source.loadBalancer;
        this.ref = source.ref;
    }

    public CustomLoadBalancerDefinition(String ref) {
        this.ref = ref;
    }

    @Override
    public CustomLoadBalancerDefinition copyDefinition() {
        return new CustomLoadBalancerDefinition(this);
    }

    public String getRef() {
        return ref;
    }

    /**
     * Refers to the custom load balancer to lookup from the registry
     */
    public void setRef(String ref) {
        this.ref = ref;
    }

    public LoadBalancer getCustomLoadBalancer() {
        return loadBalancer;
    }

    /**
     * The custom load balancer to use.
     */
    public void setCustomLoadBalancer(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    @Override
    public String toString() {
        if (getCustomLoadBalancer() != null) {
            return "CustomLoadBalancer[" + getCustomLoadBalancer() + "]";
        } else {
            return "CustomLoadBalancer[" + ref + "]";
        }
    }

}
