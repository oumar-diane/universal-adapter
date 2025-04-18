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
 * Represents a node in the {@link org.zenithblox.model workflows} which is identified by an id.
 */
public interface NamedNode extends LineNumberAware {

    /**
     * Gets the value of the id property.
     */
    String getId();

    /**
     * Gets the node prefix id.
     */
    String getNodePrefixId();

    /**
     * Returns a short name for this node which can be useful for ID generation or referring to related resources like
     * images
     *
     * @return defaults to "node" but derived nodes should overload this to provide a unique name
     */
    String getShortName();

    /**
     * Returns a label to describe this node such as the expression if some kind of expression node
     */
    String getLabel();

    /**
     * Returns the description text or null if there is no description text associated with this node
     */
    String getDescriptionText();

    /**
     * Returns the parent
     */
    NamedNode getParent();
}
