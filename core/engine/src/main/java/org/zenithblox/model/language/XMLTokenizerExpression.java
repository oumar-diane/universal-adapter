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

import org.zenithblox.Expression;
import org.zenithblox.spi.Metadata;

/**
 * Tokenize XML payloads.
 */
@Metadata(firstVersion = "2.14.0", label = "language,core,xml", title = "XML Tokenize")
public class XMLTokenizerExpression extends NamespaceAwareExpression {

    @Metadata(defaultValue = "i", enums = "i,w,u,t")
    private String mode;
    @Metadata(javaType = "java.lang.Integer")
    private String group;

    public XMLTokenizerExpression() {
    }

    protected XMLTokenizerExpression(XMLTokenizerExpression source) {
        super(source);
        this.mode = source.mode;
        this.group = source.group;
    }

    public XMLTokenizerExpression(String expression) {
        super(expression);
    }

    public XMLTokenizerExpression(Expression expression) {
        setExpressionValue(expression);
    }

    private XMLTokenizerExpression(Builder builder) {
        super(builder);
        this.mode = builder.mode;
        this.group = builder.group;
    }

    @Override
    public XMLTokenizerExpression copyDefinition() {
        return new XMLTokenizerExpression(this);
    }

    @Override
    public String getLanguage() {
        return "xtokenize";
    }

    public String getMode() {
        return mode;
    }

    /**
     * The extraction mode. The available extraction modes are:
     * <ul>
     * <li>i - injecting the contextual namespace bindings into the extracted token (default)</li>
     * <li>w - wrapping the extracted token in its ancestor context</li>
     * <li>u - unwrapping the extracted token to its child content</li>
     * <li>t - extracting the text content of the specified element</li>
     * </ul>
     */
    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getGroup() {
        return group;
    }

    /**
     * To group N parts together
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * {@code Builder} is a specific builder for {@link XMLTokenizerExpression}.
     */
    public static class Builder extends AbstractNamespaceAwareBuilder<Builder, XMLTokenizerExpression> {

        private String mode;
        private String group;

        /**
         * The extraction mode. The available extraction modes are:
         * <ul>
         * <li>i - injecting the contextual namespace bindings into the extracted token (default)</li>
         * <li>w - wrapping the extracted token in its ancestor context</li>
         * <li>u - unwrapping the extracted token to its child content</li>
         * <li>t - extracting the text content of the specified element</li>
         * </ul>
         */
        public Builder mode(String mode) {
            this.mode = mode;
            return this;
        }

        /**
         * The extraction mode.
         */
        public Builder mode(Mode mode) {
            this.mode = mode == null ? null : mode.value;
            return this;
        }

        /**
         * To group N parts together
         */
        public Builder group(String group) {
            this.group = group;
            return this;
        }

        /**
         * To group N parts together
         */
        public Builder group(int group) {
            this.group = Integer.toString(group);
            return this;
        }

        @Override
        public XMLTokenizerExpression end() {
            return new XMLTokenizerExpression(this);
        }
    }

    /**
     * {@code Mode} defines the possible extraction modes that can be used.
     */
    public enum Mode {
        INJECTING_CONTEXTUAL_NAMESPACE_BINDINGS("i"),
        WRAPPING_EXTRACTED_TOKEN("w"),
        UNWRAPPING_EXTRACTED_TOKEN("u"),
        EXTRACTING_TEXT_CONTENT("t");

        private final String value;

        Mode(String value) {
            this.value = value;
        }
    }
}
