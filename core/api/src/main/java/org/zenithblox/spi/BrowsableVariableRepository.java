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

import java.util.Map;
import java.util.stream.Stream;

/**
 * A {@link VariableRepository} that can browse the variables.
 */
public interface BrowsableVariableRepository extends VariableRepository {

    /**
     * Are there any variables in the repository.
     */
    boolean hasVariables();

    /**
     * Number of variables
     */
    int size();

    /**
     * The variable names
     */
    Stream<String> names();

    /**
     * Gets all the variables in a Map
     */
    Map<String, Object> getVariables();

    /**
     * Removes all variables
     */
    void clear();

}
