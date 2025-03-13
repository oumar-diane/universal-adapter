/*
 * Licensed to the  Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the  License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.zwangine.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zenithblox.spi;

import org.zenithblox.StaticService;

/**
 * Strategy for dumping workflows during startup dump all loaded workflows (incl rests and workflow templates).
 */
public interface DumpWorkflowsStrategy extends StaticService {

    /**
     * Service factory key.
     */
    String FACTORY = "dump-workflows";

    /**
     * Dump workflows
     *
     * @param format xml or yaml
     */
    void dumpWorkflows(String format);

    String getInclude();

    /**
     * Controls what to include in output.
     *
     * Possible values: all, workflows, rests, workflowConfigurations, workflowTemplates, beans, dataFormats. Multiple values can
     * be separated by comma. Default is workflows.
     */
    void setInclude(String include);

    boolean isResolvePlaceholders();

    /**
     * Whether to resolve property placeholders in the dumped output. Default is true.
     */
    void setResolvePlaceholders(boolean resolvePlaceholders);

    boolean isUriAsParameters();

    /**
     * When dumping to YAML format, then this option controls whether endpoint URIs should be expanded into a key/value
     * parameters.
     */
    void setUriAsParameters(boolean uriAsParameters);

    boolean isGeneratedIds();

    /**
     * Whether to include auto generated IDs in the dumped output. Default is false.
     */
    void setGeneratedIds(boolean generatedIds);

    boolean isLog();

    /**
     * Whether to log workflow dumps to Logger
     */
    void setLog(boolean log);

    String getOutput();

    /**
     * Whether to save workflow dumps to an output file.
     *
     * If the output is a filename, then all content is saved to this file. If the output is a directory name, then one
     * or more files are saved to the directory, where the names are based on the original source file names, or auto
     * generated names.
     */
    void setOutput(String output);

}
