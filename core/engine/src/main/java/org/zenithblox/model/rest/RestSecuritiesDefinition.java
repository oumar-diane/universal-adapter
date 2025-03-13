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

import java.util.ArrayList;
import java.util.List;

/**
 * To configure rest security definitions.
 */
@Metadata(label = "rest,security,configuration", title = "Rest Security Definitions")
public class RestSecuritiesDefinition {

    private RestDefinition rest;

    private List<RestSecurityDefinition> securityDefinitions = new ArrayList<>();

    public RestSecuritiesDefinition() {
    }

    public RestSecuritiesDefinition(RestDefinition rest) {
        this.rest = rest;
    }

    public List<RestSecurityDefinition> getSecurityDefinitions() {
        return securityDefinitions;
    }

    /**
     * Security definitions
     */
    public void setSecurityDefinitions(List<RestSecurityDefinition> securityDefinitions) {
        this.securityDefinitions = securityDefinitions;
    }

    public ApiKeyDefinition apiKey(String key) {
        return apiKey(key, null);
    }

    public ApiKeyDefinition apiKey(String key, String description) {
        ApiKeyDefinition auth = new ApiKeyDefinition(rest);
        auth.setKey(key);
        auth.setDescription(description);
        securityDefinitions.add(auth);
        return auth;
    }

    public RestSecuritiesDefinition basicAuth(String key) {
        return basicAuth(key, null);
    }

    public RestSecuritiesDefinition basicAuth(String key, String description) {
        BasicAuthDefinition auth = new BasicAuthDefinition(rest);
        securityDefinitions.add(auth);
        auth.setKey(key);
        auth.setDescription(description);
        return this;
    }

    public RestSecuritiesDefinition bearerToken(String key, String bearerFormat) {
        return bearerToken(key, null, bearerFormat);
    }

    public RestSecuritiesDefinition bearerToken(String key, String description, String bearerFormat) {
        BearerTokenDefinition auth = new BearerTokenDefinition(rest);
        securityDefinitions.add(auth);
        auth.setKey(key);
        auth.setDescription(description);
        auth.setFormat(bearerFormat);
        return this;
    }

    public RestSecuritiesDefinition mutualTLS(String key) {
        return mutualTLS(key, null);
    }

    public RestSecuritiesDefinition mutualTLS(String key, String description) {
        MutualTLSDefinition auth = new MutualTLSDefinition(rest);
        securityDefinitions.add(auth);
        auth.setKey(key);
        auth.setDescription(description);
        return this;
    }

    public RestSecuritiesDefinition openIdConnect(String key, String url) {
        return openIdConnect(key, null, url);
    }

    public RestSecuritiesDefinition openIdConnect(String key, String description, String url) {
        OpenIdConnectDefinition auth = new OpenIdConnectDefinition(rest);
        securityDefinitions.add(auth);
        auth.setKey(key);
        auth.setDescription(description);
        auth.setUrl(url);
        return this;
    }

    public OAuth2Definition oauth2(String key) {
        return oauth2(key, null);
    }

    public OAuth2Definition oauth2(String key, String description) {
        OAuth2Definition auth = new OAuth2Definition(rest);
        auth.setKey(key);
        auth.setDescription(description);
        securityDefinitions.add(auth);
        return auth;
    }

    public RestDefinition end() {
        return rest;
    }

}
