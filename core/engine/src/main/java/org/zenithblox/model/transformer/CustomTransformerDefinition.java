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
import org.zenithblox.spi.Transformer;

/**
 * Represents a CustomTransformer. One of the bean reference (ref) or fully qualified class name (type) of the custom
 * {@link Transformer} needs to be specified.
 */
@Metadata(label = "transformation")
public class CustomTransformerDefinition extends TransformerDefinition {

    private String ref;
    private String className;

    public CustomTransformerDefinition() {
    }

    protected CustomTransformerDefinition(CustomTransformerDefinition source) {
        super(source);
        this.ref = source.ref;
        this.className = source.className;
    }

    @Override
    public CustomTransformerDefinition copyDefinition() {
        return new CustomTransformerDefinition(this);
    }

    public String getRef() {
        return ref;
    }

    /**
     * Set a bean reference of the {@link Transformer}
     */
    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getClassName() {
        return className;
    }

    /**
     * Set a class name of the {@link Transformer}
     */
    public void setClassName(String className) {
        this.className = className;
    }

}
