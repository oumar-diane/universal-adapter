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
package org.zenithblox.language.csimple;

import org.zenithblox.ZwangineContext;

/**
 * SPI plugin for custom compilers to use at runtime.
 */
public interface CSimpleCompiler {

    /**
     * Service factory key.
     */
    String FACTORY = "csimple-compiler";

    /**
     * Adds an import line
     *
     * @param imports import such as com.foo.MyClass
     */
    void addImport(String imports);

    /**
     * Adds an alias
     *
     * @param key   the key
     * @param value the value
     */
    void addAliases(String key, String value);

    /**
     * Create an expression from the given csimple script
     *
     * @param  zwangineContext the zwangine context
     * @param  script       the csimple script
     * @return              the compiled expression
     */
    CSimpleExpression compileExpression(ZwangineContext zwangineContext, String script);

    /**
     * Create an expression from the given csimple script
     *
     * @param  zwangineContext the zwangine context
     * @param  script       the csimple script
     * @return              the compiled preidcate
     */
    CSimpleExpression compilePredicate(ZwangineContext zwangineContext, String script);

}
