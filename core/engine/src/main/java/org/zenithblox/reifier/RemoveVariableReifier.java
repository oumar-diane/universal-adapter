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
import org.zenithblox.model.ProcessorDefinition;
import org.zenithblox.model.RemoveVariableDefinition;
import org.zenithblox.processor.RemoveVariableProcessor;
import org.zenithblox.support.LanguageSupport;

public class RemoveVariableReifier extends ProcessorReifier<RemoveVariableDefinition> {

    public RemoveVariableReifier(Workflow workflow, ProcessorDefinition<?> definition) {
        super(workflow, (RemoveVariableDefinition) definition);
    }

    @Override
    public Processor createProcessor() throws Exception {
        Expression nameExpr;
        String key = parseString(definition.getName());
        if (LanguageSupport.hasSimpleFunction(key)) {
            nameExpr = zwangineContext.resolveLanguage("simple").createExpression(key);
        } else {
            nameExpr = zwangineContext.resolveLanguage("constant").createExpression(key);
        }
        nameExpr.init(zwangineContext);
        return new RemoveVariableProcessor(nameExpr);
    }
}
