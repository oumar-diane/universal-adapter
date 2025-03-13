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
package org.zenithblox.model.transformer;

import org.zenithblox.model.DataFormatDefinition;
import org.zenithblox.spi.Metadata;

/**
 * Represents a {@link org.zenithblox.processor.transformer.DataFormatTransformer} which leverages
 * {@link org.zenithblox.spi.DataFormat} to perform transformation. One of the DataFormat 'ref' or DataFormat 'type'
 * needs to be specified.
 */
@Metadata(label = "dataformat,transformation")
public class DataFormatTransformerDefinition extends TransformerDefinition {

    private DataFormatDefinition dataFormatType;

    public DataFormatTransformerDefinition() {
    }

    protected DataFormatTransformerDefinition(DataFormatTransformerDefinition source) {
        super(source);
        this.dataFormatType = source.dataFormatType != null ? source.dataFormatType.copyDefinition() : null;
    }

    @Override
    public DataFormatTransformerDefinition copyDefinition() {
        return new DataFormatTransformerDefinition(this);
    }

    public DataFormatDefinition getDataFormatType() {
        return dataFormatType;
    }

    /**
     * The data format to be used
     */
    public void setDataFormatType(DataFormatDefinition dataFormatType) {
        this.dataFormatType = dataFormatType;
    }

}
