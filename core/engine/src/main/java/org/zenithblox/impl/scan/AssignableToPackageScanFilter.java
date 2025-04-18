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
package org.zenithblox.impl.scan;

import org.zenithblox.spi.PackageScanFilter;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

/**
 * Package scan filter for testing if a given class is assignable to another class.
 */
public class AssignableToPackageScanFilter implements PackageScanFilter {
    private final Set<Class<?>> parents = new HashSet<>();
    private boolean includeAbstract;

    public AssignableToPackageScanFilter() {
    }

    public AssignableToPackageScanFilter(Class<?> parentType) {
        parents.add(parentType);
    }

    public AssignableToPackageScanFilter(Set<Class<?>> parents) {
        this.parents.addAll(parents);
    }

    public void addParentType(Class<?> parentType) {
        parents.add(parentType);
    }

    public boolean isIncludeAbstract() {
        return includeAbstract;
    }

    /**
     * Whether to include abstract classes.
     */
    public void setIncludeAbstract(boolean includeAbstract) {
        this.includeAbstract = includeAbstract;
    }

    @Override
    public boolean matches(Class<?> type) {
        if (!parents.isEmpty()) {
            for (Class<?> parent : parents) {
                if (parent.isAssignableFrom(type)) {
                    if (includeAbstract) {
                        return true;
                    } else {
                        // skip abstract classes
                        return !Modifier.isAbstract(type.getModifiers());
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(256);
        for (Class<?> parent : parents) {
            sb.append(parent.getSimpleName()).append(", ");
        }
        sb.setLength(!sb.isEmpty() ? sb.length() - 2 : 0);
        return "is assignable to any of " + sb;
    }
}
