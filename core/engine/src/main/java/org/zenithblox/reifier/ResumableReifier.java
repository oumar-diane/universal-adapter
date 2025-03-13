/*
 * Licensed to the  Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the  License, Version 2.0
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
package org.zenithblox.reifier;

import org.zenithblox.*;
import org.zenithblox.model.ProcessorDefinition;
import org.zenithblox.model.ResumableDefinition;
import org.zenithblox.processor.resume.ResumableProcessor;
import org.zenithblox.resume.ResumeStrategy;
import org.zenithblox.resume.ResumeStrategyConfiguration;
import org.zenithblox.spi.FactoryFinder;
import org.zenithblox.util.ObjectHelper;

import java.util.Optional;

public class ResumableReifier extends ProcessorReifier<ResumableDefinition> {

    protected ResumableReifier(Workflow workflow, ProcessorDefinition<?> definition) {
        super(workflow, ResumableDefinition.class.cast(definition));
    }

    @Override
    public Processor createProcessor() throws Exception {
        Processor childProcessor = createChildProcessor(false);

        ResumeStrategy resumeStrategy = resolveResumeStrategy();
        ObjectHelper.notNull(resumeStrategy, ResumeStrategy.DEFAULT_NAME, definition);

        if (resumeStrategy instanceof ZwangineContextAware zwangineContextAware) {
            zwangineContextAware.setZwangineContext(zwangineContext);
        }

        workflow.setResumeStrategy(resumeStrategy);
        LoggingLevel loggingLevel = resolveLoggingLevel();

        boolean intermittent = parseBoolean(definition.getIntermittent(), false);
        return new ResumableProcessor(resumeStrategy, childProcessor, loggingLevel, intermittent);
    }

    protected ResumeStrategy resolveResumeStrategy() {
        ResumeStrategy strategy = definition.getResumeStrategyBean();
        if (strategy == null) {
            String ref = parseString(definition.getResumeStrategy());

            if (ref != null) {
                strategy = mandatoryLookup(ref, ResumeStrategy.class);
            } else {
                final FactoryFinder factoryFinder
                        = zwangineContext.getZwangineContextExtension().getFactoryFinder(FactoryFinder.DEFAULT_PATH);

                final ResumeStrategyConfiguration resumeStrategyConfiguration = definition.getResumeStrategyConfiguration();
                Optional<ResumeStrategy> resumeStrategyOptional = factoryFinder.newInstance(
                        resumeStrategyConfiguration.resumeStrategyService(), ResumeStrategy.class);

                if (resumeStrategyOptional.isEmpty()) {
                    throw new RuntimeZwangineException("Cannot find a resume strategy class in the classpath or the registry");
                }

                final ResumeStrategy resumeStrategy = resumeStrategyOptional.get();

                resumeStrategy.setResumeStrategyConfiguration(resumeStrategyConfiguration);

                return resumeStrategy;
            }
        }

        return strategy;
    }

    protected LoggingLevel resolveLoggingLevel() {
        LoggingLevel loggingLevel = parse(LoggingLevel.class, definition.getLoggingLevel());

        if (loggingLevel == null) {
            loggingLevel = LoggingLevel.ERROR;
        }

        return loggingLevel;
    }
}
