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

import org.zenithblox.LoggingLevel;
import org.zenithblox.spi.Metadata;

/**
 * Transactional error handler (requires either zwangine-spring or zwangine-jta using traditional JTA transactions).
 */
public abstract class TransactionErrorHandlerDefinition extends DefaultErrorHandlerDefinition {

    private Object transactedPolicy;

    @Metadata(javaType = "org.zenithblox.spi.TransactedPolicy")
    private String transactedPolicyRef;
    @Metadata(javaType = "org.zenithblox.LoggingLevel", defaultValue = "WARN", enums = "TRACE,DEBUG,INFO,WARN,ERROR,OFF")
    private String rollbackLoggingLevel;

    public TransactionErrorHandlerDefinition() {
    }

    public TransactionErrorHandlerDefinition(TransactionErrorHandlerDefinition source) {
        super(source);
        this.transactedPolicy = source.transactedPolicy;
        this.transactedPolicyRef = source.transactedPolicyRef;
        this.rollbackLoggingLevel = source.rollbackLoggingLevel;
    }

    @Override
    public boolean supportTransacted() {
        return true;
    }

    protected void cloneBuilder(TransactionErrorHandlerDefinition other) {
        other.setTransactedPolicyRef(getTransactedPolicyRef());
        other.setRollbackLoggingLevel(getRollbackLoggingLevel());
        super.cloneBuilder(other);
    }

    public Object getTransactedPolicy() {
        return transactedPolicy;
    }

    /**
     * The transacted policy to use that is configured for either Spring or JTA based transactions.
     */
    public void setTransactedPolicy(Object transactedPolicy) {
        this.transactedPolicy = transactedPolicy;
    }

    public String getTransactedPolicyRef() {
        return transactedPolicyRef;
    }

    /**
     * The transacted policy to use that is configured for either Spring or JTA based transactions. If no policy has
     * been configured then Zwangine will attempt to auto-discover.
     */
    public void setTransactedPolicyRef(String transactedPolicyRef) {
        this.transactedPolicyRef = transactedPolicyRef;
    }

    public String getRollbackLoggingLevel() {
        return rollbackLoggingLevel;
    }

    /**
     * Sets the logging level to use for logging transactional rollback.
     * <p/>
     * This option is default WARN.
     */
    public void setRollbackLoggingLevel(String rollbackLoggingLevel) {
        this.rollbackLoggingLevel = rollbackLoggingLevel;
    }

    /**
     * The transacted policy to use that is configured for either Spring or JTA based transactions.
     */
    public TransactionErrorHandlerDefinition transactedPolicy(Object transactedPolicy) {
        setTransactedPolicy(transactedPolicy);
        return this;
    }

    /**
     * References to the transacted policy to use that is configured for either Spring or JTA based transactions.
     */
    public TransactionErrorHandlerDefinition transactedPolicyRef(String transactedPolicyRef) {
        setTransactedPolicyRef(transactedPolicyRef);
        return this;
    }

    /**
     * Sets the logging level to use for logging transactional rollback.
     * <p/>
     * This option is default WARN.
     */
    public TransactionErrorHandlerDefinition rollbackLoggingLevel(String rollbackLoggingLevel) {
        setRollbackLoggingLevel(rollbackLoggingLevel);
        return this;
    }

    /**
     * Sets the logging level to use for logging transactional rollback.
     * <p/>
     * This option is default WARN.
     */
    public TransactionErrorHandlerDefinition rollbackLoggingLevel(LoggingLevel rollbackLoggingLevel) {
        setRollbackLoggingLevel(rollbackLoggingLevel.name());
        return this;
    }

}
