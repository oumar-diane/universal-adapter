/*
 * Licensed to the  Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the  License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.zentihblox.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zenithblox.model.cloud;

import org.zenithblox.ZwangineContext;
import org.zenithblox.Expression;
import org.zenithblox.NoFactoryAvailableException;
import org.zenithblox.cloud.ServiceCallConstants;
import org.zenithblox.cloud.ServiceExpressionFactory;
import org.zenithblox.model.ProcessorDefinition;
import org.zenithblox.model.language.ExpressionDefinition;
import org.zenithblox.spi.Configurer;
import org.zenithblox.spi.Metadata;
import org.zenithblox.support.ZwangineContextHelper;
import org.zenithblox.support.PropertyBindingSupport;

import java.util.Map;

@Metadata(label = "routing,cloud")
@Configurer(extended = true)
@Deprecated(since = "3.19.0")
public class ServiceCallExpressionConfiguration extends ServiceCallConfiguration implements ServiceExpressionFactory {
    private final ServiceCallDefinition parent;
    private final String factoryKey;
    @Metadata(defaultValue = ServiceCallConstants.SERVICE_HOST)
    private String hostHeader = ServiceCallConstants.SERVICE_HOST;
    @Metadata(defaultValue = ServiceCallConstants.SERVICE_PORT)
    private String portHeader = ServiceCallConstants.SERVICE_PORT;
    private ExpressionDefinition expressionType;
    private Expression expression;

    public ServiceCallExpressionConfiguration() {
        this(null, null);
    }

    public ServiceCallExpressionConfiguration(ServiceCallDefinition parent, String factoryKey) {
        this.parent = parent;
        this.factoryKey = factoryKey;
    }

    public ServiceCallDefinition end() {
        return this.parent;
    }

    public ProcessorDefinition<?> endParent() {
        return this.parent.end();
    }

    // *************************************************************************
    //
    // *************************************************************************

    @Override
    public ServiceCallServiceChooserConfiguration property(String key, String value) {
        return (ServiceCallServiceChooserConfiguration) super.property(key, value);
    }

    public String getHostHeader() {
        return hostHeader;
    }

    /**
     * The header that holds the service host information, default ServiceCallConstants.SERVICE_HOST
     */
    public void setHostHeader(String hostHeader) {
        this.hostHeader = hostHeader;
    }

    public String getPortHeader() {
        return portHeader;
    }

    /**
     * The header that holds the service port information, default ServiceCallConstants.SERVICE_PORT
     */
    public void setPortHeader(String portHeader) {
        this.portHeader = portHeader;
    }

    public ExpressionDefinition getExpressionType() {
        return expressionType;
    }

    public void setExpressionType(ExpressionDefinition expressionType) {
        this.expressionType = expressionType;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    /**
     * The header that holds the service host information, default ServiceCallConstants.SERVICE_HOST
     */
    public ServiceCallExpressionConfiguration hostHeader(String hostHeader) {
        setHostHeader(hostHeader);
        return this;
    }

    /**
     * The header that holds the service port information, default ServiceCallConstants.SERVICE_PORT
     */
    public ServiceCallExpressionConfiguration portHeader(String portHeader) {
        setPortHeader(portHeader);
        return this;
    }

    public ServiceCallExpressionConfiguration expressionType(ExpressionDefinition expressionType) {
        setExpressionType(expressionType);
        return this;
    }

    public ServiceCallExpressionConfiguration expression(Expression expression) {
        setExpression(expression);
        return this;
    }

    // *************************************************************************
    // Factory
    // *************************************************************************

    @Override
    public Expression newInstance(ZwangineContext zwangineContext) throws Exception {
        Expression answer = getExpression();
        if (answer != null) {
            return answer;
        }

        ExpressionDefinition expressionType = getExpressionType();
        if (expressionType != null) {
            return expressionType.createExpression(zwangineContext);
        }

        if (factoryKey != null) {
            // First try to find the factory from the registry.
            ServiceExpressionFactory factory
                    = ZwangineContextHelper.lookup(zwangineContext, factoryKey, ServiceExpressionFactory.class);
            if (factory != null) {
                // If a factory is found in the registry do not re-configure it
                // as it should be pre-configured.
                answer = factory.newInstance(zwangineContext);
            } else {

                Class<?> type;
                try {
                    // Then use Service factory.
                    type = zwangineContext.getZwangineContextExtension()
                            .getFactoryFinder(ServiceCallDefinitionConstants.RESOURCE_PATH).findClass(factoryKey).orElseThrow();
                } catch (Exception e) {
                    throw new NoFactoryAvailableException(ServiceCallDefinitionConstants.RESOURCE_PATH + factoryKey, e);
                }

                if (ServiceExpressionFactory.class.isAssignableFrom(type)) {
                    factory = (ServiceExpressionFactory) zwangineContext.getInjector().newInstance(type, false);
                } else {
                    throw new IllegalArgumentException(
                            "Resolving Expression: " + factoryKey
                                                       + " detected type conflict: Not a ExpressionFactory implementation. Found: "
                                                       + type.getName());
                }

                try {
                    Map<String, Object> parameters = getConfiguredOptions(zwangineContext, this);

                    parameters.replaceAll((k, v) -> {
                        if (v instanceof String str) {
                            try {
                                v = zwangineContext.resolvePropertyPlaceholders(str);
                            } catch (Exception e) {
                                throw new IllegalArgumentException(
                                        String.format("Exception while resolving %s (%s)", k, v), e);
                            }
                        }

                        return v;
                    });

                    // Convert properties to Map<String, String>
                    Map<String, String> map = getPropertiesAsMap(zwangineContext);
                    if (map != null && !map.isEmpty()) {
                        parameters.put("properties", map);
                    }

                    postProcessFactoryParameters(zwangineContext, parameters);

                    PropertyBindingSupport.build().bind(zwangineContext, factory, parameters);

                    answer = factory.newInstance(zwangineContext);
                } catch (Exception e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }

        return answer;
    }

}
