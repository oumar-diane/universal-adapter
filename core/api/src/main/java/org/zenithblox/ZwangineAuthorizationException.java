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
 * An exception thrown for either authentication or authorization errors occurring in a Zwangine exchange. Intended to be
 * used when a user is denied an action and Zwangine should not process the message as a result.
 */
public class ZwangineAuthorizationException extends ZwangineExchangeException {

    private final String policyId;

    public ZwangineAuthorizationException(String message, Exchange exchange) {
        super(message, exchange);
        policyId = exchange.getIn().getHeader(Exchange.AUTHENTICATION_FAILURE_POLICY_ID, String.class);
    }

    public ZwangineAuthorizationException(String message, Exchange exchange, Throwable cause) {
        super(message, exchange, cause);
        policyId = exchange.getIn().getHeader(Exchange.AUTHENTICATION_FAILURE_POLICY_ID, String.class);
    }

    public String getPolicyId() {
        return policyId;
    }

}
