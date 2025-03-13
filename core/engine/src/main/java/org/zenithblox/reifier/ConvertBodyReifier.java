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

import org.zenithblox.Processor;
import org.zenithblox.Workflow;
import org.zenithblox.model.ConvertBodyDefinition;
import org.zenithblox.model.ProcessorDefinition;
import org.zenithblox.support.processor.ConvertBodyProcessor;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

public class ConvertBodyReifier extends ProcessorReifier<ConvertBodyDefinition> {

    public ConvertBodyReifier(Workflow workflow, ProcessorDefinition<?> definition) {
        super(workflow, ConvertBodyDefinition.class.cast(definition));
    }

    @Override
    public Processor createProcessor() throws Exception {
        Class<?> typeClass = parse(Class.class, or(definition.getTypeClass(), parseString(definition.getType())));
        String charset = validateCharset(parseString(definition.getCharset()));
        boolean mandatory = true;
        if (definition.getMandatory() != null) {
            mandatory = parseBoolean(definition.getMandatory(), true);
        }
        return new ConvertBodyProcessor(typeClass, charset, mandatory);
    }

    public static String validateCharset(String charset) throws UnsupportedCharsetException {
        if (charset != null) {
            if (Charset.isSupported(charset)) {
                return Charset.forName(charset).name();
            }
            throw new UnsupportedCharsetException(charset);
        }
        return null;
    }

}
