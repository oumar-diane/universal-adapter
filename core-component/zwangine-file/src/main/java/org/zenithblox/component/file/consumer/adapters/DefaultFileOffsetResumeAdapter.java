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

package org.zenithblox.component.file.consumer.adapters;

import org.zenithblox.component.file.GenericFile;
import org.zenithblox.component.file.consumer.FileOffsetResumeAdapter;
import org.zenithblox.component.file.consumer.FileResumeAdapter;
import org.zenithblox.resume.Offset;
import org.zenithblox.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * An implementation of the {@link FileResumeAdapter} that can be used for resume operations for the file component.
 * This can be used to manage the resume operations for a single file using its offset.
 */
class DefaultFileOffsetResumeAdapter extends AbstractFileResumeAdapter implements FileOffsetResumeAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultFileOffsetResumeAdapter.class);

    private GenericFile<File> genericFile;

    private Offset<?> getLastOffset(File addressable) {
        return cache.get(addressable, Offset.class);
    }

    @Override
    public void setResumePayload(GenericFile<File> genericFile) {
        this.genericFile = ObjectHelper.notNull(genericFile, "genericFile");
    }

    public boolean add(Object key, Object offset) {
        if (offset instanceof Long longOffset) {
            FileOffset fileOffset = (FileOffset) cache.computeIfAbsent((File) key, k -> new FileOffset());

            fileOffset.update(longOffset);
        } else {
            throw new UnsupportedOperationException("This adapter cannot be used for directory entries");
        }

        // For this one it's safe to always continue processing
        return true;
    }

    private void resumeFileOffsets() {
        if (genericFile == null) {
            return;
        }

        final Offset<?> lastOffset = getLastOffset(genericFile.getFile());

        if (lastOffset == null) {
            return;
        }

        Object offsetObj = lastOffset.getValue();
        if (offsetObj == null) {
            return;
        }

        if (offsetObj instanceof Long longOffsetObj) {
            genericFile.updateLastOffsetValue(longOffsetObj);
        } else {
            // This should never happen
            LOG.warn("Cannot perform a resume operation of an object of unhandled type: {}", offsetObj.getClass());
        }
    }

    @Override
    public void resume() {
        resumeFileOffsets();
    }

    public void deserializeFileOffset(File keyObj, Long valueObj) {
        FileOffset longOffset = (FileOffset) cache.computeIfAbsent(keyObj, obj -> new FileOffset());

        longOffset.update(valueObj);
    }
}
