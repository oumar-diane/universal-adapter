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
import org.zenithblox.model.ProcessorDefinition;
import org.zenithblox.model.RemoveHeadersDefinition;
import org.zenithblox.processor.RemoveHeadersProcessor;
import org.zenithblox.util.ObjectHelper;

import java.util.stream.Stream;

public class RemoveHeadersReifier extends ProcessorReifier<RemoveHeadersDefinition> {

    public RemoveHeadersReifier(Workflow workflow, ProcessorDefinition<?> definition) {
        super(workflow, (RemoveHeadersDefinition) definition);
    }

    @Override
    public Processor createProcessor() throws Exception {
        ObjectHelper.notNull(definition.getPattern(), "patterns", definition);
        if (definition.getExcludePatterns() != null) {
            return new RemoveHeadersProcessor(
                    parseString(definition.getPattern()), parseStrings(definition.getExcludePatterns()));
        } else if (definition.getExcludePattern() != null) {
            return new RemoveHeadersProcessor(
                    parseString(definition.getPattern()), parseStrings(new String[] { definition.getExcludePattern() }));
        } else {
            return new RemoveHeadersProcessor(parseString(definition.getPattern()), null);
        }
    }

    private String[] parseStrings(String[] array) {
        return Stream.of(array).map(this::parseString).toArray(String[]::new);
    }
}
