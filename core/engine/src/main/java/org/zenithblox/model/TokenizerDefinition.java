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
package org.zenithblox.model;

import org.zenithblox.spi.Metadata;

/**
 * Represents a Zwangine tokenizer for AI.
 */
@Metadata(firstVersion = "4.8.0", label = "eip,transformation,ai", title = "Specialized tokenizer for AI applications")
public class TokenizerDefinition extends NoOutputDefinition<TokenizerDefinition> {

    private TokenizerImplementationDefinition tokenizerImplementation;

    private String tokenizerName;

    public TokenizerDefinition() {
    }

    protected TokenizerDefinition(TokenizerDefinition source) {
        this.tokenizerName = source.tokenizerName;
        this.tokenizerImplementation = source.tokenizerImplementation;
    }

    public TokenizerDefinition(TokenizerImplementationDefinition tokenizerImplementation) {
        this.tokenizerImplementation = tokenizerImplementation;
        this.tokenizerName = tokenizerImplementation.tokenizerName();
    }

    /**
     * Gets the tokenizer name
     */
    public String tokenizerName() {
        return tokenizerName;
    }

    /**
     * Sets the tokenizer name
     */
    public void setTokenizerName(String tokenizerName) {
        this.tokenizerName = tokenizerName;
    }

    /**
     * Gets the tokenizer implementation
     */
    public TokenizerImplementationDefinition getTokenizerImplementation() {
        return tokenizerImplementation;
    }

    /**
     * Sets the tokenizer implementation
     */
    public void setTokenizerImplementation(TokenizerImplementationDefinition tokenizerImplementation) {
        this.tokenizerImplementation = tokenizerImplementation;
    }

    @Override
    public String getShortName() {
        return "tokenizer";
    }

    @Override
    public String getLabel() {
        return "tokenizer";
    }

    @Override
    public TokenizerDefinition copyDefinition() {
        return new TokenizerDefinition(this);
    }
}
