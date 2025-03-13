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
import org.zenithblox.model.ConvertVariableDefinition;
import org.zenithblox.model.ProcessorDefinition;
import org.zenithblox.support.LanguageSupport;
import org.zenithblox.support.processor.ConvertVariableProcessor;

import java.nio.charset.UnsupportedCharsetException;

public class ConvertVariableReifier extends ProcessorReifier<ConvertVariableDefinition> {

    public ConvertVariableReifier(Workflow workflow, ProcessorDefinition<?> definition) {
        super(workflow, ConvertVariableDefinition.class.cast(definition));
    }

    @Override
    public Processor createProcessor() throws Exception {
        String key = parseString(definition.getName());
        Expression nameExpr;
        if (LanguageSupport.hasSimpleFunction(key)) {
            nameExpr = zwangineContext.resolveLanguage("simple").createExpression(key);
        } else {
            nameExpr = zwangineContext.resolveLanguage("constant").createExpression(key);
        }
        nameExpr.init(zwangineContext);

        String toKey = parseString(definition.getToName());
        Expression toNameExpr = null;
        if (toKey != null) {
            if (LanguageSupport.hasSimpleFunction(toKey)) {
                toNameExpr = zwangineContext.resolveLanguage("simple").createExpression(toKey);
            } else {
                toNameExpr = zwangineContext.resolveLanguage("constant").createExpression(toKey);
            }
            toNameExpr.init(zwangineContext);
        }

        Class<?> typeClass = parse(Class.class, or(definition.getTypeClass(), parseString(definition.getType())));
        String charset = validateCharset(parseString(definition.getCharset()));
        boolean mandatory = true;
        if (definition.getMandatory() != null) {
            mandatory = parseBoolean(definition.getMandatory(), true);
        }
        return new ConvertVariableProcessor(key, nameExpr, toKey, toNameExpr, typeClass, charset, mandatory);
    }

    public static String validateCharset(String charset) throws UnsupportedCharsetException {
        return ConvertBodyReifier.validateCharset(charset);
    }

}
