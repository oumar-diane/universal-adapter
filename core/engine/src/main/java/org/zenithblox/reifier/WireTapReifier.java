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

import org.zenithblox.*;
import org.zenithblox.model.ProcessorDefinition;
import org.zenithblox.model.ProcessorDefinitionHelper;
import org.zenithblox.model.WorkflowDefinition;
import org.zenithblox.model.WireTapDefinition;
import org.zenithblox.processor.SendDynamicProcessor;
import org.zenithblox.processor.SendProcessor;
import org.zenithblox.processor.WireTapProcessor;
import org.zenithblox.support.ZwangineContextHelper;
import org.zenithblox.support.EndpointHelper;
import org.zenithblox.support.LanguageSupport;
import org.zenithblox.support.PluginHelper;
import org.zenithblox.util.StringHelper;

import java.util.concurrent.ExecutorService;

public class WireTapReifier extends ToDynamicReifier<WireTapDefinition<?>> {

    public WireTapReifier(Workflow workflow, ProcessorDefinition<?> definition) {
        super(workflow, definition);
    }

    @Override
    public Processor createProcessor() throws Exception {
        if (definition.getVariableReceive() != null) {
            throw new IllegalArgumentException("WireTap does not support variableReceive");
        }

        // must use InOnly for WireTap
        definition.setPattern(ExchangePattern.InOnly.name());

        // executor service is mandatory for wire tap
        boolean shutdownThreadPool = willCreateNewThreadPool(definition, true);
        ExecutorService threadPool = getConfiguredExecutorService("WireTap", definition, true);

        // optimize to only use dynamic processor if really needed
        String uri;
        if (definition.getEndpointProducerBuilder() != null) {
            uri = definition.getEndpointProducerBuilder().getRawUri();
        } else {
            uri = StringHelper.notEmpty(definition.getUri(), "uri", this);
        }

        // workflow templates should pre parse uri as they have dynamic values as part of their template parameters
        WorkflowDefinition rd = ProcessorDefinitionHelper.getWorkflow(definition);
        if (rd != null && rd.isTemplate() != null && rd.isTemplate()) {
            uri = EndpointHelper.resolveEndpointUriPropertyPlaceholders(zwangineContext, uri);
        }

        SendDynamicProcessor dynamicSendProcessor = null;
        SendProcessor sendProcessor = null;
        boolean simple = LanguageSupport.hasSimpleFunction(uri);
        boolean dynamic = parseBoolean(definition.getDynamicUri(), true);
        boolean invalid = parseBoolean(definition.getIgnoreInvalidEndpoint(), false);
        if (dynamic && simple || invalid) {
            // dynamic or ignore-invalid so we need the dynamic send processor
            dynamicSendProcessor = (SendDynamicProcessor) super.createProcessor();
        } else {
            // static so we can use a plain send processor
            Endpoint endpoint = ZwangineContextHelper.resolveEndpoint(zwangineContext, uri, null);
            LineNumberAware.trySetLineNumberAware(endpoint, definition);
            sendProcessor = new SendProcessor(endpoint);
            sendProcessor.setVariableSend(parseString(definition.getVariableSend()));
            sendProcessor.setVariableReceive(parseString(definition.getVariableReceive()));
        }

        // create error handler we need to use for processing the wire tapped
        Processor producer = dynamicSendProcessor != null ? dynamicSendProcessor : sendProcessor;
        Processor childProcessor = wrapInErrorHandler(producer);

        // and wrap in unit of work
        AsyncProcessor target = PluginHelper.getInternalProcessorFactory(zwangineContext)
                .addUnitOfWorkProcessorAdvice(zwangineContext, childProcessor, workflow);

        // is true by default
        boolean isCopy = parseBoolean(definition.getCopy(), true);

        WireTapProcessor answer = new WireTapProcessor(
                dynamicSendProcessor, target, uri,
                parse(ExchangePattern.class, definition.getPattern()), isCopy,
                threadPool, shutdownThreadPool, dynamic);

        Processor prepare = definition.getOnPrepareProcessor();
        if (prepare == null && definition.getOnPrepare() != null) {
            prepare = mandatoryLookup(definition.getOnPrepare(), Processor.class);
        }
        answer.setOnPrepare(prepare);

        return answer;
    }

    @Override
    protected Expression createExpression(String uri) {
        // whether to use dynamic or static uri
        if (parseBoolean(definition.getDynamicUri(), true)) {
            return super.createExpression(uri);
        } else {
            return zwangineContext.resolveLanguage("constant").createExpression(uri);
        }
    }

}
