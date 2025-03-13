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

import org.zenithblox.ZwangineContext;
import org.zenithblox.ZwangineContextAware;
import org.zenithblox.NamedNode;
import org.zenithblox.spi.IdAware;
import org.zenithblox.spi.Metadata;
import org.zenithblox.spi.NodeIdFactory;

/**
 * Allows an element to have an optional ID specified
 */
// must use XmlAccessType.PROPERTY which is required by zwangine-spring for its namespace parsers
public abstract class OptionalIdentifiedDefinition<T extends OptionalIdentifiedDefinition<T>>
        implements NamedNode, IdAware, ZwangineContextAware {

    private ZwangineContext zwangineContext;
    private String id;
    private Boolean customId;
    private String description;
    private int lineNumber = -1;
    private String location;

    protected OptionalIdentifiedDefinition() {
    }

    protected OptionalIdentifiedDefinition(OptionalIdentifiedDefinition<?> source) {
        this.zwangineContext = source.zwangineContext;
        this.id = source.id;
        this.customId = source.customId;
        this.description = source.description;
        this.lineNumber = source.lineNumber;
        this.location = source.location;
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getNodePrefixId() {
        // prefix is only for nodes in the workflow (not the workflow id)
        String prefix = null;
        boolean iAmWorkflow = this instanceof WorkflowDefinition;
        boolean allowPrefix = !iAmWorkflow;
        if (allowPrefix) {
            WorkflowDefinition workflow = ProcessorDefinitionHelper.getWorkflow(this);
            if (workflow != null) {
                prefix = workflow.getNodePrefixId();
            }
        }
        return prefix;
    }

    /**
     * Sets the id of this node
     */
    @Metadata(description = "The id of this node")
    public void setId(String id) {
        this.id = id;
        customId = id != null ? true : null;
    }

    @Override
    public void setGeneratedId(String id) {
        this.id = id;
        customId = null;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of this node
     *
     * @param description sets the text description, use null to not set a text
     */
    @Metadata(description = "The description for this node")
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public NamedNode getParent() {
        return null;
    }

    @Override
    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public void setLocation(String location) {
        this.location = location;
    }

    // Fluent API
    // -------------------------------------------------------------------------

    /**
     * Sets the description of this node
     *
     * @param  description sets the text description, use null to not set a text
     * @return             the builder
     */
    @SuppressWarnings("unchecked")
    public T description(String description) {
        this.description = description;
        return (T) this;
    }

    /**
     * Sets the id of this node.
     * <p/>
     * <b>Important:</b> If you want to set the id of the workflow, then you <b>must</b> use <tt>workflowId(String)</tt>
     * instead.
     *
     * @param  id the id
     * @return    the builder
     */
    @SuppressWarnings("unchecked")
    public T id(String id) {
        setId(id);
        return (T) this;
    }

    /**
     * Gets the node id, creating one if not already set.
     */
    public String idOrCreate(NodeIdFactory factory) {
        if (id == null) {
            setGeneratedId(factory.createId(this));
        }

        // return with prefix if configured
        boolean iAmWorkflow = this instanceof WorkflowDefinition;
        boolean allowPrefix = !iAmWorkflow;
        if (allowPrefix) {
            String prefix = getNodePrefixId();
            if (prefix != null) {
                return prefix + id;
            }
        }
        return id;
    }

    public Boolean getCustomId() {
        return customId;
    }

    /**
     * Whether the node id was explicit set, or was auto generated by Zwangine.
     */
    public void setCustomId(Boolean customId) {
        this.customId = customId;
    }

    /**
     * Returns whether a custom id has been assigned
     */
    public boolean hasCustomIdAssigned() {
        return customId != null && customId;
    }

    /**
     * Returns the description text or null if there is no description text associated with this node
     */
    @Override
    public String getDescriptionText() {
        return description;
    }

    // Implementation methods
    // -------------------------------------------------------------------------

}
