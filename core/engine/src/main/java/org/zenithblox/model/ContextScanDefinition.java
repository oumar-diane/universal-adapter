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

import java.util.ArrayList;
import java.util.List;

/**
 * Scans for Java {@link org.zenithblox.builder.WorkflowBuilder} instances in the context
 * {@link org.zenithblox.spi.Registry}.
 */
@Metadata(label = "configuration")
public class ContextScanDefinition {

    @Metadata(label = "advanced", javaType = "java.lang.Boolean")
    private String includeNonSingletons;
    private List<String> excludes = new ArrayList<>();
    private List<String> includes = new ArrayList<>();

    public ContextScanDefinition() {
    }

    public String getIncludeNonSingletons() {
        return includeNonSingletons;
    }

    /**
     * Whether to include non-singleton beans (prototypes)
     * <p/>
     * By default only singleton beans is included in the context scan
     */
    public void setIncludeNonSingletons(String includeNonSingletons) {
        this.includeNonSingletons = includeNonSingletons;
    }

    public List<String> getExcludes() {
        return excludes;
    }

    /**
     * Exclude finding workflow builder from these java package names.
     */
    public void setExcludes(List<String> excludes) {
        this.excludes = excludes;
    }

    public List<String> getIncludes() {
        return includes;
    }

    /**
     * Include finding workflow builder from these java package names.
     */
    public void setIncludes(List<String> includes) {
        this.includes = includes;
    }

    protected void clear() {
        excludes.clear();
        includes.clear();
    }
}
