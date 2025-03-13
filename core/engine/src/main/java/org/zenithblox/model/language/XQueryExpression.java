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
 * Evaluates an XQuery expressions against an XML payload.
 */
@Metadata(firstVersion = "1.0.0", label = "language,xml", title = "XQuery")
public class XQueryExpression extends NamespaceAwareExpression {

    private Object configuration;
    @Metadata(label = "advanced")
    private String configurationRef;

    public XQueryExpression() {
    }

    protected XQueryExpression(XQueryExpression source) {
        super(source);
        this.configuration = source.configuration;
        this.configurationRef = source.configurationRef;
    }

    public XQueryExpression(String expression) {
        super(expression);
    }

    private XQueryExpression(Builder builder) {
        super(builder);
        this.configuration = builder.configuration;
        this.configurationRef = builder.configurationRef;
    }

    @Override
    public XQueryExpression copyDefinition() {
        return new XQueryExpression(this);
    }

    @Override
    public String getLanguage() {
        return "xquery";
    }

    public String getConfigurationRef() {
        return configurationRef;
    }

    /**
     * Reference to a saxon configuration instance in the registry to use for xquery (requires zwangine-saxon). This may be
     * needed to add custom functions to a saxon configuration, so these custom functions can be used in xquery
     * expressions.
     */
    public void setConfigurationRef(String configurationRef) {
        this.configurationRef = configurationRef;
    }

    public Object getConfiguration() {
        return configuration;
    }

    /**
     * Custom saxon configuration (requires zwangine-saxon). This may be needed to add custom functions to a saxon
     * configuration, so these custom functions can be used in xquery expressions.
     */
    public void setConfiguration(Object configuration) {
        this.configuration = configuration;
    }

    /**
     * {@code Builder} is a specific builder for {@link XQueryExpression}.
     */
    public static class Builder extends AbstractNamespaceAwareBuilder<Builder, XQueryExpression> {

        private Object configuration;
        private String configurationRef;

        /**
         * Custom saxon configuration (requires zwangine-saxon). This may be needed to add custom functions to a saxon
         * configuration, so these custom functions can be used in xquery expressions.
         */
        public Builder configuration(Object configuration) {
            this.configuration = configuration;
            return this;
        }

        /**
         * Reference to a saxon configuration instance in the registry to use for xquery (requires zwangine-saxon). This
         * may be needed to add custom functions to a saxon configuration, so these custom functions can be used in
         * xquery expressions.
         */
        public Builder configurationRef(String configurationRef) {
            this.configurationRef = configurationRef;
            return this;
        }

        @Override
        public XQueryExpression end() {
            return new XQueryExpression(this);
        }
    }
}
