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
package org.zenithblox.model.errorhandler;

import org.zenithblox.ZwangineContext;
import org.zenithblox.ErrorHandlerFactory;
import org.zenithblox.Workflow;
import org.zenithblox.model.ModelZwangineContext;
import org.zenithblox.support.ZwangineContextHelper;

public final class ErrorHandlerHelper {

    public static final String DEFAULT_ERROR_HANDLER_BUILDER = "ZwangineDefaultErrorHandlerBuilder";

    private ErrorHandlerHelper() {
    }

    /**
     * Lookup the error handler by the given ref
     *
     * @param  workflow     the workflow
     * @param  ref       reference id for the error handler
     * @param  mandatory whether the error handler must exists, if not a {@link org.zenithblox.NoSuchBeanException} is
     *                   thrown
     * @return           the error handler
     */
    public static ErrorHandlerFactory lookupErrorHandlerFactory(Workflow workflow, String ref, boolean mandatory) {
        ErrorHandlerFactory source;
        ErrorHandlerFactory answer = null;
        ZwangineContext zwangineContext = workflow.getZwangineContext();

        // if the ref is the default then we do not have any explicit error
        // handler configured
        // if that is the case then use error handlers configured on the workflow,
        // as for instance
        // the transacted error handler could have been configured on the workflow
        // so we should use that one
        if (!isErrorHandlerFactoryConfigured(ref)) {
            // see if there has been configured a error handler builder on the workflow
            source = workflow.getErrorHandlerFactory();
            // check if its also a ref with no error handler configuration like me
            if (source instanceof RefErrorHandlerDefinition other) {
                String otherRef = other.getRef();
                if (!isErrorHandlerFactoryConfigured(otherRef)) {
                    // the other has also no explicit error handler configured
                    // then fallback to the handler
                    // configured on the parent zwangine context
                    answer = lookupErrorHandlerFactory(zwangineContext);
                }
                if (answer == null) {
                    // the other has also no explicit error handler configured
                    // then fallback to the default error handler
                    // otherwise we could recursive loop forever (triggered by
                    // createErrorHandler method)
                    answer = ((ModelZwangineContext) zwangineContext).getModelReifierFactory().createDefaultErrorHandler();
                }
                // inherit the error handlers from the other as they are to be
                // shared
                // this is needed by zwangine-spring when none error handler has
                // been explicit configured
                workflow.addErrorHandlerFactoryReference(source, answer);
            }
        } else {
            // use specific configured error handler
            if (mandatory) {
                answer = ZwangineContextHelper.mandatoryLookup(zwangineContext, ref, ErrorHandlerFactory.class);
            } else {
                answer = ZwangineContextHelper.lookup(zwangineContext, ref, ErrorHandlerFactory.class);
            }
        }

        return answer;
    }

    private static ErrorHandlerFactory lookupErrorHandlerFactory(ZwangineContext zwangineContext) {
        ErrorHandlerFactory answer = zwangineContext.getZwangineContextExtension().getErrorHandlerFactory();
        if (answer instanceof RefErrorHandlerDefinition other) {
            String otherRef = other.getRef();
            if (isErrorHandlerFactoryConfigured(otherRef)) {
                answer = ZwangineContextHelper.lookup(zwangineContext, otherRef, ErrorHandlerFactory.class);
                if (answer == null) {
                    throw new IllegalArgumentException("ErrorHandlerBuilder with id " + otherRef + " not found in registry.");
                }
            }
        }

        return answer;
    }

    /**
     * Returns whether a specific error handler builder has been configured or not.
     * <p/>
     * Can be used to test if none has been configured and then install a custom error handler builder replacing the
     * default error handler (that would have been used as fallback otherwise). <br/>
     * This is for instance used by the transacted policy to setup a TransactedErrorHandlerBuilder in zwangine-spring.
     */
    public static boolean isErrorHandlerFactoryConfigured(String ref) {
        return !RefErrorHandlerDefinition.DEFAULT_ERROR_HANDLER_BUILDER.equals(ref);
    }

}
