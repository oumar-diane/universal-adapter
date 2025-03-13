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
import org.zenithblox.RuntimeZwangineException;
import org.zenithblox.model.LoadBalancerDefinition;
import org.zenithblox.model.loadbalancer.WeightedLoadBalancerDefinition;
import org.zenithblox.processor.loadbalancer.LoadBalancer;
import org.zenithblox.processor.loadbalancer.WeightedLoadBalancer;
import org.zenithblox.processor.loadbalancer.WeightedRandomLoadBalancer;
import org.zenithblox.processor.loadbalancer.WeightedRoundRobinLoadBalancer;

import java.util.ArrayList;
import java.util.List;

public class WeightedLoadBalancerReifier extends LoadBalancerReifier<WeightedLoadBalancerDefinition> {

    public WeightedLoadBalancerReifier(Workflow workflow, LoadBalancerDefinition definition) {
        super(workflow, (WeightedLoadBalancerDefinition) definition);
    }

    @Override
    public LoadBalancer createLoadBalancer() {
        WeightedLoadBalancer loadBalancer;
        List<Integer> distributionRatioList = new ArrayList<>();

        try {
            String[] ratios = definition.getDistributionRatio().split(definition.getDistributionRatioDelimiter());
            for (String ratio : ratios) {
                distributionRatioList.add(parseInt(ratio.trim()));
            }

            boolean isRoundRobin = parseBoolean(definition.getRoundRobin(), false);
            if (isRoundRobin) {
                loadBalancer = new WeightedRoundRobinLoadBalancer(distributionRatioList);
            } else {
                loadBalancer = new WeightedRandomLoadBalancer(distributionRatioList);
            }
        } catch (Exception e) {
            throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
        }

        return loadBalancer;
    }

}
