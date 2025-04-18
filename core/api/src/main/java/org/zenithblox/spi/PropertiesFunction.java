/*
 * Licensed to the  Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the  License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.zwangine.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zenithblox.spi;

/**
 * A function that is applied instead of looking up a property placeholder.
 */
public interface PropertiesFunction {

    /**
     * Name of the function which is used as <tt>name:</tt> to let the properties component know it is a function.
     */
    String getName();

    /**
     * Applies the function.
     *
     * @param  remainder the remainder value
     * @return           a value as the result of the function
     * @see              #lookupFirst(String)
     */
    String apply(String remainder);

    /**
     * Whether the value should be looked up as a regular properties first, before applying this function.
     *
     * @param  remainder the remainder value
     * @return           true to resolve the remainder value as a property value, and then afterwards apply this
     *                   function, false to apply this function without lookup (default).
     */
    default boolean lookupFirst(String remainder) {
        return false;
    }

    /**
     * If the property value cannot be found should the property be regarded as optional and ignore missing value.
     *
     * @param  remainder the remainder value
     * @return           true to make this property as optional
     */
    default boolean optional(String remainder) {
        return false;
    }

}
