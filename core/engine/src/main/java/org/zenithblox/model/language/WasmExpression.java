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
package org.zenithblox.model.language;

import org.zenithblox.spi.Metadata;

/**
 * Call a wasm (web assembly) function.
 */
@Metadata(firstVersion = "4.5.0", label = "language", title = "Wasm")
public class WasmExpression extends TypedExpressionDefinition {

    @Metadata(required = true)
    private String module;

    public WasmExpression() {
    }

    protected WasmExpression(WasmExpression source) {
        super(source);
        this.module = source.module;
    }

    public WasmExpression(String expression) {
        super(expression);
    }

    public WasmExpression(String expression, String module) {
        super(expression);
        this.module = module;
    }

    private WasmExpression(Builder builder) {
        super(builder);
        this.module = builder.module;
    }

    @Override
    public WasmExpression copyDefinition() {
        return new WasmExpression(this);
    }

    public String getModule() {
        return module;
    }

    /**
     * Set the module (the distributable, loadable, and executable unit of code in WebAssembly) resource that provides
     * the expression function.
     */
    public void setModule(String module) {
        this.module = module;
    }

    @Override
    public String getLanguage() {
        return "wasm";
    }

    /**
     * {@code Builder} is a specific builder for {@link WasmExpression}.
     */
    public static class Builder extends AbstractBuilder<Builder, WasmExpression> {

        private String module;

        /**
         * Set the module, the distributable, loadable, and executable unit of code in WebAssembly that provides the
         * expression function.
         */
        public Builder module(String module) {
            this.module = module;
            return this;
        }

        @Override
        public WasmExpression end() {
            return new WasmExpression(this);
        }
    }
}
