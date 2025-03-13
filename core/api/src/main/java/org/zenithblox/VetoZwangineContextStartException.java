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
 * An exception to veto starting {@link ZwangineContext}.
 * <p/>
 * The option rethrowException can be used to control whether to rethrow this exception when starting ZwangineContext or
 * not.
 *
 * @see org.zenithblox.spi.LifecycleStrategy
 */
public class VetoZwangineContextStartException extends Exception {

    private final ZwangineContext context;
    private final boolean rethrowException;

    public VetoZwangineContextStartException(String message, ZwangineContext context) {
        this(message, context, true);
    }

    public VetoZwangineContextStartException(String message, ZwangineContext context, boolean rethrowException) {
        super(message);
        this.context = context;
        this.rethrowException = rethrowException;
    }

    public VetoZwangineContextStartException(String message, Throwable cause, ZwangineContext context) {
        this(message, cause, context, true);
    }

    public VetoZwangineContextStartException(String message, Throwable cause, ZwangineContext context, boolean rethrowException) {
        super(message, cause);
        this.context = context;
        this.rethrowException = rethrowException;
    }

    public ZwangineContext getContext() {
        return context;
    }

    /**
     * Whether to rethrow this exception when starting ZwangineContext, to cause an exception to be thrown from the start
     * method.
     * <p/>
     * This option is default <tt>true</tt>.
     */
    public boolean isRethrowException() {
        return rethrowException;
    }

}
