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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Models a series of string key/value pairs for configuring some global options on a Zwangine context such as max debug
 * log length.
 */
@Metadata(label = "configuration")
public class GlobalOptionsDefinition {

    private List<GlobalOptionDefinition> globalOptions;

    public GlobalOptionsDefinition() {
    }

    /**
     * A series of global options as key value pairs
     */
    public void setGlobalOptions(List<GlobalOptionDefinition> globalOptions) {
        this.globalOptions = globalOptions;
    }

    public List<GlobalOptionDefinition> getGlobalOptions() {
        return globalOptions;
    }

    public Map<String, String> asMap() {
        return getGlobalOptions().stream().collect(Collectors.toMap(o -> o.getKey(), o -> o.getValue(), (o1, o2) -> o2));
    }

}
