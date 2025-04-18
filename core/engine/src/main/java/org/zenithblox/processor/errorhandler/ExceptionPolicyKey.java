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
package org.zenithblox.processor.errorhandler;

import org.zenithblox.Predicate;

/**
 * Exception policy key is a compound key for storing: <b>workflow id</b> + <b>exception class</b> + <b>when</b> =>
 * <b>exception type</b>.
 * <p/>
 * This is used by Zwangine to store the onException types configured that has or has not predicates attached (when).
 */
public final class ExceptionPolicyKey {

    private final String workflowId;
    private final Class<? extends Throwable> exceptionClass;
    private final Predicate when;

    /**
     * Key for exception clause
     *
     * @param workflowId        the workflow, or use <tt>null</tt> for a global scoped
     * @param exceptionClass the exception class
     * @param when           optional predicate when the exception clause should trigger
     */
    public ExceptionPolicyKey(String workflowId, Class<? extends Throwable> exceptionClass, Predicate when) {
        this.workflowId = workflowId;
        this.exceptionClass = exceptionClass;
        this.when = when;
    }

    public Class<?> getExceptionClass() {
        return exceptionClass;
    }

    public Predicate getWhen() {
        return when;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ExceptionPolicyKey that = (ExceptionPolicyKey) o;

        if (exceptionClass != null ? !exceptionClass.equals(that.exceptionClass) : that.exceptionClass != null) {
            return false;
        }
        if (workflowId != null ? !workflowId.equals(that.workflowId) : that.workflowId != null) {
            return false;
        }
        if (when != null ? !when.equals(that.when) : that.when != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = workflowId != null ? workflowId.hashCode() : 0;
        result = 31 * result + (exceptionClass != null ? exceptionClass.hashCode() : 0);
        result = 31 * result + (when != null ? when.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ExceptionPolicyKey[workflow: " + (workflowId != null ? workflowId : "<global>") + ", " + exceptionClass
               + (when != null ? " " + when : "") + "]";
    }
}
