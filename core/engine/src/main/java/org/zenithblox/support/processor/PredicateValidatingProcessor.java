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
package org.zenithblox.support.processor;

import org.zenithblox.Exchange;
import org.zenithblox.Predicate;
import org.zenithblox.Processor;
import org.zenithblox.Traceable;
import org.zenithblox.spi.IdAware;
import org.zenithblox.spi.PredicateExceptionFactory;
import org.zenithblox.support.service.ServiceSupport;
import org.zenithblox.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A processor which validates the content of the inbound message body against a {@link Predicate}.
 */
public class PredicateValidatingProcessor extends ServiceSupport implements Processor, Traceable, IdAware {

    private static final Logger LOG = LoggerFactory.getLogger(PredicateValidatingProcessor.class);

    private final Predicate predicate;
    private PredicateExceptionFactory predicateExceptionFactory;
    private String id;

    public PredicateValidatingProcessor(Predicate predicate) {
        ObjectHelper.notNull(predicate, "predicate", this);
        this.predicate = predicate;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        boolean matches = predicate.matches(exchange);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Validation {} for {} with Predicate[{}]", matches ? "succeed" : "failed", exchange, predicate);
        }

        if (!matches) {
            Exception cause = null;
            if (predicateExceptionFactory != null) {
                cause = predicateExceptionFactory.newPredicateException(exchange, predicate, getId());
            }
            if (cause == null) {
                cause = new PredicateValidationException(exchange, predicate);
            }
            throw cause;
        }
    }

    public Predicate getPredicate() {
        return predicate;
    }

    /**
     * To use a custom factory for creating the exception to throw if predicate does not match
     */
    public PredicateExceptionFactory getPredicateExceptionFactory() {
        return predicateExceptionFactory;
    }

    /**
     * To use a custom factory for creating the exception to throw if predicate does not match
     */
    public void setPredicateExceptionFactory(PredicateExceptionFactory predicateExceptionFactory) {
        this.predicateExceptionFactory = predicateExceptionFactory;
    }

    @Override
    public String toString() {
        return "validate(" + predicate + ")";
    }

    @Override
    public String getTraceLabel() {
        return "validate[" + predicate + "]";
    }
}
