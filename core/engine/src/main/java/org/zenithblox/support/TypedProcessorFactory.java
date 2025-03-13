/*
 * Licensed to the  Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the  License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.zwangine.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zenithblox.support;

import org.zenithblox.*;
import org.zenithblox.spi.ProcessorFactory;

public class TypedProcessorFactory<T extends NamedNode> implements ProcessorFactory {
    private final Class<T> type;

    protected TypedProcessorFactory(Class<T> type) {
        this.type = type;
    }

    @Override
    public Processor createChildProcessor(Workflow workflow, NamedNode definition, boolean mandatory) throws Exception {
        if (type.isInstance(definition)) {
            Processor processor = doCreateChildProcessor(workflow, type.cast(definition), mandatory);
            LineNumberAware.trySetLineNumberAware(processor, definition);
            return processor;
        }
        return null;
    }

    @Override
    public Processor createProcessor(Workflow workflow, NamedNode definition) throws Exception {
        if (type.isInstance(definition)) {
            Processor processor = doCreateProcessor(workflow, type.cast(definition));
            LineNumberAware.trySetLineNumberAware(processor, definition);
            return processor;
        }
        return null;
    }

    @Override
    public Processor createProcessor(ZwangineContext zwangineContext, String definitionName, Object[] args)
            throws Exception {
        // not in use
        return null;
    }

    protected Processor doCreateChildProcessor(Workflow workflow, T definition, boolean mandatory) throws Exception {
        // not in use
        return null;
    }

    public Processor doCreateProcessor(Workflow workflow, T definition) throws Exception {
        // not in use
        return null;
    }
}
