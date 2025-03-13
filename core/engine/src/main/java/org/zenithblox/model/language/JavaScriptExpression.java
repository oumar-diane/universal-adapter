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
 * Evaluates a JavaScript expression.
 */
@Metadata(firstVersion = "3.20.0", label = "language,javascript", title = "JavaScript")
public class JavaScriptExpression extends TypedExpressionDefinition {

    public JavaScriptExpression() {
    }

    protected JavaScriptExpression(JavaScriptExpression source) {
        super(source);
    }

    public JavaScriptExpression(String expression) {
        super(expression);
    }

    private JavaScriptExpression(Builder builder) {
        super(builder);
    }

    @Override
    public JavaScriptExpression copyDefinition() {
        return new JavaScriptExpression(this);
    }

    @Override
    public String getLanguage() {
        return "js";
    }

    /**
     * {@code Builder} is a specific builder for {@link JavaScriptExpression}.
     */
    public static class Builder extends AbstractBuilder<Builder, JavaScriptExpression> {

        @Override
        public JavaScriptExpression end() {
            return new JavaScriptExpression(this);
        }
    }
}
