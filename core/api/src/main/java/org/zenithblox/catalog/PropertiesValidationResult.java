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
package org.zenithblox.catalog;

import java.io.Serializable;
import java.util.*;

abstract class PropertiesValidationResult implements Serializable {

    int errors;
    int warnings;

    // general error
    String syntaxError;
    // general warnings
    String unknownComponent;
    String incapable;

    // options
    Set<String> unknown;
    Map<String, String[]> unknownSuggestions;
    Set<String> required;
    Set<String> deprecated;
    Map<String, String> invalidEnum;
    Map<String, String[]> invalidEnumChoices;
    Map<String, String[]> invalidEnumSuggestions;
    Map<String, String> invalidMap;
    Map<String, String> invalidArray;
    Map<String, String> invalidReference;
    Map<String, String> invalidBoolean;
    Map<String, String> invalidInteger;
    Map<String, String> invalidNumber;
    Map<String, String> invalidDuration;
    Map<String, String> defaultValues;

    public boolean hasErrors() {
        return errors > 0;
    }

    public int getNumberOfErrors() {
        return errors;
    }

    public boolean hasWarnings() {
        return warnings > 0;
    }

    public int getNumberOfWarnings() {
        return warnings;
    }

    public boolean isSuccess() {
        boolean ok = syntaxError == null && unknown == null && required == null;
        if (ok) {
            ok = invalidEnum == null && invalidEnumChoices == null && invalidReference == null
                    && invalidBoolean == null && invalidInteger == null && invalidNumber == null
                    && invalidDuration == null;
        }
        if (ok) {
            ok = invalidMap == null && invalidArray == null;
        }
        return ok;
    }

    public void addSyntaxError(String syntaxError) {
        this.syntaxError = syntaxError;
        errors++;
    }

    public void addIncapable(String uri) {
        this.incapable = uri;
        warnings++;
    }

    public void addUnknownComponent(String name) {
        this.unknownComponent = name;
        warnings++;
    }

    public void addUnknown(String name) {
        if (unknown == null) {
            unknown = new LinkedHashSet<>();
        }
        if (!unknown.contains(name)) {
            unknown.add(name);
            errors++;
        }
    }

    public void addUnknownSuggestions(String name, String[] suggestions) {
        if (unknownSuggestions == null) {
            unknownSuggestions = new LinkedHashMap<>();
        }
        unknownSuggestions.put(name, suggestions);
    }

    public void addRequired(String name) {
        if (required == null) {
            required = new LinkedHashSet<>();
        }
        if (!required.contains(name)) {
            required.add(name);
            errors++;
        }
    }

    public void addDeprecated(String name) {
        if (deprecated == null) {
            deprecated = new LinkedHashSet<>();
        }
        deprecated.add(name);
    }

    private String computeErrors(String value) {
        errors++;
        return value;
    }

    public void addInvalidEnum(String name, String value) {
        if (invalidEnum == null) {
            invalidEnum = new LinkedHashMap<>();
        }

        invalidEnum.computeIfAbsent(name, k -> computeErrors(value));
    }

    public void addInvalidEnumChoices(String name, String[] choices) {
        if (invalidEnumChoices == null) {
            invalidEnumChoices = new LinkedHashMap<>();
        }
        invalidEnumChoices.put(name, choices);
    }

    public void addInvalidEnumSuggestions(String name, String[] suggestions) {
        if (invalidEnumSuggestions == null) {
            invalidEnumSuggestions = new LinkedHashMap<>();
        }
        invalidEnumSuggestions.put(name, suggestions);
    }

    public void addInvalidReference(String name, String value) {
        if (invalidReference == null) {
            invalidReference = new LinkedHashMap<>();
        }

        invalidReference.computeIfAbsent(name, k -> computeErrors(value));
    }

    public void addInvalidMap(String name, String value) {
        if (invalidMap == null) {
            invalidMap = new LinkedHashMap<>();
        }

        invalidMap.computeIfAbsent(name, k -> computeErrors(value));
    }

    public void addInvalidArray(String name, String value) {
        if (invalidArray == null) {
            invalidArray = new LinkedHashMap<>();
        }

        invalidArray.computeIfAbsent(name, k -> computeErrors(value));
    }

    public void addInvalidBoolean(String name, String value) {
        if (invalidBoolean == null) {
            invalidBoolean = new LinkedHashMap<>();
        }

        invalidBoolean.computeIfAbsent(name, k -> computeErrors(value));
    }

    public void addInvalidInteger(String name, String value) {
        if (invalidInteger == null) {
            invalidInteger = new LinkedHashMap<>();
        }

        invalidInteger.computeIfAbsent(name, k -> computeErrors(value));
    }

    public void addInvalidNumber(String name, String value) {
        if (invalidNumber == null) {
            invalidNumber = new LinkedHashMap<>();
        }

        invalidNumber.computeIfAbsent(name, k -> computeErrors(value));
    }

    public void addInvalidDuration(String name, String value) {
        if (invalidDuration == null) {
            invalidDuration = new LinkedHashMap<>();
        }

        invalidDuration.computeIfAbsent(name, k -> computeErrors(value));
    }

    public void addDefaultValue(String name, String value) {
        if (defaultValues == null) {
            defaultValues = new LinkedHashMap<>();
        }
        defaultValues.put(name, value);
    }

    public String getSyntaxError() {
        return syntaxError;
    }

    public String getIncapable() {
        return incapable;
    }

    public Set<String> getUnknown() {
        return unknown;
    }

    public Map<String, String[]> getUnknownSuggestions() {
        return unknownSuggestions;
    }

    public String getUnknownComponent() {
        return unknownComponent;
    }

    public Set<String> getRequired() {
        return required;
    }

    public Set<String> getDeprecated() {
        return deprecated;
    }

    public Map<String, String> getInvalidEnum() {
        return invalidEnum;
    }

    public Map<String, String[]> getInvalidEnumChoices() {
        return invalidEnumChoices;
    }

    public List<String> getEnumChoices(String optionName) {
        if (invalidEnumChoices != null) {
            String[] enums = invalidEnumChoices.get(optionName);
            if (enums != null) {
                return Arrays.asList(enums);
            }
        }

        return Collections.emptyList();
    }

    public Map<String, String> getInvalidReference() {
        return invalidReference;
    }

    public Map<String, String> getInvalidMap() {
        return invalidMap;
    }

    public Map<String, String> getInvalidArray() {
        return invalidArray;
    }

    public Map<String, String> getInvalidBoolean() {
        return invalidBoolean;
    }

    public Map<String, String> getInvalidInteger() {
        return invalidInteger;
    }

    public Map<String, String> getInvalidNumber() {
        return invalidNumber;
    }

    public Map<String, String> getInvalidDuration() {
        return invalidDuration;
    }

    public Map<String, String> getDefaultValues() {
        return defaultValues;
    }

    static boolean isEmpty(String value) {
        return value == null || value.isBlank();
    }

}
