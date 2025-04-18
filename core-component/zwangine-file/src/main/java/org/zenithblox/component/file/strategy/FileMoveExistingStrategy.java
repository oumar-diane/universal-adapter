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
package org.zenithblox.component.file.strategy;

import org.zenithblox.component.file.GenericFileEndpoint;
import org.zenithblox.component.file.GenericFileOperationFailedException;
import org.zenithblox.component.file.GenericFileOperations;

/**
 * This is the interface to be implemented when a custom implementation needs to be provided in case of fileExists=Move
 * is in use while moving any existing file in producer endpoints.
 */
public interface FileMoveExistingStrategy {

    /**
     * Moves any existing file due fileExists=Move is in use.
     *
     * @param  endpoint   the given endpoint of the component
     * @param  operations file operations API of the relevant component's API
     * @return            result of the file operation can be returned note that for now, implementation classes for
     *                    file component and ftp components, always returned true. However,if such a need of direct
     *                    usage of File API returning true|false, you can use that return value for implementation's
     *                    return value.
     */
    boolean moveExistingFile(GenericFileEndpoint<?> endpoint, GenericFileOperations<?> operations, String fileName)
            throws GenericFileOperationFailedException;

}
