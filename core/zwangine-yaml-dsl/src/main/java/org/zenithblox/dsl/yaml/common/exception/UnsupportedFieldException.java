/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
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
package org.zenithblox.dsl.yaml.common.exception;

import org.snakeyaml.engine.v2.exceptions.MarkedYamlEngineException;
import org.snakeyaml.engine.v2.nodes.Node;

import java.util.Optional;

public class UnsupportedFieldException extends MarkedYamlEngineException {

    public UnsupportedFieldException(Node node, String field) {
        super(null, Optional.empty(), "Unsupported field: " + field, node.getStartMark());
    }

}
