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

/**
 * Forces a rollback by stopping routing the message
 */
@Metadata(label = "eip,routing")
public class RollbackDefinition extends NoOutputDefinition<RollbackDefinition> {

    private String message;
    @Metadata(javaType = "java.lang.Boolean")
    private String markRollbackOnly;
    @Metadata(label = "advanced", javaType = "java.lang.Boolean")
    private String markRollbackOnlyLast;

    public RollbackDefinition() {
    }

    protected RollbackDefinition(RollbackDefinition source) {
        super(source);
        this.message = source.message;
        this.markRollbackOnly = source.markRollbackOnly;
        this.markRollbackOnlyLast = source.markRollbackOnlyLast;
    }

    public RollbackDefinition(String message) {
        this.message = message;
    }

    @Override
    public RollbackDefinition copyDefinition() {
        return new RollbackDefinition(this);
    }

    @Override
    public String toString() {
        if (message != null) {
            return "Rollback[" + message + "]";
        } else {
            return "Rollback";
        }
    }

    @Override
    public String getShortName() {
        return "rollback";
    }

    @Override
    public String getLabel() {
        return "rollback";
    }

    public String getMessage() {
        return message;
    }

    /**
     * Message to use in rollback exception
     */
    public void setMessage(String message) {
        this.message = message;
    }

    public String getMarkRollbackOnly() {
        return markRollbackOnly;
    }

    /**
     * Mark the transaction for rollback only (cannot be overruled to commit)
     */
    public void setMarkRollbackOnly(String markRollbackOnly) {
        this.markRollbackOnly = markRollbackOnly;
    }

    public String getMarkRollbackOnlyLast() {
        return markRollbackOnlyLast;
    }

    /**
     * Mark only last sub transaction for rollback only.
     * <p/>
     * When using sub transactions (if the transaction manager support this)
     */
    public void setMarkRollbackOnlyLast(String markRollbackOnlyLast) {
        this.markRollbackOnlyLast = markRollbackOnlyLast;
    }

}
