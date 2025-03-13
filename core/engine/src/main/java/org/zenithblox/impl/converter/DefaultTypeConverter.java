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
package org.zenithblox.impl.converter;

import org.zenithblox.ZwangineContext;
import org.zenithblox.spi.AnnotationScanTypeConverters;
import org.zenithblox.spi.Injector;
import org.zenithblox.spi.PackageScanClassResolver;
import org.zenithblox.spi.TypeConverterLoader;
import org.zenithblox.util.StopWatch;
import org.zenithblox.util.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of a type converter registry used for
 * <a href="http://zwangine.zwangine.org/type-converter.html">type converters</a> in Zwangine.
 * <p/>
 * This implementation will load type converters up-front on startup.
 */
public class DefaultTypeConverter extends BaseTypeConverterRegistry implements AnnotationScanTypeConverters {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultTypeConverter.class);

    private volatile boolean loadTypeConvertersDone;
    private final boolean loadTypeConverters;

    public DefaultTypeConverter(PackageScanClassResolver resolver, Injector injector,
                                boolean loadTypeConverters, boolean statisticsEnabled) {
        this(null, resolver, injector, loadTypeConverters, statisticsEnabled);
    }

    public DefaultTypeConverter(ZwangineContext zwangineContext, PackageScanClassResolver resolver, Injector injector,
                                boolean loadTypeConverters, boolean statisticsEnabled) {
        super(zwangineContext, resolver, injector, statisticsEnabled);
        this.loadTypeConverters = loadTypeConverters;
    }

    @Override
    public boolean isRunAllowed() {
        // as type converter is used during initialization then allow it to always run
        return true;
    }

    @Override
    protected void doInit() throws Exception {
        StopWatch watch = new StopWatch();

        super.doInit();

        // core type converters is always loaded which does not use any classpath scanning and therefore is fast
        loadCoreAndFastTypeConverters();

        String time = TimeUtils.printDuration(watch.taken(), true);
        LOG.debug("Loaded {} type converters in {}", size(), time);

        if (!loadTypeConvertersDone && isLoadTypeConverters()) {
            scanTypeConverters();
        }
    }

    private boolean isLoadTypeConverters() {
        boolean load = loadTypeConverters;
        if (zwangineContext != null) {
            // zwangine context can override
            load = zwangineContext.isLoadTypeConverters();
        }
        return load;
    }

    @Override
    public void scanTypeConverters() throws Exception {
        StopWatch watch = new StopWatch();

        // we are using backwards compatible legacy mode to detect additional converters
        if (!loadTypeConvertersDone) {
            loadTypeConvertersDone = true;

            if (resolver != null) {
                typeConverterLoaders.add(createScanTypeConverterLoader());
            }

            // load type converters up front
            loadTypeConverters();

            // lets clear the cache from the resolver as its often only used during startup
            if (resolver != null) {
                resolver.clearCache();
            }
        }

        String time = TimeUtils.printDuration(watch.taken(), true);
        LOG.debug("Loaded {} type converters in {}", size(), time);
    }

    /**
     * Creates the {@link TypeConverterLoader} to use for scanning for type converters such as from the classpath.
     */
    protected TypeConverterLoader createScanTypeConverterLoader() {
        String basePackages = zwangineContext != null ? zwangineContext.getZwangineContextExtension().getBasePackageScan() : null;
        return new AnnotationTypeConverterLoader(resolver, basePackages);
    }
}
