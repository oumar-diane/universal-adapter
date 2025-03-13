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
package org.zenithblox.reifier;

import org.zenithblox.Channel;
import org.zenithblox.Processor;
import org.zenithblox.Workflow;
import org.zenithblox.model.LoadBalanceDefinition;
import org.zenithblox.model.ProcessorDefinition;
import org.zenithblox.model.loadbalancer.FailoverLoadBalancerDefinition;
import org.zenithblox.processor.loadbalancer.LoadBalancer;
import org.zenithblox.reifier.loadbalancer.LoadBalancerReifier;

public class LoadBalanceReifier extends ProcessorReifier<LoadBalanceDefinition> {

    public LoadBalanceReifier(Workflow workflow, ProcessorDefinition<?> definition) {
        super(workflow, (LoadBalanceDefinition) definition);
    }

    @Override
    public Processor createProcessor() throws Exception {
        LoadBalancer loadBalancer = LoadBalancerReifier.reifier(workflow, definition.getLoadBalancerType()).createLoadBalancer();

        // some load balancer can only support a fixed number of outputs
        int max = definition.getLoadBalancerType().getMaximumNumberOfOutputs();
        int size = definition.getOutputs().size();
        if (size > max) {
            throw new IllegalArgumentException(
                    "To many outputs configured on " + definition.getLoadBalancerType() + ": " + size + " > " + max);
        }

        for (ProcessorDefinition<?> processorType : definition.getOutputs()) {
            // output must not be another load balancer
            // check for instanceof as the code below as there is
            // compilation errors on earlier versions of JDK6
            // on Windows boxes or with IBM JDKs etc.
            if (LoadBalanceDefinition.class.isInstance(processorType)) {
                throw new IllegalArgumentException(
                        "Loadbalancer already configured to: " + definition.getLoadBalancerType() + ". Cannot set it to: "
                                                   + processorType);
            }
            Processor processor = createProcessor(processorType);
            Channel channel = wrapChannel(processor, processorType);
            loadBalancer.addProcessor(channel);
        }

        Boolean inherit = definition.getInheritErrorHandler();
        if (definition.getLoadBalancerType() instanceof FailoverLoadBalancerDefinition) {
            // special for failover load balancer where you can configure it to
            // not inherit error handler for its children
            // but the load balancer itself should inherit so Zwangines error
            // handler can react afterwards
            inherit = true;
        }
        return wrapChannel(loadBalancer, definition, inherit);
    }

}
