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
package org.zenithblox;

/**
 * To perform optional initialization on an element after its properties has been configured.
 * <p/>
 * Currently only languages is supported using this callback.
 */
public interface AfterPropertiesConfigured {

    /**
     * Callback invoked after the element have configured its properties.
     * <p/>
     * This allows to perform any post init work.
     *
     * @param zwangineContext the Zwangine Context
     */
    void afterPropertiesConfigured(ZwangineContext zwangineContext);
}
