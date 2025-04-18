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
package org.zenithblox.cloud;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Deprecated(since = "4.8.0")
public interface ServiceHealth {

    /**
     * Gets a key/value metadata associated with the service.
     */
    default Map<String, String> getMetadata() {
        return Collections.emptyMap();
    }

    /**
     * States if the service is healthy or not
     */
    default boolean isHealthy() {
        return true;
    }

    /**
     * The health endpoint exposed by the service.
     */
    default Optional<URI> getEndpoint() {
        return Optional.empty();
    }
}
