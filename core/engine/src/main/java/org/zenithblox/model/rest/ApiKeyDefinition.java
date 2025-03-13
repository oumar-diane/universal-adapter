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
 * Rest security basic auth definition
 */
@Metadata(label = "rest,security,configuration")
public class ApiKeyDefinition extends RestSecurityDefinition {

    @Metadata(required = true)
    private String name;
    @Metadata(javaType = "java.lang.Boolean")
    private String inHeader;
    @Metadata(javaType = "java.lang.Boolean")
    private String inQuery;
    @Metadata(javaType = "java.lang.Boolean")
    private String inCookie;

    public ApiKeyDefinition() {
    }

    public ApiKeyDefinition(RestDefinition rest) {
        super(rest);
    }

    public String getName() {
        return name;
    }

    /**
     * The name of the header or query parameter to be used.
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getInHeader() {
        return inHeader;
    }

    /**
     * To use header as the location of the API key.
     */
    public void setInHeader(String inHeader) {
        this.inHeader = inHeader;
    }

    public String getInQuery() {
        return inQuery;
    }

    /**
     * To use query parameter as the location of the API key.
     */
    public void setInQuery(String inQuery) {
        this.inQuery = inQuery;
    }

    public String getInCookie() {
        return inCookie;
    }

    /**
     * To use a cookie as the location of the API key.
     */
    public void setInCookie(String inCookie) {
        this.inCookie = inCookie;
    }

    public ApiKeyDefinition withHeader(String name) {
        setName(name);
        setInHeader(Boolean.toString(true));
        setInQuery(Boolean.toString(false));
        setInCookie(Boolean.toString(false));
        return this;
    }

    public ApiKeyDefinition withQuery(String name) {
        setName(name);
        setInQuery(Boolean.toString(true));
        setInHeader(Boolean.toString(false));
        setInCookie(Boolean.toString(false));
        return this;
    }

    public ApiKeyDefinition withCookie(String name) {
        setName(name);
        setInCookie(Boolean.toString(true));
        setInHeader(Boolean.toString(false));
        setInQuery(Boolean.toString(false));
        return this;
    }

    public RestSecuritiesDefinition end() {
        return rest.getSecurityDefinitions();
    }
}
