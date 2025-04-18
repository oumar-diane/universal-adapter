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
 * Removes message exchange properties whose name matches a specified pattern
 */
@Metadata(label = "eip,transformation")
public class RemovePropertiesDefinition extends NoOutputDefinition<RemovePropertiesDefinition> {

    // in XML we cannot use String[] for attributes, so we provide a single
    // attribute instead
    private String[] excludePatterns;

    private String pattern;
    @Metadata(label = "advanced")
    private String excludePattern;

    public RemovePropertiesDefinition() {
    }

    protected RemovePropertiesDefinition(RemovePropertiesDefinition source) {
        super(source);
        this.excludePatterns = source.excludePatterns;
        this.pattern = source.pattern;
        this.excludePattern = source.excludePattern;
    }

    public RemovePropertiesDefinition(String pattern) {
        setPattern(pattern);
    }

    public RemovePropertiesDefinition(String pattern, String... excludePatterns) {
        setPattern(pattern);
        setExcludePatterns(excludePatterns);
    }

    @Override
    public RemovePropertiesDefinition copyDefinition() {
        return new RemovePropertiesDefinition(this);
    }

    @Override
    public String toString() {
        return "removeProperties[" + getPattern() + "]";
    }

    @Override
    public String getShortName() {
        return "removeProperties";
    }

    @Override
    public String getLabel() {
        return "removeProperties[" + getPattern() + "]";
    }

    /**
     * Name or pattern of properties to remove. The pattern is matched in the following order: 1 = exact match 2 =
     * wildcard (pattern ends with a * and the name starts with the pattern) 3 = regular expression (all of above is
     * case in-sensitive).
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }

    public String[] getExcludePatterns() {
        return excludePatterns;
    }

    /**
     * Name or pattern of properties to not remove. The pattern is matched in the following order: 1 = exact match 2 =
     * wildcard (pattern ends with a * and the name starts with the pattern) 3 = regular expression (all of above is
     * case in-sensitive).
     */
    public void setExcludePatterns(String[] excludePatterns) {
        this.excludePatterns = excludePatterns;
    }

    public String getExcludePattern() {
        return excludePattern;
    }

    /**
     * Name or pattern of properties to not remove. The pattern is matched in the following order: 1 = exact match 2 =
     * wildcard (pattern ends with a * and the name starts with the pattern) 3 = regular expression (all of above is
     * case in-sensitive).
     */
    public void setExcludePattern(String excludePattern) {
        this.excludePattern = excludePattern;
    }
}
