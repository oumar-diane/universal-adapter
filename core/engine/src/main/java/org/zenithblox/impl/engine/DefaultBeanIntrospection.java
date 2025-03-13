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
package org.zenithblox.impl.engine;

import org.zenithblox.*;
import org.zenithblox.spi.BeanIntrospection;
import org.zenithblox.spi.ZwangineLogger;
import org.zenithblox.support.service.ServiceSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

public class DefaultBeanIntrospection extends ServiceSupport implements BeanIntrospection, ZwangineContextAware, StartupListener {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultBeanIntrospection.class);
    private static final Pattern SECRETS = Pattern.compile(".*(passphrase|password|secretKey).*", Pattern.CASE_INSENSITIVE);

    private ZwangineContext zwangineContext;
    private volatile boolean preStartDone;
    private final List<String> preStartLogs = new ArrayList<>();
    private final AtomicLong invoked = new AtomicLong();
    private volatile boolean extendedStatistics;
    private LoggingLevel loggingLevel = LoggingLevel.TRACE;
    private ZwangineLogger logger = new ZwangineLogger(LOG, loggingLevel);

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    @Override
    public long getInvokedCounter() {
        return invoked.get();
    }

    @Override
    public void resetCounters() {
        invoked.set(0);
    }

    public boolean isExtendedStatistics() {
        return extendedStatistics;
    }

    public void setExtendedStatistics(boolean extendedStatistics) {
        this.extendedStatistics = extendedStatistics;
    }

    public LoggingLevel getLoggingLevel() {
        return loggingLevel;
    }

    public void setLoggingLevel(LoggingLevel loggingLevel) {
        this.loggingLevel = loggingLevel;
        // recreate logger as level is changed
        this.logger = new ZwangineLogger(LOG, loggingLevel);
    }

    private void log(String method, Object target, Object... args) {
        Object obj = "null";
        if (args != null && args.length > 0) {
            obj = Arrays.asList(args);
        }

        if (target != null) {
            // use Object.toString as target logging
            target = target.getClass().getName() + "@" + Integer.toHexString(target.hashCode());
        }

        String line;
        if (target == null) {
            line = "Invoked: " + invoked.get() + " times (overall) [Method: " + method + "]";
        } else if (args == null) {
            line = "Invoked: " + invoked.get() + " times (overall) [Method: " + method + ", Target: " + target + "]";
        } else {
            line = "Invoked: " + invoked.get() + " times (overall) [Method: " + method + ", Target: " + target + ", Arguments: "
                   + obj + "]";
        }

        if (preStartDone) {
            logger.log(line);
        } else {
            // remember log lines before we are starting
            preStartLogs.add(line);
        }
    }

    @Override
    public ClassInfo cacheClass(Class<?> clazz) {
        invoked.incrementAndGet();
        if (!preStartDone || logger.shouldLog()) {
            log("cacheClass", clazz);
        }
        return IntrospectionSupport.cacheClass(clazz);
    }

    @Override
    public void clearCache() {
        if (invoked.get() > 0) {
            invoked.incrementAndGet();
            if (!preStartDone || logger.shouldLog()) {
                log("clearCache", null);
            }
            IntrospectionSupport.clearCache();
        }
    }

    @Override
    public long getCachedClassesCounter() {
        if (invoked.get() > 0) {
            return IntrospectionSupport.getCacheCounter();
        } else {
            return 0;
        }
    }

    @Override
    public boolean getProperties(Object target, Map<String, Object> properties, String optionPrefix) {
        invoked.incrementAndGet();
        if (!preStartDone || logger.shouldLog()) {
            log("getProperties", target);
        }
        return IntrospectionSupport.getProperties(target, properties, optionPrefix);
    }

    @Override
    public boolean getProperties(Object target, Map<String, Object> properties, String optionPrefix, boolean includeNull) {
        invoked.incrementAndGet();
        if (!preStartDone || logger.shouldLog()) {
            log("getProperties", target);
        }
        return IntrospectionSupport.getProperties(target, properties, optionPrefix, includeNull);
    }

    @Override
    public Object getOrElseProperty(Object target, String propertyName, Object defaultValue, boolean ignoreCase) {
        invoked.incrementAndGet();
        if (!preStartDone || logger.shouldLog()) {
            log("getOrElseProperty", target, propertyName);
        }
        return IntrospectionSupport.getOrElseProperty(target, propertyName, defaultValue, ignoreCase);
    }

    @Override
    public Method getPropertyGetter(Class<?> type, String propertyName, boolean ignoreCase) throws NoSuchMethodException {
        invoked.incrementAndGet();
        if (!preStartDone || logger.shouldLog()) {
            log("getPropertyGetter", type, propertyName);
        }
        return IntrospectionSupport.getPropertyGetter(type, propertyName, ignoreCase);
    }

    @Override
    public Method getPropertySetter(Class<?> type, String propertyName) throws NoSuchMethodException {
        invoked.incrementAndGet();
        if (!preStartDone || logger.shouldLog()) {
            log("getPropertySetter", type, propertyName);
        }
        return IntrospectionSupport.getPropertySetter(type, propertyName);
    }

    @Override
    public boolean setProperty(
            ZwangineContext context, TypeConverter typeConverter, Object target, String name, Object value, String refName,
            boolean allowBuilderPattern, boolean allowPrivateSetter, boolean ignoreCase)
            throws Exception {
        invoked.incrementAndGet();
        if (!preStartDone || logger.shouldLog()) {
            Object text = value;
            if (SECRETS.matcher(name).find()) {
                text = "xxxxxx";
            }
            log("setProperty", target, name, text);
        }
        return IntrospectionSupport.setProperty(context, typeConverter, target, name, value, refName, allowBuilderPattern,
                allowPrivateSetter, ignoreCase);
    }

    @Override
    public boolean setProperty(ZwangineContext context, Object target, String name, Object value) throws Exception {
        invoked.incrementAndGet();
        if (!preStartDone || logger.shouldLog()) {
            Object text = value;
            if (SECRETS.matcher(name).find()) {
                text = "xxxxxx";
            }
            log("setProperty", target, name, text);
        }
        return IntrospectionSupport.setProperty(context, target, name, value);
    }

    @Override
    public Set<Method> findSetterMethods(
            Class<?> clazz, String name, boolean allowBuilderPattern, boolean allowPrivateSetter, boolean ignoreCase) {
        invoked.incrementAndGet();
        if (!preStartDone || logger.shouldLog()) {
            log("findSetterMethods", clazz);
        }
        return IntrospectionSupport.findSetterMethods(clazz, name, allowBuilderPattern, allowPrivateSetter, ignoreCase);
    }

    @Override
    public void afterPropertiesConfigured(ZwangineContext zwangineContext) {
        // log any pre starting logs so we can see them all
        preStartLogs.forEach(logger::log);
        preStartLogs.clear();
        preStartDone = true;
    }

    @Override
    protected void doInit() throws Exception {
        if (zwangineContext != null) {
            zwangineContext.addStartupListener(this);
        }
    }

    @Override
    protected void doStop() throws Exception {
        if (invoked.get() > 0) {
            IntrospectionSupport.stop();
        }
        if (extendedStatistics) {
            LOG.info("Stopping BeanIntrospection which was invoked: {} times", invoked.get());
        } else {
            LOG.debug("Stopping BeanIntrospection which was invoked: {} times", invoked.get());
        }
    }

    @Override
    public void onZwangineContextStarted(ZwangineContext context, boolean alreadyStarted) throws Exception {
        // ensure after properties is called
        afterPropertiesConfigured(zwangineContext);
    }
}
