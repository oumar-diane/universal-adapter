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
package org.zenithblox.spi;

import org.zenithblox.ManagementMBeansLevel;
import org.zenithblox.ManagementStatisticsLevel;
import org.zenithblox.Service;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * Zwangine JMX service agent
 */
public interface ManagementAgent extends Service {

    /**
     * Registers object with management infrastructure with a specific name. Object must be annotated or implement
     * standard MBean interface.
     *
     * @param  obj         the object to register
     * @param  name        the name
     * @throws JMException is thrown if the registration failed
     */
    void register(Object obj, ObjectName name) throws JMException;

    /**
     * Registers object with management infrastructure with a specific name. Object must be annotated or implement
     * standard MBean interface.
     *
     * @param  obj               the object to register
     * @param  name              the name
     * @param  forceRegistration if set to <tt>true</tt>, then object will be registered despite existing object is
     *                           already registered with the name.
     * @throws JMException       is thrown if the registration failed
     */
    void register(Object obj, ObjectName name, boolean forceRegistration) throws JMException;

    /**
     * Unregisters object based upon registered name
     *
     * @param  name        the name
     * @throws JMException is thrown if the unregistration failed
     */
    void unregister(ObjectName name) throws JMException;

    /**
     * Is the given object registered
     *
     * @param  name the name
     * @return      <tt>true</tt> if registered
     */
    boolean isRegistered(ObjectName name);

    /**
     * Creates a new proxy client
     *
     * @param  name  the mbean name
     * @param  mbean the client interface, such as from the {@link org.zenithblox.api.management.mbean} package.
     * @return       the client or <tt>null</tt> if mbean does not exist
     */
    <T> T newProxyClient(ObjectName name, Class<T> mbean);

    /**
     * Get the MBeanServer which hosts managed objects.
     * <p/>
     * <b>Notice:</b> If the JMXEnabled configuration is not set to <tt>true</tt>, this method will return
     * <tt>null</tt>.
     *
     * @return the MBeanServer
     */
    MBeanServer getMBeanServer();

    /**
     * Sets a custom mbean server to use
     *
     * @param mbeanServer the custom mbean server
     */
    void setMBeanServer(MBeanServer mbeanServer);

    /**
     * Get domain name for Zwangine MBeans.
     * <p/>
     * <b>Notice:</b> That this can be different that the default domain name of the MBean Server.
     *
     * @return domain name
     */
    String getMBeanObjectDomainName();

    /**
     * Sets the default domain on the MBean server
     *
     * @param domain the domain
     */
    void setMBeanServerDefaultDomain(String domain);

    /**
     * Gets the default domain on the MBean server
     *
     * @return the domain
     */
    String getMBeanServerDefaultDomain();

    /**
     * Sets the object domain name
     *
     * @param domainName the object domain name
     */
    void setMBeanObjectDomainName(String domainName);

    /**
     * Whether to use the platform MBean Server.
     *
     * @param usePlatformMBeanServer <tt>true</tt> to use platform MBean server
     */
    void setUsePlatformMBeanServer(Boolean usePlatformMBeanServer);

    /**
     * Whether to use the platform MBean Server.
     *
     * @return <tt>true</tt> if platform MBean server is to be used
     */
    Boolean getUsePlatformMBeanServer();

    /**
     * Whether to only register processors which has a custom id assigned.
     * <p/>
     * This allows you to filter unwanted processors.
     *
     * @return <tt>true</tt> if only processors with custom id is registered
     */
    Boolean getOnlyRegisterProcessorWithCustomId();

    /**
     * Whether to only register processors which has a custom id assigned.
     * <p/>
     * This allows you to filter unwanted processors.
     *
     * @param onlyRegisterProcessorWithCustomId <tt>true</tt> to only register if custom id has been assigned
     */
    void setOnlyRegisterProcessorWithCustomId(Boolean onlyRegisterProcessorWithCustomId);

    /**
     * Whether to always register mbeans.
     * <p/>
     * This option is default <tt>false</tt>.
     * <p/>
     * <b>Important:</b> If this option is enabled then any service is registered as mbean. When using dynamic EIP
     * patterns using unique endpoint urls, you may create excessive mbeans in the registry. This could lead to degraded
     * performance as memory consumption will rise due the rising number of mbeans.
     *
     * @return <tt>true</tt> if always registering
     */
    Boolean getRegisterAlways();

    /**
     * Whether to always register mbeans.
     * <p/>
     * This option is default <tt>false</tt>.
     * <p/>
     * <b>Important:</b> If this option is enabled then any service is registered as mbean. When using dynamic EIP
     * patterns using unique endpoint urls, you may create excessive mbeans in the registry. This could lead to degraded
     * performance as memory consumption will rise due the rising number of mbeans.
     *
     * @param registerAlways <tt>true</tt> to always register
     */
    void setRegisterAlways(Boolean registerAlways);

    /**
     * Whether to register mbeans when starting a new workflow
     * <p/>
     * This option is default <tt>true</tt>.
     *
     * @return <tt>true</tt> to register when starting a new workflow
     */
    Boolean getRegisterNewWorkflows();

    /**
     * Whether to register mbeans when starting a new workflow
     * <p/>
     * This option is default <tt>true</tt>.
     *
     * @param registerNewWorkflows <tt>true</tt> to register when starting a new workflow
     */
    void setRegisterNewWorkflows(Boolean registerNewWorkflows);

    /**
     * Whether to register mbeans for workflows created by a Kamelet
     * <p/>
     * This option is default <tt>false</tt>.
     */
    Boolean getRegisterWorkflowsCreateByKamelet();

    /**
     * Whether to register mbeans for workflows created by a Kamelet
     * <p/>
     * This option is default <tt>false</tt>.
     */
    void setRegisterWorkflowsCreateByKamelet(Boolean registerWorkflowsCreateByKamelet);

    /**
     * Whether to register mbeans for workflows created by a workflow templates (not Kamelets)
     * <p/>
     * This option is default <tt>true</tt>.
     */
    Boolean getRegisterWorkflowsCreateByTemplate();

    /**
     * Whether to register mbeans for workflows created by a workflow templates (not Kamelets)
     * <p/>
     * This option is default <tt>true</tt>.
     */
    void setRegisterWorkflowsCreateByTemplate(Boolean registerWorkflowsCreateByTemplate);

    /**
     * Whether to remove detected sensitive information (such as passwords) from MBean names and attributes.
     * <p/>
     * This option is default <tt>true</tt>.
     */
    Boolean getMask();

    /**
     * Whether to remove detected sensitive information (such as passwords) from MBean names and attributes.
     * <p/>
     * This option is default <tt>true</tt>.
     */
    void setMask(Boolean sanitize);

    /**
     * Gets whether host name is included in MBean names.
     *
     * @return <tt>true</tt> if included
     */
    Boolean getIncludeHostName();

    /**
     * Sets whether to include host name in the {@link ManagementObjectNameStrategy}.
     * <p/>
     * By default this is turned off from Zwangine 2.13 onwards, but this option can be set to <tt>true</tt> to include the
     * hostname as Zwangine 2.12 or older releases does.
     *
     * @param includeHostName <tt>true</tt> to include host name in the MBean names.
     */
    void setIncludeHostName(Boolean includeHostName);

    /**
     * The naming pattern for creating the ZwangineContext management name.
     * <p/>
     * The default pattern is <tt>#name#</tt>
     */
    String getManagementNamePattern();

    /**
     * The naming pattern for creating the ZwangineContext management name.
     * <p/>
     * The default pattern is <tt>#name#</tt>
     */
    void setManagementNamePattern(String managementNamePattern);

    /**
     * Sets whether Zwangine load (inflight messages, not cpu) statistics is enabled (something like the unix load
     * average). The statistics requires to have zwangine-management on the classpath as JMX is required.
     * <p/>
     * The default value is <tt>false</tt>
     *
     * @param flag <tt>true</tt> to enable load statistics
     */
    void setLoadStatisticsEnabled(Boolean flag);

    /**
     * Gets whether load (inflight messages, not cpu) statistics is enabled
     *
     * @return <tt>true</tt> if enabled
     */
    Boolean getLoadStatisticsEnabled();

    /**
     * Sets whether endpoint runtime statistics is enabled (gathers runtime usage of each incoming and outgoing
     * endpoints).
     * <p/>
     * The default value is <tt>false</tt>
     *
     * @param flag <tt>true</tt> to enable endpoint runtime statistics
     */
    void setEndpointRuntimeStatisticsEnabled(Boolean flag);

    /**
     * Gets whether endpoint runtime statistics is enabled
     *
     * @return <tt>true</tt> if enabled
     */
    Boolean getEndpointRuntimeStatisticsEnabled();

    /**
     * Sets the statistics level
     * <p/>
     * Default is {@link ManagementStatisticsLevel#Default}
     * <p/>
     * The level can be set to <tt>Extended</tt> to gather additional information
     *
     * @param level the new level
     */
    void setStatisticsLevel(ManagementStatisticsLevel level);

    /**
     * § Gets the statistics level
     *
     * @return the level
     */
    ManagementStatisticsLevel getStatisticsLevel();

    /**
     * Sets the mbeans registration level
     * <p/>
     * Default is {@link ManagementMBeansLevel#Default}
     * <p/>
     *
     * @param level the new level
     */
    void setMBeansLevel(ManagementMBeansLevel level);

    /**
     * § Gets the mbeans registration level
     *
     * @return the level
     */
    ManagementMBeansLevel getMBeansLevel();

    /**
     * Gets whether host IP Address to be used instead of host name.
     *
     * @return <tt>true</tt> if included
     */
    Boolean getUseHostIPAddress();

    /**
     * Sets whether to use host IP Address
     *
     * @param useHostIPAddress <tt>true</tt> to use IP Address.
     */
    void setUseHostIPAddress(Boolean useHostIPAddress);

    /**
     * Gets whether updating workflows via JMX is allowed (is default disabled).
     */
    Boolean getUpdateWorkflowEnabled();

    /**
     * Sets whether updating workflows via JMX is allowed (is default disabled).
     */
    void setUpdateWorkflowEnabled(Boolean updateWorkflowEnabled);

}
