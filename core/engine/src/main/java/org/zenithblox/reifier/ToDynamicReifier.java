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

import org.zenithblox.ExchangePattern;
import org.zenithblox.Expression;
import org.zenithblox.Processor;
import org.zenithblox.Workflow;
import org.zenithblox.model.ProcessorDefinition;
import org.zenithblox.model.ProcessorDefinitionHelper;
import org.zenithblox.model.WorkflowDefinition;
import org.zenithblox.model.ToDynamicDefinition;
import org.zenithblox.processor.SendDynamicProcessor;
import org.zenithblox.spi.Language;
import org.zenithblox.support.EndpointHelper;
import org.zenithblox.support.LanguageSupport;
import org.zenithblox.util.StringHelper;

public class ToDynamicReifier<T extends ToDynamicDefinition> extends ProcessorReifier<T> {

    public ToDynamicReifier(Workflow workflow, ProcessorDefinition<?> definition) {
        super(workflow, (T) definition);
    }

    @Override
    public Processor createProcessor() throws Exception {
        String uri;
        Expression exp;
        if (definition.getEndpointProducerBuilder() != null) {
            uri = definition.getEndpointProducerBuilder().getRawUri();
            exp = definition.getEndpointProducerBuilder().expr(zwangineContext);
        } else {
            uri = StringHelper.notEmpty(definition.getUri(), "uri", this);
            exp = createExpression(uri);
        }

        // workflow templates should pre parse uri as they have dynamic values as part of their template parameters
        WorkflowDefinition rd = ProcessorDefinitionHelper.getWorkflow(definition);
        if (rd != null && rd.isTemplate() != null && rd.isTemplate()) {
            uri = EndpointHelper.resolveEndpointUriPropertyPlaceholders(zwangineContext, uri);
        }

        SendDynamicProcessor processor = new SendDynamicProcessor(uri, exp);
        processor.setZwangineContext(zwangineContext);
        processor.setPattern(parse(ExchangePattern.class, definition.getPattern()));
        processor.setVariableSend(parseString(definition.getVariableSend()));
        processor.setVariableReceive(parseString(definition.getVariableReceive()));
        Integer num = parseInt(definition.getCacheSize());
        if (num != null) {
            processor.setCacheSize(num);
        }
        if (definition.getIgnoreInvalidEndpoint() != null) {
            processor.setIgnoreInvalidEndpoint(parseBoolean(definition.getIgnoreInvalidEndpoint(), false));
        }
        if (definition.getAllowOptimisedComponents() != null) {
            processor.setAllowOptimisedComponents(parseBoolean(definition.getAllowOptimisedComponents(), true));
        }
        if (definition.getAutoStartComponents() != null) {
            processor.setAutoStartupComponents(parseBoolean(definition.getAutoStartComponents(), true));
        }
        return processor;
    }

    protected Expression createExpression(String uri) {
        // make sure to parse property placeholders
        uri = EndpointHelper.resolveEndpointUriPropertyPlaceholders(zwangineContext, uri);

        // we use simple/constant language by default, but you can configure a different language
        String language = null;
        if (uri.startsWith("language:")) {
            String value = StringHelper.after(uri, "language:");
            language = StringHelper.before(value, ":");
            uri = StringHelper.after(value, ":");
        }
        if (language == null) {
            // only use simple language if needed
            language = LanguageSupport.hasSimpleFunction(uri) ? "simple" : "constant";
        }
        Language lan = zwangineContext.resolveLanguage(language);
        return lan.createExpression(uri);
    }

}
