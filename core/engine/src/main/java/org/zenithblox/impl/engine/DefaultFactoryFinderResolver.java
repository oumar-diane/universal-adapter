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

import org.zenithblox.spi.ClassResolver;
import org.zenithblox.spi.FactoryFinder;
import org.zenithblox.spi.FactoryFinderResolver;

/**
 * Default factory finder.
 */
public class DefaultFactoryFinderResolver implements FactoryFinderResolver {

    @Override
    public FactoryFinder resolveFactoryFinder(ClassResolver classResolver, String resourcePath) {
        return new DefaultFactoryFinder(classResolver, resourcePath);
    }

    @Override
    public FactoryFinder resolveBootstrapFactoryFinder(ClassResolver classResolver, String resourcePath) {
        return new BootstrapFactoryFinder(classResolver, resourcePath);
    }
}
