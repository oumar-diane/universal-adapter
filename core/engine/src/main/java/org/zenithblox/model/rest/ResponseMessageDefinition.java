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

import org.zenithblox.spi.Metadata;
import org.zenithblox.util.StringHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * To specify the rest operation response messages.
 */
@Metadata(label = "rest")
public class ResponseMessageDefinition {

    private VerbDefinition verb;

    @Metadata(defaultValue = "200")
    private String code;
    private String message;
    private String responseModel;
    private List<ResponseHeaderDefinition> headers;
    private List<RestPropertyDefinition> examples;

    public ResponseMessageDefinition(VerbDefinition verb) {
        this();
        this.verb = verb;
    }

    public ResponseMessageDefinition() {
        this.code = "200";
        this.message = "success";
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getResponseModel() {
        return responseModel;
    }

    public void setResponseModel(String responseModel) {
        this.responseModel = responseModel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ResponseHeaderDefinition> getHeaders() {
        return headers;
    }

    public void setHeaders(List<ResponseHeaderDefinition> headers) {
        this.headers = headers;
    }

    public List<RestPropertyDefinition> getExamples() {
        return examples;
    }

    /**
     * Examples of response messages
     */
    public void setExamples(List<RestPropertyDefinition> examples) {
        this.examples = examples;
    }

    /**
     * The response code such as a HTTP status code
     */
    public ResponseMessageDefinition code(int code) {
        setCode(Integer.toString(code));
        return this;
    }

    /**
     * The response code such as a HTTP status code. Can use <tt>general</tt>, or other words to indicate general error
     * responses that do not map to a specific HTTP status code
     */
    public ResponseMessageDefinition code(String code) {
        setCode(code);
        return this;
    }

    /**
     * The response message (description)
     */
    public ResponseMessageDefinition message(String msg) {
        setMessage(msg);
        return this;
    }

    /**
     * The response model
     */
    public ResponseMessageDefinition responseModel(Class<?> type) {
        setResponseModel(type.getCanonicalName());
        return this;
    }

    /**
     * Adds an example
     */
    public ResponseMessageDefinition example(String key, String example) {
        if (examples == null) {
            examples = new ArrayList<>();
        }
        examples.add(new RestPropertyDefinition(key, example));
        return this;
    }

    /**
     * Adds a response header
     */
    public ResponseHeaderDefinition header(String name) {
        if (headers == null) {
            headers = new ArrayList<>();
        }
        ResponseHeaderDefinition header = new ResponseHeaderDefinition(this);
        header.setName(name);
        headers.add(header);
        return header;
    }

    /**
     * Ends the configuration of this response message
     */
    public RestDefinition endResponseMessage() {
        // code and message is mandatory
        StringHelper.notEmpty(code, "code");
        StringHelper.notEmpty(message, "message");
        verb.getResponseMsgs().add(this);
        return verb.getRest();
    }

}
