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

package org.zenithblox.processor;

import org.zenithblox.*;
import org.zenithblox.spi.NormalizedEndpointUri;

final class ProcessorHelper {

    private ProcessorHelper() {
    }

    static Object prepareRecipient(Exchange exchange, Object recipient) throws NoTypeConversionAvailableException {
        if (recipient instanceof Endpoint || recipient instanceof NormalizedEndpointUri) {
            return recipient;
        } else if (recipient instanceof String string) {
            // trim strings as end users might have added spaces between separators
            recipient = string.trim();
        }
        if (recipient != null) {
            ZwangineContext ecc = exchange.getContext();
            String uri;
            if (recipient instanceof String string) {
                uri = string;
            } else {
                // convert to a string type we can work with
                uri = ecc.getTypeConverter().mandatoryConvertTo(String.class, exchange, recipient);
            }
            // optimize and normalize endpoint
            return ecc.getZwangineContextExtension().normalizeUri(uri);
        }
        return null;
    }

    static Endpoint getExistingEndpoint(Exchange exchange, Object recipient) {
        return getExistingEndpoint(exchange.getContext(), recipient);
    }

    static Endpoint getExistingEndpoint(ZwangineContext context, Object recipient) {
        if (recipient instanceof Endpoint endpoint) {
            return endpoint;
        }
        if (recipient != null) {
            if (recipient instanceof NormalizedEndpointUri nu) {
                ExtendedZwangineContext ecc = context.getZwangineContextExtension();
                return ecc.hasEndpoint(nu);
            } else {
                String uri = recipient.toString();
                return context.hasEndpoint(uri);
            }
        }
        return null;
    }
}
