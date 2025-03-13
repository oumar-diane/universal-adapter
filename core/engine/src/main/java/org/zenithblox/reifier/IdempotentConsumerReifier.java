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
import org.zenithblox.model.IdempotentConsumerDefinition;
import org.zenithblox.model.ProcessorDefinition;
import org.zenithblox.processor.idempotent.IdempotentConsumer;
import org.zenithblox.spi.IdempotentRepository;
import org.zenithblox.util.ObjectHelper;

public class IdempotentConsumerReifier extends ExpressionReifier<IdempotentConsumerDefinition> {

    public IdempotentConsumerReifier(Workflow workflow, ProcessorDefinition<?> definition) {
        super(workflow, IdempotentConsumerDefinition.class.cast(definition));
    }

    @Override
    public Processor createProcessor() throws Exception {
        Processor childProcessor = this.createChildProcessor(true);

        IdempotentRepository idempotentRepository = resolveIdempotentRepository();
        ObjectHelper.notNull(idempotentRepository, "idempotentRepository", definition);

        Expression expression = createExpression(definition.getExpression());

        // these boolean should be true by default
        boolean eager = parseBoolean(definition.getEager(), true);
        boolean duplicate = parseBoolean(definition.getSkipDuplicate(), true);
        boolean remove = parseBoolean(definition.getRemoveOnFailure(), true);
        // these boolean should be false by default
        boolean completionEager = parseBoolean(definition.getCompletionEager(), false);

        return new IdempotentConsumer(
                expression, idempotentRepository, eager, completionEager, duplicate, remove, childProcessor);
    }

    /**
     * Strategy method to resolve the {@link org.zenithblox.spi.IdempotentRepository} to use
     *
     * @return the repository
     */
    protected <T> IdempotentRepository resolveIdempotentRepository() {
        IdempotentRepository repo = definition.getIdempotentRepositoryBean();
        String ref = parseString(definition.getIdempotentRepository());
        if (repo == null && ref != null) {
            repo = mandatoryLookup(ref, IdempotentRepository.class);
        }
        return repo;
    }
}
