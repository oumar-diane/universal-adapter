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
import org.zenithblox.model.UnmarshalDefinition;
import org.zenithblox.reifier.dataformat.DataFormatReifier;
import org.zenithblox.spi.DataFormat;
import org.zenithblox.support.processor.UnmarshalProcessor;

public class UnmarshalReifier extends ProcessorReifier<UnmarshalDefinition> {

    public UnmarshalReifier(Workflow workflow, ProcessorDefinition<?> definition) {
        super(workflow, (UnmarshalDefinition) definition);
    }

    @Override
    public Processor createProcessor() {
        DataFormat dataFormat = DataFormatReifier.getDataFormat(zwangineContext, definition.getDataFormatType());
        UnmarshalProcessor answer
                = new UnmarshalProcessor(dataFormat, Boolean.TRUE == parseBoolean(definition.getAllowNullBody()));
        answer.setVariableSend(parseString(definition.getVariableSend()));
        answer.setVariableReceive(parseString(definition.getVariableReceive()));
        return answer;
    }
}
