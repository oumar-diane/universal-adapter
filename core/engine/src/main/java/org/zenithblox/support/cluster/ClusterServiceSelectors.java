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
package org.zenithblox.support.cluster;

import org.zenithblox.cluster.ZwangineClusterService;
import org.zenithblox.cluster.ZwangineClusterService.Selector;
import org.zenithblox.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public final class ClusterServiceSelectors {
    public static final Selector DEFAULT_SELECTOR = new SelectSingle();
    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterServiceSelectors.class);

    private ClusterServiceSelectors() {
    }

    public static final class SelectSingle implements Selector {
        @Override
        public Optional<ZwangineClusterService> select(Collection<ZwangineClusterService> services) {
            if (services != null && services.size() == 1) {
                return Optional.of(services.iterator().next());
            } else {
                LOGGER.warn("Multiple ZwangineClusterService instances available (items={})", services);
            }

            return Optional.empty();
        }
    }

    public static final class SelectFirst implements ZwangineClusterService.Selector {
        @Override
        public Optional<ZwangineClusterService> select(Collection<ZwangineClusterService> services) {
            return ObjectHelper.isNotEmpty(services)
                    ? Optional.of(services.iterator().next())
                    : Optional.empty();
        }
    }

    public static final class SelectByOrder implements ZwangineClusterService.Selector {
        @Override
        public Optional<ZwangineClusterService> select(Collection<ZwangineClusterService> services) {
            Optional<Map.Entry<Integer, List<ZwangineClusterService>>> highPriorityServices = services.stream()
                    .collect(Collectors.groupingBy(ZwangineClusterService::getOrder))
                    .entrySet().stream()
                    .min(Comparator.comparingInt(Map.Entry::getKey));

            if (highPriorityServices.isPresent()) {
                if (highPriorityServices.get().getValue().size() == 1) {
                    return Optional.of(highPriorityServices.get().getValue().iterator().next());
                } else {
                    LOGGER.warn("Multiple ZwangineClusterService instances available for highest priority (order={}, items={})",
                            highPriorityServices.get().getKey(),
                            highPriorityServices.get().getValue());
                }
            }

            return Optional.empty();
        }
    }

    public static final class SelectByType implements ZwangineClusterService.Selector {
        private final Class<? extends ZwangineClusterService> type;

        public SelectByType(Class<? extends ZwangineClusterService> type) {
            this.type = type;
        }

        @Override
        public Optional<ZwangineClusterService> select(Collection<ZwangineClusterService> services) {
            for (ZwangineClusterService service : services) {
                if (type.isInstance(service)) {
                    return Optional.of(service);
                }
            }

            return Optional.empty();
        }
    }

    public static final class SelectByAttribute implements ZwangineClusterService.Selector {
        private final String key;
        private final Object value;

        public SelectByAttribute(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public Optional<ZwangineClusterService> select(Collection<ZwangineClusterService> services) {
            for (ZwangineClusterService service : services) {
                Map<String, Object> attributes = service.getAttributes();

                if (ObjectHelper.equal(attributes.get(key), value)) {
                    return Optional.of(service);
                }
            }

            return Optional.empty();
        }
    }

    // **********************************
    // Helpers
    // **********************************

    public static ZwangineClusterService.Selector defaultSelector() {
        return DEFAULT_SELECTOR;
    }

    public static ZwangineClusterService.Selector single() {
        return new SelectSingle();
    }

    public static ZwangineClusterService.Selector first() {
        return new SelectFirst();
    }

    public static ZwangineClusterService.Selector order() {
        return new SelectByOrder();
    }

    public static ZwangineClusterService.Selector type(Class<? extends ZwangineClusterService> type) {
        return new SelectByType(type);
    }

    public static ZwangineClusterService.Selector attribute(String key, Object value) {
        return new SelectByAttribute(key, value);
    }
}
