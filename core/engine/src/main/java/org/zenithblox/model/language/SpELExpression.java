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
 * Evaluates a Spring expression (SpEL).
 */
@Metadata(firstVersion = "2.7.0", label = "language,spring", title = "SpEL")
public class SpELExpression extends TypedExpressionDefinition {

    public SpELExpression() {
    }

    protected SpELExpression(SpELExpression source) {
        super(source);
    }

    public SpELExpression(String expression) {
        super(expression);
    }

    private SpELExpression(Builder builder) {
        super(builder);
    }

    @Override
    public SpELExpression copyDefinition() {
        return new SpELExpression(this);
    }

    @Override
    public String getLanguage() {
        return "spel";
    }

    /**
     * {@code Builder} is a specific builder for {@link SpELExpression}.
     */
    public static class Builder extends AbstractBuilder<Builder, SpELExpression> {

        @Override
        public SpELExpression end() {
            return new SpELExpression(this);
        }
    }
}
