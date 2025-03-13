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

import org.zenithblox.dsl.yaml.common.YamlDeserializerBase;
import org.zenithblox.model.BeanFactoryDefinition;
import org.zenithblox.model.TemplatedWorkflowDefinition;
import org.zenithblox.model.TemplatedWorkflowParameterDefinition;
import org.zenithblox.spi.annotations.YamlIn;
import org.zenithblox.spi.annotations.YamlProperty;
import org.zenithblox.spi.annotations.YamlType;
import org.snakeyaml.engine.v2.nodes.Node;

import java.util.List;

@YamlIn
@YamlType(
          nodes = { "templated-workflow", "templatedWorkflow" },
          types = TemplatedWorkflowDefinition.class,
          order = org.zenithblox.dsl.yaml.common.YamlDeserializerResolver.ORDER_LOWEST - 1,
          properties = {
                  @YamlProperty(name = "workflow-id",
                                type = "string"),
                  @YamlProperty(name = "prefix-id",
                                type = "string"),
                  @YamlProperty(name = "workflow-template-ref",
                                type = "string",
                                required = true),
                  @YamlProperty(name = "parameters",
                                type = "array:org.zenithblox.model.TemplatedWorkflowParameterDefinition"),
                  @YamlProperty(name = "beans",
                                type = "array:org.zenithblox.model.BeanFactoryDefinition")
          })
public class TemplatedWorkflowDefinitionDeserializer extends YamlDeserializerBase<TemplatedWorkflowDefinition> {

    public TemplatedWorkflowDefinitionDeserializer() {
        super(TemplatedWorkflowDefinition.class);
    }

    @Override
    protected TemplatedWorkflowDefinition newInstance() {
        return new TemplatedWorkflowDefinition();
    }

    @Override
    protected boolean setProperty(
            TemplatedWorkflowDefinition target, String propertyKey, String propertyName, Node node) {

        propertyKey = org.zenithblox.util.StringHelper.dashToZwangineCase(propertyKey);
        switch (propertyKey) {
            case "workflowId": {
                target.setWorkflowId(asText(node));
                break;
            }
            case "prefixId": {
                target.setPrefixId(asText(node));
                break;
            }
            case "workflowTemplateRef": {
                target.setWorkflowTemplateRef(asText(node));
                break;
            }
            case "parameters": {
                List<TemplatedWorkflowParameterDefinition> items = asFlatList(node, TemplatedWorkflowParameterDefinition.class);
                target.setParameters(items);
                break;
            }
            case "beans": {
                List<BeanFactoryDefinition<TemplatedWorkflowDefinition>> items
                        = (List) asFlatList(node, BeanFactoryDefinition.class);
                target.setBeans(items);
                break;
            }
            default: {
                return false;
            }
        }
        return true;
    }
}
