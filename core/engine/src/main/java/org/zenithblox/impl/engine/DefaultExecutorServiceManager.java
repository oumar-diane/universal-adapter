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
import org.zenithblox.NamedNode;
import org.zenithblox.spi.IdAware;
import org.zenithblox.spi.NodeIdFactory;
import org.zenithblox.spi.ThreadPoolProfile;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Default {@link org.zenithblox.spi.ExecutorServiceManager}.
 */
public class DefaultExecutorServiceManager extends BaseExecutorServiceManager {

    public DefaultExecutorServiceManager(ZwangineContext zwangineContext) {
        super(zwangineContext);
    }

    @Override
    public ExecutorService newThreadPool(Object source, String name, ThreadPoolProfile profile) {
        return super.newThreadPool(forceId(source), name, profile);
    }

    @Override
    public ExecutorService newCachedThreadPool(Object source, String name) {
        return super.newCachedThreadPool(forceId(source), name);
    }

    @Override
    public ScheduledExecutorService newScheduledThreadPool(Object source, String name, ThreadPoolProfile profile) {
        return super.newScheduledThreadPool(forceId(source), name, profile);
    }

    protected Object forceId(Object source) {
        if (source instanceof NamedNode node && source instanceof IdAware idAware) {
            NodeIdFactory factory = getZwangineContext().getZwangineContextExtension().getContextPlugin(NodeIdFactory.class);
            if (node.getId() == null) {
                String id = factory.createId(node);
                // we auto generated an id to be assigned
                idAware.setGeneratedId(id);
            }
        }
        return source;
    }

}
