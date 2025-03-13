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
import org.zenithblox.model.language.JoorExpression;

@Deprecated(since = "4.3.0")
public class JoorExpressionReifier extends TypedExpressionReifier<JoorExpression> {

    public JoorExpressionReifier(ZwangineContext zwangineContext, ExpressionDefinition definition) {
        super(zwangineContext, definition);
    }

    @Override
    protected Object[] createProperties() {
        Object[] properties = new Object[3];
        properties[0] = asResultType();
        properties[1] = parseBoolean(definition.getPreCompile());
        properties[2] = parseBoolean(definition.getSingleQuotes());
        return properties;
    }

    @Override
    public boolean isResolveOptionalExternalScriptEnabled() {
        // we handle this in zwangine-joor
        return false;
    }

}
