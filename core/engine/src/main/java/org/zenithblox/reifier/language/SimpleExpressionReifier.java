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
package org.zenithblox.reifier.language;

import org.zenithblox.ZwangineContext;
import org.zenithblox.model.language.ExpressionDefinition;
import org.zenithblox.model.language.SimpleExpression;

public class SimpleExpressionReifier extends TypedExpressionReifier<SimpleExpression> {

    public SimpleExpressionReifier(ZwangineContext zwangineContext, ExpressionDefinition definition) {
        super(zwangineContext, definition);
    }

    @Override
    public boolean isResolveOptionalExternalScriptEnabled() {
        // simple language will handle to resolve external scripts as they can be dynamic using simple language itself
        return false;
    }

    @Override
    protected Object[] createProperties() {
        Object[] properties = new Object[2];
        properties[0] = asResultType();
        properties[1] = parseBoolean(definition.getTrim());
        return properties;
    }

}
