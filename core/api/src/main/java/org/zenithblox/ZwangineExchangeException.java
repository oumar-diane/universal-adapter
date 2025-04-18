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
 * An exception caused by a specific message {@link Exchange}
 */
public class ZwangineExchangeException extends ZwangineException {
    private static final @Serial long serialVersionUID = -8721487431101572630L;
    // exchange is not guaranteed to be serializable so we set it as transient
    private final transient Exchange exchange;

    public ZwangineExchangeException(String message, Exchange exchange) {
        super(ZwangineExchangeException.createExceptionMessage(message, exchange, null));
        this.exchange = exchange;
    }

    public ZwangineExchangeException(String message, Exchange exchange, Throwable cause) {
        super(ZwangineExchangeException.createExceptionMessage(message, exchange, cause), cause);
        this.exchange = exchange;
    }

    /**
     * Returns the exchange which caused the exception
     */
    public Exchange getExchange() {
        return exchange;
    }

    /**
     * Creates an exception message with the provided details.
     * <p/>
     * All fields is optional so you can pass in only an exception, or just a message etc. or any combination.
     *
     * @param  message  the message
     * @param  exchange the exchange
     * @param  cause    the caused exception
     * @return          an error message (without stacktrace from exception)
     */
    public static String createExceptionMessage(String message, Exchange exchange, Throwable cause) {
        StringBuilder sb = new StringBuilder(1024);
        if (message != null) {
            sb.append(message);
        }
        if (exchange != null) {
            if (!sb.isEmpty()) {
                sb.append(". ");
            }
            sb.append(exchange);
        }
        if (cause != null) {
            if (!sb.isEmpty()) {
                sb.append(". ");
            }
            sb.append("Caused by: [").append(cause.getClass().getName()).append(" - ")
                    .append(cause.getMessage()).append(']');
        }
        return sb.toString().trim();
    }

}
