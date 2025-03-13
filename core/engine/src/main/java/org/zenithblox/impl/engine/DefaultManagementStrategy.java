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
import org.zenithblox.ZwangineContextAware;
import org.zenithblox.NamedNode;
import org.zenithblox.impl.event.DefaultEventFactory;
import org.zenithblox.spi.*;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.support.service.ServiceSupport;
import org.zenithblox.util.ObjectHelper;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A default management strategy that does <b>not</b> manage.
 * <p/>
 * This is default only used if Zwangine detects that it cannot use the JMX capable
 * {@link org.zenithblox.management.JmxManagementStrategy} strategy. Then Zwangine will fallback to use this instead that
 * is basically a simple and <tt>noop</tt> strategy.
 * <p/>
 * This class can also be used to extend your custom management implement. In fact the JMX capable provided by Zwangine
 * extends this class as well.
 *
 * @see org.zenithblox.management.JmxManagementStrategy
 */
public class DefaultManagementStrategy extends ServiceSupport implements ManagementStrategy, ZwangineContextAware {

    private final List<EventNotifier> eventNotifiers = new CopyOnWriteArrayList<>();
    private final List<EventNotifier> startedEventNotifiers = new CopyOnWriteArrayList<>();
    private EventFactory eventFactory = new DefaultEventFactory();
    private ManagementObjectNameStrategy managementObjectNameStrategy;
    private ManagementObjectStrategy managementObjectStrategy;
    private ManagementAgent managementAgent;
    private ZwangineContext zwangineContext;

    public DefaultManagementStrategy() {
    }

    public DefaultManagementStrategy(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    public DefaultManagementStrategy(ZwangineContext zwangineContext, ManagementAgent managementAgent) {
        this.zwangineContext = zwangineContext;
        this.managementAgent = managementAgent;
    }

    @Override
    public List<EventNotifier> getEventNotifiers() {
        return eventNotifiers;
    }

    @Override
    public List<EventNotifier> getStartedEventNotifiers() {
        return startedEventNotifiers;
    }

    @Override
    public void addEventNotifier(EventNotifier eventNotifier) {
        this.eventNotifiers.add(eventNotifier);
        if (isStarted()) {
            // already started
            this.startedEventNotifiers.add(eventNotifier);
        }
        if (getZwangineContext() != null) {
            // inject zwangine context if needed
            ZwangineContextAware.trySetZwangineContext(eventNotifier, getZwangineContext());
            // okay we have an event notifier that accepts exchange events so its applicable
            if (!eventNotifier.isIgnoreExchangeEvents()) {
                getZwangineContext().getZwangineContextExtension().setEventNotificationApplicable(true);
            }
        }
    }

    @Override
    public boolean removeEventNotifier(EventNotifier eventNotifier) {
        startedEventNotifiers.remove(eventNotifier);
        return eventNotifiers.remove(eventNotifier);
    }

    @Override
    public EventFactory getEventFactory() {
        return eventFactory;
    }

    @Override
    public void setEventFactory(EventFactory eventFactory) {
        this.eventFactory = eventFactory;
    }

    @Override
    public ManagementObjectNameStrategy getManagementObjectNameStrategy() {
        return managementObjectNameStrategy;
    }

    @Override
    public void setManagementObjectNameStrategy(ManagementObjectNameStrategy managementObjectNameStrategy) {
        this.managementObjectNameStrategy = managementObjectNameStrategy;
    }

    @Override
    public ManagementObjectStrategy getManagementObjectStrategy() {
        return managementObjectStrategy;
    }

    @Override
    public void setManagementObjectStrategy(ManagementObjectStrategy managementObjectStrategy) {
        this.managementObjectStrategy = managementObjectStrategy;
    }

    @Override
    public ManagementAgent getManagementAgent() {
        return managementAgent;
    }

    @Override
    public void setManagementAgent(ManagementAgent managementAgent) {
        this.managementAgent = managementAgent;
    }

    @Override
    public boolean manageProcessor(NamedNode definition) {
        return false;
    }

    @Override
    public void manageObject(Object managedObject) throws Exception {
        // noop
    }

    @Override
    public void unmanageObject(Object managedObject) throws Exception {
        // noop
    }

    @Override
    public boolean isManaged(Object managedObject) {
        // noop
        return false;
    }

    @Override
    public boolean isManagedName(Object name) {
        // noop
        return false;
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    @Override
    public void notify(ZwangineEvent event) throws Exception {
        if (!eventNotifiers.isEmpty()) {
            for (EventNotifier notifier : eventNotifiers) {
                if (notifier.isEnabled(event)) {
                    notifier.notify(event);
                }
            }
        }
    }

    @Override
    protected void doInit() throws Exception {
        ObjectHelper.notNull(getZwangineContext(), "ZwangineContext", this);
        if (!getEventNotifiers().isEmpty()) {
            getZwangineContext().getZwangineContextExtension().setEventNotificationApplicable(true);
        }
        for (EventNotifier notifier : eventNotifiers) {
            // inject ZwangineContext if the service is aware
            ZwangineContextAware.trySetZwangineContext(notifier, zwangineContext);
        }
        ServiceHelper.initService(eventNotifiers, managementAgent);

        if (managementObjectStrategy == null) {
            managementObjectStrategy = createManagementObjectStrategy();
        }
        ZwangineContextAware.trySetZwangineContext(managementObjectStrategy, getZwangineContext());

        if (managementObjectNameStrategy == null) {
            managementObjectNameStrategy = createManagementObjectNameStrategy();
        }
        ZwangineContextAware.trySetZwangineContext(managementObjectNameStrategy, getZwangineContext());

        ServiceHelper.initService(managementObjectStrategy, managementObjectNameStrategy);
    }

    @Override
    protected void doStart() throws Exception {
        ServiceHelper.startService(eventNotifiers, managementAgent, managementObjectStrategy, managementObjectNameStrategy);
        startedEventNotifiers.addAll(eventNotifiers);
    }

    @Override
    protected void doStop() throws Exception {
        startedEventNotifiers.clear();
        ServiceHelper.stopService(managementObjectNameStrategy, managementObjectStrategy, managementAgent, eventNotifiers);
    }

    protected ManagementObjectNameStrategy createManagementObjectNameStrategy(String domain) {
        return null;
    }

    protected ManagementObjectStrategy createManagementObjectStrategy() {
        return null;
    }

    protected ManagementObjectNameStrategy createManagementObjectNameStrategy() {
        String domain = managementAgent != null ? managementAgent.getMBeanObjectDomainName() : null;
        return createManagementObjectNameStrategy(domain);
    }

}
