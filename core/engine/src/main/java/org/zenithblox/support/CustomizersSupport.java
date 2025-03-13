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
import org.zenithblox.Component;
import org.zenithblox.spi.*;

public final class CustomizersSupport {
    private CustomizersSupport() {
    }

    /**
     * Determine the value of the "enabled" flag for a hierarchy of properties.
     *
     * @param  zwangineContext the {@link ZwangineContext}
     * @param  prefixes     an ordered list of prefixed (less restrictive to more restrictive)
     * @return              the value of the key `enabled` for most restrictive prefix
     */
    public static boolean isEnabled(ZwangineContext zwangineContext, String... prefixes) {
        boolean answer = true;

        // Loop over all the prefixes to find out the value of the key `enabled`
        // for the most restrictive prefix.
        for (String prefix : prefixes) {
            String property = prefix.endsWith(".") ? prefix + "enabled" : prefix + ".enabled";

            // evaluate the value of the current prefix using the parent one as
            // default value so if the `enabled` property is not set, the parent
            // one is used.
            answer = zwangineContext.getPropertiesComponent()
                    .resolveProperty(property)
                    .map(Boolean::valueOf)
                    .orElse(answer);
        }

        return answer;
    }

    /**
     * Base class for policies
     */
    private static class ZwangineContextAwarePolicy implements ZwangineContextAware {
        private ZwangineContext zwangineContext;

        @Override
        public ZwangineContext getZwangineContext() {
            return this.zwangineContext;
        }

        @Override
        public void setZwangineContext(ZwangineContext zwangineContext) {
            this.zwangineContext = zwangineContext;
        }
    }

    /**
     * A {@link ComponentCustomizer.Policy} that uses a hierarchical lists of properties to determine if customization
     * is enabled for the given {@link org.zenithblox.Component}.
     */
    public static final class ComponentCustomizationEnabledPolicy
            extends ZwangineContextAwarePolicy
            implements ComponentCustomizer.Policy {

        @Override
        public boolean test(String name, Component target) {
            return isEnabled(
                    getZwangineContext(),
                    "zwangine.customizer",
                    "zwangine.customizer.component",
                    "zwangine.customizer.component." + name);
        }
    }

    /**
     * A {@link DataFormatCustomizer.Policy} that uses a hierarchical lists of properties to determine if customization
     * is enabled for the given {@link org.zenithblox.spi.DataFormat}.
     */
    public static final class DataFormatCustomizationEnabledPolicy
            extends ZwangineContextAwarePolicy
            implements DataFormatCustomizer.Policy {

        @Override
        public boolean test(String name, DataFormat target) {
            return isEnabled(
                    getZwangineContext(),
                    "zwangine.customizer",
                    "zwangine.customizer.dataformat",
                    "zwangine.customizer.dataformat." + name);
        }
    }

    /**
     * A {@link LanguageCustomizer.Policy} that uses a hierarchical lists of properties to determine if customization is
     * enabled for the given {@link org.zenithblox.spi.Language}.
     */
    public static final class LanguageCustomizationEnabledPolicy
            extends ZwangineContextAwarePolicy
            implements LanguageCustomizer.Policy {

        @Override
        public boolean test(String name, Language target) {
            return isEnabled(
                    getZwangineContext(),
                    "zwangine.customizer",
                    "zwangine.customizer.language",
                    "zwangine.customizer.language." + name);
        }
    }
}
