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
 * Evaluates a Python expression.
 */
@Metadata(firstVersion = "3.19.0", label = "language,python", title = "Python")
public class PythonExpression extends TypedExpressionDefinition {

    public PythonExpression() {
    }

    protected PythonExpression(PythonExpression source) {
        super(source);
    }

    public PythonExpression(String expression) {
        super(expression);
    }

    private PythonExpression(Builder builder) {
        super(builder);
    }

    @Override
    public PythonExpression copyDefinition() {
        return new PythonExpression(this);
    }

    @Override
    public String getLanguage() {
        return "python";
    }

    /**
     * {@code Builder} is a specific builder for {@link PythonExpression}.
     */
    public static class Builder extends AbstractBuilder<Builder, PythonExpression> {

        @Override
        public PythonExpression end() {
            return new PythonExpression(this);
        }
    }
}
