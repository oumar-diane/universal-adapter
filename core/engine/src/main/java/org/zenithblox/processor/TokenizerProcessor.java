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

package org.zenithblox.processor;

import org.zenithblox.*;
import org.zenithblox.spi.IdAware;
import org.zenithblox.spi.WorkflowIdAware;
import org.zenithblox.spi.Tokenizer;
import org.zenithblox.support.AsyncProcessorConverterHelper;
import org.zenithblox.support.AsyncProcessorSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * This implements the core processor for handling tokenization
 */
public class TokenizerProcessor extends AsyncProcessorSupport
        implements Navigate<Processor>,
        ZwangineContextAware, IdAware, WorkflowIdAware {

    private final AsyncProcessor processor;
    private final Tokenizer tokenizer;

    private ZwangineContext zwangineContext;
    private String id;
    private String workflowId;

    public TokenizerProcessor(Processor processor, Tokenizer tokenizer) {
        this.processor = AsyncProcessorConverterHelper.convert(processor);
        this.tokenizer = tokenizer;
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        final String[] tokenized = tokenizer.tokenize(exchange);

        exchange.getMessage().setBody(tokenized);
        return processor.process(exchange, callback);

    }

    @Override
    public List<Processor> next() {
        if (!hasNext()) {
            return null;
        }
        List<Processor> answer = new ArrayList<>(1);
        answer.add(processor);
        return answer;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getWorkflowId() {
        return workflowId;
    }

    @Override
    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }
}
