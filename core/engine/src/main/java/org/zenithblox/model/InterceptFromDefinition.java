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
package org.zenithblox.model;

import org.zenithblox.spi.Metadata;

/**
 * Intercepts incoming messages
 */
@Metadata(label = "configuration")
public class InterceptFromDefinition extends InterceptDefinition {

    protected String uri;

    public InterceptFromDefinition() {
    }

    public InterceptFromDefinition(InterceptFromDefinition source) {
        super(source);
        this.uri = source.uri;
    }

    public InterceptFromDefinition(String uri) {
        this.uri = uri;
    }

    @Override
    public InterceptFromDefinition copyDefinition() {
        return new InterceptFromDefinition(this);
    }

    @Override
    public String toString() {
        return "InterceptFrom[" + getOutputs() + "]";
    }

    @Override
    public String getShortName() {
        return "interceptFrom";
    }

    @Override
    public String getLabel() {
        return "interceptFrom";
    }

    @Override
    public boolean isAbstract() {
        return true;
    }

    @Override
    public boolean isTopLevelOnly() {
        return true;
    }

    public String getUri() {
        return uri;
    }

    /**
     * Intercept incoming messages from the uri or uri pattern. If this option is not configured, then all incoming
     * messages is intercepted.
     */
    public void setUri(String uri) {
        this.uri = uri;
    }
}
