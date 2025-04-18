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
package org.zenithblox.component.file;

import org.zenithblox.Exchange;
import org.zenithblox.component.file.strategy.FileMoveExistingStrategy;
import org.zenithblox.util.FileUtil;
import org.zenithblox.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.zenithblox.component.file.MoveExistingFileStrategyUtils.completePartialRelativePath;

public class GenericFileDefaultMoveExistingFileStrategy implements FileMoveExistingStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(GenericFileDefaultMoveExistingFileStrategy.class);

    /**
     * Moves any existing file due fileExists=Move is in use.
     */
    @Override
    public boolean moveExistingFile(GenericFileEndpoint<?> endpoint, GenericFileOperations<?> operations, String fileName)
            throws GenericFileOperationFailedException {

        // need to evaluate using a dummy and simulate the file first, to have
        // access to all the file attributes
        // create a dummy exchange as Exchange is needed for expression
        // evaluation
        // we support only the following 3 tokens.
        Exchange dummy = endpoint.createExchange();
        String parent = FileUtil.onlyPath(fileName);
        String onlyName = FileUtil.stripPath(fileName);
        dummy.getIn().setHeader(FileConstants.FILE_NAME, fileName);
        dummy.getIn().setHeader(FileConstants.FILE_NAME_ONLY, onlyName);
        dummy.getIn().setHeader(FileConstants.FILE_PARENT, parent);

        String to = endpoint.getMoveExisting().evaluate(dummy, String.class);

        if (ObjectHelper.isEmpty(to)) {
            throw new GenericFileOperationFailedException(
                    "moveExisting evaluated as empty String, cannot move existing file: " + fileName);
        }

        to = completePartialRelativePath(to, onlyName, parent);

        // we must normalize it (to avoid having both \ and / in the name which
        // confuses java.io.File)
        to = FileUtil.normalizePath(to);

        // ensure any paths is created before we rename as the renamed file may
        // be in a different path (which may be non exiting)
        // use java.io.File to compute the file path
        File toFile = new File(to);
        String directory = toFile.getParent();
        boolean absolute = FileUtil.isAbsolute(toFile);
        if (directory != null) {
            if (!operations.buildDirectory(directory, absolute)) {
                LOG.debug("Cannot build directory [{}] (could be because of denied permissions)", directory);
            }
        }

        // deal if there already exists a file
        if (operations.existsFile(to)) {
            if (endpoint.isEagerDeleteTargetFile()) {
                LOG.trace("Deleting existing file: {}", to);
                if (!operations.deleteFile(to)) {
                    throw new GenericFileOperationFailedException("Cannot delete file: " + to);
                }
            } else {
                throw new GenericFileOperationFailedException(
                        "Cannot move existing file from: " + fileName + " to: " + to + " as there already exists a file: "
                                                              + to);
            }
        }

        LOG.trace("Moving existing file: {} to: {}", fileName, to);
        if (!operations.renameFile(fileName, to)) {
            throw new GenericFileOperationFailedException("Cannot rename file from: " + fileName + " to: " + to);
        }

        return true;
    }

}
