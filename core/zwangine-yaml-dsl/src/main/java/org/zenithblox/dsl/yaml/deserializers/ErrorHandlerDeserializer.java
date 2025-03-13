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

import org.zenithblox.ZwangineContext;
import org.zenithblox.ErrorHandlerFactory;
import org.zenithblox.dsl.yaml.common.YamlDeserializationContext;
import org.zenithblox.dsl.yaml.common.YamlDeserializerResolver;
import org.zenithblox.dsl.yaml.common.exception.UnsupportedFieldException;
import org.zenithblox.dsl.yaml.common.exception.YamlDeserializationException;
import org.zenithblox.model.ErrorHandlerDefinition;
import org.zenithblox.model.errorhandler.*;
import org.zenithblox.spi.ZwangineContextCustomizer;
import org.zenithblox.spi.annotations.YamlIn;
import org.zenithblox.spi.annotations.YamlProperty;
import org.zenithblox.spi.annotations.YamlType;
import org.snakeyaml.engine.v2.api.ConstructNode;
import org.snakeyaml.engine.v2.nodes.MappingNode;
import org.snakeyaml.engine.v2.nodes.Node;
import org.snakeyaml.engine.v2.nodes.NodeTuple;

import static org.zenithblox.dsl.yaml.common.YamlDeserializerSupport.*;

@YamlIn
@YamlType(
          inline = false,
          nodes = { "error-handler", "errorHandler" },
          order = YamlDeserializerResolver.ORDER_DEFAULT,
          properties = {
                  @YamlProperty(name = "id", type = "string", description = "The id of this node", displayName = "Id"),
                  @YamlProperty(name = "deadLetterChannel",
                                type = "object:org.zenithblox.model.errorhandler.DeadLetterChannelDefinition",
                                oneOf = "errorHandler"),
                  @YamlProperty(name = "defaultErrorHandler",
                                type = "object:org.zenithblox.model.errorhandler.DefaultErrorHandlerDefinition",
                                oneOf = "errorHandler"),
                  @YamlProperty(name = "jtaTransactionErrorHandler",
                                type = "object:org.zenithblox.model.errorhandler.JtaTransactionErrorHandlerDefinition",
                                oneOf = "errorHandler"),
                  @YamlProperty(name = "noErrorHandler",
                                type = "object:org.zenithblox.model.errorhandler.NoErrorHandlerDefinition",
                                oneOf = "errorHandler"),
                  @YamlProperty(name = "refErrorHandler",
                                type = "object:org.zenithblox.model.errorhandler.RefErrorHandlerDefinition",
                                oneOf = "errorHandler"),
                  @YamlProperty(name = "springTransactionErrorHandler",
                                type = "object:org.zenithblox.model.errorhandler.SpringTransactionErrorHandlerDefinition",
                                oneOf = "errorHandler"),
          })
public class ErrorHandlerDeserializer implements ConstructNode {

    private final boolean global;

    public ErrorHandlerDeserializer() {
        this(false);
    }

    public ErrorHandlerDeserializer(boolean global) {
        this.global = global;
    }

    private static ZwangineContextCustomizer customizer(ErrorHandlerDefinition builder) {
        return new ZwangineContextCustomizer() {
            @Override
            public void configure(ZwangineContext zwangineContext) {
                zwangineContext.getZwangineContextExtension().setErrorHandlerFactory(builder.getErrorHandlerType());
            }
        };
    }

    @Override
    public Object construct(Node node) {
        final MappingNode bn = asMappingNode(node);
        final YamlDeserializationContext dc = getDeserializationContext(node);

        ErrorHandlerFactory factory = null;
        for (NodeTuple tuple : bn.getValue()) {
            String key = asText(tuple.getKeyNode());
            Node val = tuple.getValueNode();

            setDeserializationContext(val, dc);

            key = org.zenithblox.util.StringHelper.dashToZwangineCase(key);
            switch (key) {
                case "deadLetterChannel":
                    factory = asType(val, DeadLetterChannelDefinition.class);
                    break;
                case "defaultErrorHandler":
                    factory = asType(val, DefaultErrorHandlerDefinition.class);
                    break;
                case "jtaTransactionErrorHandler":
                case "springTransactionErrorHandler":
                    factory = asType(val, JtaTransactionErrorHandlerDefinition.class);
                    break;
                case "noErrorHandler":
                    factory = asType(val, NoErrorHandlerDefinition.class);
                    break;
                case "refErrorHandler":
                    factory = asType(val, RefErrorHandlerDefinition.class);
                    break;
                case "id": {
                    // not in use
                    break;
                }
                default:
                    throw new UnsupportedFieldException(val, key);
            }
        }

        if (factory == null) {
            throw new YamlDeserializationException(node, "Unable to determine the error handler type for the node");
        }

        // wrap in model
        ErrorHandlerDefinition answer = new ErrorHandlerDefinition();
        answer.setErrorHandlerType(factory);

        if (global) {
            // global scoped should register factory on zwangine context via customizer
            return customizer(answer);
        }
        return answer;
    }

}
