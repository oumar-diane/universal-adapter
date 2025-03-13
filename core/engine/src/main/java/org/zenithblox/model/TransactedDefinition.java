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
package org.zenithblox.model;

import org.zenithblox.spi.Metadata;
import org.zenithblox.spi.Policy;
import org.zenithblox.spi.TransactedPolicy;

import java.util.List;

/**
 * Enables transaction on the workflow
 */
@Metadata(label = "configuration")
public class TransactedDefinition extends OutputDefinition<TransactedDefinition> {

    // JAXB does not support changing the ref attribute from required to
    // optional
    // if we extend PolicyDefinition so we must make a copy of the class
    public static final String PROPAGATION_REQUIRED = "PROPAGATION_REQUIRED";
    private Class<? extends Policy> type = TransactedPolicy.class;
    private Policy policy;

    private String ref;

    public TransactedDefinition() {
    }

    protected TransactedDefinition(TransactedDefinition source) {
        super(source);
        this.type = source.type;
        this.policy = source.policy;
        this.ref = source.ref;
    }

    public TransactedDefinition(Policy policy) {
        this.policy = policy;
    }

    @Override
    public TransactedDefinition copyDefinition() {
        return new TransactedDefinition(this);
    }

    @Override
    public List<ProcessorDefinition<?>> getOutputs() {
        return outputs;
    }

    @Override
    public void setOutputs(List<ProcessorDefinition<?>> outputs) {
        super.setOutputs(outputs);
    }

    @Override
    public String toString() {
        String desc = description();
        if (org.zenithblox.util.ObjectHelper.isEmpty(desc)) {
            return "Transacted";
        } else {
            return "Transacted[" + desc + "]";
        }
    }

    protected String description() {
        if (ref != null) {
            return "ref:" + ref;
        } else if (policy != null) {
            return policy.toString();
        } else {
            return "";
        }
    }

    @Override
    public String getShortName() {
        return "transacted";
    }

    @Override
    public String getLabel() {
        String desc = description();
        if (org.zenithblox.util.ObjectHelper.isEmpty(desc)) {
            return "transacted";
        } else {
            return "transacted[" + desc + "]";
        }
    }

    @Override
    public boolean isAbstract() {
        return true;
    }

    @Override
    public boolean isTopLevelOnly() {
        // transacted is top level as we only allow have it configured once per
        // workflow
        return true;
    }

    @Override
    public boolean isWrappingEntireOutput() {
        return true;
    }

    public Policy getPolicy() {
        return policy;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public Class<? extends Policy> getType() {
        return type;
    }

    /**
     * Sets a policy type that this definition should scope within.
     * <p/>
     * Is used for convention over configuration situations where the policy should be automatic looked up in the
     * registry and it should be based on this type. For instance a {@link org.zenithblox.spi.TransactedPolicy} can be
     * set as type for easy transaction configuration.
     * <p/>
     * Will by default scope to the wide {@link Policy}
     *
     * @param type the policy type
     */
    public void setType(Class<? extends Policy> type) {
        this.type = type;
    }

    /**
     * Sets a reference to use for lookup the policy in the registry.
     *
     * @param  ref the reference
     * @return     the builder
     */
    public TransactedDefinition ref(String ref) {
        setRef(ref);
        return this;
    }

}
