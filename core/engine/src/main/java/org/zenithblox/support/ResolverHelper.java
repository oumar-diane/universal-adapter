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
package org.zenithblox.support;

import org.zenithblox.ZwangineContext;
import org.zenithblox.Component;
import org.zenithblox.spi.DataFormat;
import org.zenithblox.spi.DataFormatFactory;
import org.zenithblox.spi.FactoryFinder;
import org.zenithblox.spi.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Some helper methods for new resolvers (like {@link org.zenithblox.spi.ComponentResolver},
 * {@link org.zenithblox.spi.DataFormatResolver}, etc.).
 */
public final class ResolverHelper {

    public static final String COMPONENT_FALLBACK_SUFFIX = "-component";

    public static final String DATA_FORMAT_FALLBACK_SUFFIX = "-dataformat";

    public static final String DATA_FORMAT_FACTORY_FALLBACK_SUFFIX = "-dataformat-factory";

    public static final String LANGUAGE_FALLBACK_SUFFIX = "-language";

    private static final Logger LOG = LoggerFactory.getLogger(ResolverHelper.class);

    private static final LookupExceptionHandler EXCEPTION_HANDLER = new LookupExceptionHandler();

    /**
     * Utility classes should not have a public constructor.
     */
    private ResolverHelper() {
    }

    public static Component lookupComponentInRegistryWithFallback(ZwangineContext context, String name) {
        return lookupComponentInRegistryWithFallback(context, name, EXCEPTION_HANDLER);
    }

    public static Component lookupComponentInRegistryWithFallback(
            ZwangineContext context, String name, LookupExceptionHandler exceptionHandler) {
        Object bean
                = lookupInRegistry(context, Component.class, false, exceptionHandler, name, name + COMPONENT_FALLBACK_SUFFIX);
        if (bean != null) {
            if (bean instanceof Component component) {
                return component;
            } else {
                // let's use Zwangine's type conversion mechanism to convert things like ZwangineContext
                // and other types into a valid Component
                Component component = ZwangineContextHelper.convertTo(context, Component.class, bean);
                if (component != null) {
                    return component;
                }
            }
        }

        if (bean != null) {
            LOG.debug("Found Component with incompatible class: {}", bean.getClass().getName());
        }
        return null;
    }

    public static DataFormat lookupDataFormatInRegistryWithFallback(ZwangineContext context, String name) {
        return lookupDataFormatInRegistryWithFallback(context, name, EXCEPTION_HANDLER);
    }

    public static DataFormat lookupDataFormatInRegistryWithFallback(
            ZwangineContext context, String name, LookupExceptionHandler exceptionHandler) {
        Object bean = lookupInRegistry(context, DataFormat.class, false, exceptionHandler, name,
                name + DATA_FORMAT_FALLBACK_SUFFIX);
        if (bean instanceof DataFormat dataFormat) {
            return dataFormat;
        }

        if (bean != null) {
            LOG.debug("Found DataFormat with incompatible class: {}", bean.getClass().getName());
        }
        return null;
    }

    public static DataFormatFactory lookupDataFormatFactoryInRegistryWithFallback(ZwangineContext context, String name) {
        return lookupDataFormatFactoryInRegistryWithFallback(context, name, EXCEPTION_HANDLER);
    }

    public static DataFormatFactory lookupDataFormatFactoryInRegistryWithFallback(
            ZwangineContext context, String name, LookupExceptionHandler exceptionHandler) {
        Object bean = lookupInRegistry(context, DataFormatFactory.class, false, exceptionHandler, name,
                name + DATA_FORMAT_FACTORY_FALLBACK_SUFFIX);
        if (bean instanceof DataFormatFactory dataFormatFactory) {
            return dataFormatFactory;
        }

        if (bean != null) {
            LOG.debug("Found DataFormatFactory with incompatible class: {}", bean.getClass().getName());
        }
        return null;
    }

    public static Language lookupLanguageInRegistryWithFallback(ZwangineContext context, String name) {
        return lookupLanguageInRegistryWithFallback(context, name, EXCEPTION_HANDLER);
    }

    public static Language lookupLanguageInRegistryWithFallback(
            ZwangineContext context, String name, LookupExceptionHandler exceptionHandler) {
        Object bean = lookupInRegistry(context, Language.class, false, exceptionHandler, name, name + LANGUAGE_FALLBACK_SUFFIX);
        if (bean instanceof Language language) {
            return language;
        }

        if (bean != null) {
            LOG.debug("Found Language with incompatible class: {}", bean.getClass().getName());
        }
        return null;
    }

    /**
     * Create an instance of the given factory.
     *
     * @param  zwangineContext the {@link ZwangineContext}
     * @param  factoryPath  the path of the factory file
     * @param  factoryKey   the key used top lookup the factory class
     * @param  factoryClass the type of the class
     * @return              an instance fo the given factory
     */
    public static <T> Optional<T> resolveService(
            ZwangineContext zwangineContext, String factoryPath, String factoryKey, Class<T> factoryClass) {
        return resolveService(
                zwangineContext,
                zwangineContext.getZwangineContextExtension().getFactoryFinder(factoryPath),
                factoryKey, factoryClass);
    }

    /**
     * Create an instance of the given factory using the default factory finder
     *
     * @param  zwangineContext the {@link ZwangineContext}
     * @param  factoryKey   the key used top lookup the factory class
     * @param  factoryClass the type of the class
     * @return              an instance fo the given factory
     */
    public static <T> Optional<T> resolveService(
            ZwangineContext zwangineContext, String factoryKey, Class<T> factoryClass) {
        return resolveService(
                zwangineContext,
                zwangineContext.getZwangineContextExtension().getDefaultFactoryFinder(),
                factoryKey, factoryClass);
    }

    /**
     * Create an instance of the given factory.
     *
     * @param  zwangineContext  the {@link ZwangineContext}
     * @param  factoryFinder the {@link FactoryFinder} to use
     * @param  factoryKey    the key used top lookup the factory class
     * @param  factoryClass  the type of the class
     * @return               an instance fo the given factory
     */
    public static <T> Optional<T> resolveService(
            ZwangineContext zwangineContext, FactoryFinder factoryFinder, String factoryKey, Class<T> factoryClass) {

        // use factory finder to find a custom implementations
        Class<?> type = null;

        try {
            type = factoryFinder.findClass(factoryKey).orElse(null);
        } catch (Exception e) {
            // ignore
        }

        if (type != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Found {}: {} via: {}{}", factoryClass.getSimpleName(), type.getName(), FactoryFinder.DEFAULT_PATH,
                        factoryKey);
            }
            if (factoryClass.isAssignableFrom(type)) {
                final Object instance = zwangineContext.getInjector().newInstance(type, false);
                final T answer = factoryClass.cast(instance);

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Detected and using {}: {}", factoryClass.getSimpleName(), answer);
                }

                return Optional.of(answer);
            } else {
                throw new IllegalArgumentException(
                        "Type is not a " + factoryClass.getSimpleName() + " implementation. Found: " + type.getName());
            }
        }
        return Optional.empty();
    }

    private static Object lookupInRegistry(
            ZwangineContext context, Class<?> type, boolean lookupByNameAndType, LookupExceptionHandler exceptionHandler,
            String... names) {
        for (String name : names) {
            try {
                Object bean;
                if (lookupByNameAndType) {
                    bean = context.getRegistry().lookupByNameAndType(name, type);
                } else {
                    bean = context.getRegistry().lookupByName(name);
                }
                LOG.debug("Lookup {} with name {} in registry. Found: {}", type.getSimpleName(), name, bean);
                if (bean != null) {
                    return bean;
                }
            } catch (Exception e) {
                exceptionHandler.handleException(e, LOG, name);
            }
        }

        return null;
    }

    public static class LookupExceptionHandler {

        public void handleException(Exception e, Logger log, String name) {
            log.debug("Ignored error looking up bean: {}", name, e);
        }

    }

}
