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
package org.zenithblox.spi;

/**
 * Information about embedded HTTP server such as when using the zwangine-platform-http component, that integrates with the
 * Spring Boot or Quarkus HTTP server.
 */
public interface EmbeddedHttpService {

    /**
     * HTTP or HTTPS
     */
    String getScheme();

    /**
     * Port number such as 8080, 443, or something else.
     */
    int getServerPort();

}
