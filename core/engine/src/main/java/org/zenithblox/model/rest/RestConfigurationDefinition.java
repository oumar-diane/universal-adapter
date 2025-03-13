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
package org.zenithblox.model.rest;

import org.zenithblox.ZwangineContext;
import org.zenithblox.spi.Metadata;
import org.zenithblox.spi.RestConfiguration;
import org.zenithblox.support.ZwangineContextHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * To configure rest
 */
@Metadata(label = "rest")
public class RestConfigurationDefinition {

    @Metadata(enums = "platform-http,servlet,jetty,undertow,netty-http,coap")
    private String component;
    @Metadata(label = "consumer,advanced", enums = "openapi,swagger")
    private String apiComponent;
    @Metadata(label = "producer,advanced", enums = "vertx-http,http,undertow,netty-http")
    private String producerComponent;
    private String scheme;
    private String host;
    private String port;
    @Metadata(label = "consumer,advanced")
    private String apiHost;
    @Metadata(label = "consumer,advanced", javaType = "java.lang.Boolean")
    private String useXForwardHeaders;
    @Metadata(label = "producer,advanced")
    private String producerApiDoc;
    @Metadata(label = "consumer")
    private String contextPath;
    @Metadata(label = "consumer")
    private String apiContextPath;
    @Metadata(label = "consumer,advanced")
    private String apiContextWorkflowId;
    @Metadata(label = "consumer,advanced", javaType = "java.lang.Boolean", defaultValue = "false")
    private String apiVendorExtension;
    @Metadata(label = "consumer,advanced", defaultValue = "allLocalIp")
    private RestHostNameResolver hostNameResolver;
    @Metadata(defaultValue = "off", enums = "off,auto,json,xml,json_xml")
    private RestBindingMode bindingMode;
    @Metadata(label = "consumer,advanced")
    private String bindingPackageScan;
    @Metadata(label = "advanced", javaType = "java.lang.Boolean", defaultValue = "false")
    private String skipBindingOnErrorCode;
    @Metadata(label = "consumer,advanced", javaType = "java.lang.Boolean", defaultValue = "false")
    private String clientRequestValidation;
    @Metadata(label = "consumer,advanced", javaType = "java.lang.Boolean", defaultValue = "false")
    private String enableCORS;
    @Metadata(label = "consumer,advanced", javaType = "java.lang.Boolean", defaultValue = "false")
    private String enableNoContentResponse;
    @Metadata(label = "consumer", javaType = "java.lang.Boolean", defaultValue = "true")
    private String inlineWorkflows;
    @Metadata(label = "advanced")
    private String jsonDataFormat;
    @Metadata(label = "advanced")
    private String xmlDataFormat;
    @Metadata(label = "advanced")
    private List<RestPropertyDefinition> componentProperties = new ArrayList<>();
    @Metadata(label = "advanced")
    private List<RestPropertyDefinition> endpointProperties = new ArrayList<>();
    @Metadata(label = "consumer,advanced")
    private List<RestPropertyDefinition> consumerProperties = new ArrayList<>();
    @Metadata(label = "advanced")
    private List<RestPropertyDefinition> dataFormatProperties = new ArrayList<>();
    @Metadata(label = "consumer,advanced")
    private List<RestPropertyDefinition> apiProperties = new ArrayList<>();
    @Metadata(label = "consumer,advanced")
    private List<RestPropertyDefinition> corsHeaders = new ArrayList<>();

    public String getComponent() {
        return component;
    }

    /**
     * The Zwangine Rest component to use for the REST transport (consumer), such as netty-http, jetty, servlet, undertow.
     * If no component has been explicit configured, then Zwangine will lookup if there is a Zwangine component that
     * integrates with the Rest DSL, or if a org.zenithblox.spi.RestConsumerFactory is registered in the registry. If
     * either one is found, then that is being used.
     */
    public void setComponent(String component) {
        this.component = component;
    }

    public String getApiComponent() {
        return apiComponent;
    }

    /**
     * The name of the Zwangine component to use as the REST API. If no API Component has been explicit configured, then
     * Zwangine will lookup if there is a Zwangine component responsible for servicing and generating the REST API
     * documentation, or if a org.zenithblox.spi.RestApiProcessorFactory is registered in the registry. If either one
     * is found, then that is being used.
     */
    public void setApiComponent(String apiComponent) {
        this.apiComponent = apiComponent;
    }

    public String getProducerComponent() {
        return producerComponent;
    }

    /**
     * Sets the name of the Zwangine component to use as the REST producer
     */
    public void setProducerComponent(String producerComponent) {
        this.producerComponent = producerComponent;
    }

    public String getScheme() {
        return scheme;
    }

    /**
     * The scheme to use for exposing the REST service. Usually http or https is supported.
     * <p/>
     * The default value is http
     */
    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getHost() {
        return host;
    }

    /**
     * The hostname to use for exposing the REST service.
     */
    public void setHost(String host) {
        this.host = host;
    }

    public String getApiHost() {
        return apiHost;
    }

    /**
     * To use a specific hostname for the API documentation (such as swagger or openapi)
     * <p/>
     * This can be used to override the generated host with this configured hostname
     */
    public void setApiHost(String apiHost) {
        this.apiHost = apiHost;
    }

    public String getPort() {
        return port;
    }

    /**
     * The port number to use for exposing the REST service. Notice if you use servlet component then the port number
     * configured here does not apply, as the port number in use is the actual port number the servlet component is
     * using. eg if using  Tomcat its the tomcat http port, if using  Karaf its the HTTP service in Karaf
     * that uses port 8181 by default etc. Though in those situations setting the port number here, allows tooling and
     * JMX to know the port number, so its recommended to set the port number to the number that the servlet engine
     * uses.
     */
    public void setPort(String port) {
        this.port = port;
    }

    public String getProducerApiDoc() {
        return producerApiDoc;
    }

    /**
     * Sets the location of the api document the REST producer will use to validate the REST uri and query parameters
     * are valid accordingly to the api document.
     * <p/>
     * The location of the api document is loaded from classpath by default, but you can use <tt>file:</tt> or
     * <tt>http:</tt> to refer to resources to load from file or http url.
     */
    public void setProducerApiDoc(String producerApiDoc) {
        this.producerApiDoc = producerApiDoc;
    }

    public String getContextPath() {
        return contextPath;
    }

    /**
     * Sets a leading context-path the REST services will be using.
     * <p/>
     * This can be used when using components such as <tt>zwangine-servlet</tt> where the deployed web application is
     * deployed using a context-path. Or for components such as <tt>zwangine-jetty</tt> or <tt>zwangine-netty-http</tt> that
     * includes a HTTP server.
     */
    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getApiContextPath() {
        return apiContextPath;
    }

    /**
     * Sets a leading context-path the REST API will be using.
     * <p/>
     * This can be used when using components such as <tt>zwangine-servlet</tt> where the deployed web application is
     * deployed using a context-path.
     *
     * @param contextPath the API context path
     */
    public void setApiContextPath(String contextPath) {
        this.apiContextPath = contextPath;
    }

    public String getApiContextWorkflowId() {
        return apiContextWorkflowId;
    }

    /**
     * Sets the workflow id to use for the workflow that services the REST API.
     * <p/>
     * The workflow will by default use an auto assigned workflow id.
     *
     * @param apiContextWorkflowId the workflow id
     */
    public void setApiContextWorkflowId(String apiContextWorkflowId) {
        this.apiContextWorkflowId = apiContextWorkflowId;
    }

    public String getApiVendorExtension() {
        return apiVendorExtension;
    }

    /**
     * Whether vendor extension is enabled in the Rest APIs. If enabled then Zwangine will include additional information
     * as vendor extension (eg keys starting with x-) such as workflow ids, class names etc. Not all 3rd party API gateways
     * and tools supports vendor-extensions when importing your API docs.
     */
    public void setApiVendorExtension(String apiVendorExtension) {
        this.apiVendorExtension = apiVendorExtension;
    }

    public RestHostNameResolver getHostNameResolver() {
        return hostNameResolver;
    }

    /**
     * If no hostname has been explicit configured, then this resolver is used to compute the hostname the REST service
     * will be using.
     */
    public void setHostNameResolver(RestHostNameResolver hostNameResolver) {
        this.hostNameResolver = hostNameResolver;
    }

    public RestBindingMode getBindingMode() {
        return bindingMode;
    }

    /**
     * Sets the binding mode to use.
     * <p/>
     * The default value is off
     */
    public void setBindingMode(RestBindingMode bindingMode) {
        this.bindingMode = bindingMode;
    }

    public String getBindingPackageScan() {
        return bindingPackageScan;
    }

    /**
     * Package name to use as base (offset) for classpath scanning of POJO classes are located when using binding mode
     * is enabled for JSon or XML. Multiple package names can be separated by comma.
     */
    public void setBindingPackageScan(String bindingPackageScan) {
        this.bindingPackageScan = bindingPackageScan;
    }

    public String getSkipBindingOnErrorCode() {
        return skipBindingOnErrorCode;
    }

    /**
     * Whether to skip binding on output if there is a custom HTTP error code header. This allows to build custom error
     * messages that do not bind to json / xml etc, as success messages otherwise will do.
     */
    public void setSkipBindingOnErrorCode(String skipBindingOnErrorCode) {
        this.skipBindingOnErrorCode = skipBindingOnErrorCode;
    }

    public String getClientRequestValidation() {
        return clientRequestValidation;
    }

    /**
     * Whether to enable validation of the client request to check:
     *
     * 1) Content-Type header matches what the Rest DSL consumes; returns HTTP Status 415 if validation error. 2) Accept
     * header matches what the Rest DSL produces; returns HTTP Status 406 if validation error. 3) Missing required data
     * (query parameters, HTTP headers, body); returns HTTP Status 400 if validation error. 4) Parsing error of the
     * message body (JSon, XML or Auto binding mode must be enabled); returns HTTP Status 400 if validation error.
     */
    public void setClientRequestValidation(String clientRequestValidation) {
        this.clientRequestValidation = clientRequestValidation;
    }

    public String getEnableCORS() {
        return enableCORS;
    }

    /**
     * Whether to enable CORS headers in the HTTP response.
     * <p/>
     * The default value is false.
     */
    public void setEnableCORS(String enableCORS) {
        this.enableCORS = enableCORS;
    }

    public String getEnableNoContentResponse() {
        return enableNoContentResponse;
    }

    /**
     * Whether to return HTTP 204 with an empty body when a response contains an empty JSON object or XML root object.
     * <p/>
     * The default value is false.
     */
    public void setEnableNoContentResponse(String enableNoContentResponse) {
        this.enableNoContentResponse = enableNoContentResponse;
    }

    public String getInlineWorkflows() {
        return inlineWorkflows;
    }

    /**
     * Inline workflows in rest-dsl which are linked using direct endpoints.
     *
     * Each service in Rest DSL is an individual workflow, meaning that you would have at least two workflows per service
     * (rest-dsl, and the workflow linked from rest-dsl). By inlining (default) allows Zwangine to optimize and inline this as
     * a single workflow, however this requires to use direct endpoints, which must be unique per service. If a workflow is
     * not using direct endpoint then the rest-dsl is not inlined, and will become an individual workflow.
     *
     * This option is default <tt>true</tt>.
     */
    public void setInlineWorkflows(String inlineWorkflows) {
        this.inlineWorkflows = inlineWorkflows;
    }

    public String getJsonDataFormat() {
        return jsonDataFormat;
    }

    /**
     * Name of specific json data format to use. By default jackson will be used. Important: This option is only for
     * setting a custom name of the data format, not to refer to an existing data format instance.
     */
    public void setJsonDataFormat(String jsonDataFormat) {
        this.jsonDataFormat = jsonDataFormat;
    }

    public String getXmlDataFormat() {
        return xmlDataFormat;
    }

    /**
     * Name of specific XML data format to use. By default jaxb will be used. Important: This option is only for setting
     * a custom name of the data format, not to refer to an existing data format instance.
     */
    public void setXmlDataFormat(String xmlDataFormat) {
        this.xmlDataFormat = xmlDataFormat;
    }

    public List<RestPropertyDefinition> getComponentProperties() {
        return componentProperties;
    }

    /**
     * Allows to configure as many additional properties for the rest component in use.
     */
    public void setComponentProperties(List<RestPropertyDefinition> componentProperties) {
        this.componentProperties = componentProperties;
    }

    public List<RestPropertyDefinition> getEndpointProperties() {
        return endpointProperties;
    }

    /**
     * Allows to configure as many additional properties for the rest endpoint in use.
     */
    public void setEndpointProperties(List<RestPropertyDefinition> endpointProperties) {
        this.endpointProperties = endpointProperties;
    }

    public List<RestPropertyDefinition> getConsumerProperties() {
        return consumerProperties;
    }

    /**
     * Allows to configure as many additional properties for the rest consumer in use.
     */
    public void setConsumerProperties(List<RestPropertyDefinition> consumerProperties) {
        this.consumerProperties = consumerProperties;
    }

    public List<RestPropertyDefinition> getDataFormatProperties() {
        return dataFormatProperties;
    }

    /**
     * Allows to configure as many additional properties for the data formats in use. For example set property
     * prettyPrint to true to have json outputted in pretty mode. The properties can be prefixed to denote the option is
     * only for either JSON or XML and for either the IN or the OUT. The prefixes are:
     * <ul>
     * <li>json.in.</li>
     * <li>json.out.</li>
     * <li>xml.in.</li>
     * <li>xml.out.</li>
     * </ul>
     * For example a key with value "xml.out.mustBeJAXBElement" is only for the XML data format for the outgoing. A key
     * without a prefix is a common key for all situations.
     */
    public void setDataFormatProperties(List<RestPropertyDefinition> dataFormatProperties) {
        this.dataFormatProperties = dataFormatProperties;
    }

    public List<RestPropertyDefinition> getApiProperties() {
        return apiProperties;
    }

    /**
     * Allows to configure as many additional properties for the api documentation. For example set property api.title
     * to my cool stuff
     */
    public void setApiProperties(List<RestPropertyDefinition> apiProperties) {
        this.apiProperties = apiProperties;
    }

    public List<RestPropertyDefinition> getCorsHeaders() {
        return corsHeaders;
    }

    /**
     * Allows to configure custom CORS headers.
     */
    public void setCorsHeaders(List<RestPropertyDefinition> corsHeaders) {
        this.corsHeaders = corsHeaders;
    }

    public String getUseXForwardHeaders() {
        return useXForwardHeaders;
    }

    /**
     * Whether to use X-Forward headers to set host etc. for OpenApi.
     *
     * This may be needed in special cases involving reverse-proxy and networking going from HTTP to HTTPS etc. Then the
     * proxy can send X-Forward headers (X-Forwarded-Proto) that influences the host names in the OpenAPI schema that
     * zwangine-openapi-java generates from Rest DSL workflows.
     */
    public void setUseXForwardHeaders(String useXForwardHeaders) {
        this.useXForwardHeaders = useXForwardHeaders;
    }

    // Fluent API
    // -------------------------------------------------------------------------

    /**
     * To use a specific Zwangine rest component (consumer)
     */
    public RestConfigurationDefinition component(String componentId) {
        setComponent(componentId);
        return this;
    }

    /**
     * To use a specific Zwangine rest API component
     */
    public RestConfigurationDefinition apiComponent(String componentId) {
        setApiComponent(componentId);
        return this;
    }

    /**
     * To use a specific Zwangine rest component (producer)
     */
    public RestConfigurationDefinition producerComponent(String componentId) {
        setProducerComponent(componentId);
        return this;
    }

    /**
     * To use a specific scheme such as http/https
     */
    public RestConfigurationDefinition scheme(String scheme) {
        setScheme(scheme);
        return this;
    }

    /**
     * To define the host to use, such as 0.0.0.0 or localhost
     */
    public RestConfigurationDefinition host(String host) {
        setHost(host);
        return this;
    }

    /**
     * To define a specific host to use for API documentation instead of using a generated API hostname that is relative
     * to the REST service host.
     */
    public RestConfigurationDefinition apiHost(String host) {
        setApiHost(host);
        return this;
    }

    /**
     * To specify the port number to use for the REST service
     */
    public RestConfigurationDefinition port(int port) {
        setPort(Integer.toString(port));
        return this;
    }

    /**
     * To specify the port number to use for the REST service
     */
    public RestConfigurationDefinition port(String port) {
        setPort(port);
        return this;
    }

    /**
     * Sets the location of the api document the REST producer will use to validate the REST uri and query parameters
     * are valid accordingly to the api document.
     * <p/>
     * The location of the api document is loaded from classpath by default, but you can use <tt>file:</tt> or
     * <tt>http:</tt> to refer to resources to load from file or http url.
     */
    public RestConfigurationDefinition producerApiDoc(String apiDoc) {
        setProducerApiDoc(apiDoc);
        return this;
    }

    /**
     * Sets a leading context-path the REST API will be using.
     * <p/>
     * This can be used when using components such as <tt>zwangine-servlet</tt> where the deployed web application is
     * deployed using a context-path. Or for components such as <tt>zwangine-jetty</tt> or <tt>zwangine-netty-http</tt> that
     * includes a HTTP server.
     */
    public RestConfigurationDefinition apiContextPath(String contextPath) {
        setApiContextPath(contextPath);
        return this;
    }

    /**
     * Sets the workflow id to use for the workflow that services the REST API.
     * <p/>
     * The workflow will by default use an auto assigned workflow id.
     */
    public RestConfigurationDefinition apiContextWorkflowId(String apiContextWorkflowId) {
        setApiContextWorkflowId(apiContextWorkflowId);
        return this;
    }

    /**
     * Whether vendor extension is enabled in the Rest APIs. If enabled then Zwangine will include additional information
     * as vendor extension (eg keys starting with x-) such as workflow ids, class names etc. Some API tooling may not
     * support vendor extensions and this option can then be turned off.
     */
    public RestConfigurationDefinition apiVendorExtension(boolean vendorExtension) {
        setApiVendorExtension(vendorExtension ? "true" : "false");
        return this;
    }

    /**
     * Whether vendor extension is enabled in the Rest APIs. If enabled then Zwangine will include additional information
     * as vendor extension (eg keys starting with x-) such as workflow ids, class names etc. Some API tooling may not
     * support vendor extensions and this option can then be turned off.
     */
    public RestConfigurationDefinition apiVendorExtension(String vendorExtension) {
        setApiVendorExtension(vendorExtension);
        return this;
    }

    /**
     * Sets a leading context-path the REST services will be using.
     * <p/>
     * This can be used when using components such as <tt>zwangine-servlet</tt> where the deployed web application is
     * deployed using a context-path.
     */
    public RestConfigurationDefinition contextPath(String contextPath) {
        setContextPath(contextPath);
        return this;
    }

    /**
     * To specify the hostname resolver
     */
    public RestConfigurationDefinition hostNameResolver(RestHostNameResolver hostNameResolver) {
        setHostNameResolver(hostNameResolver);
        return this;
    }

    /**
     * To specify the binding mode
     */
    public RestConfigurationDefinition bindingMode(RestBindingMode bindingMode) {
        setBindingMode(bindingMode);
        return this;
    }

    /**
     * To specify the binding mode
     */
    public RestConfigurationDefinition bindingMode(String bindingMode) {
        setBindingMode(RestBindingMode.valueOf(bindingMode.toLowerCase()));
        return this;
    }

    /**
     * Package name to use as base (offset) for classpath scanning of POJO classes are located when using binding mode
     * is enabled for JSon or XML. Multiple package names can be separated by comma.
     */
    public RestConfigurationDefinition bindingPackageScan(String bindingPackageScan) {
        setBindingPackageScan(bindingPackageScan);
        return this;
    }

    /**
     * To specify whether to skip binding output if there is a custom HTTP error code
     */
    public RestConfigurationDefinition skipBindingOnErrorCode(boolean skipBindingOnErrorCode) {
        setSkipBindingOnErrorCode(skipBindingOnErrorCode ? "true" : "false");
        return this;
    }

    /**
     * To specify whether to skip binding output if there is a custom HTTP error code
     */
    public RestConfigurationDefinition skipBindingOnErrorCode(String skipBindingOnErrorCode) {
        setSkipBindingOnErrorCode(skipBindingOnErrorCode);
        return this;
    }

    /**
     * Whether to enable validation of the client request to check:
     *
     * 1) Content-Type header matches what the Rest DSL consumes; returns HTTP Status 415 if validation error. 2) Accept
     * header matches what the Rest DSL produces; returns HTTP Status 406 if validation error. 3) Missing required data
     * (query parameters, HTTP headers, body); returns HTTP Status 400 if validation error. 4) Parsing error of the
     * message body (JSon, XML or Auto binding mode must be enabled); returns HTTP Status 400 if validation error.
     */
    public RestConfigurationDefinition clientRequestValidation(boolean clientRequestValidation) {
        setClientRequestValidation(clientRequestValidation ? "true" : "false");
        return this;
    }

    /**
     * Whether to enable validation of the client request to check:
     *
     * 1) Content-Type header matches what the Rest DSL consumes; returns HTTP Status 415 if validation error. 2) Accept
     * header matches what the Rest DSL produces; returns HTTP Status 406 if validation error. 3) Missing required data
     * (query parameters, HTTP headers, body); returns HTTP Status 400 if validation error. 4) Parsing error of the
     * message body (JSon, XML or Auto binding mode must be enabled); returns HTTP Status 400 if validation error.
     */
    public RestConfigurationDefinition clientRequestValidation(String clientRequestValidation) {
        setClientRequestValidation(clientRequestValidation);
        return this;
    }

    /**
     * To specify whether to enable CORS which means Zwangine will automatic include CORS in the HTTP headers in the
     * response.
     */
    public RestConfigurationDefinition enableCORS(boolean enableCORS) {
        setEnableCORS(enableCORS ? "true" : "false");
        return this;
    }

    /**
     * To specify whether to enable CORS which means Zwangine will automatic include CORS in the HTTP headers in the
     * response.
     */
    public RestConfigurationDefinition enableCORS(String enableCORS) {
        setEnableCORS(enableCORS);
        return this;
    }

    /**
     * To Specify whether to return HTTP 204 with an empty body when a response contains an empty JSON object or XML
     * root object.
     */
    public RestConfigurationDefinition enableNoContentResponse(boolean enableNoContentResponse) {
        setEnableNoContentResponse(enableNoContentResponse ? "true" : "false");
        return this;
    }

    /**
     * To specify whether to return HTTP 204 with an empty body when a response contains an empty JSON object or XML
     * root object.
     */
    public RestConfigurationDefinition enableNoContentResponse(String enableNoContentResponse) {
        setEnableNoContentResponse(enableNoContentResponse);
        return this;
    }

    /**
     * Inline workflows in rest-dsl which are linked using direct endpoints.
     *
     * Each service in Rest DSL is an individual workflow, meaning that you would have at least two workflows per service
     * (rest-dsl, and the workflow linked from rest-dsl). By inlining (default) allows Zwangine to optimize and inline this as
     * a single workflow, however this requires to use direct endpoints, which must be unique per service. If a workflow is
     * not using direct endpoint then the rest-dsl is not inlined, and will become an individual workflow.
     *
     * This option is default <tt>true</tt>.
     */
    public RestConfigurationDefinition inlineWorkflows(String inlineWorkflows) {
        setInlineWorkflows(inlineWorkflows);
        return this;
    }

    /**
     * Inline workflows in rest-dsl which are linked using direct endpoints.
     *
     * Each service in Rest DSL is an individual workflow, meaning that you would have at least two workflows per service
     * (rest-dsl, and the workflow linked from rest-dsl). By inlining (default) allows Zwangine to optimize and inline this as
     * a single workflow, however this requires to use direct endpoints, which must be unique per service. If a workflow is
     * not using direct endpoint then the rest-dsl is not inlined, and will become an individual workflow.
     *
     * This option is default <tt>true</tt>.
     */
    public RestConfigurationDefinition inlineWorkflows(boolean inlineWorkflows) {
        setInlineWorkflows(inlineWorkflows ? "true" : "false");
        return this;
    }

    /**
     * To use a specific json data format
     * <p/>
     * <b>Important:</b> This option is only for setting a custom name of the data format, not to refer to an existing
     * data format instance.
     *
     * @param name name of the data format to {@link org.zenithblox.ZwangineContext#resolveDataFormat(String)
     *             resolve}
     */
    public RestConfigurationDefinition jsonDataFormat(String name) {
        setJsonDataFormat(name);
        return this;
    }

    /**
     * To use a specific XML data format
     * <p/>
     * <b>Important:</b> This option is only for setting a custom name of the data format, not to refer to an existing
     * data format instance.
     *
     * @param name name of the data format to {@link org.zenithblox.ZwangineContext#resolveDataFormat(String)
     *             resolve}
     */
    public RestConfigurationDefinition xmlDataFormat(String name) {
        setXmlDataFormat(name);
        return this;
    }

    /**
     * For additional configuration options on component level
     * <p/>
     * The value can use <tt>#</tt> to refer to a bean to lookup in the registry.
     */
    public RestConfigurationDefinition componentProperty(String key, String value) {
        RestPropertyDefinition prop = new RestPropertyDefinition();
        prop.setKey(key);
        prop.setValue(value);
        getComponentProperties().add(prop);
        return this;
    }

    /**
     * For additional configuration options on endpoint level
     * <p/>
     * The value can use <tt>#</tt> to refer to a bean to lookup in the registry.
     */
    public RestConfigurationDefinition endpointProperty(String key, String value) {
        RestPropertyDefinition prop = new RestPropertyDefinition();
        prop.setKey(key);
        prop.setValue(value);
        getEndpointProperties().add(prop);
        return this;
    }

    /**
     * For additional configuration options on consumer level
     * <p/>
     * The value can use <tt>#</tt> to refer to a bean to lookup in the registry.
     */
    public RestConfigurationDefinition consumerProperty(String key, String value) {
        RestPropertyDefinition prop = new RestPropertyDefinition();
        prop.setKey(key);
        prop.setValue(value);
        getConsumerProperties().add(prop);
        return this;
    }

    /**
     * For additional configuration options on data format level
     * <p/>
     * The value can use <tt>#</tt> to refer to a bean to lookup in the registry.
     */
    public RestConfigurationDefinition dataFormatProperty(String key, String value) {
        RestPropertyDefinition prop = new RestPropertyDefinition();
        prop.setKey(key);
        prop.setValue(value);
        getDataFormatProperties().add(prop);
        return this;
    }

    /**
     * For configuring an api property, such as <tt>api.title</tt>, or <tt>api.version</tt>.
     */
    public RestConfigurationDefinition apiProperty(String key, String value) {
        RestPropertyDefinition prop = new RestPropertyDefinition();
        prop.setKey(key);
        prop.setValue(value);
        getApiProperties().add(prop);
        return this;
    }

    /**
     * For configuring CORS headers
     */
    public RestConfigurationDefinition corsHeaderProperty(String key, String value) {
        RestPropertyDefinition prop = new RestPropertyDefinition();
        prop.setKey(key);
        prop.setValue(value);
        getCorsHeaders().add(prop);
        return this;
    }

    /**
     * Shortcut for setting the Access-Control-Allow-Credentials header.
     */
    public RestConfigurationDefinition corsAllowCredentials(boolean corsAllowCredentials) {
        return corsHeaderProperty("Access-Control-Allow-Credentials", String.valueOf(corsAllowCredentials));
    }

    /**
     * Whether to use X-Forward headers to set host etc. for OpenApi.
     *
     * This may be needed in special cases involving reverse-proxy and networking going from HTTP to HTTPS etc. Then the
     * proxy can send X-Forward headers (X-Forwarded-Proto) that influences the host names in the OpenAPI schema that
     * zwangine-openapi-java generates from Rest DSL workflows.
     */
    public RestConfigurationDefinition useXForwardHeaders(boolean useXForwardHeaders) {
        setUseXForwardHeaders(useXForwardHeaders ? "true" : "false");
        return this;
    }

    /**
     * Whether to use X-Forward headers to set host etc. for OpenApi.
     *
     * This may be needed in special cases involving reverse-proxy and networking going from HTTP to HTTPS etc. Then the
     * proxy can send X-Forward headers (X-Forwarded-Proto) that influences the host names in the OpenAPI schema that
     * zwangine-openapi-java generates from Rest DSL workflows.
     */
    public RestConfigurationDefinition useXForwardHeaders(String useXForwardHeaders) {
        setUseXForwardHeaders(useXForwardHeaders);
        return this;
    }

    // Implementation
    // -------------------------------------------------------------------------

    /**
     * Configured an instance of a {@link org.zenithblox.spi.RestConfiguration} instance based on the definition
     *
     * @param  context   the zwangine context
     * @param  target    the {@link org.zenithblox.spi.RestConfiguration} target
     * @return           the configuration
     * @throws Exception is thrown if error creating the configuration
     */
    public RestConfiguration asRestConfiguration(ZwangineContext context, RestConfiguration target) throws Exception {
        if (component != null) {
            target.setComponent(ZwangineContextHelper.parseText(context, component));
        }
        if (apiComponent != null) {
            target.setApiComponent(ZwangineContextHelper.parseText(context, apiComponent));
        }
        if (producerComponent != null) {
            target.setProducerComponent(ZwangineContextHelper.parseText(context, producerComponent));
        }
        if (scheme != null) {
            target.setScheme(ZwangineContextHelper.parseText(context, scheme));
        }
        if (host != null) {
            target.setHost(ZwangineContextHelper.parseText(context, host));
        }
        if (useXForwardHeaders != null) {
            target.setUseXForwardHeaders(ZwangineContextHelper.parseBoolean(context, useXForwardHeaders));
        }
        if (apiHost != null) {
            target.setApiHost(ZwangineContextHelper.parseText(context, apiHost));
        }
        if (port != null) {
            target.setPort(ZwangineContextHelper.parseInteger(context, port));
        }
        if (producerApiDoc != null) {
            target.setProducerApiDoc(ZwangineContextHelper.parseText(context, producerApiDoc));
        }
        if (apiContextPath != null) {
            target.setApiContextPath(ZwangineContextHelper.parseText(context, apiContextPath));
        }
        if (apiContextWorkflowId != null) {
            target.setApiContextWorkflowId(ZwangineContextHelper.parseText(context, apiContextWorkflowId));
        }
        if (apiVendorExtension != null) {
            target.setApiVendorExtension(ZwangineContextHelper.parseBoolean(context, apiVendorExtension));
        }
        if (contextPath != null) {
            target.setContextPath(ZwangineContextHelper.parseText(context, contextPath));
        }
        if (hostNameResolver != null) {
            target.setHostNameResolver(hostNameResolver.name());
        }
        if (bindingMode != null) {
            target.setBindingMode(bindingMode.name());
        }
        if (bindingPackageScan != null) {
            target.setBindingPackageScan(bindingPackageScan);
        }
        if (skipBindingOnErrorCode != null) {
            target.setSkipBindingOnErrorCode(ZwangineContextHelper.parseBoolean(context, skipBindingOnErrorCode));
        }
        if (clientRequestValidation != null) {
            target.setClientRequestValidation(ZwangineContextHelper.parseBoolean(context, clientRequestValidation));
        }
        if (enableCORS != null) {
            target.setEnableCORS(ZwangineContextHelper.parseBoolean(context, enableCORS));
        }
        if (enableNoContentResponse != null) {
            target.setEnableNoContentResponse(ZwangineContextHelper.parseBoolean(context, enableNoContentResponse));
        }
        if (inlineWorkflows != null) {
            target.setInlineWorkflows(ZwangineContextHelper.parseBoolean(context, inlineWorkflows));
        }
        if (jsonDataFormat != null) {
            target.setJsonDataFormat(jsonDataFormat);
        }
        if (xmlDataFormat != null) {
            target.setXmlDataFormat(xmlDataFormat);
        }
        if (!componentProperties.isEmpty()) {
            Map<String, Object> props = new HashMap<>();
            for (RestPropertyDefinition prop : componentProperties) {
                String key = prop.getKey();
                String value = ZwangineContextHelper.parseText(context, prop.getValue());
                props.put(key, value);
            }
            target.setComponentProperties(props);
        }
        if (!endpointProperties.isEmpty()) {
            Map<String, Object> props = new HashMap<>();
            for (RestPropertyDefinition prop : endpointProperties) {
                String key = prop.getKey();
                String value = ZwangineContextHelper.parseText(context, prop.getValue());
                props.put(key, value);
            }
            target.setEndpointProperties(props);
        }
        if (!consumerProperties.isEmpty()) {
            Map<String, Object> props = new HashMap<>();
            for (RestPropertyDefinition prop : consumerProperties) {
                String key = prop.getKey();
                String value = ZwangineContextHelper.parseText(context, prop.getValue());
                props.put(key, value);
            }
            target.setConsumerProperties(props);
        }
        if (!dataFormatProperties.isEmpty()) {
            Map<String, Object> props = new HashMap<>();
            for (RestPropertyDefinition prop : dataFormatProperties) {
                String key = prop.getKey();
                String value = ZwangineContextHelper.parseText(context, prop.getValue());
                props.put(key, value);
            }
            target.setDataFormatProperties(props);
        }
        if (!apiProperties.isEmpty()) {
            Map<String, Object> props = new HashMap<>();
            for (RestPropertyDefinition prop : apiProperties) {
                String key = prop.getKey();
                String value = ZwangineContextHelper.parseText(context, prop.getValue());
                props.put(key, value);
            }
            target.setApiProperties(props);
        }
        if (!corsHeaders.isEmpty()) {
            Map<String, String> props = new HashMap<>();
            for (RestPropertyDefinition prop : corsHeaders) {
                String key = prop.getKey();
                String value = ZwangineContextHelper.parseText(context, prop.getValue());
                props.put(key, value);
            }
            target.setCorsHeaders(props);
        }
        return target;
    }

}
