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
package org.zenithblox.reifier.loadbalancer;

import org.zenithblox.Workflow;
import org.zenithblox.model.LoadBalancerDefinition;
import org.zenithblox.model.loadbalancer.CustomLoadBalancerDefinition;
import org.zenithblox.processor.loadbalancer.LoadBalancer;
import org.zenithblox.util.StringHelper;

public class CustomLoadBalancerReifier extends LoadBalancerReifier<CustomLoadBalancerDefinition> {

    public CustomLoadBalancerReifier(Workflow workflow, LoadBalancerDefinition definition) {
        super(workflow, (CustomLoadBalancerDefinition) definition);
    }

    @Override
    public LoadBalancer createLoadBalancer() {
        if (definition.getCustomLoadBalancer() != null) {
            return definition.getCustomLoadBalancer();
        }
        StringHelper.notEmpty(definition.getRef(), "ref", this);
        return mandatoryLookup(definition.getRef(), LoadBalancer.class);
    }

}
