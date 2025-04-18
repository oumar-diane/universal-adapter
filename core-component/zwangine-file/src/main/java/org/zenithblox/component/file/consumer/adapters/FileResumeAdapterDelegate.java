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
import org.zenithblox.component.file.consumer.DirectoryEntriesResumeAdapter;
import org.zenithblox.component.file.consumer.FileOffsetResumeAdapter;
import org.zenithblox.component.file.consumer.FileResumeAdapter;
import org.zenithblox.resume.Cacheable;
import org.zenithblox.resume.Deserializable;
import org.zenithblox.resume.Offset;
import org.zenithblox.resume.OffsetKey;
import org.zenithblox.resume.cache.ResumeCache;
import org.zenithblox.spi.annotations.JdkService;
import org.zenithblox.support.resume.OffsetKeys;
import org.zenithblox.support.resume.Offsets;

import java.io.File;
import java.nio.ByteBuffer;

@JdkService("file-adapter-factory")
public class FileResumeAdapterDelegate
        implements FileResumeAdapter, Cacheable, Deserializable, FileOffsetResumeAdapter, DirectoryEntriesResumeAdapter {
    private final DefaultDirectoryEntriesResumeAdapter directoryEntriesResumeAdapter
            = new DefaultDirectoryEntriesResumeAdapter();
    private final DefaultFileOffsetResumeAdapter fileOffsetResumeAdapter = new DefaultFileOffsetResumeAdapter();

    @Override
    public void setResumePayload(GenericFile<File> genericFile) {
        fileOffsetResumeAdapter.setResumePayload(genericFile);
    }

    @Override
    public boolean add(OffsetKey<?> key, Offset<?> offset) {
        Object offsetObj = offset.getValue();

        if (offsetObj instanceof Long) {
            return fileOffsetResumeAdapter.add(key, offset);
        } else {
            return directoryEntriesResumeAdapter.add(key, offset);
        }
    }

    @Override
    public void setCache(ResumeCache<?> cache) {
        fileOffsetResumeAdapter.setCache(cache);
        directoryEntriesResumeAdapter.setCache(cache);
    }

    @Override
    public ResumeCache<?> getCache() {
        return fileOffsetResumeAdapter.getCache();
    }

    @Override
    public boolean deserialize(ByteBuffer keyBuffer, ByteBuffer valueBuffer) {
        Object keyObj = deserializeKey(keyBuffer);
        Object valueObj = deserializeValue(valueBuffer);

        if (valueObj instanceof File file) {
            directoryEntriesResumeAdapter.deserializeFileEntry((File) keyObj, file);
        }

        if (valueObj instanceof Long aLong) {
            fileOffsetResumeAdapter.deserializeFileOffset((File) keyObj, aLong);
        }

        return add(OffsetKeys.of(keyObj), Offsets.of(valueObj));
    }

    @Override
    public void resume() {
        fileOffsetResumeAdapter.resume();
        directoryEntriesResumeAdapter.resume();
    }

    @Override
    public boolean resume(File file) {
        return directoryEntriesResumeAdapter.resume(file);
    }
}
