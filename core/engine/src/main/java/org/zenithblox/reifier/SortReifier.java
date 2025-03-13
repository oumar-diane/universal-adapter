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
import org.zenithblox.model.SortDefinition;
import org.zenithblox.processor.SortProcessor;
import org.zenithblox.support.ObjectHelper;

import java.util.Comparator;

public class SortReifier<T, U extends SortDefinition<T>> extends ExpressionReifier<U> {

    public SortReifier(Workflow workflow, ProcessorDefinition<?> definition) {
        super(workflow, (U) definition);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Processor createProcessor() throws Exception {
        // lookup in registry
        Comparator<? super T> comp = definition.getComparatorBean();
        if (comp == null && definition.getComparator() != null) {
            comp = mandatoryLookup(definition.getComparator(), Comparator.class);
        }

        // if no comparator then default on to string representation
        if (comp == null) {
            comp = (Comparator<T>) (o1, o2) -> ObjectHelper.compare(o1, o2);
        }

        // if no expression provided then default to body expression
        Expression exp;
        if (definition.getExpression() == null) {
            exp = zwangineContext.resolveLanguage("simple").createExpression("${body}");
        } else {
            exp = createExpression(definition.getExpression());
        }
        return new SortProcessor<T>(exp, comp);
    }

}
