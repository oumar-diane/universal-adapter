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

import org.zenithblox.Experimental;
import org.zenithblox.spi.UuidGenerator;

/**
 * {@link UuidGenerator} which is turned off for exchange ids, but generated UUIDs for everything else.
 *
 * This is only intended for development for performance profiling - do not use in production. Some EIPs and
 * functionalities of Zwangine requires exchange IDs to be unique and this generated will therefore not work in all
 * situations.
 */
@Experimental
public class OffUuidGenerator extends DefaultUuidGenerator {

    @Override
    public String generateExchangeUuid() {
        return "";
    }
}
