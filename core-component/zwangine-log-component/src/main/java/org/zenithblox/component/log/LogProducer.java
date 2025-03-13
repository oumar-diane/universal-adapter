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
package org.zenithblox.component.log;

import org.zenithblox.*;
import org.zenithblox.support.AsyncProcessorConverterHelper;
import org.zenithblox.support.DefaultAsyncProducer;

/**
 * Log producer.
 */
public class LogProducer extends DefaultAsyncProducer {

    private final AsyncProcessor logger;

    public LogProducer(Endpoint endpoint, Processor logger) {
        super(endpoint);
        this.logger = AsyncProcessorConverterHelper.convert(logger);
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        return logger.process(exchange, callback);
    }

    public Processor getLogger() {
        return logger;
    }
}
