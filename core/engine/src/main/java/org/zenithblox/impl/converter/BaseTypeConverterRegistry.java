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

import org.zenithblox.*;
import org.zenithblox.spi.Injector;
import org.zenithblox.spi.PackageScanClassResolver;
import org.zenithblox.spi.TypeConverterLoader;
import org.zenithblox.support.PluginHelper;
import org.zenithblox.util.IOHelper;
import org.zenithblox.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Base implementation of a type converter registry used for <a href="http://zwangine.zwangine.org/type-converter.html">type
 * converters</a> in Zwangine.
 */
public abstract class BaseTypeConverterRegistry extends CoreTypeConverterRegistry {

    public static final String META_INF_SERVICES_UBER_TYPE_CONVERTER_LOADER
            = "META-INF/services.org.zenithblox/UberTypeConverterLoader";
    public static final String META_INF_SERVICES_TYPE_CONVERTER_LOADER
            = "META-INF/services.org.zenithblox/TypeConverterLoader";
    public static final String META_INF_SERVICES_FALLBACK_TYPE_CONVERTER
            = "META-INF/services.org.zenithblox/FallbackTypeConverter";

    private static final Logger LOG = LoggerFactory.getLogger(BaseTypeConverterRegistry.class);

    protected final List<TypeConverterLoader> typeConverterLoaders = new ArrayList<>();
    protected ZwangineContext zwangineContext;
    protected PackageScanClassResolver resolver;
    protected Injector injector;

    public BaseTypeConverterRegistry(ZwangineContext zwangineContext, PackageScanClassResolver resolver, Injector injector,
                                     boolean statisticsEnabled) {
        super(statisticsEnabled);

        this.zwangineContext = zwangineContext;
        this.injector = injector;
        this.resolver = resolver;
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    public List<TypeConverterLoader> getTypeConverterLoaders() {
        return typeConverterLoaders;
    }

    @Override
    public void addTypeConverters(Object typeConverters) {
        LOG.trace("Adding type converters: {}", typeConverters);
        try {
            // scan the class for @Converter and load them into this registry
            TypeConvertersLoader loader;
            if (typeConverters instanceof Class tcs) {
                loader = new TypeConvertersLoader(tcs);
            } else {
                loader = new TypeConvertersLoader(typeConverters);
            }
            ZwangineContextAware.trySetZwangineContext(loader, getZwangineContext());
            loader.load(this);
        } catch (TypeConverterLoaderException e) {
            throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
        }
    }

    @Override
    public void addFallbackTypeConverter(TypeConverter typeConverter, boolean canPromote) {
        super.addFallbackTypeConverter(typeConverter, canPromote);
        ZwangineContextAware.trySetZwangineContext(typeConverter, zwangineContext);
    }

    private void addCoreFallbackTypeConverterToList(
            TypeConverter typeConverter, boolean canPromote,
            List<FallbackTypeConverter> converters) {
        LOG.trace("Adding core fallback type converter: {} which can promote: {}", typeConverter, canPromote);

        // add in top of fallback as the toString() fallback will nearly always be able to convert
        // the last one which is add to the FallbackTypeConverter will be called at the first place
        converters.add(0, new FallbackTypeConverter(typeConverter, canPromote));

        ZwangineContextAware.trySetZwangineContext(typeConverter, zwangineContext);
    }

    @Override
    public Injector getInjector() {
        return injector;
    }

    @Override
    public void setInjector(Injector injector) {
        this.injector = injector;
    }

    public PackageScanClassResolver getResolver() {
        return resolver;
    }

    /**
     * Loads the core type converters which is mandatory to use Zwangine, and also loads the fast type converters
     * (generated via @Converter(loader = true).
     */
    public void loadCoreAndFastTypeConverters() throws Exception {
        Collection<String> names = findTypeConverterLoaderClasses();

        for (String name : names) {
            LOG.debug("Resolving TypeConverterLoader: {}", name);
            Class<?> clazz = null;
            for (ClassLoader loader : getResolver().getClassLoaders()) {
                try {
                    clazz = loader.loadClass(name);
                } catch (Exception e) {
                    // ignore
                }
                if (clazz != null) {
                    break;
                }
            }
            if (clazz == null) {
                throw new ClassNotFoundException(name);
            }
            Object obj = getInjector().newInstance(clazz, false);
            ZwangineContextAware.trySetZwangineContext(obj, getZwangineContext());
            if (obj instanceof TypeConverterLoader loader) {
                ZwangineContextAware.trySetZwangineContext(loader, getZwangineContext());
                LOG.debug("TypeConverterLoader: {} loading converters", name);
                loader.load(this);
            }
        }
    }

    /**
     * Finds the type converter loader classes from the classpath looking for text files on the classpath at the
     * {@link #META_INF_SERVICES_UBER_TYPE_CONVERTER_LOADER} and {@link #META_INF_SERVICES_TYPE_CONVERTER_LOADER}
     * locations.
     */
    protected Collection<String> findTypeConverterLoaderClasses() throws IOException {
        Collection<String> loaders = new LinkedHashSet<>();
        findTypeConverterLoaderClasses(loaders, META_INF_SERVICES_UBER_TYPE_CONVERTER_LOADER);
        findTypeConverterLoaderClasses(loaders, META_INF_SERVICES_TYPE_CONVERTER_LOADER);
        return loaders;
    }

    protected void findTypeConverterLoaderClasses(Collection<String> loaders, String basePath) throws IOException {
        Collection<URL> loaderResources = getLoaderUrls(basePath);
        for (URL url : loaderResources) {
            LOG.debug("Loading file {} to retrieve list of type converters, from url: {}",
                    basePath, url);
            BufferedReader reader = IOHelper.buffered(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
            String line;
            do {
                line = reader.readLine();
                if (line != null && !line.startsWith("#") && !line.isEmpty()) {
                    loaders.add(line);
                }
            } while (line != null);
            IOHelper.close(reader);
        }
    }

    protected Collection<URL> getLoaderUrls(String basePath) throws IOException {
        List<URL> loaderResources = new ArrayList<>();
        addResources(basePath, loaderResources);
        return loaderResources;
    }

    /**
     * Checks if the registry is loaded and if not lazily load it
     */
    protected void loadTypeConverters() throws Exception {
        for (TypeConverterLoader loader : getTypeConverterLoaders()) {
            ZwangineContextAware.trySetZwangineContext(loader, getZwangineContext());
            loader.load(this);
        }

        // lets try load any other fallback converters
        try {
            loadFallbackTypeConverters();
        } catch (NoFactoryAvailableException e) {
            // ignore its fine to have none
        }
    }

    /**
     * Finds the fallback type converter classes from the classpath looking for text files on the classpath at the
     * {@link #META_INF_SERVICES_FALLBACK_TYPE_CONVERTER} location.
     */
    protected Collection<String> findFallbackTypeConverterClasses() throws IOException {
        Set<String> loaders = new LinkedHashSet<>();
        Collection<URL> loaderResources = getFallbackUrls();
        for (URL url : loaderResources) {
            LOG.debug("Loading file {} to retrieve list of fallback type converters, from url: {}",
                    META_INF_SERVICES_FALLBACK_TYPE_CONVERTER, url);
            BufferedReader reader = IOHelper.buffered(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
            try {
                reader.lines()
                        .map(String::trim)
                        .filter(l -> !l.isEmpty())
                        .filter(l -> !l.startsWith("#"))
                        .forEach(loaders::add);
            } finally {
                IOHelper.close(reader, url.toString(), LOG);
            }
        }
        return loaders;
    }

    protected Collection<URL> getFallbackUrls() throws IOException {
        List<URL> loaderResources = new ArrayList<>();
        addResources(META_INF_SERVICES_FALLBACK_TYPE_CONVERTER, loaderResources);
        return loaderResources;
    }

    private void addResources(String metaInfServicesFallbackTypeConverter, List<URL> loaderResources) throws IOException {
        for (ClassLoader classLoader : resolver.getClassLoaders()) {
            Enumeration<URL> resources = classLoader.getResources(metaInfServicesFallbackTypeConverter);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                loaderResources.add(url);
            }
        }
    }

    protected void loadFallbackTypeConverters() throws IOException, ClassNotFoundException {
        Collection<String> names = findFallbackTypeConverterClasses();
        for (String name : names) {
            LOG.debug("Resolving FallbackTypeConverter: {}", name);
            Class<?> clazz = getResolver().getClassLoaders().stream()
                    .map(cl -> ObjectHelper.loadClass(name, cl))
                    .filter(Objects::nonNull)
                    .findAny().orElseThrow(() -> new ClassNotFoundException(name));
            Object obj = getInjector().newInstance(clazz, false);
            if (obj instanceof TypeConverter fb) {
                LOG.debug("Adding loaded FallbackTypeConverter: {}", name);
                addFallbackTypeConverter(fb, false);
            }
        }
    }

    @Override
    protected void doInit() throws Exception {
        super.doInit();

        if (injector == null && zwangineContext != null) {
            injector = zwangineContext.getInjector();
        }
        if (resolver == null && zwangineContext != null) {
            resolver = PluginHelper.getPackageScanClassResolver(zwangineContext);
        }

        List<FallbackTypeConverter> fallbacks = new ArrayList<>();
        // add to string first as it will then be last in the last as to string can nearly
        // always convert something to a string so we want it only as the last resort
        // ToStringTypeConverter should NOT allow to be promoted
        addCoreFallbackTypeConverterToList(new ToStringTypeConverter(), false, fallbacks);
        // arrays is okay to be promoted
        addCoreFallbackTypeConverterToList(new ArrayTypeConverter(), true, fallbacks);
        // and future should also not allowed to be promoted
        addCoreFallbackTypeConverterToList(new FutureTypeConverter(this), false, fallbacks);
        // add sync processor to async processor converter is to be promoted
        addCoreFallbackTypeConverterToList(new AsyncProcessorTypeConverter(), true, fallbacks);

        // add all core fallback converters at once which is faster (profiler)
        fallbackConverters.addAll(fallbacks);
    }

}
