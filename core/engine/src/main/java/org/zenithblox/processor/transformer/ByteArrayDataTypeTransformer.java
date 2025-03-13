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
package org.zenithblox.processor.transformer;

import org.zenithblox.Exchange;
import org.zenithblox.Message;
import org.zenithblox.spi.DataType;
import org.zenithblox.spi.DataTypeTransformer;
import org.zenithblox.spi.Transformer;

/**
 * Generic binary data type uses Zwangine message body converter mechanism to convert content to byte array representation.
 */
@DataTypeTransformer(name = "application-octet-stream")
public class ByteArrayDataTypeTransformer extends Transformer {

    private static final Transformer DELEGATE = new TypeConverterTransformer(byte[].class);

    @Override
    public void transform(Message message, DataType from, DataType to) throws Exception {
        DELEGATE.transform(message, from, to);

        message.setHeader(Exchange.CONTENT_TYPE, "application/octet-stream");
    }
}
