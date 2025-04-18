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
import org.zenithblox.util.ObjectHelper;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Helper that validates component parameters.
 */
public final class ResultErrorHelper {

    private ResultErrorHelper() {
    }

    // **********************************
    // Helpers
    // **********************************

    /**
     *
     * @param      parameterName the required option
     * @param      parameters    the parameters
     * @return                   an optional error
     *
     * @deprecated               use {@link #requiresOption(Map, String)} instead
     */
    @Deprecated
    public static Optional<VerificationError> requiresOption(String parameterName, Map<String, Object> parameters) {
        return Optional.ofNullable(requiresOption(parameters, parameterName));
    }

    /**
     *
     * @param  parameterName the required option
     * @param  parameters    the parameters
     * @return               an error or null
     */
    public static VerificationError requiresOption(Map<String, Object> parameters, String parameterName) {
        if (ObjectHelper.isEmpty(parameters.get(parameterName))) {
            return ResultErrorBuilder.withMissingOption(parameterName).build();
        }
        return null;
    }

    /**
     * Validates that the given parameters satisfy any grouped options ({@link OptionsGroup}). A parameter set is valid
     * if it is present and required by least one of the groups.
     *
     * <p>
     * As an example consider that there are two option groups that can be specified:
     * <ul>
     * <li>optionA: requires param1 and param2
     * <li>optionB: requires param1 and param3
     * </ul>
     *
     * Valid parameters are those that include param1 and either param2 and/or param3.
     *
     * <p>
     * Note the special syntax of {@link OptionsGroup#getOptions()} that can require an property
     * ({@code "propertyName"}) or can forbid the presence of a property ({@code "!propertyName"}).
     *
     * <p>
     * With that if in the example above if param2 is specified specifying param3 is not allowed, and vice versa option
     * groups should be defined with options:
     * <ul>
     * <li>optionA: ["param1", "param2", "!param3"]
     * <li>optionB: ["param1", "!param2", "param3"]
     * </ul>
     *
     * @param parameters given parameters of a component
     * @param groups     groups of options
     * @see              OptionsGroup
     */
    public static List<VerificationError> requiresAny(Map<String, Object> parameters, OptionsGroup... groups) {
        return requiresAny(parameters, Arrays.asList(groups));
    }

    /**
     * Validates that the given parameters satisfy any grouped options ({@link OptionsGroup}). A parameter set is valid
     * if it is present and required by least one of the groups.
     *
     * @param parameters given parameters of a component
     * @param groups     groups of options
     * @see              #requiresAny(Map, OptionsGroup...)
     * @see              OptionsGroup
     */
    public static List<VerificationError> requiresAny(Map<String, Object> parameters, Collection<OptionsGroup> groups) {
        final List<VerificationError> verificationErrors = new ArrayList<>();
        final Set<String> keys = new HashSet<>(parameters.keySet());

        for (OptionsGroup group : groups) {
            final Set<String> required = required(group.getOptions());
            final Set<String> excluded = excluded(group.getOptions());

            final ResultErrorBuilder builder = new ResultErrorBuilder()
                    .code(VerificationError.StandardCode.ILLEGAL_PARAMETER_GROUP_COMBINATION)
                    .detail(VerificationError.GroupAttribute.GROUP_NAME, group.getName())
                    .detail(VerificationError.GroupAttribute.GROUP_OPTIONS, String.join(",", parameters(group.getOptions())));

            if (keys.containsAll(required)) {
                // All the options of this group are found so we are good
                final Set<String> shouldBeExcluded = new HashSet<>(keys);
                shouldBeExcluded.retainAll(excluded);

                if (shouldBeExcluded.isEmpty()) {
                    // None of the excluded properties is present, also good
                    return Collections.emptyList();
                }

                shouldBeExcluded.forEach(builder::parameterKey);
                verificationErrors.add(builder.build());
            } else {

                for (String option : required) {
                    if (!parameters.containsKey(option)) {
                        builder.parameterKey(option);
                    }
                }

                for (String option : excluded) {
                    if (parameters.containsKey(option)) {
                        builder.parameterKey(option);
                    }
                }

                verificationErrors.add(builder.build());
            }
        }

        return verificationErrors;
    }

    static Set<String> required(final Set<String> options) {
        return options.stream().filter(o -> !o.startsWith("!")).collect(Collectors.toSet());
    }

    static Set<String> excluded(final Set<String> options) {
        return options.stream().filter(o -> o.startsWith("!")).map(o -> o.substring(1)).collect(Collectors.toSet());
    }

    static Set<String> parameters(final Set<String> options) {

        return options.stream().map(o -> o.replaceFirst("!", ""))
                .collect(Collectors.toCollection(TreeSet::new));
    }
}
