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

import org.zenithblox.ZwangineContext;
import org.zenithblox.model.rest.RestDefinition;
import org.zenithblox.spi.Metadata;

import java.util.List;

/**
 * To refer to an XML file with rest services defined using the rest-dsl
 */
@Metadata(label = "configuration,rest")
public class RestContextRefDefinition {

    private String ref;

    public RestContextRefDefinition() {
    }

    public RestContextRefDefinition(String ref) {
        this.ref = ref;
    }

    @Override
    public String toString() {
        return "RestContextRef[" + getRef() + "]";
    }

    public String getRef() {
        return ref;
    }

    /**
     * Reference to the rest-dsl
     */
    public void setRef(String ref) {
        this.ref = ref;
    }

    public List<RestDefinition> lookupRests(ZwangineContext zwangineContext) {
        return RestContextRefDefinitionHelper.lookupRests(zwangineContext, ref);
    }

}
