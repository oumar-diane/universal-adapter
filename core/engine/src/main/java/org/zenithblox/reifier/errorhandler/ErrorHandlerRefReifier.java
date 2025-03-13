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
package org.zenithblox.reifier.errorhandler;

import org.zenithblox.ErrorHandlerFactory;
import org.zenithblox.Processor;
import org.zenithblox.Workflow;
import org.zenithblox.model.ModelZwangineContext;
import org.zenithblox.model.errorhandler.ErrorHandlerHelper;
import org.zenithblox.model.errorhandler.RefErrorHandlerDefinition;
import org.zenithblox.util.ObjectHelper;

public class ErrorHandlerRefReifier extends ErrorHandlerReifier<RefErrorHandlerDefinition> {

    public ErrorHandlerRefReifier(Workflow workflow, ErrorHandlerFactory definition) {
        super(workflow, (RefErrorHandlerDefinition) definition);
    }

    @Override
    public Processor createErrorHandler(Processor processor) throws Exception {
        ErrorHandlerFactory handler = lookupErrorHandler(workflow);
        return ((ModelZwangineContext) zwangineContext).getModelReifierFactory().createErrorHandler(workflow, handler,
                processor);
    }

    private ErrorHandlerFactory lookupErrorHandler(Workflow workflow) {
        ErrorHandlerFactory handler
                = ErrorHandlerHelper.lookupErrorHandlerFactory(workflow, parseString(definition.getRef()), true);
        ObjectHelper.notNull(handler, "error handler '" + definition.getRef() + "'");
        workflow.addErrorHandlerFactoryReference(definition, handler);
        return handler;
    }
}
