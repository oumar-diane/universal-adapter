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
package org.zenithblox.processor;

import org.zenithblox.ZwangineContext;
import org.zenithblox.spi.FactoryFinder;
import org.zenithblox.spi.SendDynamicAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendDynamicAwareResolver {

    public static final String RESOURCE_PATH = "META-INF/services/org/zentihblox/zwangine/send-dynamic/";

    private static final Logger LOG = LoggerFactory.getLogger(SendDynamicAwareResolver.class);

    private FactoryFinder factoryFinder;

    public SendDynamicAware resolve(ZwangineContext context, String scheme) {

        // use factory finder to find a custom implementations
        Class<?> type = null;
        try {
            type = findFactory(scheme, context);
        } catch (Exception e) {
            // ignore
        }

        if (type != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Found SendDynamicAware: {} via: {}{}", type.getName(), factoryFinder.getResourcePath(), scheme);
            }
            if (SendDynamicAware.class.isAssignableFrom(type)) {
                SendDynamicAware answer = (SendDynamicAware) context.getInjector().newInstance(type, false);
                answer.setScheme(scheme);
                answer.setZwangineContext(context);
                return answer;
            } else {
                throw new IllegalArgumentException("Type is not a SendDynamicAware implementation. Found: " + type.getName());
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

}
