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
package org.zenithblox.impl;

import org.zenithblox.ZwangineContext;
import org.zenithblox.ZwangineContextAware;
import org.zenithblox.NamedNode;
import org.zenithblox.model.*;
import org.zenithblox.model.rest.RestDefinition;
import org.zenithblox.model.rest.RestsDefinition;
import org.zenithblox.spi.DumpWorkflowsStrategy;
import org.zenithblox.spi.ModelToXMLDumper;
import org.zenithblox.spi.ModelToYAMLDumper;
import org.zenithblox.spi.Resource;
import org.zenithblox.spi.annotations.JdkService;
import org.zenithblox.support.PluginHelper;
import org.zenithblox.support.ResourceSupport;
import org.zenithblox.support.service.ServiceSupport;
import org.zenithblox.util.FileUtil;
import org.zenithblox.util.IOHelper;
import org.zenithblox.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.zenithblox.support.LoggerHelper.stripSourceLocationLineNumber;

/**
 * Default {@link DumpWorkflowsStrategy} that dumps the workflows to standard logger.
 */
@JdkService("default-" + DumpWorkflowsStrategy.FACTORY)
public class DefaultDumpWorkflowsStrategy extends ServiceSupport implements DumpWorkflowsStrategy, ZwangineContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDumpWorkflowsStrategy.class);
    private static final String DIVIDER = "--------------------------------------------------------------------------------";

    private final AtomicInteger counter = new AtomicInteger();
    private ZwangineContext zwangineContext;

    private String include = "workflows";
    private boolean resolvePlaceholders = true;
    private boolean uriAsParameters;
    private boolean generatedIds = true;
    private boolean log = true;
    private String output;
    private String outputFileName;

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    public String getInclude() {
        return include;
    }

    public void setInclude(String include) {
        this.include = include;
    }

    public boolean isResolvePlaceholders() {
        return resolvePlaceholders;
    }

    public void setResolvePlaceholders(boolean resolvePlaceholders) {
        this.resolvePlaceholders = resolvePlaceholders;
    }

    public boolean isGeneratedIds() {
        return generatedIds;
    }

    public void setGeneratedIds(boolean generatedIds) {
        this.generatedIds = generatedIds;
    }

    public boolean isLog() {
        return log;
    }

    public void setLog(boolean log) {
        this.log = log;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        String name = FileUtil.stripPath(output);
        if (name != null && name.contains(".")) {
            outputFileName = name;
            this.output = FileUtil.onlyPath(output);
            if (this.output == null || this.output.isEmpty()) {
                this.output = ".";
            }
        } else {
            this.output = output;
        }
    }

    public boolean isUriAsParameters() {
        return uriAsParameters;
    }

    public void setUriAsParameters(boolean uriAsParameters) {
        this.uriAsParameters = uriAsParameters;
    }

    @Override
    public void dumpWorkflows(String format) {
        if ("yaml".equalsIgnoreCase(format)) {
            doDumpWorkflowsAsYaml(zwangineContext);
        } else if ("xml".equalsIgnoreCase(format)) {
            doDumpWorkflowsAsXml(zwangineContext);
        }
    }

    protected void doDumpWorkflowsAsYaml(ZwangineContext zwangineContext) {
        final ModelToYAMLDumper dumper = PluginHelper.getModelToYAMLDumper(zwangineContext);
        final Model model = zwangineContext.getZwangineContextExtension().getContextPlugin(Model.class);
        final DummyResource dummy = new DummyResource(null, null);
        final Set<String> files = new HashSet<>();

        if (include.contains("*") || include.contains("all") || include.contains("beans")) {
            int size = model.getCustomBeans().size();
            if (size > 0) {
                Map<Resource, List<BeanFactoryDefinition>> groups = new LinkedHashMap<>();
                for (BeanFactoryDefinition bean : model.getCustomBeans()) {
                    Resource res = bean.getResource();
                    if (res == null) {
                        res = dummy;
                    }
                    List<BeanFactoryDefinition> beans = groups.computeIfAbsent(res, resource -> new ArrayList<>());
                    beans.add(bean);
                }
                StringBuilder sbLog = new StringBuilder();
                for (Map.Entry<Resource, List<BeanFactoryDefinition>> entry : groups.entrySet()) {
                    List<BeanFactoryDefinition> beans = entry.getValue();
                    Resource resource = entry.getKey();

                    StringBuilder sbLocal = new StringBuilder();
                    doDumpYamlBeans(zwangineContext, beans, resource == dummy ? null : resource, dumper, "beans", sbLocal, sbLog);
                    // dump each resource into its own file
                    doDumpToDirectory(resource, sbLocal, "beans", "yaml", files);
                }
                if (!sbLog.isEmpty() && log) {
                    LOG.info("Dumping {} beans as YAML", size);
                    LOG.info("{}", sbLog);
                }
            }
        }

        if (include.contains("*") || include.contains("all") || include.contains("dataFormats")) {
            int size = model.getDataFormats().size();
            if (size > 0) {
                Map<Resource, Map<String, DataFormatDefinition>> groups = new LinkedHashMap<>();
                for (Map.Entry<String, DataFormatDefinition> entry : model.getDataFormats().entrySet()) {
                    Resource res = entry.getValue().getResource();
                    if (res == null) {
                        res = dummy;
                    }
                    Map<String, DataFormatDefinition> dfs = groups.computeIfAbsent(res, resource -> new LinkedHashMap<>());
                    dfs.put(entry.getKey(), entry.getValue());
                }
                StringBuilder sbLog = new StringBuilder();
                for (Map.Entry<Resource, Map<String, DataFormatDefinition>> entry : groups.entrySet()) {
                    Map<String, DataFormatDefinition> dfs = entry.getValue();
                    Resource resource = entry.getKey();

                    StringBuilder sbLocal = new StringBuilder();
                    doDumpYamlDataFormats(zwangineContext, dfs, resource == dummy ? null : resource, dumper, "dataFormats",
                            sbLocal, sbLog);
                    // dump each resource into its own file
                    doDumpToDirectory(resource, sbLocal, "dataFormats", "yaml", files);
                }
                if (!sbLog.isEmpty() && log) {
                    LOG.info("Dumping {} data formats as YAML", size);
                    LOG.info("{}", sbLog);
                }
            }
        }

        if (include.contains("*") || include.contains("all") || include.contains("rests")) {
            int size = model.getRestDefinitions().size();
            if (size > 0) {
                Map<Resource, RestsDefinition> groups = new LinkedHashMap<>();
                for (RestDefinition rest : model.getRestDefinitions()) {
                    Resource res = rest.getResource();
                    if (res == null) {
                        res = dummy;
                    }
                    RestsDefinition rests = groups.computeIfAbsent(res, resource -> new RestsDefinition());
                    rests.getRests().add(rest);
                }
                StringBuilder sbLog = new StringBuilder();
                for (Map.Entry<Resource, RestsDefinition> entry : groups.entrySet()) {
                    RestsDefinition def = entry.getValue();
                    Resource resource = entry.getKey();

                    StringBuilder sbLocal = new StringBuilder();
                    doDumpYaml(zwangineContext, def, resource == dummy ? null : resource, dumper, "rests", sbLocal, sbLog);
                    // dump each resource into its own file
                    doDumpToDirectory(resource, sbLocal, "rests", "yaml", files);
                }
                if (!sbLog.isEmpty() && log) {
                    LOG.info("Dumping {} rests as YAML", size);
                    LOG.info("{}", sbLog);
                }
            }
        }

        if (include.contains("*") || include.contains("all") || include.contains("workflowConfigurations")
                || include.contains("workflow-configurations")) {
            int size = model.getWorkflowConfigurationDefinitions().size();
            if (size > 0) {
                Map<Resource, WorkflowConfigurationsDefinition> groups = new LinkedHashMap<>();
                for (WorkflowConfigurationDefinition config : model.getWorkflowConfigurationDefinitions()) {
                    Resource res = config.getResource();
                    if (res == null) {
                        res = dummy;
                    }
                    WorkflowConfigurationsDefinition workflows
                            = groups.computeIfAbsent(res, resource -> new WorkflowConfigurationsDefinition());
                    workflows.getWorkflowConfigurations().add(config);
                }
                StringBuilder sbLog = new StringBuilder();
                for (Map.Entry<Resource, WorkflowConfigurationsDefinition> entry : groups.entrySet()) {
                    WorkflowConfigurationsDefinition def = entry.getValue();
                    Resource resource = entry.getKey();

                    StringBuilder sbLocal = new StringBuilder();
                    doDumpYaml(zwangineContext, def, resource == dummy ? null : resource, dumper, "workflow-configurations", sbLocal,
                            sbLog);
                    // dump each resource into its own file
                    doDumpToDirectory(resource, sbLocal, "workflow-configurations", "yaml", files);
                }
                if (!sbLog.isEmpty() && log) {
                    LOG.info("Dumping {} workflow-configurations as YAML", size);
                    LOG.info("{}", sbLog);
                }
            }
        }

        if (include.contains("*") || include.contains("all") || include.contains("workflowTemplates")
                || include.contains("workflow-templates")) {
            int size = model.getWorkflowTemplateDefinitions().size();
            if (size > 0) {
                Map<Resource, WorkflowTemplatesDefinition> groups = new LinkedHashMap<>();
                for (WorkflowTemplateDefinition rt : model.getWorkflowTemplateDefinitions()) {
                    Resource res = rt.getResource();
                    if (res == null) {
                        res = dummy;
                    }
                    WorkflowTemplatesDefinition rests = groups.computeIfAbsent(res, resource -> new WorkflowTemplatesDefinition());
                    rests.getWorkflowTemplates().add(rt);
                }
                StringBuilder sbLog = new StringBuilder();
                for (Map.Entry<Resource, WorkflowTemplatesDefinition> entry : groups.entrySet()) {
                    WorkflowTemplatesDefinition def = entry.getValue();
                    Resource resource = entry.getKey();

                    StringBuilder sbLocal = new StringBuilder();
                    doDumpYaml(zwangineContext, def, resource == dummy ? null : resource, dumper, "workflow-templates", sbLocal,
                            sbLog);
                    // dump each resource into its own file
                    doDumpToDirectory(resource, sbLocal, "workflow-templates", "yaml", files);
                }
                if (!sbLog.isEmpty() && log) {
                    LOG.info("Dumping {} workflow-templates as YAML", size);
                    LOG.info("{}", sbLog);
                }
            }
        }

        if (include.contains("*") || include.contains("all") || include.contains("workflows")) {
            int size = model.getWorkflowDefinitions().size();
            if (size > 0) {
                Map<Resource, WorkflowsDefinition> groups = new LinkedHashMap<>();
                for (WorkflowDefinition workflow : model.getWorkflowDefinitions()) {
                    if ((workflow.isRest() != null && workflow.isRest()) || (workflow.isTemplate() != null && workflow.isTemplate())) {
                        // skip workflows that are rest/templates
                        continue;
                    }
                    Resource res = workflow.getResource();
                    if (res == null) {
                        res = dummy;
                    }
                    WorkflowsDefinition workflows = groups.computeIfAbsent(res, resource -> new WorkflowsDefinition());
                    workflows.getWorkflows().add(workflow);
                }
                StringBuilder sbLog = new StringBuilder();
                for (Map.Entry<Resource, WorkflowsDefinition> entry : groups.entrySet()) {
                    WorkflowsDefinition def = entry.getValue();
                    Resource resource = entry.getKey();

                    StringBuilder sbLocal = new StringBuilder();
                    doDumpYaml(zwangineContext, def, resource == dummy ? null : resource, dumper, "workflows", sbLocal, sbLog);
                    // dump each resource into its own file
                    doDumpToDirectory(resource, sbLocal, "workflows", "yaml", files);
                }
                if (!sbLog.isEmpty() && log) {
                    LOG.info("Dumping {} workflows as YAML", size);
                    LOG.info("{}", sbLog);
                }
            }
        }

    }

    protected void doDumpYaml(
            ZwangineContext zwangineContext, NamedNode def, Resource resource,
            ModelToYAMLDumper dumper, String kind, StringBuilder sbLocal, StringBuilder sbLog) {
        try {
            String dump = dumper.dumpModelAsYaml(zwangineContext, def, resolvePlaceholders, uriAsParameters, generatedIds);
            sbLocal.append(dump);
            appendLogDump(resource, dump, sbLog);
        } catch (Exception e) {
            LOG.warn("Error dumping {}} to YAML due to {}. This exception is ignored.", kind, e.getMessage(), e);
        }
    }

    protected void doDumpYamlBeans(
            ZwangineContext zwangineContext, List beans, Resource resource,
            ModelToYAMLDumper dumper, String kind, StringBuilder sbLocal, StringBuilder sbLog) {
        try {
            String dump = dumper.dumpBeansAsYaml(zwangineContext, beans);
            sbLocal.append(dump);
            appendLogDump(resource, dump, sbLog);
        } catch (Exception e) {
            LOG.warn("Error dumping {}} to YAML due to {}. This exception is ignored.", kind, e.getMessage(), e);
        }
    }

    protected void doDumpYamlDataFormats(
            ZwangineContext zwangineContext, Map dataFormats, Resource resource,
            ModelToYAMLDumper dumper, String kind, StringBuilder sbLocal, StringBuilder sbLog) {
        try {
            String dump = dumper.dumpDataFormatsAsYaml(zwangineContext, dataFormats);
            sbLocal.append(dump);
            appendLogDump(resource, dump, sbLog);
        } catch (Exception e) {
            LOG.warn("Error dumping {}} to YAML due to {}. This exception is ignored.", kind, e.getMessage(), e);
        }
    }

    protected void doDumpXmlDataFormats(
            ZwangineContext zwangineContext, Map dataFormats, Resource resource,
            ModelToXMLDumper dumper, String kind, StringBuilder sbLocal, StringBuilder sbLog) {
        try {
            String dump = dumper.dumpDataFormatsAsXml(zwangineContext, dataFormats);
            sbLocal.append(dump);
            appendLogDump(resource, dump, sbLog);
        } catch (Exception e) {
            LOG.warn("Error dumping {}} to XML due to {}. This exception is ignored.", kind, e.getMessage(), e);
        }
    }

    protected void doDumpWorkflowsAsXml(ZwangineContext zwangineContext) {
        final ModelToXMLDumper dumper = PluginHelper.getModelToXMLDumper(zwangineContext);
        final Model model = zwangineContext.getZwangineContextExtension().getContextPlugin(Model.class);
        final DummyResource dummy = new DummyResource(null, null);
        final Set<String> files = new HashSet<>();

        if (include.contains("*") || include.contains("all") || include.contains("beans")) {
            int size = model.getCustomBeans().size();
            if (size > 0) {
                Map<Resource, List<BeanFactoryDefinition>> groups = new LinkedHashMap<>();
                for (BeanFactoryDefinition bean : model.getCustomBeans()) {
                    Resource res = bean.getResource();
                    if (res == null) {
                        res = dummy;
                    }
                    List<BeanFactoryDefinition> beans = groups.computeIfAbsent(res, resource -> new ArrayList<>());
                    beans.add(bean);
                }
                StringBuilder sbLog = new StringBuilder();
                for (Map.Entry<Resource, List<BeanFactoryDefinition>> entry : groups.entrySet()) {
                    List<BeanFactoryDefinition> beans = entry.getValue();
                    Resource resource = entry.getKey();

                    StringBuilder sbLocal = new StringBuilder();
                    doDumpXmlBeans(zwangineContext, beans, resource == dummy ? null : resource, dumper, "beans", sbLocal, sbLog);
                    // dump each resource into its own file
                    doDumpToDirectory(resource, sbLocal, "beans", "xml", files);
                }
                if (!sbLog.isEmpty() && log) {
                    LOG.info("Dumping {} beans as XML", size);
                    LOG.info("{}", sbLog);
                }
            }
        }

        if (include.contains("*") || include.contains("all") || include.contains("dataFormats")) {
            int size = model.getDataFormats().size();
            if (size > 0) {
                Map<Resource, Map<String, DataFormatDefinition>> groups = new LinkedHashMap<>();
                for (Map.Entry<String, DataFormatDefinition> entry : model.getDataFormats().entrySet()) {
                    Resource res = entry.getValue().getResource();
                    if (res == null) {
                        res = dummy;
                    }
                    Map<String, DataFormatDefinition> dfs = groups.computeIfAbsent(res, resource -> new LinkedHashMap<>());
                    dfs.put(entry.getKey(), entry.getValue());
                }
                StringBuilder sbLog = new StringBuilder();
                for (Map.Entry<Resource, Map<String, DataFormatDefinition>> entry : groups.entrySet()) {
                    Map<String, DataFormatDefinition> dfs = entry.getValue();
                    Resource resource = entry.getKey();

                    StringBuilder sbLocal = new StringBuilder();
                    doDumpXmlDataFormats(zwangineContext, dfs, resource == dummy ? null : resource, dumper, "dataFormats", sbLocal,
                            sbLog);
                    // dump each resource into its own file
                    doDumpToDirectory(resource, sbLocal, "dataFormats", "xml", files);
                }
                if (!sbLog.isEmpty() && log) {
                    LOG.info("Dumping {} data formats as XML", size);
                    LOG.info("{}", sbLog);
                }
            }
        }

        if (include.contains("*") || include.contains("all") || include.contains("rests")) {
            int size = model.getRestDefinitions().size();
            if (size > 0) {
                Map<Resource, RestsDefinition> groups = new LinkedHashMap<>();
                for (RestDefinition rest : model.getRestDefinitions()) {
                    Resource res = rest.getResource();
                    if (res == null) {
                        res = dummy;
                    }
                    RestsDefinition workflows = groups.computeIfAbsent(res, resource -> new RestsDefinition());
                    workflows.getRests().add(rest);
                }
                StringBuilder sbLog = new StringBuilder();
                for (Map.Entry<Resource, RestsDefinition> entry : groups.entrySet()) {
                    RestsDefinition def = entry.getValue();
                    Resource resource = entry.getKey();

                    StringBuilder sbLocal = new StringBuilder();
                    doDumpXml(zwangineContext, def, resource == dummy ? null : resource, dumper, "rest", "rests", sbLocal, sbLog);
                    // dump each resource into its own file
                    doDumpToDirectory(resource, sbLocal, "rests", "xml", files);
                }
                if (!sbLog.isEmpty() && log) {
                    LOG.info("Dumping {} rests as XML", size);
                    LOG.info("{}", sbLog);
                }
            }
        }

        if (include.contains("*") || include.contains("all") || include.contains("workflowConfigurations")
                || include.contains("workflow-configurations")) {
            int size = model.getWorkflowConfigurationDefinitions().size();
            if (size > 0) {
                Map<Resource, WorkflowConfigurationsDefinition> groups = new LinkedHashMap<>();
                for (WorkflowConfigurationDefinition config : model.getWorkflowConfigurationDefinitions()) {
                    Resource res = config.getResource();
                    if (res == null) {
                        res = dummy;
                    }
                    WorkflowConfigurationsDefinition workflows
                            = groups.computeIfAbsent(res, resource -> new WorkflowConfigurationsDefinition());
                    workflows.getWorkflowConfigurations().add(config);
                }
                StringBuilder sbLog = new StringBuilder();
                for (Map.Entry<Resource, WorkflowConfigurationsDefinition> entry : groups.entrySet()) {
                    WorkflowConfigurationsDefinition def = entry.getValue();
                    Resource resource = entry.getKey();

                    StringBuilder sbLocal = new StringBuilder();
                    doDumpXml(zwangineContext, def, resource == dummy ? null : resource, dumper, "workflowConfiguration",
                            "workflow-configurations",
                            sbLocal, sbLog);
                    // dump each resource into its own file
                    doDumpToDirectory(resource, sbLocal, "workflow-configurations", "xml", files);
                }
                if (!sbLog.isEmpty() && log) {
                    LOG.info("Dumping {} workflow-configurations as XML", size);
                    LOG.info("{}", sbLog);
                }
            }
        }

        if (include.contains("*") || include.contains("all") || include.contains("workflowTemplates")
                || include.contains("workflow-templates")) {
            int size = model.getWorkflowTemplateDefinitions().size();
            if (size > 0) {
                Map<Resource, WorkflowTemplatesDefinition> groups = new LinkedHashMap<>();
                for (WorkflowTemplateDefinition rt : model.getWorkflowTemplateDefinitions()) {
                    Resource res = rt.getResource();
                    if (res == null) {
                        res = dummy;
                    }
                    WorkflowTemplatesDefinition workflows = groups.computeIfAbsent(res, resource -> new WorkflowTemplatesDefinition());
                    workflows.getWorkflowTemplates().add(rt);
                }
                StringBuilder sbLog = new StringBuilder();
                for (Map.Entry<Resource, WorkflowTemplatesDefinition> entry : groups.entrySet()) {
                    WorkflowTemplatesDefinition def = entry.getValue();
                    Resource resource = entry.getKey();

                    StringBuilder sbLocal = new StringBuilder();
                    doDumpXml(zwangineContext, def, resource == dummy ? null : resource, dumper, "workflowTemplate",
                            "workflow-templates", sbLocal, sbLog);
                    // dump each resource into its own file
                    doDumpToDirectory(resource, sbLocal, "workflow-templates", "xml", files);
                }
                if (!sbLog.isEmpty() && log) {
                    LOG.info("Dumping {} workflow-templates as XML", size);
                    LOG.info("{}", sbLog);
                }
            }
        }

        if (include.contains("*") || include.contains("all") || include.contains("workflows")) {
            int size = model.getWorkflowDefinitions().size();
            if (size > 0) {
                Map<Resource, WorkflowsDefinition> groups = new LinkedHashMap<>();
                for (WorkflowDefinition workflow : model.getWorkflowDefinitions()) {
                    if ((workflow.isRest() != null && workflow.isRest()) || (workflow.isTemplate() != null && workflow.isTemplate())) {
                        // skip workflows that are rest/templates
                        continue;
                    }
                    Resource res = workflow.getResource();
                    if (res == null) {
                        res = dummy;
                    }
                    WorkflowsDefinition workflows = groups.computeIfAbsent(res, resource -> new WorkflowsDefinition());
                    workflows.getWorkflows().add(workflow);
                }
                StringBuilder sbLog = new StringBuilder();
                for (Map.Entry<Resource, WorkflowsDefinition> entry : groups.entrySet()) {
                    WorkflowsDefinition def = entry.getValue();
                    Resource resource = entry.getKey();

                    StringBuilder sbLocal = new StringBuilder();
                    doDumpXml(zwangineContext, def, resource == dummy ? null : resource, dumper, "workflow", "workflows", sbLocal,
                            sbLog);
                    // dump each resource into its own file
                    doDumpToDirectory(resource, sbLocal, "workflows", "xml", files);
                }
                if (!sbLog.isEmpty() && log) {
                    LOG.info("Dumping {} workflows as XML", size);
                    LOG.info("{}", sbLog);
                }
            }
        }

        if (output != null && !files.isEmpty()) {
            // all XML files need to have <zwangine> as root tag
            doAdjustXmlFiles(files);
        }
    }

    protected void doDumpXmlBeans(
            ZwangineContext zwangineContext, List beans, Resource resource,
            ModelToXMLDumper dumper, String kind, StringBuilder sbLocal, StringBuilder sbLog) {
        try {
            String dump = dumper.dumpBeansAsXml(zwangineContext, beans);
            sbLocal.append(dump);
            appendLogDump(resource, dump, sbLog);
        } catch (Exception e) {
            LOG.warn("Error dumping {}} to XML due to {}. This exception is ignored.", kind, e.getMessage(), e);
        }
    }

    protected void doDumpXml(
            ZwangineContext zwangineContext, NamedNode def, Resource resource,
            ModelToXMLDumper dumper, String replace, String kind, StringBuilder sbLocal, StringBuilder sbLog) {
        try {
            String xml = dumper.dumpModelAsXml(zwangineContext, def, resolvePlaceholders, generatedIds);
            // remove spring schema xmlns that zwangine-jaxb dumper includes
            xml = StringHelper.replaceFirst(xml, " xmlns=\"http://zwangine.zwangine.org/schema/spring\">", ">");
            xml = xml.replace("</" + replace + ">", "</" + replace + ">\n");
            // remove outer tag (workflows, rests, etc)
            replace = replace + "s";
            xml = StringHelper.replaceFirst(xml, "<" + replace + ">", "");
            xml = StringHelper.replaceFirst(xml, "</" + replace + ">", "");

            sbLocal.append(xml);
            appendLogDump(resource, xml, sbLog);
        } catch (Exception e) {
            LOG.warn("Error dumping {}} to XML due to {}. This exception is ignored.", kind, e.getMessage(), e);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    protected void doDumpToDirectory(Resource resource, StringBuilder sbLocal, String kind, String ext, Set<String> files) {
        if (output != null && !sbLocal.isEmpty()) {
            // make sure directory exists
            File dir = new File(output);
            dir.mkdirs();

            String name = resolveFileName(ext, resource);
            boolean newFile = files.isEmpty() || !files.contains(name);
            File target = new File(output, name);
            try {
                if (newFile) {
                    // write as new file (override old file if exists)
                    IOHelper.writeText(sbLocal.toString(), target);
                } else {
                    // append to existing file
                    IOHelper.appendText(sbLocal.toString(), target);
                }
                files.add(name);
                LOG.info("Dumped {} to file: {}", kind, target);
            } catch (IOException e) {
                throw new RuntimeException("Error dumping " + kind + " to file: " + target, e);
            }
        }
    }

    protected void doAdjustXmlFiles(Set<String> files) {
        for (String name : files) {
            if (name.endsWith(".xml")) {
                try {
                    File file = new File(output, name);
                    // wrap xml files with <zwangine> root tag
                    StringBuilder sb = new StringBuilder();
                    sb.append("<zwangine>\n\n");
                    String xml = IOHelper.loadText(new FileInputStream(file));
                    sb.append(xml);
                    sb.append("\n</zwangine>\n");
                    IOHelper.writeText(sb.toString(), file);
                } catch (Exception e) {
                    LOG.warn("Error adjusting dumped XML file: {} due to {}. This exception is ignored.", name, e.getMessage(),
                            e);
                }
            }
        }
    }

    protected void appendLogDump(Resource resource, String dump, StringBuilder sbLog) {
        String loc = null;
        if (resource != null) {
            loc = extractLocationName(resource.getLocation());
        }
        if (loc != null) {
            sbLog.append(String.format("%nSource: %s%n%s%n%s%n", loc, DIVIDER, dump));
        } else {
            sbLog.append(String.format("%n%n%s%n", dump));
        }
    }

    private static final class DummyResource extends ResourceSupport {

        private DummyResource(String scheme, String location) {
            super(scheme, location);
        }

        @Override
        public boolean exists() {
            return true;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return null; // not in use
        }
    }

    private static String extractLocationName(String loc) {
        if (loc == null) {
            return null;
        }
        loc = stripSourceLocationLineNumber(loc);
        if (loc != null) {
            if (loc.contains(":")) {
                // strip prefix
                loc = StringHelper.after(loc, ":", loc);

                // file based such as xml and yaml
                loc = FileUtil.stripPath(loc);
            }
        }
        return loc;
    }

    protected String resolveFileName(String ext, Resource resource) {
        if (outputFileName != null) {
            return outputFileName;
        }

        // compute name from resource or auto-generated
        String name = resource != null ? resource.getLocation() : null;
        if (name == null) {
            name = "dump" + counter.incrementAndGet();
        }
        // strip scheme
        if (name.contains(":")) {
            name = StringHelper.after(name, ":");
        }
        return FileUtil.onlyName(name, true) + "." + ext;
    }

}
