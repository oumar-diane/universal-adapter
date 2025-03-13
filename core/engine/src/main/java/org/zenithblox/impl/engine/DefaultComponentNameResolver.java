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

import org.zenithblox.ZwangineContext;
import org.zenithblox.RuntimeZwangineException;
import org.zenithblox.spi.ComponentNameResolver;
import org.zenithblox.spi.Resource;
import org.zenithblox.support.PluginHelper;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class DefaultComponentNameResolver implements ComponentNameResolver {

    public static final String RESOURCE_PATH = "META-INF/services.org.zenithblox/component/*";

    @Override
    public Set<String> resolveNames(ZwangineContext zwangineContext) {
        try {
            return PluginHelper.getPackageScanResourceResolver(zwangineContext)
                    .findResources(RESOURCE_PATH)
                    .stream()
                    .map(Resource::getLocation)
                    // remove leading path to only keep name
                    // searching for last separator: Jar path separator (/), Unix path (/) and Windows path separator (\)
                    .map(l -> l.substring(Math.max(l.lastIndexOf('/'), l.lastIndexOf('\\')) + 1))
                    .collect(Collectors.toCollection(TreeSet::new));
        } catch (Exception e) {
            throw new RuntimeZwangineException(e);
        }
    }
}
