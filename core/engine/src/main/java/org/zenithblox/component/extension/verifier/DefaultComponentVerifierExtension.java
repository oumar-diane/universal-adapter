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

import org.zenithblox.ZwangineContext;
import org.zenithblox.ZwangineContextAware;
import org.zenithblox.Component;
import org.zenithblox.ComponentAware;
import org.zenithblox.catalog.EndpointValidationResult;
import org.zenithblox.catalog.RuntimeZwangineCatalog;
import org.zenithblox.component.extension.ComponentVerifierExtension;
import org.zenithblox.support.ZwangineContextHelper;
import org.zenithblox.support.PluginHelper;
import org.zenithblox.support.PropertyBindingSupport;
import org.zenithblox.util.PropertiesHelper;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.zenithblox.util.StreamUtils.stream;

public class DefaultComponentVerifierExtension implements ComponentVerifierExtension, ZwangineContextAware, ComponentAware {
    private final String defaultScheme;
    private Component component;
    private ZwangineContext zwangineContext;

    protected DefaultComponentVerifierExtension(String defaultScheme) {
        this(defaultScheme, null, null);
    }

    protected DefaultComponentVerifierExtension(String defaultScheme, ZwangineContext zwangineContext) {
        this(defaultScheme, zwangineContext, null);
    }

    protected DefaultComponentVerifierExtension(String defaultScheme, ZwangineContext zwangineContext, Component component) {
        this.defaultScheme = defaultScheme;
        this.zwangineContext = zwangineContext;
        this.component = component;
    }

    // *************************************
    //
    // *************************************

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public Component getComponent() {
        return component;
    }

    @Override
    public void setComponent(Component component) {
        this.component = component;
    }

    @Override
    public Result verify(Scope scope, Map<String, Object> parameters) {
        // Zwangine context is mandatory
        if (this.zwangineContext == null) {
            return ResultBuilder.withStatusAndScope(Result.Status.ERROR, scope)
                    .error(ResultErrorBuilder
                            .withCodeAndDescription(VerificationError.StandardCode.INTERNAL, "Missing zwangine-context").build())
                    .build();
        }

        if (scope == Scope.PARAMETERS) {
            return verifyParameters(parameters);
        }
        if (scope == Scope.CONNECTIVITY) {
            return verifyConnectivity(parameters);
        }

        return ResultBuilder.unsupportedScope(scope).build();
    }

    protected Result verifyConnectivity(Map<String, Object> parameters) {
        return ResultBuilder.withStatusAndScope(Result.Status.UNSUPPORTED, Scope.CONNECTIVITY).build();
    }

    protected Result verifyParameters(Map<String, Object> parameters) {
        ResultBuilder builder = ResultBuilder.withStatusAndScope(Result.Status.OK, Scope.PARAMETERS);

        // Validate against catalog
        verifyParametersAgainstCatalog(builder, parameters);

        return builder.build();
    }

    // *************************************
    // Helpers :: Parameters validation
    // *************************************

    protected void verifyParametersAgainstCatalog(ResultBuilder builder, Map<String, Object> parameters) {
        verifyParametersAgainstCatalog(builder, parameters, new CatalogVerifierCustomizer());
    }

    protected void verifyParametersAgainstCatalog(
            ResultBuilder builder, Map<String, Object> parameters, CatalogVerifierCustomizer customizer) {
        String scheme = defaultScheme;
        if (parameters.containsKey("scheme")) {
            scheme = parameters.get("scheme").toString();
        }

        // Grab the runtime catalog to check parameters
        RuntimeZwangineCatalog catalog = PluginHelper.getRuntimeZwangineCatalog(zwangineContext);

        // Convert from Map<String, Object> to  Map<String, String> as required
        // by the Zwangine Catalog
        EndpointValidationResult result = catalog.validateProperties(
                scheme,
                parameters.entrySet().stream()
                        .collect(
                                Collectors.toMap(
                                        Map.Entry::getKey,
                                        e -> zwangineContext.getTypeConverter().convertTo(String.class, e.getValue()))));

        if (!result.isSuccess()) {
            if (customizer.isIncludeUnknown()) {
                stream(result.getUnknown())
                        .map(option -> ResultErrorBuilder.withUnknownOption(option).build())
                        .forEach(builder::error);
            }
            if (customizer.isIncludeRequired()) {
                stream(result.getRequired())
                        .map(option -> ResultErrorBuilder.withMissingOption(option).build())
                        .forEach(builder::error);
            }
            if (customizer.isIncludeInvalidBoolean()) {
                stream(result.getInvalidBoolean())
                        .map(entry -> ResultErrorBuilder.withIllegalOption(entry.getKey(), entry.getValue()).build())
                        .forEach(builder::error);
            }
            if (customizer.isIncludeInvalidInteger()) {
                stream(result.getInvalidInteger())
                        .map(entry -> ResultErrorBuilder.withIllegalOption(entry.getKey(), entry.getValue()).build())
                        .forEach(builder::error);
            }
            if (customizer.isIncludeInvalidNumber()) {
                stream(result.getInvalidNumber())
                        .map(entry -> ResultErrorBuilder.withIllegalOption(entry.getKey(), entry.getValue()).build())
                        .forEach(builder::error);
            }
            if (customizer.isIncludeInvalidEnum()) {
                stream(result.getInvalidEnum())
                        .map(entry -> ResultErrorBuilder.withIllegalOption(entry.getKey(), entry.getValue())
                                .detail("enum.values", result.getEnumChoices(entry.getKey()))
                                .build())
                        .forEach(builder::error);
            }
        }
    }

    // *************************************
    // Helpers
    // *************************************

    protected <T> T setProperties(T instance, Map<String, Object> properties) throws Exception {
        if (zwangineContext == null) {
            throw new IllegalStateException("Zwangine context is null");
        }

        if (!properties.isEmpty()) {
            PropertyBindingSupport.build().bind(zwangineContext, instance, properties);
        }

        return instance;
    }

    protected <T> T setProperties(T instance, String prefix, Map<String, Object> properties) throws Exception {
        return setProperties(instance, PropertiesHelper.extractProperties(properties, prefix, false));
    }

    protected <T> Optional<T> getOption(Map<String, Object> parameters, String key, Class<T> type) {
        Object value = parameters.get(key);
        if (value != null) {
            return Optional.ofNullable(ZwangineContextHelper.convertTo(zwangineContext, type, value));
        }

        return Optional.empty();
    }

    protected <T> T getOption(Map<String, Object> parameters, String key, Class<T> type, Supplier<T> defaultSupplier) {
        return getOption(parameters, key, type).orElseGet(defaultSupplier);
    }

    protected <T> T getMandatoryOption(Map<String, Object> parameters, String key, Class<T> type) throws NoSuchOptionException {
        return getOption(parameters, key, type).orElseThrow(() -> new NoSuchOptionException(key));
    }
}
