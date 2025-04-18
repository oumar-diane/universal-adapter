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
 * Gets a property from the Exchange.
 */
@Metadata(firstVersion = "2.0.0", label = "language,core", title = "ExchangeProperty")
public class ExchangePropertyExpression extends ExpressionDefinition {

    public ExchangePropertyExpression() {
    }

    protected ExchangePropertyExpression(ExchangePropertyExpression source) {
        super(source);
    }

    public ExchangePropertyExpression(String name) {
        super(name);
    }

    private ExchangePropertyExpression(Builder builder) {
        super(builder);
    }

    @Override
    public ExchangePropertyExpression copyDefinition() {
        return new ExchangePropertyExpression(this);
    }

    @Override
    public String getLanguage() {
        return "exchangeProperty";
    }

    /**
     * {@code Builder} is a specific builder for {@link ExchangePropertyExpression}.
     */
    public static class Builder extends AbstractBuilder<Builder, ExchangePropertyExpression> {

        @Override
        public ExchangePropertyExpression end() {
            return new ExchangePropertyExpression(this);
        }
    }
}
