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

package org.zenithblox.resume;

import org.zenithblox.util.ObjectHelper;

import java.io.File;
import java.nio.ByteBuffer;

/**
 * An interface that represents resumable objects that can be serialized to a medium
 */
@FunctionalInterface
public interface Serializable {
    int TYPE_INTEGER = 0;
    int TYPE_LONG = 1;
    int TYPE_STRING = 2;
    int TYPE_FILE = 3;

    int BYTES = 1024;

    /**
     * Serializes this offset into a buffer of bytes
     *
     * @param  obj the object to serialize
     * @return     a ByteBuffer instance with the serialized contents of this object
     */
    default ByteBuffer serialize(Object obj) {
        ObjectHelper.notNull(obj, "Cannot perform serialization on a null object");

        if (obj instanceof Long value) {
            ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + Long.BYTES);
            buffer.putInt(TYPE_LONG);

            long data = value.longValue();
            buffer.putLong(data);
            return buffer;
        }
        if (obj instanceof String str) {
            byte[] data = str.getBytes();

            ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + data.length);
            buffer.putInt(TYPE_STRING);
            buffer.put(data);

            return buffer;
        }
        if (obj instanceof File file) {

            byte[] data = file.getPath().getBytes();
            ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + data.length);

            buffer.putInt(TYPE_FILE);
            buffer.put(data);

            return buffer;
        }

        return null;
    }

    /**
     * Serializes this offset into a buffer of bytes
     *
     * @return a ByteBuffer instance with the serialized contents of this object
     */
    ByteBuffer serialize();
}
