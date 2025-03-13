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
package org.zenithblox.model.app;

import org.zenithblox.model.*;
import org.zenithblox.model.rest.RestConfigurationDefinition;
import org.zenithblox.model.rest.RestDefinition;
import org.zenithblox.spi.Metadata;
import org.zenithblox.spi.annotations.DslProperty;
import org.zenithblox.spi.annotations.ExternalSchemaElement;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Container for beans, workflows, and more.
 *
 * Important this is only supported when using XML DSL with zwangine-xml-io-dsl. This is NOT for the classic old Spring XML
 * DSL used by Zwangine 1.x/2.x.
 */
@Metadata(label = "configuration")
public class BeansDefinition {

    // This class is not meant to be used with Zwangine Java DSL, but it's needed to generate XML Schema and MX parser methods

    private List<ComponentScanDefinition> componentScanning = new ArrayList<>();

    // this is a place for <bean> element definition, without conflicting with <bean> elements referring
    // to "bean processors"
    private List<BeanFactoryDefinition> beans = new ArrayList<>();

    // support for legacy spring <beans> and blueprint <bean> files to be parsed and loaded
    // for migration and tooling effort (need to be in a single @XmlAnyElement as otherwise
    // this causes zwangine-spring-xml to generate an invalid XSD
    @ExternalSchemaElement(names = { "beans", "bean", "alias" }, names2 = "bean",
                           namespace = "http://www.springframework.org/schema/beans",
                           namespace2 = "http://www.osgi.org/xmlns/blueprint/v1.0.0",
                           documentElement = "beans",
                           documentElement2 = "blueprint")
    private List<Element> springOrBlueprintBeans = new ArrayList<>();

    // the order comes from <zwangineContext> (org.zenithblox.spring.xml.ZwangineContextFactoryBean)
    // to make things less confusing, as it's not easy to simply tell JAXB to use <xsd:choice maxOccurs="unbounded">
    // over a set of unrelated elements

    // initially we'll be supporting only these elements which are parsed by
    // org.zenithblox.dsl.xml.io.XmlWorkflowsBuilderLoader in zwangine-xml-io-dsl

    @DslProperty(name = "dataFormats") // yaml-dsl
    private List<DataFormatDefinition> dataFormats;
    private List<RestConfigurationDefinition> restConfigurations = new ArrayList<>();
    private List<RestDefinition> rests = new ArrayList<>();
    private List<WorkflowConfigurationDefinition> workflowConfigurations = new ArrayList<>();
    private List<WorkflowTemplateDefinition> workflowTemplates = new ArrayList<>();
    private List<TemplatedWorkflowDefinition> templatedWorkflows = new ArrayList<>();
    private List<WorkflowDefinition> workflows = new ArrayList<>();

    public List<ComponentScanDefinition> getComponentScanning() {
        return componentScanning;
    }

    /**
     * Component scanning that can auto-discover Zwangine workflow builders from the classpath.
     */
    public void setComponentScanning(List<ComponentScanDefinition> componentScanning) {
        this.componentScanning = componentScanning;
    }

    public List<BeanFactoryDefinition> getBeans() {
        return beans;
    }

    /**
     * List of bean
     */
    public void setBeans(List<BeanFactoryDefinition> beans) {
        this.beans = beans;
    }

    public List<Element> getSpringOrBlueprintBeans() {
        return springOrBlueprintBeans;
    }

    /**
     * Support for legacy Spring beans and Blueprint bean files to be parsed and loaded for migration and tooling
     * effort.
     */
    public void setSpringOrBlueprintBeans(List<Element> springOrBlueprintBeans) {
        this.springOrBlueprintBeans = springOrBlueprintBeans;
    }

    public List<RestConfigurationDefinition> getRestConfigurations() {
        return restConfigurations;
    }

    /**
     * Zwangine Rest DSL Configuration
     */
    public void setRestConfigurations(List<RestConfigurationDefinition> restConfigs) {
        this.restConfigurations = restConfigs;
    }

    public List<RestDefinition> getRests() {
        return rests;
    }

    /**
     * Zwangine Rest DSL
     */
    public void setRests(List<RestDefinition> rests) {
        this.rests = rests;
    }

    public List<WorkflowConfigurationDefinition> getWorkflowConfigurations() {
        return workflowConfigurations;
    }

    /**
     * Zwangine workflow configurations
     */
    public void setWorkflowConfigurations(List<WorkflowConfigurationDefinition> workflowConfigurations) {
        this.workflowConfigurations = workflowConfigurations;
    }

    public List<WorkflowTemplateDefinition> getWorkflowTemplates() {
        return workflowTemplates;
    }

    /**
     * Zwangine workflow templates
     */
    public void setWorkflowTemplates(List<WorkflowTemplateDefinition> workflowTemplates) {
        this.workflowTemplates = workflowTemplates;
    }

    public List<TemplatedWorkflowDefinition> getTemplatedWorkflows() {
        return templatedWorkflows;
    }

    /**
     * Zwangine workflows to be created from template
     */
    public void setTemplatedWorkflows(List<TemplatedWorkflowDefinition> templatedWorkflows) {
        this.templatedWorkflows = templatedWorkflows;
    }

    public List<WorkflowDefinition> getWorkflows() {
        return workflows;
    }

    /**
     * Zwangine workflows
     */
    public void setWorkflows(List<WorkflowDefinition> workflows) {
        this.workflows = workflows;
    }

    public List<DataFormatDefinition> getDataFormats() {
        return dataFormats;
    }

    /**
     * Zwangine data formats
     */
    public void setDataFormats(List<DataFormatDefinition> dataFormats) {
        this.dataFormats = dataFormats;
    }

}
