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

import org.zenithblox.StaticService;
import org.zenithblox.VariableAware;

/**
 * Repository for storing and accessing variables.
 */
public interface VariableRepository extends StaticService, VariableAware {

    /**
     * The id of this repository.
     */
    String getId();

    /**
     * Removes the given variable
     *
     * @param  name of the variable
     * @return      the old value of the variable, or <tt>null</tt> if there was no variable for the given name
     */
    Object removeVariable(String name);

}
