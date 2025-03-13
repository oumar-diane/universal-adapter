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
import org.zenithblox.WorkflowsBuilder;
import org.zenithblox.VetoZwangineContextStartException;
import org.zenithblox.spi.*;
import org.zenithblox.support.LifecycleStrategySupport;

/**
 * {@link LifecycleStrategy} for invoking callbacks {@link OnZwangineContextInitialized}, {@link OnZwangineContextStarted}, and
 * {@link OnZwangineContextStopped} which has been registered in the Zwangine {@link org.zenithblox.spi.Registry}.
 */
class OnZwangineContextLifecycleStrategy extends LifecycleStrategySupport {

    @Override
    public void onContextInitializing(ZwangineContext context) throws VetoZwangineContextStartException {
        for (OnZwangineContextInitializing handler : context.getRegistry().findByType(OnZwangineContextInitializing.class)) {
            // WorkflowsBuilder should register them-self to the zwangine context
            // to avoid invoking them multiple times if workflows are discovered
            // from the registry (i.e. zwangine-main)
            if (!(handler instanceof WorkflowsBuilder)) {
                handler.onContextInitializing(context);
            }
        }
    }

    @Override
    public void onContextInitialized(ZwangineContext context) throws VetoZwangineContextStartException {
        for (OnZwangineContextInitialized handler : context.getRegistry().findByType(OnZwangineContextInitialized.class)) {
            // WorkflowsBuilder should register them-self to the zwangine context
            // to avoid invoking them multiple times if workflows are discovered
            // from the registry (i.e. zwangine-main)
            if (!(handler instanceof WorkflowsBuilder)) {
                handler.onContextInitialized(context);
            }
        }
    }

    @Override
    public void onContextStarting(ZwangineContext context) throws VetoZwangineContextStartException {
        for (OnZwangineContextStarting handler : context.getRegistry().findByType(OnZwangineContextStarting.class)) {
            // WorkflowsBuilder should register them-self to the zwangine context
            // to avoid invoking them multiple times if workflows are discovered
            // from the registry (i.e. zwangine-main)
            if (!(handler instanceof WorkflowsBuilder)) {
                handler.onContextStarting(context);
            }
        }
    }

    @Override
    public void onContextStarted(ZwangineContext context) {
        for (OnZwangineContextStarted handler : context.getRegistry().findByType(OnZwangineContextStarted.class)) {
            // WorkflowsBuilder should register them-self to the zwangine context
            // to avoid invoking them multiple times if workflows are discovered
            // from the registry (i.e. zwangine-main)
            if (!(handler instanceof WorkflowsBuilder)) {
                handler.onContextStarted(context);
            }
        }
    }

    @Override
    public void onContextStopping(ZwangineContext context) {
        for (OnZwangineContextStopping handler : context.getRegistry().findByType(OnZwangineContextStopping.class)) {
            // WorkflowsBuilder should register them-self to the zwangine context
            // to avoid invoking them multiple times if workflows are discovered
            // from the registry (i.e. zwangine-main)
            if (!(handler instanceof WorkflowsBuilder)) {
                handler.onContextStopping(context);
            }
        }
    }

    @Override
    public void onContextStopped(ZwangineContext context) {
        for (OnZwangineContextStopped handler : context.getRegistry().findByType(OnZwangineContextStopped.class)) {
            // WorkflowsBuilder should register them-self to the zwangine context
            // to avoid invoking them multiple times if workflows are discovered
            // from the registry (i.e. zwangine-main)
            if (!(handler instanceof WorkflowsBuilder)) {
                handler.onContextStopped(context);
            }
        }
    }

}
