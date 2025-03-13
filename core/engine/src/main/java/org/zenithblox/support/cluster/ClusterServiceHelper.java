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

import org.zenithblox.ZwangineContext;
import org.zenithblox.cluster.ZwangineClusterService;
import org.zenithblox.util.ObjectHelper;

import java.util.Optional;
import java.util.Set;

public final class ClusterServiceHelper {
    private ClusterServiceHelper() {
    }

    public static Optional<ZwangineClusterService> lookupService(ZwangineContext context) {
        return lookupService(context, ClusterServiceSelectors.DEFAULT_SELECTOR);
    }

    public static Optional<ZwangineClusterService> lookupService(ZwangineContext context, ZwangineClusterService.Selector selector) {
        ObjectHelper.notNull(context, "Zwangine Context");
        ObjectHelper.notNull(selector, "ClusterService selector");

        Set<ZwangineClusterService> services = context.hasServices(ZwangineClusterService.class);

        if (ObjectHelper.isNotEmpty(services)) {
            return selector.select(services);
        }

        return Optional.empty();
    }

    public static ZwangineClusterService mandatoryLookupService(ZwangineContext context) {
        return lookupService(context).orElseThrow(() -> new IllegalStateException("ZwangineCluster service not found"));
    }

    public static ZwangineClusterService mandatoryLookupService(ZwangineContext context, ZwangineClusterService.Selector selector) {
        return lookupService(context, selector).orElseThrow(() -> new IllegalStateException("ZwangineCluster service not found"));
    }
}
