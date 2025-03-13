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

package org.zenithblox.reifier.tokenizer;

import org.zenithblox.Processor;
import org.zenithblox.Workflow;
import org.zenithblox.RuntimeZwangineException;
import org.zenithblox.model.TokenizerDefinition;
import org.zenithblox.model.TokenizerImplementationDefinition;
import org.zenithblox.model.tokenizer.LangChain4jTokenizerDefinition;
import org.zenithblox.processor.TokenizerProcessor;
import org.zenithblox.reifier.ProcessorReifier;
import org.zenithblox.spi.FactoryFinder;
import org.zenithblox.spi.Tokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class TokenizerReifier<T extends TokenizerDefinition> extends ProcessorReifier<T> {
    private static final Logger LOG = LoggerFactory.getLogger(TokenizerReifier.class);
    private static final String TOKENIZER_PATH = FactoryFinder.DEFAULT_PATH + "tokenizer/";

    public TokenizerReifier(Workflow workflow, T definition) {
        super(workflow, definition);
    }

    public Processor createProcessor() throws Exception {
        Processor childProcessor = createChildProcessor(false);

        final FactoryFinder factoryFinder
                = zwangineContext.getZwangineContextExtension().getFactoryFinder(TOKENIZER_PATH);

        final Optional<Tokenizer> tokenize = factoryFinder.newInstance(
                definition.tokenizerName(), Tokenizer.class);

        if (tokenize.isEmpty()) {
            throw new RuntimeZwangineException(
                    "Cannot find a tokenizer named: " + definition.tokenizerName() + " in the classpath");
        }

        final Tokenizer tokenizer = tokenize.get();
        LOG.info("Creating a tokenizer of type {}", tokenizer.getClass().getName());
        configure(tokenizer);

        return new TokenizerProcessor(childProcessor, tokenizer);
    }

    protected void configure(Tokenizer tokenizer) {
        final TokenizerImplementationDefinition tokenizerImplementation = definition.getTokenizerImplementation();
        Tokenizer.Configuration configuration = tokenizerImplementation.configuration();
        if (configuration == null) {
            configuration = tokenizer.newConfiguration();

            if (tokenizerImplementation instanceof LangChain4jTokenizerDefinition ltd) {
                configuration.setMaxOverlap(Integer.valueOf(ltd.getMaxOverlap()));
                configuration.setMaxTokens(Integer.valueOf(ltd.getMaxTokens()));
                configuration.setType(ltd.getTokenizerType());
            }
        }

        tokenizer.configure(configuration);
    }
}
