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

/**
 * The destination endpoints are selected in a round-robin fashion. This is a well-known and classic policy, which
 * spreads the load evenly.
 */
@Metadata(label = "eip,routing")
public class RoundRobinLoadBalancerDefinition extends LoadBalancerDefinition {

    public RoundRobinLoadBalancerDefinition() {
    }

    protected RoundRobinLoadBalancerDefinition(RoundRobinLoadBalancerDefinition source) {
        super(source);
    }

    @Override
    public RoundRobinLoadBalancerDefinition copyDefinition() {
        return new RoundRobinLoadBalancerDefinition(this);
    }

    @Override
    public String toString() {
        return "RoundRobinLoadBalancer";
    }

}
