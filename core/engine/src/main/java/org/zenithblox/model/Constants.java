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
package org.zenithblox.model;

/**
 * Various constants.
 */
public final class Constants {

    public static final String JAXB_CONTEXT_PACKAGES = "org.zenithblox:"
                                                       + "org.zenithblox.model:"
                                                       + "org.zenithblox.model.app:"
                                                       + "org.zenithblox.model.cloud:"
                                                       + "org.zenithblox.model.config:"
                                                       + "org.zenithblox.model.dataformat:"
                                                       + "org.zenithblox.model.errorhandler:"
                                                       + "org.zenithblox.model.language:"
                                                       + "org.zenithblox.model.loadbalancer:"
                                                       + "org.zenithblox.model.rest:"
                                                       + "org.zenithblox.model.transformer:"
                                                       + "org.zenithblox.model.validator";

    public static final String PLACEHOLDER_QNAME = "http://zwangine.zentihblox.org/schema/placeholder";

    private Constants() {
    }

}
