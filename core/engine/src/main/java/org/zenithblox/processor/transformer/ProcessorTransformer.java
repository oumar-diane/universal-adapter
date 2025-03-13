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

import org.zenithblox.ZwangineContext;
import org.zenithblox.Exchange;
import org.zenithblox.Message;
import org.zenithblox.Processor;
import org.zenithblox.spi.DataType;
import org.zenithblox.spi.Transformer;
import org.zenithblox.support.DefaultExchange;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link Transformer} implementation which leverages {@link Processor} to perform transformation.
 *
 * {@see Transformer}
 */
public class ProcessorTransformer extends Transformer {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessorTransformer.class);

    private Processor processor;
    private String transformerString;

    public ProcessorTransformer() {
    }

    public ProcessorTransformer(ZwangineContext context) {
        setZwangineContext(context);
    }

    /**
     * Perform data transformation with specified from/to type using Processor.
     *
     * @param message message to apply transformation
     * @param from    'from' data type
     * @param to      'to' data type
     */
    @Override
    public void transform(Message message, DataType from, DataType to) throws Exception {
        Exchange exchange = message.getExchange();
        ZwangineContext context = exchange.getContext();
        if (from.isJavaType()) {
            Object input = message.getBody();
            Class<?> fromClass = context.getClassResolver().resolveClass(from.getName());
            if (!fromClass.isAssignableFrom(input.getClass())) {
                LOG.debug("Converting to: {}", fromClass.getName());
                input = context.getTypeConverter().mandatoryConvertTo(fromClass, input);
                message.setBody(input);
            }
        }

        LOG.debug("Sending to transform processor: {}", processor);
        // must create a copy in this way
        Exchange transformExchange = new DefaultExchange(exchange);
        transformExchange.setIn(message);
        transformExchange.getExchangeExtension().setProperties(exchange.getProperties());
        processor.process(transformExchange);
        Message answer = transformExchange.getMessage();

        if (to.isJavaType()) {
            Object answerBody = answer.getBody();
            Class<?> toClass = context.getClassResolver().resolveClass(to.getName());
            if (!toClass.isAssignableFrom(answerBody.getClass())) {
                LOG.debug("Converting to: {}", toClass.getName());
                answerBody = context.getTypeConverter().mandatoryConvertTo(toClass, answerBody);
                answer.setBody(answerBody);
            }
        }

        message.copyFrom(answer);
    }

    /**
     * Set processor to use
     *
     * @param  processor Processor
     * @return           this ProcessorTransformer instance
     */
    public ProcessorTransformer setProcessor(Processor processor) {
        this.processor = processor;
        this.transformerString = null;
        return this;
    }

    @Override
    public String toString() {
        if (transformerString == null) {
            transformerString = String.format("ProcessorTransformer[name='%s', from='%s', to='%s', processor='%s']",
                    getName(), getFrom(), getTo(), processor);
        }
        return transformerString;
    }

    @Override
    protected void doBuild() throws Exception {
        ServiceHelper.buildService(processor);
    }

    @Override
    protected void doInit() throws Exception {
        ServiceHelper.initService(processor);
    }

    @Override
    protected void doStart() throws Exception {
        ObjectHelper.notNull(processor, "processor", this);
        ServiceHelper.startService(this.processor);
    }

    @Override
    protected void doStop() throws Exception {
        ServiceHelper.stopService(this.processor);
    }
}
