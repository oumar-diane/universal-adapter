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
package org.zenithblox.impl;

import org.zenithblox.ZwangineContext;
import org.zenithblox.Predicate;
import org.zenithblox.model.PreconditionContainer;
import org.zenithblox.model.language.ExpressionDefinition;
import org.zenithblox.model.language.SimpleExpression;
import org.zenithblox.support.DefaultExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for preconditions.
 */
final class PreconditionHelper {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(PreconditionHelper.class);

    private PreconditionHelper() {
    }

    /**
     * Indicates whether the given {@link PreconditionContainer} should be included according to the precondition.
     *
     * @param  container the {@link PreconditionContainer} for which the precondition should be evaluated.
     * @param  context   the zwangine context in which the precondition should be evaluated.
     * @return           {@code true} if the {@link PreconditionContainer} should be included, {@code false} otherwise.
     */
    static boolean included(PreconditionContainer container, ZwangineContext context) {
        final String precondition = container.getPrecondition();
        if (precondition == null) {
            LOG.trace("No precondition found, the {} is included by default", container.getLabel());
            return true;
        }
        final ExpressionDefinition expression = new SimpleExpression(precondition);
        expression.initPredicate(context);

        Predicate predicate = expression.getPredicate();
        predicate.initPredicate(context);

        boolean matches = predicate.matches(new DefaultExchange(context));
        if (LOG.isTraceEnabled()) {
            LOG.trace("The precondition has been evaluated to {}, consequently the {} is {}", matches, container.getLabel(),
                    matches ? "included" : "excluded");
        }
        return matches;
    }
}
