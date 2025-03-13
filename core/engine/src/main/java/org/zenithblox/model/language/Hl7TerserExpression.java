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
 * Get the value of a HL7 message field specified by terse location specification syntax.
 */
@Metadata(firstVersion = "2.11.0", label = "language,hl7", title = "HL7 Terser")
public class Hl7TerserExpression extends SingleInputTypedExpressionDefinition {

    public Hl7TerserExpression() {
    }

    protected Hl7TerserExpression(Hl7TerserExpression source) {
        super(source);
    }

    public Hl7TerserExpression(String expression) {
        super(expression);
    }

    private Hl7TerserExpression(Builder builder) {
        super(builder);
    }

    @Override
    public Hl7TerserExpression copyDefinition() {
        return new Hl7TerserExpression(this);
    }

    @Override
    public String getLanguage() {
        return "hl7terser";
    }

    /**
     * {@code Builder} is a specific builder for {@link Hl7TerserExpression}.
     */
    public static class Builder extends AbstractBuilder<Builder, Hl7TerserExpression> {

        @Override
        public Hl7TerserExpression end() {
            return new Hl7TerserExpression(this);
        }
    }
}
