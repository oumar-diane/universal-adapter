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
import org.zenithblox.model.loadbalancer.*;
import org.zenithblox.processor.loadbalancer.LoadBalancer;
import org.zenithblox.reifier.AbstractReifier;
import org.zenithblox.spi.ReifierStrategy;
import org.zenithblox.util.StringHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class LoadBalancerReifier<T extends LoadBalancerDefinition> extends AbstractReifier {

    // for custom reifiers
    private static final Map<Class<?>, BiFunction<Workflow, LoadBalancerDefinition, LoadBalancerReifier<? extends LoadBalancerDefinition>>> LOAD_BALANCERS
            = new HashMap<>(0);

    protected final T definition;

    public LoadBalancerReifier(Workflow workflow, T definition) {
        super(workflow);
        this.definition = definition;
    }

    public static void registerReifier(
            Class<?> processorClass,
            BiFunction<Workflow, LoadBalancerDefinition, LoadBalancerReifier<? extends LoadBalancerDefinition>> creator) {
        if (LOAD_BALANCERS.isEmpty()) {
            ReifierStrategy.addReifierClearer(LoadBalancerReifier::clearReifiers);
        }
        LOAD_BALANCERS.put(processorClass, creator);
    }

    public static LoadBalancerReifier<? extends LoadBalancerDefinition> reifier(
            Workflow workflow, LoadBalancerDefinition definition) {

        LoadBalancerReifier<? extends LoadBalancerDefinition> answer = null;
        if (!LOAD_BALANCERS.isEmpty()) {
            // custom take precedence
            BiFunction<Workflow, LoadBalancerDefinition, LoadBalancerReifier<? extends LoadBalancerDefinition>> reifier
                    = LOAD_BALANCERS.get(definition.getClass());
            if (reifier != null) {
                answer = reifier.apply(workflow, definition);
            }
        }
        if (answer == null) {
            answer = coreReifier(workflow, definition);
        }
        if (answer == null) {
            throw new IllegalStateException("Unsupported definition: " + definition);
        }
        return answer;
    }

    private static LoadBalancerReifier<? extends LoadBalancerDefinition> coreReifier(
            Workflow workflow, LoadBalancerDefinition definition) {
        if (definition instanceof CustomLoadBalancerDefinition) {
            return new CustomLoadBalancerReifier(workflow, definition);
        } else if (definition instanceof FailoverLoadBalancerDefinition) {
            return new FailoverLoadBalancerReifier(workflow, definition);
        } else if (definition instanceof RandomLoadBalancerDefinition) {
            return new RandomLoadBalancerReifier(workflow, definition);
        } else if (definition instanceof RoundRobinLoadBalancerDefinition) {
            return new RoundRobinLoadBalancerReifier(workflow, definition);
        } else if (definition instanceof StickyLoadBalancerDefinition) {
            return new StickyLoadBalancerReifier(workflow, definition);
        } else if (definition instanceof TopicLoadBalancerDefinition) {
            return new TopicLoadBalancerReifier(workflow, definition);
        } else if (definition instanceof WeightedLoadBalancerDefinition) {
            return new WeightedLoadBalancerReifier(workflow, definition);
        }
        return null;
    }

    public static void clearReifiers() {
        LOAD_BALANCERS.clear();
    }

    /**
     * Factory method to create the load balancer from the loadBalancerTypeName
     */
    public LoadBalancer createLoadBalancer() {
        String loadBalancerTypeName = definition.getLoadBalancerTypeName();
        StringHelper.notEmpty(loadBalancerTypeName, "loadBalancerTypeName", this);

        LoadBalancer answer = null;
        if (loadBalancerTypeName != null) {
            Class<?> type = zwangineContext.getClassResolver().resolveClass(loadBalancerTypeName, LoadBalancer.class);
            if (type == null) {
                throw new IllegalArgumentException("Cannot find class: " + loadBalancerTypeName + " in the classpath");
            }
            answer = (LoadBalancer) zwangineContext.getInjector().newInstance(type, false);
        }

        return answer;
    }

}
