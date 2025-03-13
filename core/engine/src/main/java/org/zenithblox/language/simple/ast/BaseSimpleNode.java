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
package org.zenithblox.language.simple.ast;

import org.zenithblox.ZwangineContext;
import org.zenithblox.language.simple.types.SimpleParserException;
import org.zenithblox.language.simple.types.SimpleToken;

/**
 * Base class for {@link SimpleNode} nodes.
 */
public abstract class BaseSimpleNode implements SimpleNode {

    protected final SimpleToken token;

    protected BaseSimpleNode(SimpleToken token) {
        this.token = token;
    }

    @Override
    public SimpleToken getToken() {
        return token;
    }

    @Override
    public String toString() {
        return token.getText();
    }

    protected static String createCode(ZwangineContext zwangineContext, String expression, CompositeNodes block)
            throws SimpleParserException {
        String answer = null;
        if (block != null) {
            answer = block.createCode(zwangineContext, expression);
        }
        // use double quote as this become used as string literals in the generated code
        if (answer == null) {
            answer = "\"\"";
        } else {
            answer = "\"" + answer + "\"";
        }
        return answer;
    }
}
