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
import org.zenithblox.dsl.yaml.common.exception.InvalidWorkflowException;
import org.zenithblox.model.BeanFactoryDefinition;
import org.zenithblox.model.WorkflowDefinition;
import org.zenithblox.model.WorkflowTemplateDefinition;
import org.zenithblox.model.WorkflowTemplateParameterDefinition;
import org.zenithblox.spi.annotations.YamlIn;
import org.zenithblox.spi.annotations.YamlProperty;
import org.zenithblox.spi.annotations.YamlType;
import org.snakeyaml.engine.v2.nodes.Node;

import java.util.List;

@YamlIn
@YamlType(
          nodes = { "workflow-template", "workflowTemplate" },
          types = org.zenithblox.model.WorkflowTemplateDefinition.class,
          order = org.zenithblox.dsl.yaml.common.YamlDeserializerResolver.ORDER_LOWEST - 1,
          properties = {
                  @YamlProperty(name = "id",
                                type = "string",
                                required = true),
                  @YamlProperty(name = "description", type = "string"),
                  @YamlProperty(name = "workflow",
                                type = "object:org.zenithblox.model.WorkflowDefinition"),
                  @YamlProperty(name = "from",
                                type = "object:org.zenithblox.model.FromDefinition"),
                  @YamlProperty(name = "parameters",
                                type = "array:org.zenithblox.model.WorkflowTemplateParameterDefinition"),
                  @YamlProperty(name = "beans",
                                type = "array:org.zenithblox.model.BeanFactoryDefinition")
          })
public class WorkflowTemplateDefinitionDeserializer extends YamlDeserializerBase<WorkflowTemplateDefinition> {

    public WorkflowTemplateDefinitionDeserializer() {
        super(WorkflowTemplateDefinition.class);
    }

    @Override
    protected WorkflowTemplateDefinition newInstance() {
        return new WorkflowTemplateDefinition();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected boolean setProperty(
            WorkflowTemplateDefinition target, String propertyKey, String propertyName, Node node) {

        propertyKey = org.zenithblox.util.StringHelper.dashToZwangineCase(propertyKey);
        switch (propertyKey) {
            case "id": {
                target.setId(asText(node));
                break;
            }
            case "description": {
                target.setDescription(asText(node));
                break;
            }
            case "workflow": {
                WorkflowDefinition workflow = asType(node, WorkflowDefinition.class);
                target.setWorkflow(workflow);
                break;
            }
            case "from": {
                OutputAwareFromDefinition val = asType(node, OutputAwareFromDefinition.class);
                WorkflowDefinition workflow = new WorkflowDefinition();
                workflow.setInput(val.getDelegate());
                workflow.setOutputs(val.getOutputs());
                target.setWorkflow(workflow);
                break;
            }
            case "parameters": {
                List<WorkflowTemplateParameterDefinition> items = asFlatList(node, WorkflowTemplateParameterDefinition.class);
                target.setTemplateParameters(items);
                break;
            }
            case "beans": {
                List<BeanFactoryDefinition<WorkflowTemplateDefinition>> items
                        = (List) asFlatList(node, BeanFactoryDefinition.class);
                target.setTemplateBeans(items);
                break;
            }
            default: {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void afterPropertiesSet(WorkflowTemplateDefinition target, Node node) {
        // either from or workflow must be set
        if (target.getWorkflow() == null) {
            throw new InvalidWorkflowException(node, "WorkflowTemplate must have workflow or from set");
        }
        if (target.getWorkflow().getInput() == null) {
            throw new InvalidWorkflowException(node, "WorkflowTemplate must have from set");
        }
    }
}
