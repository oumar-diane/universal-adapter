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

import org.zenithblox.Processor;
import org.zenithblox.Workflow;
import org.zenithblox.model.CircuitBreakerDefinition;
import org.zenithblox.model.ProcessorDefinition;

public class CircuitBreakerReifier extends ProcessorReifier<CircuitBreakerDefinition> {

    public CircuitBreakerReifier(Workflow workflow, ProcessorDefinition<?> definition) {
        super(workflow, CircuitBreakerDefinition.class.cast(definition));
    }

    @Override
    public Processor createProcessor() throws Exception {
        throw new IllegalStateException(
                "Cannot find zwangine-resilience4j or zwangine-microprofile-fault-tolerance on the classpath.");
    }

}
