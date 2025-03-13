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

import java.io.Serial;

/**
 * Base class for all Zwangine unchecked exceptions.
 */
public class RuntimeZwangineException extends RuntimeException {
    private static final @Serial long serialVersionUID = 8046489554418284257L;

    public RuntimeZwangineException() {
    }

    public RuntimeZwangineException(String message) {
        super(message);
    }

    public RuntimeZwangineException(String message, Throwable cause) {
        super(message, cause);
    }

    public RuntimeZwangineException(Throwable cause) {
        super(cause);
    }

    /**
     * Wraps the caused exception in a {@link RuntimeZwangineException} if its not already such an exception.
     *
     * @param  e the caused exception
     * @return   the wrapper exception
     */
    public static RuntimeZwangineException wrapRuntimeZwangineException(Throwable e) {
        if (e instanceof RuntimeZwangineException re) {
            // don't double wrap
            return re;
        } else {
            return new RuntimeZwangineException(e);
        }
    }

    /**
     * Wraps the caused exception in a {@link RuntimeZwangineException} if its not already a runtime exception.
     *
     * @param  e the caused exception
     * @return   the wrapper exception
     */
    public static RuntimeException wrapRuntimeException(Throwable e) {
        if (e instanceof RuntimeException re) {
            // don't double wrap
            return re;
        } else {
            return new RuntimeZwangineException(e);
        }
    }
}
