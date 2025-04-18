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
package org.zenithblox.impl.engine;

import org.zenithblox.*;
import org.zenithblox.spi.StreamCachingStrategy;
import org.zenithblox.util.ObjectHelper;

/**
 * Helper for {@link org.zenithblox.StreamCache} in Zwangine workflow engine.
 */
final class StreamCachingHelper {

    private StreamCachingHelper() {
    }

    public static StreamCache convertToStreamCache(StreamCachingStrategy strategy, Exchange exchange, Message message) {
        // check if body is already cached
        try {
            Object body = message.getBody();
            if (body == null) {
                return null;
            } else if (body instanceof StreamCache sc) {
                // reset so the cache is ready to be used before processing
                sc.reset();
                return sc;
            }
        } catch (Exception e) {
            handleException(exchange, null, e);
        }
        // check if we somewhere failed due to a stream caching exception
        Throwable cause = exchange.getException();
        if (cause == null) {
            cause = exchange.getProperty(ExchangePropertyKey.EXCEPTION_CAUGHT, Throwable.class);
        }
        return tryStreamCache(strategy, exchange, message, cause);
    }

    private static StreamCache tryStreamCache(
            StreamCachingStrategy strategy, Exchange exchange, Message inMessage, Throwable cause) {
        final boolean failed = cause != null && ObjectHelper.getException(StreamCacheException.class, cause) != null;
        if (!failed) {
            boolean disabled = exchange.getExchangeExtension().isStreamCacheDisabled();
            if (disabled) {
                return null;
            }
            try {
                // cache the body and if we could do that replace it as the new body
                StreamCache sc = strategy.cache(exchange);
                if (sc != null) {
                    inMessage.setBody(sc);
                }
                return sc;
            } catch (Exception e) {
                handleException(exchange, e);
            }
        }
        return null;
    }

    private static void handleException(Exchange exchange, Exception e) {
        handleException(exchange, exchange.getMessage().getBody(), e);
    }

    private static void handleException(Exchange exchange, Object value, Exception e) {
        // lets allow Zwangines error handler to deal with stream cache failures
        StreamCacheException tce = new StreamCacheException(value, e);
        exchange.setException(tce);
        // because this is stream caching error then we cannot use redelivery as the message body is corrupt
        // so mark as redelivery exhausted
        exchange.getExchangeExtension().setRedeliveryExhausted(true);
    }

}
