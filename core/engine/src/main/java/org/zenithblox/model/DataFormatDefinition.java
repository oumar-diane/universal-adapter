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

import org.zenithblox.spi.DataFormat;
import org.zenithblox.spi.Metadata;
import org.zenithblox.spi.Resource;
import org.zenithblox.spi.ResourceAware;

/**
 * Represents a Zwangine data format
 */
@Metadata(label = "dataformat,transformation")
public class DataFormatDefinition extends IdentifiedType implements CopyableDefinition<DataFormatDefinition>, ResourceAware {

    private DataFormat dataFormat;
    private String dataFormatName;
    private Resource resource;

    public DataFormatDefinition() {
    }

    public DataFormatDefinition(DataFormatDefinition source) {
        this.dataFormat = source.dataFormat;
        this.dataFormatName = source.dataFormatName;
    }

    public DataFormatDefinition(DataFormat dataFormat) {
        this.dataFormat = dataFormat;
    }

    public DataFormatDefinition copyDefinition() {
        throw new UnsupportedOperationException("Should be implemented in child classes");
    }

    protected DataFormatDefinition(String dataFormatName) {
        this.dataFormatName = dataFormatName;
    }

    public String getDataFormatName() {
        return dataFormatName;
    }

    public void setDataFormatName(String dataFormatName) {
        this.dataFormatName = dataFormatName;
    }

    public DataFormat getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(DataFormat dataFormat) {
        this.dataFormat = dataFormat;
    }

    @Override
    public Resource getResource() {
        return resource;
    }

    @Override
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public String getShortName() {
        String name = getClass().getSimpleName();
        if (name.endsWith("DataFormat")) {
            name = name.substring(0, name.indexOf("DataFormat"));
        }
        return name;
    }

}
