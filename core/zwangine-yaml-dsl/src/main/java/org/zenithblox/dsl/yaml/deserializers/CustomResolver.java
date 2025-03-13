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

import org.zenithblox.dsl.yaml.common.YamlDeserializerResolver;
import org.zenithblox.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snakeyaml.engine.v2.api.ConstructNode;

public class CustomResolver implements YamlDeserializerResolver {
    public static final Logger LOG = LoggerFactory.getLogger(CustomResolver.class);

    @Override
    public int getOrder() {
        return YamlDeserializerResolver.ORDER_DEFAULT;
    }

    private final BeansDeserializer beansDeserializer;

    public CustomResolver(BeansDeserializer beansDeserializer) {
        this.beansDeserializer = beansDeserializer;
    }

    @Override
    public ConstructNode resolve(String id) {
        if (id != null && id.contains("-")) {
            LOG.warn(
                    "The kebab-case '{}' is deprecated and it will be removed in the next version. Use the zwangineCase '{}' instead.",
                    id, StringHelper.dashToZwangineCase(id));
        }

        id = org.zenithblox.util.StringHelper.dashToZwangineCase(id);
        switch (id) {
            //
            // Workflow
            //
            case "from":
                return new WorkflowFromDefinitionDeserializer();
            case "org.zenithblox.model.FromDefinition":
                return new FromDefinitionDeserializer();
            case "workflow":
            case "org.zenithblox.model.WorkflowDefinition":
                return new WorkflowDefinitionDeserializer();
            case "workflowConfiguration":
            case "org.zenithblox.model.WorkflowConfigurationDefinition":
                return new WorkflowConfigurationDefinitionDeserializer();
            case "workflowTemplate":
            case "org.zenithblox.model.WorkflowTemplateDefinition":
                return new WorkflowTemplateDefinitionDeserializer();
            case "templatedWorkflow":
            case "org.zenithblox.model.TemplatedWorkflowDefinition":
                return new TemplatedWorkflowDefinitionDeserializer();
            case "org.zenithblox.dsl.yaml.deserializers.OutputAwareFromDefinition":
                return new OutputAwareFromDefinitionDeserializer();

            //
            // Expression
            //
            case "expression":
            case "org.zenithblox.model.language.ExpressionDefinition":
                return new ExpressionDeserializers.ExpressionDefinitionDeserializers();
            case "org.zenithblox.model.ExpressionSubElementDefinition":
                return new ExpressionDeserializers.ExpressionSubElementDefinitionDeserializers();

            //
            // Misc
            //
            case "beans":
                return beansDeserializer;
            case "dataFormats":
                return new DataFormatsDefinitionDeserializer();
            case "org.zenithblox.model.ErrorHandlerDefinition":
                return new ErrorHandlerDeserializer();
            case "errorHandler":
                // must be a global error handler
                return new ErrorHandlerDeserializer(true);
            case "org.zenithblox.model.ProcessorDefinition":
                return new ProcessorDefinitionDeserializer();
            default:
                return null;
        }
    }
}
