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

import org.zenithblox.AsyncProcessor;
import org.zenithblox.Expression;
import org.zenithblox.Processor;
import org.zenithblox.Workflow;
import org.zenithblox.model.ProcessorDefinition;
import org.zenithblox.model.RoutingSlipDefinition;
import org.zenithblox.processor.RoutingSlip;

import static org.zenithblox.model.RoutingSlipDefinition.DEFAULT_DELIMITER;

public class RoutingSlipReifier extends ExpressionReifier<RoutingSlipDefinition<?>> {

    public RoutingSlipReifier(Workflow workflow, ProcessorDefinition<?> definition) {
        super(workflow, (RoutingSlipDefinition) definition);
    }

    @Override
    public Processor createProcessor() throws Exception {
        Expression expression = createExpression(definition.getExpression());
        String delimiter = parseString(definition.getUriDelimiter());
        if (delimiter == null) {
            delimiter = DEFAULT_DELIMITER;
        }

        RoutingSlip routingSlip = new RoutingSlip(zwangineContext, expression, delimiter);
        if (definition.getIgnoreInvalidEndpoints() != null) {
            routingSlip.setIgnoreInvalidEndpoints(parseBoolean(definition.getIgnoreInvalidEndpoints(), false));
        }
        Integer num = parseInt(definition.getCacheSize());
        if (num != null) {
            routingSlip.setCacheSize(num);
        }

        // and wrap this in an error handler
        AsyncProcessor processor = routingSlip.newRoutingSlipProcessorForErrorHandler();
        AsyncProcessor errorHandler = (AsyncProcessor) wrapInErrorHandler(processor);
        routingSlip.setErrorHandler(errorHandler);

        return routingSlip;
    }

}
