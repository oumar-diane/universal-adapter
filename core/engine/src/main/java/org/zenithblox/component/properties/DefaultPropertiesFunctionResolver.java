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
package org.zenithblox.component.properties;

import org.zenithblox.ZwangineContext;
import org.zenithblox.ZwangineContextAware;
import org.zenithblox.NonManagedService;
import org.zenithblox.StaticService;
import org.zenithblox.spi.FactoryFinder;
import org.zenithblox.spi.PropertiesFunction;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.support.service.ServiceSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Default {@link PropertiesFunctionResolver}.
 */
public class DefaultPropertiesFunctionResolver extends ServiceSupport
        implements PropertiesFunctionResolver, ZwangineContextAware, NonManagedService, StaticService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultPropertiesFunctionResolver.class);

    private ZwangineContext zwangineContext;
    private FactoryFinder factoryFinder;
    private final Map<String, PropertiesFunction> functions = new LinkedHashMap<>();

    public DefaultPropertiesFunctionResolver() {
        // include out of the box functions
        addPropertiesFunction(new EnvPropertiesFunction());
        addPropertiesFunction(new SysPropertiesFunction());
        addPropertiesFunction(new ServicePropertiesFunction());
        addPropertiesFunction(new ServiceHostPropertiesFunction());
        addPropertiesFunction(new ServicePortPropertiesFunction());
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
    public void addPropertiesFunction(PropertiesFunction function) {
        this.functions.put(function.getName(), function);
    }

    @Override
    public Map<String, PropertiesFunction> getFunctions() {
        return functions;
    }

    @Override
    public boolean hasFunction(String name) {
        return functions.containsKey(name);
    }

    @Override
    public PropertiesFunction resolvePropertiesFunction(String name) {
        PropertiesFunction answer = functions.get(name);
        if (answer == null) {
            answer = resolve(zwangineContext, name);
            if (answer != null) {
                functions.put(name, answer);
            }
        }
        return answer;
    }

    private PropertiesFunction resolve(ZwangineContext context, String name) {
        // use factory finder to find a custom implementations
        Class<?> type = null;
        try {
            type = findFactory(name, context);
        } catch (Exception e) {
            // ignore
        }

        if (type != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Found PropertiesFunction: {} via: {}{}", type.getName(), factoryFinder.getResourcePath(), name);
            }
            if (PropertiesFunction.class.isAssignableFrom(type)) {
                PropertiesFunction answer = (PropertiesFunction) context.getInjector().newInstance(type, false);
                ZwangineContextAware.trySetZwangineContext(answer, zwangineContext);
                ServiceHelper.startService(answer);
                return answer;
            } else {
                throw new IllegalArgumentException("Type is not a PropertiesFunction implementation. Found: " + type.getName());
            }
        }

        return null;
    }

    private Class<?> findFactory(String name, ZwangineContext context) {
        if (factoryFinder == null) {
            factoryFinder = context.getZwangineContextExtension().getFactoryFinder(RESOURCE_PATH);
        }
        return factoryFinder.findClass(name).orElse(null);
    }

    @Override
    protected void doInit() throws Exception {
        ServiceHelper.initService(functions.values());
    }

    @Override
    protected void doStart() throws Exception {
        ServiceHelper.startService(functions.values());
    }

    @Override
    protected void doStop() throws Exception {
        ServiceHelper.stopService(functions.values());
    }

    @Override
    protected void doShutdown() throws Exception {
        ServiceHelper.stopAndShutdownService(functions.values());
    }
}
