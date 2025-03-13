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
import org.zenithblox.model.DataFormatDefinition;
import org.zenithblox.model.ProcessorDefinitionHelper;
import org.zenithblox.spi.Metadata;
import org.zenithblox.spi.Resource;
import org.zenithblox.spi.ResourceAware;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configure data formats.
 */
@Metadata(label = "dataformat,transformation", title = "Data formats")
public class DataFormatsDefinition implements CopyableDefinition<DataFormatsDefinition>, ResourceAware {

    private List<DataFormatDefinition> dataFormats;
    private Resource resource;

    public DataFormatsDefinition() {
    }

    protected DataFormatsDefinition(DataFormatsDefinition source) {
        this.dataFormats = ProcessorDefinitionHelper.deepCopyDefinitions(source.dataFormats);
    }

    @Override
    public DataFormatsDefinition copyDefinition() {
        return new DataFormatsDefinition(this);
    }

    /**
     * A list holding the configured data formats
     */
    public void setDataFormats(List<DataFormatDefinition> dataFormats) {
        this.dataFormats = dataFormats;
    }

    public List<DataFormatDefinition> getDataFormats() {
        return dataFormats;
    }

    /***
     * @return A Map of the contained DataFormatType's indexed by id.
     */
    public Map<String, DataFormatDefinition> asMap() {
        Map<String, DataFormatDefinition> dataFormatsAsMap = new HashMap<>();
        for (DataFormatDefinition dataFormatType : getDataFormats()) {
            dataFormatsAsMap.put(dataFormatType.getId(), dataFormatType);
        }
        return dataFormatsAsMap;
    }

    @Override
    public Resource getResource() {
        return resource;
    }

    @Override
    public void setResource(Resource resource) {
        this.resource = resource;
    }
}
