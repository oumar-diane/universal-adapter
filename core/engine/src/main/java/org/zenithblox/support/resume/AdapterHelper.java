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

package org.zenithblox.support.resume;

import org.zenithblox.ZwangineContext;
import org.zenithblox.RuntimeZwangineException;
import org.zenithblox.resume.*;
import org.zenithblox.resume.cache.ResumeCache;
import org.zenithblox.spi.FactoryFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static java.lang.String.format;

public final class AdapterHelper {
    private static final Logger LOG = LoggerFactory.getLogger(AdapterHelper.class);

    private AdapterHelper() {
    }

    public static ResumeAdapter eval(ZwangineContext context, ResumeAware<?> resumeAware, ResumeStrategy resumeStrategy) {
        assert context != null;
        assert resumeAware != null;
        assert resumeStrategy != null;

        LOG.debug("Using the factory finder to search for the resume adapter");
        final FactoryFinder factoryFinder
                = context.getZwangineContextExtension().getFactoryFinder(FactoryFinder.DEFAULT_PATH);

        LOG.debug("Creating a new resume adapter");
        Optional<ResumeAdapter> adapterOptional
                = factoryFinder.newInstance(resumeAware.adapterFactoryService(), ResumeAdapter.class);

        if (adapterOptional.isEmpty()) {
            throw new RuntimeZwangineException("Cannot find a resume adapter class in the consumer classpath or in the registry");
        }

        final ResumeAdapter resumeAdapter = adapterOptional.get();
        LOG.debug("Using the acquired resume adapter: {}", resumeAdapter.getClass().getName());

        if (resumeAdapter instanceof Cacheable cacheableAdapter) {
            final ResumeStrategyConfiguration resumeStrategyConfiguration = resumeStrategy.getResumeStrategyConfiguration();

            final ResumeCache<?> resumeCache = resumeStrategyConfiguration.getResumeCache();
            if (resumeCache != null) {
                cacheableAdapter.setCache(resumeCache);
            } else {
                LOG.error("No cache was provided in the configuration for the cacheable resume adapter {}",
                        resumeAdapter.getClass().getName());
                throw new RuntimeZwangineException(
                        format("No cache was provided in the configuration for the cacheable resume adapter %s",
                                resumeAdapter.getClass().getName()));
            }
        } else {
            LOG.debug("The resume adapter {} is not cacheable", resumeAdapter.getClass().getName());
        }

        return resumeAdapter;
    }
}
