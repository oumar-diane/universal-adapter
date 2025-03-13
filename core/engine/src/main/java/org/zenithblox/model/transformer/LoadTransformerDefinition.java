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
package org.zenithblox.model.transformer;

import org.zenithblox.spi.Metadata;

/**
 * Loads one to many {@link org.zenithblox.spi.Transformer} via {@link org.zenithblox.spi.TransformerLoader}.
 * Supports classpath scan to load transformer implementations configured for instance via annotation configuration.
 */
@Metadata(label = "transformation")
public class LoadTransformerDefinition extends TransformerDefinition {

    private String packageScan;
    @Metadata(javaType = "java.lang.Boolean", defaultValue = "false")
    private String defaults;

    public LoadTransformerDefinition() {
    }

    protected LoadTransformerDefinition(LoadTransformerDefinition source) {
        super(source);
        this.packageScan = source.packageScan;
        this.defaults = source.defaults;
    }

    @Override
    public LoadTransformerDefinition copyDefinition() {
        return new LoadTransformerDefinition(this);
    }

    public String getDefaults() {
        return defaults;
    }

    /**
     * Enable loading of default transformers.
     */
    public void setDefaults(String defaults) {
        this.defaults = defaults;
    }

    public String getPackageScan() {
        return packageScan;
    }

    /**
     * Set the classpath location to scan for annotated transformers.
     */
    public void setPackageScan(String packageScan) {
        this.packageScan = packageScan;
    }

}
