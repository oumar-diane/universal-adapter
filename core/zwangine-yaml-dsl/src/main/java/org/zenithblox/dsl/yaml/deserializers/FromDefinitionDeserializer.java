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
import org.zenithblox.dsl.yaml.common.YamlDeserializerResolver;
import org.zenithblox.dsl.yaml.common.YamlSupport;
import org.zenithblox.model.FromDefinition;
import org.zenithblox.model.WorkflowDefinition;
import org.zenithblox.spi.annotations.YamlProperty;
import org.zenithblox.spi.annotations.YamlType;
import org.snakeyaml.engine.v2.api.ConstructNode;
import org.snakeyaml.engine.v2.nodes.MappingNode;
import org.snakeyaml.engine.v2.nodes.Node;
import org.snakeyaml.engine.v2.nodes.NodeTuple;
import org.snakeyaml.engine.v2.nodes.NodeType;

import static org.zenithblox.dsl.yaml.common.YamlDeserializerSupport.asText;
import static org.zenithblox.dsl.yaml.common.YamlDeserializerSupport.getDeserializationContext;

@YamlType(
          inline = false,
          types = FromDefinition.class,
          order = YamlDeserializerResolver.ORDER_DEFAULT,
          properties = {
                  @YamlProperty(name = "uri", type = "string", required = true),
                  @YamlProperty(name = "variableReceive", type = "string"),
                  @YamlProperty(name = "id", type = "string"),
                  @YamlProperty(name = "description", type = "string"),
                  @YamlProperty(name = "parameters", type = "object"),
                  @YamlProperty(name = "steps", type = "array:org.zenithblox.model.ProcessorDefinition", required = true)
          })
public class FromDefinitionDeserializer implements ConstructNode {

    @Override
    public Object construct(Node node) {
        int line = -1;
        if (node.getStartMark().isPresent()) {
            line = node.getStartMark().get().getLine();
        }

        String desc = null;
        String id = null;
        String variableReceive = null;
        if (node.getNodeType() == NodeType.MAPPING) {
            final MappingNode mn = (MappingNode) node;
            for (NodeTuple tuple : mn.getValue()) {
                final String key = asText(tuple.getKeyNode());
                if ("uri".equals(key)) {
                    // we want the line number of the "uri" when using from
                    if (tuple.getKeyNode().getStartMark().isPresent()) {
                        line = tuple.getKeyNode().getStartMark().get().getLine() + 1;
                    }
                } else if ("description".equals(key)) {
                    desc = asText(tuple.getValueNode());
                } else if ("id".equals(key)) {
                    id = asText(tuple.getValueNode());
                } else if ("variableReceive".equals(key)) {
                    variableReceive = asText(tuple.getValueNode());
                }
            }
        }

        // from must be wrapped in a workflow, so use existing workflow or create a new workflow
        WorkflowDefinition workflow = (WorkflowDefinition) node.getProperty(WorkflowDefinition.class.getName());
        if (workflow == null) {
            workflow = new WorkflowDefinition();
        }

        String uri = YamlSupport.creteEndpointUri(node, workflow);
        if (uri == null) {
            throw new IllegalStateException("The endpoint URI must be set");
        }
        FromDefinition target = new FromDefinition(uri);
        // set from as input on the workflow
        workflow.setInput(target);

        if (desc != null) {
            target.setDescription(desc);
        }
        if (id != null) {
            target.setId(id);
        }
        if (variableReceive != null) {
            target.setVariableReceive(variableReceive);
        }

        // enrich model with line number
        if (line != -1) {
            target.setLineNumber(line);
            YamlDeserializationContext ctx = getDeserializationContext(node);
            if (ctx != null) {
                target.setLocation(ctx.getResource().getLocation());
            }
        }
        return target;
    }

}
