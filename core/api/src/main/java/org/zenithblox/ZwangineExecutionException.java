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
 * Exception occurred during execution/processing of an {@link Exchange}.
 * <p/>
 * Is usually thrown to the caller when using the {@link org.zenithblox.ProducerTemplate} to send messages to Zwangine.
 */
public class ZwangineExecutionException extends RuntimeExchangeException {

    public ZwangineExecutionException(String message, Exchange exchange) {
        super(message, exchange);
    }

    public ZwangineExecutionException(String message, Exchange exchange, Throwable cause) {
        super(message, exchange, cause);
    }

    /**
     * Wraps the caused exception in a {@link ZwangineExecutionException} if its not already such an exception.
     *
     * @param  e the caused exception
     * @return   the wrapper exception
     */
    public static ZwangineExecutionException wrapZwangineExecutionException(Exchange exchange, Throwable e) {
        if (e instanceof ZwangineExecutionException ce) {
            // don't double wrap
            return ce;
        } else {
            return new ZwangineExecutionException("Exception occurred during execution", exchange, e);
        }
    }
}
