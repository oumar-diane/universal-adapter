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

import org.zenithblox.Expression;
import org.zenithblox.Processor;
import org.zenithblox.Workflow;
import org.zenithblox.model.PollDefinition;
import org.zenithblox.model.ProcessorDefinition;
import org.zenithblox.processor.PollProcessor;
import org.zenithblox.spi.Language;
import org.zenithblox.support.EndpointHelper;
import org.zenithblox.support.LanguageSupport;
import org.zenithblox.util.StringHelper;

public class PollReifier extends ProcessorReifier<PollDefinition> {

    public PollReifier(Workflow workflow, ProcessorDefinition<?> definition) {
        super(workflow, (PollDefinition) definition);
    }

    @Override
    public Processor createProcessor() throws Exception {
        String uri;
        if (definition.getEndpointConsumerBuilder() != null) {
            uri = definition.getEndpointConsumerBuilder().getRawUri();
        } else {
            uri = StringHelper.notEmpty(definition.getUri(), "uri", this);
        }
        Expression exp = createExpression(uri);
        long timeout = parseDuration(definition.getTimeout(), 20000);
        PollProcessor answer = new PollProcessor(exp, uri, timeout);
        answer.setVariableReceive(parseString(definition.getVariableReceive()));
        return answer;
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
