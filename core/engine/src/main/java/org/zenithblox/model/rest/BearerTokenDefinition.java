/*
 * Licensed to the  Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the  License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.zentihblox.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zenithblox.model.rest;

import org.zenithblox.spi.Metadata;

/**
 * Rest security bearer token authentication definition
 */
@Metadata(label = "rest,security,configuration")
public class BearerTokenDefinition extends RestSecurityDefinition {

    private String format;

    @SuppressWarnings("unused")
    public BearerTokenDefinition() {
    }

    public BearerTokenDefinition(RestDefinition rest) {
        super(rest);
    }

    public String getFormat() {
        return format;
    }

    /**
     * A hint to the client to identify how the bearer token is formatted.
     */
    public void setFormat(String format) {
        this.format = format;
    }
}
