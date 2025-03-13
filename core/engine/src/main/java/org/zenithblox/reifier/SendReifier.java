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
import org.zenithblox.model.ToDefinition;
import org.zenithblox.processor.SendProcessor;
import org.zenithblox.support.ZwangineContextHelper;

public class SendReifier extends ProcessorReifier<ToDefinition> {

    public SendReifier(Workflow workflow, ProcessorDefinition<?> definition) {
        super(workflow, (ToDefinition) definition);
    }

    @Override
    public Processor createProcessor() throws Exception {
        SendProcessor answer = new SendProcessor(resolveEndpoint(), parse(ExchangePattern.class, definition.getPattern()));
        answer.setVariableSend(parseString(definition.getVariableSend()));
        answer.setVariableReceive(parseString(definition.getVariableReceive()));
        return answer;
    }

    public Endpoint resolveEndpoint() {
        Endpoint answer;
        if (definition.getEndpoint() == null) {
            if (definition.getEndpointProducerBuilder() == null) {
                answer = ZwangineContextHelper.resolveEndpoint(zwangineContext, definition.getEndpointUri(), null);
            } else {
                answer = definition.getEndpointProducerBuilder().resolve(zwangineContext);
            }
        } else {
            answer = definition.getEndpoint();
        }
        LineNumberAware.trySetLineNumberAware(answer, definition);
        return answer;
    }

}
