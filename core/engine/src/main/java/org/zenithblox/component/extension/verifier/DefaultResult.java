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
package org.zenithblox.component.extension.verifier;

import org.zenithblox.component.extension.ComponentVerifierExtension;

import java.util.List;

public class DefaultResult implements ComponentVerifierExtension.Result {
    private final ComponentVerifierExtension.Scope scope;
    private final Status status;
    private final List<ComponentVerifierExtension.VerificationError> verificationErrors;

    public DefaultResult(ComponentVerifierExtension.Scope scope, Status status,
                         List<ComponentVerifierExtension.VerificationError> verificationErrors) {
        this.scope = scope;
        this.status = status;
        this.verificationErrors = verificationErrors;
    }

    @Override
    public ComponentVerifierExtension.Scope getScope() {
        return scope;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public List<ComponentVerifierExtension.VerificationError> getErrors() {
        return verificationErrors;
    }

    @Override
    public String toString() {
        return "DefaultResult{"
               + "scope=" + scope
               + ", status=" + status
               + ", errors=" + verificationErrors
               + '}';
    }
}
