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
package org.zenithblox.model.dataformat;

import org.zenithblox.model.CopyableDefinition;
import org.zenithblox.spi.Metadata;

/**
 * To configure headers for UniVocity data formats.
 */
@Metadata(label = "dataformat,transformation,csv", title = "uniVocity Header")
public class UniVocityHeader implements CopyableDefinition<UniVocityHeader> {

    private String name;
    private String length;

    public UniVocityHeader() {
    }

    protected UniVocityHeader(UniVocityHeader source) {
        this.name = source.name;
        this.length = source.length;
    }

    @Override
    public UniVocityHeader copyDefinition() {
        return new UniVocityHeader(this);
    }

    public String getName() {
        return name;
    }

    /**
     * Header name
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getLength() {
        return length;
    }

    /**
     * Header length
     */
    public void setLength(String length) {
        this.length = length;
    }
}
