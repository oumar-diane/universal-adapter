/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
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
package org.zenithblox.dsl.yaml.deserializers;

import org.zenithblox.dsl.yaml.common.YamlSupport;
import org.zenithblox.model.FromDefinition;
import org.zenithblox.model.ToDefinition;
import org.snakeyaml.engine.v2.api.ConstructNode;
import org.snakeyaml.engine.v2.nodes.Node;

public final class EndpointDeserializers {

    private EndpointDeserializers() {
    }

    public static class From implements ConstructNode {
        private final String scheme;

        public From(String scheme) {
            this.scheme = scheme;
        }

        @Override
        public Object construct(Node node) {
            return YamlSupport.creteEndpoint(scheme, node, FromDefinition::new);
        }
    }

    public static class To implements ConstructNode {
        private final String scheme;

        public To(String scheme) {
            this.scheme = scheme;
        }

        @Override
        public Object construct(Node node) {
            return YamlSupport.creteEndpoint(scheme, node, ToDefinition::new);
        }
    }
}
