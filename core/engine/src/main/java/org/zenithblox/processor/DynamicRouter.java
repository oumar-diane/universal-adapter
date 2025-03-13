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
package org.zenithblox.processor;

import org.zenithblox.ZwangineContext;
import org.zenithblox.Exchange;
import org.zenithblox.Expression;
import org.zenithblox.support.ObjectHelper;

import java.util.Iterator;

/**
 * Implements a <a href="http://zwangine.zwangine.org/dynamic-workflow.html">Dynamic Router</a> pattern where the
 * destination(s) is computed at runtime.
 * <p/>
 * This implementation builds on top of {@link org.zenithblox.processor.RoutingSlip} which contains the most logic.
 */
public class DynamicRouter extends RoutingSlip {

    public DynamicRouter(ZwangineContext zwangineContext) {
        super(zwangineContext);
    }

    public DynamicRouter(ZwangineContext zwangineContext, Expression expression, String uriDelimiter) {
        super(zwangineContext, expression, uriDelimiter);
    }

    @Override
    protected RoutingSlipIterator createRoutingSlipIterator(Exchange exchange, Expression expression) throws Exception {
        return new DynamicRoutingSlipIterator(expression);
    }

    /**
     * The dynamic routing slip iterator.
     */
    private final class DynamicRoutingSlipIterator implements RoutingSlipIterator {

        private final Expression slip;
        private Iterator<?> current;

        private DynamicRoutingSlipIterator(Expression slip) {
            this.slip = slip;
        }

        @Override
        public boolean hasNext(Exchange exchange) {
            if (current != null && current.hasNext()) {
                return true;
            }
            // evaluate next slip
            Object routingSlip = slip.evaluate(exchange, Object.class);
            if (routingSlip == null) {
                return false;
            }
            current = ObjectHelper.createIterator(routingSlip, uriDelimiter);
            return current.hasNext();
        }

        @Override
        public Object next(Exchange exchange) {
            return current.next();
        }
    }
}
