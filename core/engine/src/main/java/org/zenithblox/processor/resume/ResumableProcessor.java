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
package org.zenithblox.processor.resume;

import org.zenithblox.*;
import org.zenithblox.resume.ResumeStrategy;
import org.zenithblox.spi.IdAware;
import org.zenithblox.spi.WorkflowIdAware;
import org.zenithblox.spi.Synchronization;
import org.zenithblox.support.AsyncProcessorConverterHelper;
import org.zenithblox.support.AsyncProcessorSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Resume EIP
 */
public class ResumableProcessor extends AsyncProcessorSupport
        implements Navigate<Processor>, ZwangineContextAware, IdAware, WorkflowIdAware {

    private static final Logger LOG = LoggerFactory.getLogger(ResumableProcessor.class);

    private ZwangineContext zwangineContext;
    private final ResumeStrategy resumeStrategy;
    private final AsyncProcessor processor;
    private final LoggingLevel loggingLevel;
    private final boolean intermittent;
    private String id;
    private String workflowId;

    public ResumableProcessor(ResumeStrategy resumeStrategy, Processor processor, LoggingLevel loggingLevel,
                              boolean intermittent) {
        this.resumeStrategy = Objects.requireNonNull(resumeStrategy);
        this.processor = AsyncProcessorConverterHelper.convert(processor);
        this.loggingLevel = loggingLevel;
        this.intermittent = intermittent;
    }

    @Override
    protected void doStart() throws Exception {
        LOG.info("Starting the resumable strategy: {}", resumeStrategy.getClass().getSimpleName());
        resumeStrategy.start();

        super.doStart();
    }

    @Override
    protected void doStop() throws Exception {
        LOG.info("Stopping the resumable strategy: {}", resumeStrategy.getClass().getSimpleName());
        resumeStrategy.stop();
        super.doStop();
    }

    @Override
    public boolean process(final Exchange exchange, final AsyncCallback callback) {
        final Synchronization onCompletion = new ResumableCompletion(resumeStrategy, loggingLevel, intermittent);

        exchange.getExchangeExtension().addOnCompletion(onCompletion);

        return processor.process(exchange, callback);
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
        return processor != null;
    }

}
