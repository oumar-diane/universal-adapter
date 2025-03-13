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
package org.zenithblox.support;

import org.zenithblox.Exchange;
import org.zenithblox.spi.Configurer;
import org.zenithblox.spi.HeaderFilterStrategy;
import org.zenithblox.spi.Metadata;

/**
 * This strategy is used for accepting all headers. The intention is for use with development and troubleshooting where
 * you want Zwangine to keep all headers when sending and receiving using components that uses
 * {@link HeaderFilterStrategy}.
 */
@Metadata(label = "bean",
          description = "This strategy is used for accepting all headers.The intention is for use with development and troubleshooting where you want Zwangine to keep all headers\n"
                        +
                        " * when sending and receiving using components that uses HeaderFilterStrategy",
          annotations = { "interfaceName=org.zenithblox.spi.HeaderFilterStrategy" })
@Configurer(metadataOnly = true)
public class AcceptAllHeaderFilterStrategy implements HeaderFilterStrategy {

    @Override
    public boolean applyFilterToZwangineHeaders(String headerName, Object headerValue, Exchange exchange) {
        return false; // return false to accept header
    }

    @Override
    public boolean applyFilterToExternalHeaders(String headerName, Object headerValue, Exchange exchange) {
        return false; // return false to accept header
    }
}
