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
 * Evaluates a Java (Java compiled once at runtime) expression.
 */
@Metadata(firstVersion = "4.3.0", label = "language", title = "Java")
public class JavaExpression extends TypedExpressionDefinition {

    @Metadata(label = "advanced", defaultValue = "true", javaType = "java.lang.Boolean")
    private String preCompile;
    @Metadata(label = "advanced", defaultValue = "true", javaType = "java.lang.Boolean")
    private String singleQuotes;

    public JavaExpression() {
    }

    protected JavaExpression(JavaExpression source) {
        super(source);
        this.preCompile = source.preCompile;
        this.singleQuotes = source.singleQuotes;
    }

    public JavaExpression(String expression) {
        super(expression);
    }

    private JavaExpression(Builder builder) {
        super(builder);
        this.preCompile = builder.preCompile;
        this.singleQuotes = builder.singleQuotes;
    }

    @Override
    public JavaExpression copyDefinition() {
        return new JavaExpression(this);
    }

    @Override
    public String getLanguage() {
        return "java";
    }

    public String getPreCompile() {
        return preCompile;
    }

    /**
     * Whether the expression should be pre compiled once during initialization phase. If this is turned off, then the
     * expression is reloaded and compiled on each evaluation.
     */
    public void setPreCompile(String preCompile) {
        this.preCompile = preCompile;
    }

    public String getSingleQuotes() {
        return singleQuotes;
    }

    /**
     * Whether single quotes can be used as replacement for double quotes. This is convenient when you need to work with
     * strings inside strings.
     */
    public void setSingleQuotes(String singleQuotes) {
        this.singleQuotes = singleQuotes;
    }

    /**
     * {@code Builder} is a specific builder for {@link JavaExpression}.
     */
    public static class Builder extends AbstractBuilder<Builder, JavaExpression> {

        private String preCompile;
        private String singleQuotes;

        /**
         * Whether the expression should be pre compiled once during initialization phase. If this is turned off, then
         * the expression is reloaded and compiled on each evaluation.
         */
        public Builder preCompile(String preCompile) {
            this.preCompile = preCompile;
            return this;
        }

        /**
         * Whether the expression should be pre compiled once during initialization phase. If this is turned off, then
         * the expression is reloaded and compiled on each evaluation.
         */
        public Builder preCompile(boolean preCompile) {
            this.preCompile = Boolean.toString(preCompile);
            return this;
        }

        /**
         * Whether single quotes can be used as replacement for double quotes. This is convenient when you need to work
         * with strings inside strings.
         */
        public Builder singleQuotes(String singleQuotes) {
            this.singleQuotes = singleQuotes;
            return this;
        }

        /**
         * Whether single quotes can be used as replacement for double quotes. This is convenient when you need to work
         * with strings inside strings.
         */
        public Builder singleQuotes(boolean singleQuotes) {
            this.singleQuotes = Boolean.toString(singleQuotes);
            return this;
        }

        @Override
        public JavaExpression end() {
            return new JavaExpression(this);
        }
    }
}
