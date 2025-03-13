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
import org.zenithblox.spi.Tokenizer;

@Metadata(firstVersion = "4.8.0", label = "eip,transformation,ai", title = "LangChain4J Tokenizer")
public class TokenizerImplementationDefinition extends IdentifiedType
        implements CopyableDefinition<TokenizerImplementationDefinition> {

    private String tokenizerName;
    private Tokenizer.Configuration configuration;

    public TokenizerImplementationDefinition() {
    }

    protected TokenizerImplementationDefinition(TokenizerImplementationDefinition source) {
        this.tokenizerName = source.tokenizerName;
        this.configuration = source.configuration;
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
     * Gets the tokenizer configuration
     */
    public Tokenizer.Configuration configuration() {
        return configuration;
    }

    /**
     * Sets the tokenizer configuration
     */
    public void setConfiguration(Tokenizer.Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public TokenizerImplementationDefinition copyDefinition() {
        throw new UnsupportedOperationException("Must be implemented in the concrete classes");
    }
}
