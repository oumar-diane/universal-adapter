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

import org.zenithblox.model.OptionalIdentifiedDefinition;
import org.zenithblox.model.ToDefinition;
import org.zenithblox.spi.Metadata;

import java.util.ArrayList;
import java.util.List;

/**
 * A rest operation (such as GET, POST etc.)
 */
@Metadata(label = "rest")
public abstract class VerbDefinition extends OptionalIdentifiedDefinition<VerbDefinition> {

    private RestDefinition rest;

    private List<ParamDefinition> params = new ArrayList<>();
    private List<ResponseMessageDefinition> responseMsgs = new ArrayList<>();
    private List<SecurityDefinition> security = new ArrayList<>();

    private String path;
    private String consumes;
    private String produces;
    @Metadata(label = "advanced", javaType = "java.lang.Boolean")
    private String disabled;
    @Metadata(label = "advanced")
    private String type;
    private Class<?> typeClass;
    @Metadata(label = "advanced")
    private String outType;
    private Class<?> outTypeClass;
    @Metadata(defaultValue = "off", enums = "off,auto,json,xml,json_xml")
    private String bindingMode;
    @Metadata(label = "advanced", javaType = "java.lang.Boolean", defaultValue = "false")
    private String skipBindingOnErrorCode;
    @Metadata(label = "advanced", javaType = "java.lang.Boolean", defaultValue = "false")
    private String clientRequestValidation;
    @Metadata(label = "advanced", javaType = "java.lang.Boolean", defaultValue = "false")
    private String enableCORS;
    @Metadata(label = "advanced", javaType = "java.lang.Boolean", defaultValue = "false")
    private String enableNoContentResponse;
    @Metadata(label = "advanced", javaType = "java.lang.Boolean", defaultValue = "true")
    private String apiDocs;
    @Metadata(label = "advanced", javaType = "java.lang.Boolean", defaultValue = "false")
    private String deprecated;
    @Metadata(label = "advanced", javaType = "java.lang.Boolean")
    private String streamCache;
    private String workflowId;
    private ToDefinition to;

    @Override
    public String getShortName() {
        return "verb";
    }

    @Override
    public String getLabel() {
        return "verb";
    }

    public String getDeprecated() {
        return deprecated;
    }

    /**
     * Marks this rest operation as deprecated in OpenApi documentation.
     */
    public void setDeprecated(String deprecated) {
        this.deprecated = deprecated;
    }

    public VerbDefinition deprecated() {
        this.deprecated = "true";
        return this;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    /**
     * Whether stream caching is enabled on this rest operation.
     */
    public String getStreamCache() {
        return streamCache;
    }

    /**
     * Whether stream caching is enabled on this rest operation.
     */
    public void setStreamCache(String streamCache) {
        this.streamCache = streamCache;
    }

    /**
     * Enable or disables stream caching for this rest operation.
     *
     * @param  streamCache whether to use stream caching (true or false), the value can be a property placeholder
     * @return             the builder
     */
    public VerbDefinition streamCache(String streamCache) {
        setStreamCache(streamCache);
        return this;
    }

    /**
     * Sets the id of the workflow
     */
    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public List<ParamDefinition> getParams() {
        return params;
    }

    /**
     * To specify the REST operation parameters.
     */
    public void setParams(List<ParamDefinition> params) {
        this.params = params;
    }

    public List<ResponseMessageDefinition> getResponseMsgs() {
        return responseMsgs;
    }

    /**
     * Sets operation response messages.
     */
    public void setResponseMsgs(List<ResponseMessageDefinition> responseMsgs) {
        this.responseMsgs = responseMsgs;
    }

    public List<SecurityDefinition> getSecurity() {
        return security;
    }

    /**
     * Sets the security settings for this verb.
     */
    public void setSecurity(List<SecurityDefinition> security) {
        this.security = security;
    }

    public String getPath() {
        return path;
    }

    /**
     * The path mapping URIs of this REST operation such as /{id}.
     */
    public void setPath(String path) {
        this.path = path;
    }

    public String getConsumes() {
        return consumes;
    }

    /**
     * To define the content type what the REST service consumes (accept as input), such as application/xml or
     * application/json. This option will override what may be configured on a parent level
     */
    public void setConsumes(String consumes) {
        this.consumes = consumes;
    }

    public String getProduces() {
        return produces;
    }

    /**
     * To define the content type what the REST service produces (uses for output), such as application/xml or
     * application/json This option will override what may be configured on a parent level
     */
    public void setProduces(String produces) {
        this.produces = produces;
    }

    public String getDisabled() {
        return disabled;
    }

    /**
     * Whether to disable this REST service from the workflow during build time. Once an REST service has been disabled
     * then it cannot be enabled later at runtime.
     */
    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }

    public String getBindingMode() {
        return bindingMode;
    }

    /**
     * Sets the binding mode to use. This option will override what may be configured on a parent level
     * <p/>
     * The default value is off
     */
    public void setBindingMode(String bindingMode) {
        this.bindingMode = bindingMode;
    }

    public String getSkipBindingOnErrorCode() {
        return skipBindingOnErrorCode;
    }

    /**
     * Whether to skip binding on output if there is a custom HTTP error code header. This allows to build custom error
     * messages that do not bind to json / xml etc, as success messages otherwise will do. This option will override
     * what may be configured on a parent level
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
     * Whether to enable CORS headers in the HTTP response. This option will override what may be configured on a parent
     * level
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

    public String getType() {
        return type;
    }

    /**
     * Sets the class name to use for binding from input to POJO for the incoming data This option will override what
     * may be configured on a parent level.
     * <p/>
     * The name of the class of the input data. Append a [] to the end of the name if you want the input to be an array
     * type.
     */
    public void setType(String type) {
        this.type = type;
    }

    public Class<?> getTypeClass() {
        return typeClass;
    }

    /**
     * Sets the class to use for binding from input to POJO for the incoming data This option will override what may be
     * configured on a parent level.
     */
    public void setTypeClass(Class<?> typeClass) {
        this.typeClass = typeClass;
    }

    public String getOutType() {
        return outType;
    }

    /**
     * Sets the class name to use for binding from POJO to output for the outgoing data This option will override what
     * may be configured on a parent level
     * <p/>
     * The name of the class of the input data. Append a [] to the end of the name if you want the input to be an array
     * type.
     */
    public void setOutType(String outType) {
        this.outType = outType;
    }

    public Class<?> getOutTypeClass() {
        return outTypeClass;
    }

    /**
     * Sets the class to use for binding from POJO to output for the outgoing data This option will override what may be
     * configured on a parent level.
     */
    public void setOutTypeClass(Class<?> outTypeClass) {
        this.outTypeClass = outTypeClass;
    }

    public String getApiDocs() {
        return apiDocs;
    }

    /**
     * Whether to include or exclude this rest operation in API documentation.
     * <p/>
     * The default value is true.
     */
    public void setApiDocs(String apiDocs) {
        this.apiDocs = apiDocs;
    }

    public ToDefinition getTo() {
        return to;
    }

    /**
     * The Zwangine endpoint this REST service will call, such as a direct endpoint to link to an existing workflow that
     * handles this REST call.
     */
    public void setTo(ToDefinition to) {
        if (this.to != null) {
            throw new IllegalArgumentException(
                    "This verb has already set to endpoint. It is not possible to configure multiple 'to' with Rest DSL.");
        }
        this.to = to;
    }

    public RestDefinition getRest() {
        return rest;
    }

    public void setRest(RestDefinition rest) {
        this.rest = rest;
    }

    // Fluent API
    // -------------------------------------------------------------------------

    public RestDefinition get() {
        return rest.get();
    }

    public RestDefinition get(String uri) {
        return rest.get(uri);
    }

    public RestDefinition post() {
        return rest.post();
    }

    public RestDefinition post(String uri) {
        return rest.post(uri);
    }

    public RestDefinition put() {
        return rest.put();
    }

    public RestDefinition put(String uri) {
        return rest.put(uri);
    }

    public RestDefinition delete() {
        return rest.delete();
    }

    public RestDefinition delete(String uri) {
        return rest.delete(uri);
    }

    public RestDefinition head() {
        return rest.head();
    }

    public RestDefinition head(String uri) {
        return rest.head(uri);
    }

    public RestDefinition verb(String verb) {
        return rest.verb(verb);
    }

    public RestDefinition verb(String verb, String uri) {
        return rest.verb(verb, uri);
    }

    public abstract String asVerb();

}
