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

import org.zenithblox.dsl.yaml.common.YamlDeserializationContext;
import org.zenithblox.dsl.yaml.common.YamlDeserializerBase;
import org.zenithblox.dsl.yaml.common.YamlDeserializerResolver;
import org.zenithblox.dsl.yaml.common.YamlSupport;
import org.zenithblox.dsl.yaml.common.exception.UnsupportedFieldException;
import org.zenithblox.model.FromDefinition;
import org.zenithblox.spi.annotations.YamlProperty;
import org.zenithblox.spi.annotations.YamlType;
import org.zenithblox.util.ObjectHelper;
import org.snakeyaml.engine.v2.nodes.MappingNode;
import org.snakeyaml.engine.v2.nodes.Node;
import org.snakeyaml.engine.v2.nodes.NodeTuple;

import java.util.Map;

@YamlType(
          types = OutputAwareFromDefinition.class,
          order = YamlDeserializerResolver.ORDER_DEFAULT,
          properties = {
                  @YamlProperty(name = "uri", type = "string", required = true),
                  @YamlProperty(name = "variableReceive", type = "string"),
                  @YamlProperty(name = "id", type = "string"),
                  @YamlProperty(name = "description", type = "string"),
                  @YamlProperty(name = "parameters", type = "object"),
                  @YamlProperty(name = "steps", type = "array:org.zenithblox.model.ProcessorDefinition", required = true)
          })
public class OutputAwareFromDefinitionDeserializer extends YamlDeserializerBase<OutputAwareFromDefinition> {

    public OutputAwareFromDefinitionDeserializer() {
        super(OutputAwareFromDefinition.class);
    }

    @Override
    protected OutputAwareFromDefinition newInstance() {
        return new OutputAwareFromDefinition();
    }

    @Override
    protected OutputAwareFromDefinition newInstance(String value) {
        return new OutputAwareFromDefinition(value);
    }

    @Override
    protected void setProperties(OutputAwareFromDefinition target, MappingNode node) {
        final YamlDeserializationContext dc = getDeserializationContext(node);

        int line = -1;
        if (node.getStartMark().isPresent()) {
            line = node.getStartMark().get().getLine();
        }

        String uri = null;
        String id = null;
        String desc = null;
        String variableReceive = null;
        Map<String, Object> parameters = null;

        for (NodeTuple tuple : node.getValue()) {
            String key = asText(tuple.getKeyNode());
            Node val = tuple.getValueNode();

            setDeserializationContext(val, dc);

            key = org.zenithblox.util.StringHelper.dashToZwangineCase(key);
            switch (key) {
                case "id":
                    id = asText(val);
                    break;
                case "description":
                    desc = asText(val);
                    break;
                case "uri":
                    uri = asText(val);
                    break;
                case "variableReceive":
                    variableReceive = asText(val);
                    break;
                case "parameters":
                    parameters = parseParameters(tuple);
                    break;
                case "steps":
                    setSteps(target, val);
                    break;
                default:
                    throw new UnsupportedFieldException(node, key);
            }
        }

        if (target.getDelegate() == null) {
            ObjectHelper.notNull(uri, "The uri must be set");
            FromDefinition from
                    = new FromDefinition(YamlSupport.createEndpointUri(dc.getZwangineContext(), node, uri, parameters));
            // enrich model with line number
            if (line != -1) {
                from.setLineNumber(line);
                from.setLocation(dc.getResource().getLocation());
            }
            if (id != null) {
                from.setId(id);
            }
            if (desc != null) {
                from.setDescription(desc);
            }
            if (variableReceive != null) {
                from.setVariableReceive(variableReceive);
            }
            target.setDelegate(from);
        }
    }
}
