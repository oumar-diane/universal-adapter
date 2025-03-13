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
import org.zenithblox.dsl.yaml.common.exception.UnsupportedFieldException;
import org.zenithblox.model.*;
import org.zenithblox.spi.annotations.YamlIn;
import org.zenithblox.spi.annotations.YamlProperty;
import org.zenithblox.spi.annotations.YamlType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snakeyaml.engine.v2.nodes.MappingNode;
import org.snakeyaml.engine.v2.nodes.Node;
import org.snakeyaml.engine.v2.nodes.NodeTuple;

@YamlIn
@YamlType(
          nodes = "workflow",
          types = WorkflowDefinition.class,
          order = YamlDeserializerResolver.ORDER_DEFAULT,
          properties = {
                  @YamlProperty(name = "id", type = "string"),
                  @YamlProperty(name = "description", type = "string"),
                  @YamlProperty(name = "group", type = "string"),
                  @YamlProperty(name = "nodePrefixId", type = "string"),
                  @YamlProperty(name = "precondition", type = "string"),
                  @YamlProperty(name = "workflowConfigurationId", type = "string"),
                  @YamlProperty(name = "autoStartup", type = "boolean"),
                  @YamlProperty(name = "workflowPolicy", type = "string"),
                  @YamlProperty(name = "startupOrder", type = "number"),
                  @YamlProperty(name = "streamCache", type = "boolean"),
                  @YamlProperty(name = "messageHistory", type = "boolean"),
                  @YamlProperty(name = "logMask", type = "boolean"),
                  @YamlProperty(name = "trace", type = "boolean"),
                  @YamlProperty(name = "errorHandlerRef", type = "string"),
                  @YamlProperty(name = "errorHandler", type = "object:org.zenithblox.model.ErrorHandlerDefinition"),
                  @YamlProperty(name = "shutdownWorkflow", type = "enum:Default,Defer",
                                defaultValue = "Default",
                                description = "To control how to shut down the workflow."),
                  @YamlProperty(name = "shutdownRunningTask", type = "enum:CompleteCurrentTaskOnly,CompleteAllTasks",
                                defaultValue = "CompleteCurrentTaskOnly",
                                description = "To control how to shut down the workflow."),
                  @YamlProperty(name = "inputType", type = "object:org.zenithblox.model.InputTypeDefinition"),
                  @YamlProperty(name = "outputType", type = "object:org.zenithblox.model.OutputTypeDefinition"),
                  @YamlProperty(name = "from", type = "object:org.zenithblox.model.FromDefinition", required = true)
          })
public class WorkflowDefinitionDeserializer extends YamlDeserializerBase<WorkflowDefinition> {

    private static final Logger LOG = LoggerFactory.getLogger(WorkflowDefinitionDeserializer.class);

    public WorkflowDefinitionDeserializer() {
        super(WorkflowDefinition.class);
    }

    @Override
    protected WorkflowDefinition newInstance() {
        return new WorkflowDefinition();
    }

    @Override
    protected void setProperties(WorkflowDefinition target, MappingNode node) {
        final YamlDeserializationContext dc = getDeserializationContext(node);

        for (NodeTuple tuple : node.getValue()) {
            String key = asText(tuple.getKeyNode());
            Node val = tuple.getValueNode();

            setDeserializationContext(val, dc);

            key = org.zenithblox.util.StringHelper.dashToZwangineCase(key);
            switch (key) {
                case "id":
                    target.setId(asText(val));
                    break;
                case "description":
                    target.setDescription(asText(val));
                    break;
                case "precondition":
                    target.setPrecondition(asText(val));
                    break;
                case "group":
                    target.setGroup(asText(val));
                    break;
                case "nodePrefixId":
                    target.setNodePrefixId(asText(val));
                    break;
                case "workflowConfigurationId":
                    target.setWorkflowConfigurationId(asText(val));
                    break;
                case "autoStartup":
                    target.setAutoStartup(asText(val));
                    break;
                case "workflowPolicy":
                    target.setWorkflowPolicyRef(asText(val));
                    break;
                case "startupOrder":
                    target.setStartupOrder(asInt(val));
                    break;
                case "streamCaching":
                    // backwards compatible
                    LOG.warn("Old option name detected! Option streamCaching should be renamed to streamCache");
                    target.setStreamCache(asText(val));
                    break;
                case "streamCache":
                    target.setStreamCache(asText(val));
                    break;
                case "logMask":
                    target.setLogMask(asText(val));
                    break;
                case "messageHistory":
                    target.setMessageHistory(asText(val));
                    break;
                case "shutdownWorkflow":
                    target.setShutdownWorkflow(asText(val));
                    break;
                case "shutdownRunningTask":
                    target.setShutdownRunningTask(asText(val));
                    break;
                case "trace":
                    target.setTrace(asText(val));
                    break;
                case "errorHandlerRef":
                    target.setErrorHandlerRef(asText(val));
                    break;
                case "errorHandler":
                    target.setErrorHandler(asType(val, ErrorHandlerDefinition.class));
                    break;
                case "inputType":
                    target.setInputType(asType(val, InputTypeDefinition.class));
                    break;
                case "outputType":
                    target.setOutputType(asType(val, OutputTypeDefinition.class));
                    break;
                case "from":
                    val.setProperty(WorkflowDefinition.class.getName(), target);
                    target.setInput(asType(val, FromDefinition.class));
                    break;
                default:
                    throw new UnsupportedFieldException(node, key);
            }
        }
    }

}
