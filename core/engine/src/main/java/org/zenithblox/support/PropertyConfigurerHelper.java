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
import org.zenithblox.spi.PropertyConfigurer;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.util.ObjectHelper;

/**
 * Helper class for dealing with configurers.
 *
 * @see org.zenithblox.spi.PropertyConfigurer
 * @see org.zenithblox.spi.PropertyConfigurerGetter
 */
public final class PropertyConfigurerHelper {

    private PropertyConfigurerHelper() {
    }

    /**
     * Resolves the given configurer.
     *
     * @param  context the zwangine context
     * @param  target  the target object for which we need a {@link org.zenithblox.spi.PropertyConfigurer}
     * @return         the resolved configurer, or <tt>null</tt> if no configurer could be found
     */
    public static PropertyConfigurer resolvePropertyConfigurer(ZwangineContext context, Object target) {
        ObjectHelper.notNull(target, "target");
        ObjectHelper.notNull(context, "context");

        PropertyConfigurer configurer = null;

        if (target instanceof Component component) {
            // the component needs to be initialized to have the configurer ready
            ServiceHelper.initService(target);
            configurer = component.getComponentPropertyConfigurer();
        }

        if (configurer == null) {
            String name = target.getClass().getName();
            // see if there is a configurer for it
            configurer = PluginHelper.getConfigurerResolver(context)
                    .resolvePropertyConfigurer(name, context);
        }

        return configurer;
    }

    /**
     * Resolves the given configurer.
     *
     * @param  context    the zwangine context
     * @param  targetType the target object type for which we need a {@link org.zenithblox.spi.PropertyConfigurer}
     * @return            the resolved configurer, or <tt>null</tt> if no configurer could be found
     */
    public static PropertyConfigurer resolvePropertyConfigurer(ZwangineContext context, Class<?> targetType) {
        ObjectHelper.notNull(targetType, "targetType");
        ObjectHelper.notNull(context, "context");

        String name = targetType.getName();
        // see if there is a configurer for it
        return PluginHelper.getConfigurerResolver(context)
                .resolvePropertyConfigurer(name, context);
    }

    /**
     * Resolves the given configurer.
     *
     * @param  context the zwangine context
     * @param  target  the target object for which we need a {@link org.zenithblox.spi.PropertyConfigurer}
     * @param  type    the specific type of {@link org.zenithblox.spi.PropertyConfigurer}
     * @return         the resolved configurer, or <tt>null</tt> if no configurer could be found
     */
    public static <T> T resolvePropertyConfigurer(ZwangineContext context, Object target, Class<T> type) {
        ObjectHelper.notNull(target, "target");
        ObjectHelper.notNull(context, "context");

        PropertyConfigurer configurer = resolvePropertyConfigurer(context, target);
        if (type.isInstance(configurer)) {
            return type.cast(configurer);
        }

        return null;
    }
}
