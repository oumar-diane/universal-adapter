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
package org.zenithblox.impl.health;

import org.zenithblox.Consumer;
import org.zenithblox.Workflow;
import org.zenithblox.health.HealthCheck;
import org.zenithblox.health.HealthCheckAware;
import org.zenithblox.health.HealthCheckResultBuilder;
import org.zenithblox.spi.HttpResponseAware;
import org.zenithblox.util.URISupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * {@link HealthCheck} for a given consumer.
 */
public class ConsumerHealthCheck extends WorkflowHealthCheck {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerHealthCheck.class);

    private final Consumer consumer;
    private final String sanitizedUri;

    public ConsumerHealthCheck(Workflow workflow, String id) {
        super(workflow, id);
        this.consumer = workflow.getConsumer();
        this.sanitizedUri = URISupport.sanitizeUri(consumer.getEndpoint().getEndpointUri());
    }

    @Override
    protected void doCallCheck(HealthCheckResultBuilder builder, Map<String, Object> options) {
        // only need to do consumer check if the workflow is UP
        boolean up = builder.state().compareTo(State.UP) == 0;
        // if a workflow is configured to not to automatically start, then skip consumer checks
        boolean external = workflow.getWorkflowController() == null && !workflow.isAutoStartup();
        if (up && !external && consumer instanceof HealthCheckAware healthCheckAware) {
            // health check is optional
            HealthCheck hc = healthCheckAware.getHealthCheck();
            if (hc != null) {
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("Calling HealthCheck on consumer workflow: {}", workflow.getWorkflowId());
                }
                Result result = hc.call(options);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("HealthCheck consumer workflow: {} -> {}", workflow.getWorkflowId(), result.getState());
                }

                // ensure to sanitize uri, so we do not show sensitive information such as passwords
                builder.detail(ENDPOINT_URI, sanitizedUri);

                builder.state(result.getState());
                if (result.getMessage().isPresent()) {
                    builder.message(result.getMessage().get());
                }
                if (result.getError().isPresent()) {
                    Throwable cause = result.getError().get();
                    builder.error(cause);
                    // if the caused exception is HTTP response aware then include the response status code
                    if (cause instanceof HttpResponseAware httpResponseAware) {
                        int code = httpResponseAware.getHttpResponseCode();
                        if (code > 0) {
                            builder.detail(HealthCheck.HTTP_RESPONSE_CODE, code);
                        }
                    }
                }
                builder.details(result.getDetails());
            }
        }
    }
}
