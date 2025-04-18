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
package org.zenithblox.util.concurrent;

import java.util.function.Supplier;

/**
 * A {@link ThreadLocal} with an assigned name that makes introspection and debugging easier.
 */
public final class NamedThreadLocal<T> extends ThreadLocal<T> {

    private final String name;
    private final Supplier<T> supplier;

    public NamedThreadLocal(String name) {
        this(name, null);
    }

    public NamedThreadLocal(String name, Supplier<T> supplier) {
        this.name = name;
        this.supplier = supplier;
    }

    @Override
    protected T initialValue() {
        return supplier != null ? supplier.get() : null;
    }

    @Override
    public String toString() {
        return name;
    }

}
