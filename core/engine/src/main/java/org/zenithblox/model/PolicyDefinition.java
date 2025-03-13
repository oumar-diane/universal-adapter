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
 * Defines a policy the workflow will use
 */
@Metadata(label = "configuration")
public class PolicyDefinition extends OutputDefinition<PolicyDefinition> {

    private Class<? extends Policy> type;
    private Policy policy;

    private String ref;

    public PolicyDefinition() {
    }

    protected PolicyDefinition(PolicyDefinition source) {
        super(source);
        this.type = source.type;
        this.policy = source.policy;
        this.ref = source.ref;
    }

    public PolicyDefinition(Policy policy) {
        this.policy = policy;
    }

    @Override
    public PolicyDefinition copyDefinition() {
        return new PolicyDefinition(this);
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
        return "Policy[" + description() + "]";
    }

    public Policy getPolicy() {
        return policy;
    }

    public Class<? extends Policy> getType() {
        return type;
    }

    protected String description() {
        if (policy != null) {
            return policy.toString();
        } else {
            return "ref:" + ref;
        }
    }

    @Override
    public String getShortName() {
        // a policy can be a hidden disguise for a transacted definition
        boolean transacted = type != null && type.isAssignableFrom(TransactedPolicy.class);
        return transacted ? "transacted" : "policy";
    }

    @Override
    public String getLabel() {
        return getShortName() + "[" + getDescription() + "]";
    }

    @Override
    public boolean isAbstract() {
        // policy should NOT be abstract
        return false;
    }

    @Override
    public boolean isTopLevelOnly() {
        // a policy is often top-level but you can have it in lower-levels as
        // well
        return false;
    }

    @Override
    public boolean isWrappingEntireOutput() {
        return true;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
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
    public PolicyDefinition ref(String ref) {
        setRef(ref);
        return this;
    }

}
