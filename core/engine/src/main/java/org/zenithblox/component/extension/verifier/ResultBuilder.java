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
import org.zenithblox.util.function.ThrowingBiConsumer;
import org.zenithblox.util.function.ThrowingConsumer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public final class ResultBuilder {
    private ComponentVerifierExtension.Scope scope;
    private ComponentVerifierExtension.Result.Status status;
    private List<ComponentVerifierExtension.VerificationError> verificationErrors;

    public ResultBuilder() {
        this.scope = null;
        this.status = null;
    }

    // **********************************
    // Accessors
    // **********************************

    public ResultBuilder scope(ComponentVerifierExtension.Scope scope) {
        this.scope = scope;
        return this;
    }

    public ResultBuilder status(ComponentVerifierExtension.Result.Status status) {
        this.status = status;
        return this;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Deprecated
    public ResultBuilder error(Optional<ComponentVerifierExtension.VerificationError> verificationError) {
        return error(verificationError.orElse(null));
    }

    public ResultBuilder error(ComponentVerifierExtension.VerificationError verificationError) {
        if (verificationError != null) {
            if (this.verificationErrors == null) {
                this.verificationErrors = new ArrayList<>();
            }

            this.verificationErrors.add(verificationError);
            this.status = ComponentVerifierExtension.Result.Status.ERROR;
        }
        return this;
    }

    public ResultBuilder error(Supplier<ComponentVerifierExtension.VerificationError> supplier) {
        return error(supplier.get());
    }

    public ResultBuilder error(ThrowingConsumer<ResultBuilder, Exception> consumer) {
        try {
            consumer.accept(this);
        } catch (NoSuchOptionException e) {
            error(ResultErrorBuilder.withMissingOption(e.getOptionName()).build());
        } catch (IllegalOptionException e) {
            error(ResultErrorBuilder.withIllegalOption(e.getOptionName(), e.getOptionValue()).build());
        } catch (Exception e) {
            error(ResultErrorBuilder.withException(e).build());
        }

        return this;
    }

    public <T> ResultBuilder error(T data, ThrowingBiConsumer<ResultBuilder, T, Exception> consumer) {
        try {
            consumer.accept(this, data);
        } catch (NoSuchOptionException e) {
            error(ResultErrorBuilder.withMissingOption(e.getOptionName()).build());
        } catch (IllegalOptionException e) {
            error(ResultErrorBuilder.withIllegalOption(e.getOptionName(), e.getOptionValue()).build());
        } catch (Exception e) {
            error(ResultErrorBuilder.withException(e).build());
        }

        return this;
    }

    public ResultBuilder errors(List<ComponentVerifierExtension.VerificationError> verificationErrors) {
        verificationErrors.forEach(this::error);
        return this;
    }

    // **********************************
    // Build
    // **********************************

    public ComponentVerifierExtension.Result build() {
        return new DefaultResult(
                scope != null ? scope : ComponentVerifierExtension.Scope.PARAMETERS,
                status != null ? status : ComponentVerifierExtension.Result.Status.UNSUPPORTED,
                verificationErrors != null ? Collections.unmodifiableList(verificationErrors) : Collections.emptyList());
    }

    // **********************************
    // Helpers
    // **********************************

    public static ResultBuilder withStatus(ComponentVerifierExtension.Result.Status status) {
        return new ResultBuilder().status(status);
    }

    public static ResultBuilder withStatusAndScope(
            ComponentVerifierExtension.Result.Status status, ComponentVerifierExtension.Scope scope) {
        return new ResultBuilder().status(status).scope(scope);
    }

    public static ResultBuilder withScope(ComponentVerifierExtension.Scope scope) {
        return new ResultBuilder().scope(scope);
    }

    public static ResultBuilder unsupported() {
        return withStatusAndScope(ComponentVerifierExtension.Result.Status.UNSUPPORTED,
                ComponentVerifierExtension.Scope.PARAMETERS);
    }

    public static ResultBuilder unsupportedScope(ComponentVerifierExtension.Scope scope) {
        return withStatusAndScope(ComponentVerifierExtension.Result.Status.UNSUPPORTED, scope);
    }
}
