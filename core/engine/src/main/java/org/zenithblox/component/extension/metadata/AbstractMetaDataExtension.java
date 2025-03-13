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
package org.zenithblox.component.extension.metadata;

import org.zenithblox.ZwangineContext;
import org.zenithblox.ZwangineContextAware;
import org.zenithblox.Component;
import org.zenithblox.ComponentAware;
import org.zenithblox.component.extension.MetaDataExtension;

public abstract class AbstractMetaDataExtension implements MetaDataExtension, ComponentAware, ZwangineContextAware {
    private ZwangineContext zwangineContext;
    private Component component;

    protected AbstractMetaDataExtension() {
        this(null, null);
    }

    protected AbstractMetaDataExtension(Component component) {
        this(component, component.getZwangineContext());
    }

    protected AbstractMetaDataExtension(ZwangineContext zwangineContext) {
        this(null, zwangineContext);
    }

    protected AbstractMetaDataExtension(Component component, ZwangineContext zwangineContext) {
        this.component = component;
        this.zwangineContext = zwangineContext;
    }

    @Override
    public void setComponent(Component component) {
        this.component = component;
    }

    @Override
    public Component getComponent() {
        return component;
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }
}
