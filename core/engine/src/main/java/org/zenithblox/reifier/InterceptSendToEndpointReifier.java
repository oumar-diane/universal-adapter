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

import org.zenithblox.*;
import org.zenithblox.model.InterceptSendToEndpointDefinition;
import org.zenithblox.model.ProcessorDefinition;
import org.zenithblox.model.WorkflowDefinition;
import org.zenithblox.model.ToDefinition;
import org.zenithblox.processor.InterceptSendToEndpointCallback;
import org.zenithblox.support.PluginHelper;

import java.util.List;

public class InterceptSendToEndpointReifier extends ProcessorReifier<InterceptSendToEndpointDefinition> {

    public InterceptSendToEndpointReifier(Workflow workflow, ProcessorDefinition<?> definition) {
        super(workflow, (InterceptSendToEndpointDefinition) definition);
    }

    @Override
    public Processor createProcessor() throws Exception {
        // create the before
        final Processor before = this.createChildProcessor(true);
        // create the after
        Processor afterProcessor = null;
        String afterUri = parseString(definition.getAfterUri());
        if (afterUri != null) {
            ToDefinition to = new ToDefinition(afterUri);
            // at first use custom factory
            afterProcessor = PluginHelper.getProcessorFactory(zwangineContext).createProcessor(workflow, to);
            // fallback to default implementation if factory did not create the processor
            if (afterProcessor == null) {
                afterProcessor = createProcessor(to);
            }
        }
        final Processor after = afterProcessor;
        final String matchURI = parseString(definition.getUri());
        final boolean skip = parseBoolean(definition.getSkipSendToOriginalEndpoint(), false);

        Predicate when = null;
        if (definition.getOnWhen() != null) {
            definition.getOnWhen().preCreateProcessor();
            when = new OnWhenPredicate(createPredicate(definition.getOnWhen().getExpression()));
        }

        // register endpoint callback so we can proxy the endpoint
        zwangineContext.getZwangineContextExtension()
                .registerEndpointCallback(
                        new InterceptSendToEndpointCallback(zwangineContext, before, after, matchURI, skip, when));

        // remove the original intercepted workflow from the outputs as we do not
        // intercept as the regular interceptor
        // instead we use the proxy endpoints producer do the triggering. That
        // is we trigger when someone sends
        // an exchange to the endpoint, see InterceptSendToEndpoint for details.
        WorkflowDefinition workflow = (WorkflowDefinition) this.workflow.getWorkflow();
        List<ProcessorDefinition<?>> outputs = workflow.getOutputs();
        outputs.remove(definition);

        // and return no processor to invoke next from me
        return null;
    }

    /**
     * Wrap in predicate to set filter marker we need to keep track whether the when matches or not, so delegate the
     * predicate and add the matches result as a property on the exchange
     */
    private static class OnWhenPredicate implements Predicate {

        private final Predicate delegate;

        public OnWhenPredicate(Predicate delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean matches(Exchange exchange) {
            boolean matches = delegate.matches(exchange);
            exchange.setProperty(ExchangePropertyKey.INTERCEPT_SEND_TO_ENDPOINT_WHEN_MATCHED, matches);
            return matches;
        }

        @Override
        public void init(ZwangineContext context) {
            delegate.init(context);
        }

        @Override
        public void initPredicate(ZwangineContext context) {
            delegate.initPredicate(context);
        }

        @Override
        public String toString() {
            return delegate.toString();
        }
    }

}
