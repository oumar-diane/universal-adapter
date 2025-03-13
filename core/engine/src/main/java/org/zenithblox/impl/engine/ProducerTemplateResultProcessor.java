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
package org.zenithblox.impl.engine;

import org.zenithblox.Exchange;
import org.zenithblox.Processor;

/**
 * To be used by producer template to prepare the message body for a given expected result type.
 */
class ProducerTemplateResultProcessor implements Processor {

    private final Class<?> type;

    public ProducerTemplateResultProcessor(Class<?> type) {
        this.type = type;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        if (exchange.getMessage().getBody() == null) {
            // only convert if there is a body
            return;
        }

        if (exchange.getException() != null) {
            // do not convert if an exception has been thrown as if we attempt to convert and it also fails with a new
            // exception then it will override the existing exception
            return;
        }

        // should be mandatory
        Object newBody = exchange.getMessage().getMandatoryBody(type);
        exchange.getMessage().setBody(newBody);
    }
}
