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

/**
 * Factory for intercepting sending to endpoint pattern and sending to mock component. This requires having zwangine-mock
 * on the classpath.
 */
public interface MockSendToEndpointStrategyFactory {

    String FACTORY = "mock-send-to-endpoint-strategy-factory";

    /**
     * Mock sending to endpoint
     *
     * @param  pattern pattern for intercepting (null or * = intercept all, otherwise its an text pattern (and regexp).
     * @param  skip    whether to skip sending to original endpoint (only to mock endpoint).
     * @return         the endpoint strategy that intercepts.
     */
    EndpointStrategy mock(String pattern, boolean skip);

}
