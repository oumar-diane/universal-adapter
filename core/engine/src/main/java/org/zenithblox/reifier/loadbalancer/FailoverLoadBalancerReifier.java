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
import org.zenithblox.model.loadbalancer.FailoverLoadBalancerDefinition;
import org.zenithblox.processor.loadbalancer.FailOverLoadBalancer;
import org.zenithblox.processor.loadbalancer.LoadBalancer;
import org.zenithblox.util.ObjectHelper;

import java.util.ArrayList;
import java.util.List;

public class FailoverLoadBalancerReifier extends LoadBalancerReifier<FailoverLoadBalancerDefinition> {

    public FailoverLoadBalancerReifier(Workflow workflow, LoadBalancerDefinition definition) {
        super(workflow, (FailoverLoadBalancerDefinition) definition);
    }

    @Override
    public LoadBalancer createLoadBalancer() {
        FailOverLoadBalancer answer;

        List<Class<?>> classes = new ArrayList<>();
        if (!definition.getExceptionTypes().isEmpty()) {
            classes.addAll(definition.getExceptionTypes());
        } else if (!definition.getExceptions().isEmpty()) {
            for (String name : definition.getExceptions()) {
                Class<?> type = zwangineContext.getClassResolver().resolveClass(name);
                if (type == null) {
                    throw new IllegalArgumentException("Cannot find class: " + name + " in the classpath");
                }
                if (!ObjectHelper.isAssignableFrom(Throwable.class, type)) {
                    throw new IllegalArgumentException("Class is not an instance of Throwable: " + type);
                }
                classes.add(type);
            }
        }
        if (classes.isEmpty()) {
            answer = new FailOverLoadBalancer();
        } else {
            answer = new FailOverLoadBalancer(classes);
        }

        Integer num = parseInt(definition.getMaximumFailoverAttempts());
        if (num != null) {
            answer.setMaximumFailoverAttempts(num);
        }
        if (definition.getRoundRobin() != null) {
            answer.setRoundRobin(parseBoolean(definition.getRoundRobin(), false));
        }
        if (definition.getSticky() != null) {
            answer.setSticky(parseBoolean(definition.getSticky(), false));
        }

        return answer;
    }

}
