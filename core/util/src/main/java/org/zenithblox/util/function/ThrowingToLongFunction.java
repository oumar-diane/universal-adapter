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
package org.zenithblox.util.function;

/**
 * Represents a function that produces a long-valued result and may thrown an exception.
 *
 * @param <I> the type of the input of the function
 * @param <T> the type of the exception the accept method may throw
 *
 * @see       java.util.function.ToLongFunction
 */
@FunctionalInterface
public interface ThrowingToLongFunction<I, T extends Throwable> {
    /**
     * Applies this function to the given argument, potentially throwing an exception.
     *
     * @param  in the function argument
     * @return    the function result
     * @throws T  the exception that may be thrown
     */
    long apply(I in) throws T;
}
