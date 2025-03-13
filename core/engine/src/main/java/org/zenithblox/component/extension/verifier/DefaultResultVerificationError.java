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

import org.zenithblox.component.extension.ComponentVerifierExtension.VerificationError;

import java.util.Map;
import java.util.Set;

public class DefaultResultVerificationError implements VerificationError {
    private final Code code;
    private final String description;
    private final Set<String> parameterKeys;
    private final Map<Attribute, Object> details;

    public DefaultResultVerificationError(Code code, String description, Set<String> parameterKeys,
                                          Map<Attribute, Object> details) {
        this.code = code;
        this.description = description;
        this.parameterKeys = parameterKeys;
        this.details = details;
    }

    @Override
    public Code getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Set<String> getParameterKeys() {
        return parameterKeys;
    }

    @Override
    public Map<Attribute, Object> getDetails() {
        return details;
    }

    @Override
    public String toString() {
        return "DefaultResultError{"
               + "code='" + code + '\''
               + ", description='" + description + '\''
               + ", parameterKeys=" + parameterKeys
               + ", details=" + details
               + '}';
    }
}
