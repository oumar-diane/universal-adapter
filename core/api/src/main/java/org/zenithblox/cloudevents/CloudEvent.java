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
package org.zenithblox.cloudevents;

import org.zenithblox.Exchange;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public interface CloudEvent {

    String CAMEL_CLOUD_EVENT_ID = "ZwangineCloudEventID";
    String CAMEL_CLOUD_EVENT_SOURCE = "ZwangineCloudEventSource";
    String CAMEL_CLOUD_EVENT_VERSION = "ZwangineCloudEventVersion";
    String CAMEL_CLOUD_EVENT_TYPE = "ZwangineCloudEventType";
    String CAMEL_CLOUD_EVENT_TYPE_VERSION = "ZwangineCloudEventTypeVersion";
    String CAMEL_CLOUD_EVENT_DATA_CONTENT_TYPE = "ZwangineCloudEventDataContentType";
    String CAMEL_CLOUD_EVENT_DATA_CONTENT_ENCODING = "ZwangineCloudEventDataContentEncoding";
    String CAMEL_CLOUD_EVENT_SCHEMA_URL = "ZwangineCloudEventSchemaURL";
    String CAMEL_CLOUD_EVENT_SUBJECT = "ZwangineCloudEventSubject";
    String CAMEL_CLOUD_EVENT_TIME = "ZwangineCloudEventTime";
    String CAMEL_CLOUD_EVENT_EXTENSIONS = "ZwangineCloudEventExtensions";
    String CAMEL_CLOUD_EVENT_CONTENT_TYPE = Exchange.CONTENT_TYPE;

    String DEFAULT_CAMEL_CLOUD_EVENT_TYPE = "org.zenithblox.event";
    String DEFAULT_CAMEL_CLOUD_EVENT_SOURCE = "org.zenithblox";

    // MIME type
    String APPLICATION_OCTET_STREAM_MIME_TYPE = "application/octet-stream";
    String TEXT_PLAIN_MIME_TYPE = "text/plain";

    /**
     * The CloudEvent spec version.
     */
    String version();

    /**
     * List of supported attributes.
     */
    Collection<Attribute> attributes();

    /**
     * Find attribute by id.
     */
    default Optional<Attribute> attribute(String id) {
        return attributes().stream()
                .filter(a -> Objects.equals(id, a.id()))
                .findFirst();
    }

    /**
     * Mandatory find attribute by id.
     */
    default Attribute mandatoryAttribute(String id) {
        return attributes().stream()
                .filter(a -> Objects.equals(id, a.id()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unable to find attribute with id: " + id));
    }

    /**
     * Construct event time from given Zwangine exchange.
     */
    default String getEventTime(Exchange exchange) {
        final ZonedDateTime created = exchange.getClock().asZonedCreationDateTime();
        return DateTimeFormatter.ISO_INSTANT.format(created);
    }

    /**
     * Mandatory find http attribute by id.
     */
    default String httpAttribute(String id) {
        return mandatoryAttribute(id).http();
    }

    /**
     * Mandatory find json attribute by id.
     */
    default String jsonAttribute(String id) {
        return mandatoryAttribute(id).json();
    }

    interface Attribute {
        /**
         * The ID of the attributes, can be used to look it up.
         */
        String id();

        /**
         * The name of the http header.
         */
        String http();

        /**
         * The name of the json field.
         */
        String json();

        static Attribute simple(String id, String http, String json) {
            return new Attribute() {
                @Override
                public String id() {
                    return id;
                }

                @Override
                public String http() {
                    return http;
                }

                @Override
                public String json() {
                    return json;
                }
            };
        }
    }
}
