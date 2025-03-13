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
package org.zenithblox.processor.validator;

import org.zenithblox.*;
import org.zenithblox.spi.DataType;
import org.zenithblox.spi.Validator;
import org.zenithblox.support.ExchangeHelper;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link Validator} implementation which leverages {@link Processor} to perform validation.
 *
 * {@see Validator}
 */
public class ProcessorValidator extends Validator {
    private static final Logger LOG = LoggerFactory.getLogger(ProcessorValidator.class);

    private Processor processor;
    private String validatorString;

    public ProcessorValidator(ZwangineContext context) {
        setZwangineContext(context);
    }

    /**
     * Perform content validation with specified type using Processor.
     *
     * @param message message to apply validation
     * @param type    'from' data type
     */
    @Override
    public void validate(Message message, DataType type) throws ValidationException {
        Exchange exchange = message.getExchange();

        LOG.debug("Sending to validate processor '{}'", processor);
        // create a new exchange to use during validation to avoid side-effects on original exchange
        Exchange copy = ExchangeHelper.createCorrelatedCopy(exchange, false, true);
        try {
            processor.process(copy);

            // if the validation failed then propagate the exception
            if (copy.getException() != null) {
                exchange.setException(copy.getException());
            } else {
                // success copy result
                ExchangeHelper.copyResults(exchange, copy);
            }
        } catch (Exception e) {
            if (e instanceof ValidationException validationException) {
                throw validationException;
            } else {
                throw new ValidationException(String.format("Validation failed for '%s'", type), exchange, e);
            }
        }
    }

    /**
     * Set processor to use
     *
     * @param  processor Processor
     * @return           this ProcessorTransformer instance
     */
    public ProcessorValidator setProcessor(Processor processor) {
        this.processor = processor;
        this.validatorString = null;
        return this;
    }

    @Override
    public String toString() {
        if (validatorString == null) {
            validatorString = String.format("ProcessorValidator[type='%s', processor='%s']", getType(), processor);
        }
        return validatorString;
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
