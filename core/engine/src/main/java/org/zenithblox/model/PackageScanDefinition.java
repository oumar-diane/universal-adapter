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
 * Scans for Java {@link org.zenithblox.builder.WorkflowBuilder} classes in java packages
 */
@Metadata(label = "configuration")
public class PackageScanDefinition {

    private List<String> packages = new ArrayList<>();
    @Metadata(label = "advanced")
    private List<String> excludes = new ArrayList<>();
    @Metadata(label = "advanced")
    private List<String> includes = new ArrayList<>();

    public PackageScanDefinition() {
    }

    public List<String> getExcludes() {
        return excludes;
    }

    public List<String> getIncludes() {
        return includes;
    }

    public List<String> getPackages() {
        return packages;
    }

    /**
     * Sets the java package names to use for scanning for workflow builder classes
     */
    public void setPackages(List<String> packages) {
        this.packages = packages;
    }

    /**
     * Exclude finding workflow builder from these java package names.
     */
    public void setExcludes(List<String> excludes) {
        this.excludes = excludes;
    }

    /**
     * Include finding workflow builder from these java package names.
     */
    public void setIncludes(List<String> includes) {
        this.includes = includes;
    }

    protected void clear() {
        packages.clear();
        excludes.clear();
        includes.clear();
    }
}
