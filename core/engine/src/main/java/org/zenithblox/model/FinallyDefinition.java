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

/**
 * Path traversed when a try, catch, finally block exits
 */
@Metadata(label = "error")
public class FinallyDefinition extends OutputDefinition<FinallyDefinition> {

    public FinallyDefinition() {
    }

    protected FinallyDefinition(FinallyDefinition source) {
        super(source);
    }

    @Override
    public FinallyDefinition copyDefinition() {
        return new FinallyDefinition(this);
    }

    @Override
    public String toString() {
        return "DoFinally[" + getOutputs() + "]";
    }

    @Override
    public String getShortName() {
        return "doFinally";
    }

    @Override
    public String getLabel() {
        return "doFinally";
    }

    @Override
    public List<ProcessorDefinition<?>> getOutputs() {
        return outputs;
    }

    @Override
    public void setOutputs(List<ProcessorDefinition<?>> outputs) {
        super.setOutputs(outputs);
    }

}
