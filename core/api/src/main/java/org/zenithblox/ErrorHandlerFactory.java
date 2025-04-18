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
package org.zenithblox;

/**
 * Factory for creating {@link org.zenithblox.spi.ErrorHandler}s.
 */
public interface ErrorHandlerFactory {

    /**
     * Whether this error handler supports transacted exchanges.
     */
    boolean supportTransacted();

    /**
     * Clones this factory so each workflow has its private builder to use, to avoid changes from one workflow to influence
     * the others.
     * <p/>
     * This is needed by the current Zwangine workflow architecture
     *
     * @return a clone of this factory
     */
    ErrorHandlerFactory cloneBuilder();

}
