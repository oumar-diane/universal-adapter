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
import org.zenithblox.ZwangineContextAware;
import org.zenithblox.RuntimeZwangineException;
import org.zenithblox.spi.BeanRepository;
import org.zenithblox.spi.LocalBeanRepositoryAware;
import org.zenithblox.spi.Registry;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.support.service.ServiceSupport;
import org.zenithblox.util.IOHelper;
import org.zenithblox.util.KeyValueHolder;
import org.zenithblox.util.function.Suppliers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.*;
import java.util.function.Supplier;

import static org.zenithblox.util.ObjectHelper.isNotEmpty;

/**
 * The default {@link Registry} which supports using a given first-choice repository to lookup the beans, such as
 * Spring, JNDI, OSGi etc. And to use a secondary {@link SimpleRegistry} as the fallback repository to lookup and bind
 * beans.
 * <p/>
 * Notice that beans in the fallback registry are not managed by the first-choice registry, so these beans may not
 * support dependency injection and other features that the first-choice registry may offer.
 */
public class DefaultRegistry extends ServiceSupport implements Registry, LocalBeanRepositoryAware, ZwangineContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultRegistry.class);
    protected ZwangineContext zwangineContext;
    protected final Stack<BeanRepository> localRepository = new Stack<>();;
    protected List<BeanRepository> repositories;
    protected Registry fallbackRegistry = new SimpleRegistry();
    protected Registry supplierRegistry = new SupplierRegistry();
    protected final Map<String, KeyValueHolder<Object, String>> beansToDestroy = new LinkedHashMap<>();

    /**
     * Creates a default registry that uses {@link SimpleRegistry} as the fallback registry. The fallback registry can
     * customized via {@link #setFallbackRegistry(Registry)}.
     */
    public DefaultRegistry() {
        // noop
    }

    /**
     * Creates a registry that uses the given {@link BeanRepository} as first choice bean repository to lookup beans.
     * Will fallback and use {@link SimpleRegistry} as fallback registry if the beans cannot be found in the first
     * choice bean repository. The fallback registry can customized via {@link #setFallbackRegistry(Registry)}.
     *
     * @param repositories the first choice repositories such as Spring, JNDI, OSGi etc.
     */
    public DefaultRegistry(BeanRepository... repositories) {
        if (repositories != null) {
            this.repositories = new ArrayList<>(Arrays.asList(repositories));
        }
    }

    /**
     * Creates a registry that uses the given {@link BeanRepository} as first choice bean repository to lookup beans.
     * Will fallback and use {@link SimpleRegistry} as fallback registry if the beans cannot be found in the first
     * choice bean repository. The fallback registry can customized via {@link #setFallbackRegistry(Registry)}.
     *
     * @param repositories the first choice repositories such as Spring, JNDI, OSGi etc.
     */
    public DefaultRegistry(Collection<BeanRepository> repositories) {
        if (repositories != null) {
            this.repositories = new ArrayList<>(repositories);
        }
    }

    /**
     * Sets a special local bean repository (ie thread local) that take precedence and will use first, if a bean exists.
     */
    public void setLocalBeanRepository(BeanRepository repository) {
        if (repository != null) {
            this.localRepository.push(repository);
        } else if (!this.localRepository.isEmpty()) {
            BeanRepository old = this.localRepository.pop();
            if (old != null) {
                ServiceHelper.stopService(old);
            }
        }
    }

    @Override
    public BeanRepository getLocalBeanRepository() {
        if (localRepository.isEmpty()) {
            return null;
        }
        return localRepository.peek();
    }

    /**
     * Gets the fallback {@link Registry}
     */
    public Registry getFallbackRegistry() {
        return fallbackRegistry;
    }

    /**
     * To use a custom {@link Registry} as fallback.
     */
    public void setFallbackRegistry(Registry fallbackRegistry) {
        this.fallbackRegistry = fallbackRegistry;
    }

    /**
     * Gets the supplier {@link Registry}
     */
    public Registry getSupplierRegistry() {
        return supplierRegistry;
    }

    /**
     * To use a custom {@link Registry} for suppliers.
     */
    public void setSupplierRegistry(Registry supplierRegistry) {
        this.supplierRegistry = supplierRegistry;
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    /**
     * Gets the bean repositories.
     *
     * @return the bean repositories, or <tt>null</tt> if none are in use.
     */
    public List<BeanRepository> getRepositories() {
        if (repositories == null) {
            return null;
        } else {
            return Collections.unmodifiableList(repositories);
        }
    }

    /**
     * Adds a custom {@link BeanRepository}.
     */
    public void addBeanRepository(BeanRepository repository) {
        if (repository == null) {
            repositories = new ArrayList<>();
        }
        repositories.add(repository);
    }

    @Override
    public void bind(String id, Class<?> type, Object bean) throws RuntimeZwangineException {
        bind(id, type, bean, null, null);
    }

    @Override
    public void bind(String id, Class<?> type, Object bean, String initMethod, String destroyMethod)
            throws RuntimeZwangineException {
        if (bean != null) {
            // automatic inject zwangine context in bean if its aware
            ZwangineContextAware.trySetZwangineContext(bean, zwangineContext);
            if (isNotEmpty(initMethod)) {
                try {
                    org.zenithblox.support.ObjectHelper.invokeMethodSafe(initMethod, bean);
                } catch (Exception e) {
                    throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
                }
            }
            fallbackRegistry.bind(id, type, bean);
            if (isNotEmpty(destroyMethod)) {
                beansToDestroy.put(id, new KeyValueHolder<>(bean, destroyMethod));
            }
        }
    }

    @Override
    public void bind(String id, Class<?> type, Supplier<Object> bean) throws RuntimeZwangineException {
        if (bean != null) {
            // wrap in cached supplier (memorize)
            supplierRegistry.bind(id, type, Suppliers.memorize(bean));
        }
    }

    @Override
    public void bindAsPrototype(String id, Class<?> type, Supplier<Object> bean) throws RuntimeZwangineException {
        if (bean != null) {
            supplierRegistry.bind(id, type, bean);
        }
    }

    @Override
    public void unbind(String id) {
        supplierRegistry.unbind(id);
        fallbackRegistry.unbind(id);
        // destroy on unbind
        destroyBean(id, true);
    }

    protected void destroyBean(String name, boolean remove) {
        var holder = remove ? beansToDestroy.remove(name) : beansToDestroy.get(name);
        if (holder != null) {
            String destroyMethod = holder.getValue();
            Object target = holder.getKey();
            try {
                org.zenithblox.support.ObjectHelper.invokeMethodSafe(destroyMethod, target);
            } catch (Exception e) {
                LOG.warn("Error invoking destroy method: {} on bean: {} due to: {}. This exception is ignored.",
                        destroyMethod, target, e.getMessage(), e);
            }
        }
    }

    @Override
    public Object lookupByName(String name) {
        Object answer;
        try {
            // Must avoid attempting placeholder resolution when looking up
            // the properties component or else we end up in an infinite loop.
            if (zwangineContext != null && !name.equals("properties")) {
                name = zwangineContext.resolvePropertyPlaceholders(name);
            }
        } catch (Exception e) {
            throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
        }

        // local repository takes precedence
        BeanRepository local = getLocalBeanRepository();
        if (local != null) {
            answer = local.lookupByName(name);
            if (answer != null) {
                return unwrap(answer);
            }
        }

        if (repositories != null) {
            for (BeanRepository r : repositories) {
                answer = r.lookupByName(name);
                if (answer != null) {
                    return unwrap(answer);
                }
            }
        }
        answer = supplierRegistry.lookupByName(name);
        if (answer == null) {
            answer = fallbackRegistry.lookupByName(name);
        }
        if (answer != null) {
            answer = unwrap(answer);
        }
        return answer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T lookupByNameAndType(String name, Class<T> type) {
        T answer;
        try {
            // Must avoid attempting placeholder resolution when looking up
            // the properties component or else we end up in an infinite loop.
            if (zwangineContext != null && !name.equals("properties")) {
                name = zwangineContext.resolvePropertyPlaceholders(name);
            }
        } catch (Exception e) {
            throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
        }

        // local repository takes precedence
        BeanRepository local = getLocalBeanRepository();
        if (local != null) {
            answer = local.lookupByNameAndType(name, type);
            if (answer != null) {
                return (T) unwrap(answer);
            }
        }
        if (repositories != null) {
            for (BeanRepository r : repositories) {
                answer = r.lookupByNameAndType(name, type);
                if (answer != null) {
                    return (T) unwrap(answer);
                }
            }
        }

        answer = supplierRegistry.lookupByNameAndType(name, type);
        if (answer == null) {
            answer = fallbackRegistry.lookupByNameAndType(name, type);
        }
        if (answer != null) {
            answer = (T) unwrap(answer);
        }
        return answer;
    }

    @Override
    public <T> Map<String, T> findByTypeWithName(Class<T> type) {
        Map<String, T> answer = new LinkedHashMap<>();

        // local repository takes precedence
        BeanRepository local = getLocalBeanRepository();
        if (local != null) {
            Map<String, T> found = local.findByTypeWithName(type);
            if (found != null && !found.isEmpty()) {
                answer.putAll(found);
            }
        }

        if (repositories != null) {
            for (BeanRepository r : repositories) {
                Map<String, T> found = r.findByTypeWithName(type);
                if (found != null && !found.isEmpty()) {
                    answer.putAll(found);
                }
            }
        }

        Map<String, T> found = supplierRegistry.findByTypeWithName(type);
        if (found != null && !found.isEmpty()) {
            answer.putAll(found);
        }
        found = fallbackRegistry.findByTypeWithName(type);
        if (found != null && !found.isEmpty()) {
            answer.putAll(found);
        }

        return answer;
    }

    @Override
    public <T> Set<T> findByType(Class<T> type) {
        Set<T> answer = new LinkedHashSet<>();

        // local repository takes precedence
        BeanRepository local = getLocalBeanRepository();
        if (local != null) {
            Set<T> found = local.findByType(type);
            if (found != null && !found.isEmpty()) {
                answer.addAll(found);
            }
        }

        if (repositories != null) {
            for (BeanRepository r : repositories) {
                Set<T> found = r.findByType(type);
                if (found != null && !found.isEmpty()) {
                    answer.addAll(found);
                }
            }
        }

        Set<T> found = supplierRegistry.findByType(type);
        if (found != null && !found.isEmpty()) {
            answer.addAll(found);
        }
        found = fallbackRegistry.findByType(type);
        if (found != null && !found.isEmpty()) {
            answer.addAll(found);
        }

        return answer;
    }

    @Override
    public <T> T findSingleByType(Class<T> type) {
        T found = null;

        // local repository takes precedence
        BeanRepository local = getLocalBeanRepository();
        if (local != null) {
            found = local.findSingleByType(type);
        }

        if (found == null && repositories != null) {
            for (BeanRepository r : repositories) {
                found = r.findSingleByType(type);
                if (found != null) {
                    break;
                }
            }
        }

        if (found == null) {
            found = supplierRegistry.findSingleByType(type);
        }
        if (found == null) {
            found = fallbackRegistry.findSingleByType(type);
        }

        return found;
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        if (supplierRegistry instanceof Closeable closeable) {
            IOHelper.close(closeable);
        }
        if (fallbackRegistry instanceof Closeable closeable) {
            IOHelper.close(closeable);
        }
        ServiceHelper.stopAndShutdownServices(supplierRegistry, fallbackRegistry);
        // destroy beans on shutdown
        for (String name : beansToDestroy.keySet()) {
            destroyBean(name, false);
        }
        beansToDestroy.clear();
    }
}
