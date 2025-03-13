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

import org.zenithblox.ZwangineContext;
import org.zenithblox.Workflow;
import org.zenithblox.RuntimeZwangineException;
import org.zenithblox.model.ProcessorDefinition;
import org.zenithblox.spi.Policy;
import org.zenithblox.spi.TransactedPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

import static org.zenithblox.model.TransactedDefinition.PROPAGATION_REQUIRED;

public abstract class AbstractPolicyReifier<T extends ProcessorDefinition<?>> extends ProcessorReifier<T> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPolicyReifier.class);

    public AbstractPolicyReifier(Workflow workflow, T definition) {
        super(workflow, definition);
    }

    public AbstractPolicyReifier(ZwangineContext zwangineContext, T definition) {
        super(zwangineContext, definition);
    }

    public Policy resolvePolicy(Policy policy, String ref, Class<? extends Policy> type) {
        if (policy != null) {
            return policy;
        }
        // explicit ref given so lookup by it
        if (org.zenithblox.util.ObjectHelper.isNotEmpty(ref)) {
            return mandatoryLookup(ref, Policy.class);
        }

        // no explicit reference given from user so we can use some convention
        // over configuration here

        // try to lookup by scoped type
        Policy answer = null;
        if (type != null) {
            // try find by type, note that this method is not supported by all registry
            Policy found = findSingleByType(type);
            if (found != null) {
                return found;
            }
        }

        // for transacted routing try the default REQUIRED name
        if (type == TransactedPolicy.class) {
            // still not found try with the default name PROPAGATION_REQUIRED
            answer = lookupByNameAndType(PROPAGATION_REQUIRED, TransactedPolicy.class);
        }

        // this logic only applies if we are a transacted policy
        // still no policy found then try lookup the platform transaction
        // manager and use it as policy
        if (answer == null && type == TransactedPolicy.class) {
            Class<?> tmClazz = zwangineContext.getClassResolver()
                    .resolveClass("org.springframework.transaction.PlatformTransactionManager");
            if (tmClazz != null) {
                // see if we can find the platform transaction manager in the
                // registry
                Object transactionManager = mandatoryFindSingleByType(tmClazz);
                // only one platform manager then use it as default and
                // create a transacted
                // policy with it and default to required

                // as we do not want dependency on spring jars in the
                // zwangine-core we use
                // reflection to lookup classes and create new objects and
                // call methods
                // as this is only done during workflow building it does not
                // matter that we
                // use reflection as performance is no a concern during
                // workflow building
                LOG.debug("One instance of PlatformTransactionManager found in registry: {}", transactionManager);
                Class<?> txClazz = zwangineContext.getClassResolver()
                        .resolveClass("org.zenithblox.spring.spi.SpringTransactionPolicy");
                if (txClazz != null) {
                    LOG.debug("Creating a new temporary SpringTransactionPolicy using the PlatformTransactionManager: {}",
                            transactionManager);
                    TransactedPolicy txPolicy
                            = org.zenithblox.support.ObjectHelper.newInstance(txClazz, TransactedPolicy.class);
                    Method method;
                    try {
                        method = txClazz.getMethod("setTransactionManager", tmClazz);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeZwangineException(
                                "Cannot get method setTransactionManager(PlatformTransactionManager) on class: " + txClazz);
                    }
                    org.zenithblox.support.ObjectHelper.invokeMethod(method, txPolicy, transactionManager);
                    return txPolicy;
                } else {
                    // zwangine-spring is missing on the classpath
                    throw new RuntimeZwangineException(
                            "Cannot create a transacted policy as zwangine-spring.jar is not on the classpath!");
                }
            }
        }

        return answer;
    }
}
