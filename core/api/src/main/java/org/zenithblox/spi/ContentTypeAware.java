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
 * Represents an object that can have an associated content type. Such as a file or http resource.
 */
public interface ContentTypeAware {
    /**
     * The content type. Usually a value that conforms to the media type specification outlined in RFC 6838.
     *
     * @return The content type string. Can be {@code null} if the content type has not been set or is not known.
     */
    String getContentType();

    /**
     * Sets the content type.
     *
     * @param contentType The content type string
     */
    void setContentType(String contentType);
}
