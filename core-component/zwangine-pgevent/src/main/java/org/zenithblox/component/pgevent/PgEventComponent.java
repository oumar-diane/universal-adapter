/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
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
package org.zenithblox.component.pgevent;

import org.zenithblox.Endpoint;
import org.zenithblox.spi.annotations.Component;
import org.zenithblox.support.DefaultComponent;

import java.util.Map;

/**
 * Represents the component that manages {@link PgEventEndpoint}.
 */
@Component("pgevent")
public class PgEventComponent extends DefaultComponent {

    public PgEventComponent() {
    }

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        Endpoint endpoint = new PgEventEndpoint(uri, this);
        setProperties(endpoint, parameters);
        return endpoint;
    }
}
