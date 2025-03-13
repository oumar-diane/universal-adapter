/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
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
package org.zenithblox.dsl.yaml;

import org.zenithblox.ZwangineContextAware;
import org.zenithblox.ErrorHandlerFactory;
import org.zenithblox.RuntimeZwangineException;
import org.zenithblox.builder.WorkflowBuilder;
import org.zenithblox.builder.WorkflowConfigurationBuilder;
import org.zenithblox.dsl.yaml.common.YamlDeserializationContext;
import org.zenithblox.dsl.yaml.common.YamlDeserializerSupport;
import org.zenithblox.dsl.yaml.deserializers.OutputAwareFromDefinition;
import org.zenithblox.model.*;
import org.zenithblox.model.errorhandler.DeadLetterChannelDefinition;
import org.zenithblox.model.errorhandler.DefaultErrorHandlerDefinition;
import org.zenithblox.model.errorhandler.NoErrorHandlerDefinition;
import org.zenithblox.model.rest.RestConfigurationDefinition;
import org.zenithblox.model.rest.RestDefinition;
import org.zenithblox.model.rest.VerbDefinition;
import org.zenithblox.spi.ZwangineContextCustomizer;
import org.zenithblox.spi.DataType;
import org.zenithblox.spi.Resource;
import org.zenithblox.spi.annotations.WorkflowsLoader;
import org.zenithblox.support.ObjectHelper;
import org.zenithblox.support.PropertyBindingSupport;
import org.zenithblox.util.URISupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.api.YamlUnicodeReader;
import org.snakeyaml.engine.v2.composer.Composer;
import org.snakeyaml.engine.v2.nodes.*;
import org.snakeyaml.engine.v2.parser.Parser;
import org.snakeyaml.engine.v2.parser.ParserImpl;
import org.snakeyaml.engine.v2.scanner.StreamReader;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.zenithblox.dsl.yaml.common.YamlDeserializerSupport.*;

@WorkflowsLoader(YamlWorkflowsBuilderLoader.EXTENSION)
public class YamlWorkflowsBuilderLoader extends YamlWorkflowsBuilderLoaderSupport {

    public static final String EXTENSION = "yaml";
    public static final String[] SUPPORTED_EXTENSION = { EXTENSION, "zwangine.yaml", "pipe.yaml" };

    private static final Logger LOG = LoggerFactory.getLogger(YamlWorkflowsBuilderLoader.class);

    // API versions for Pipe
    // we are lenient so lets just assume we can work with any of the v1 even if
    // they evolve
    @Deprecated
    private static final String PIPE_VERSION = "zwangine.zentihblox.org/v1";
    private static final String STRIMZI_VERSION = "kafka.strimzi.io/v1beta2";
    private static final String KNATIVE_MESSAGING_VERSION = "messaging.knative.dev/v1";
    private static final String KNATIVE_EVENTING_VERSION = "eventing.knative.dev/v1";
    private static final String KNATIVE_EVENT_TYPE = "org.zenithblox.event";

    private final Map<String, Boolean> preparseDone = new ConcurrentHashMap<>();

    public YamlWorkflowsBuilderLoader() {
        super(EXTENSION);
    }

    YamlWorkflowsBuilderLoader(String extension) {
        super(extension);
    }

    @Override
    public boolean isSupportedExtension(String extension) {
        // this builder can support multiple extensions
        return Arrays.asList(SUPPORTED_EXTENSION).contains(extension);
    }

    protected WorkflowBuilder builder(final YamlDeserializationContext ctx, final Node root) {

        // we need to keep track of already configured items as the yaml-dsl returns a
        // WorkflowConfigurationBuilder that is capable of both workflow and workflow
        // configurations
        // which can lead to the same items being processed twice
        final Set<Integer> indexes = new HashSet<>();

        return new WorkflowConfigurationBuilder() {
            @Override
            public void configure() throws Exception {
                setDeserializationContext(root, ctx);

                Object target = preConfigureNode(root, ctx, false);
                if (target == null) {
                    return;
                }

                Iterator<?> it = ObjectHelper.createIterator(target);
                while (it.hasNext()) {
                    target = it.next();
                    if (target instanceof Node && isSequenceNode((Node) target)) {
                        SequenceNode seq = asSequenceNode((Node) target);
                        for (Node node : seq.getValue()) {
                            int idx = -1;
                            if (node.getStartMark().isPresent()) {
                                idx = node.getStartMark().get().getIndex();
                            }
                            if (idx == -1 || !indexes.contains(idx)) {
                                Object item = ctx.mandatoryResolve(node).construct(node);
                                boolean accepted = doConfigure(item);
                                if (accepted && idx != -1) {
                                    indexes.add(idx);
                                }
                            }
                        }
                    } else {
                        doConfigure(target);
                    }
                }

                // knowing this is the last time an YAML may have been parsed, we can clear the
                // cache
                // (workflow may get reloaded later)
                Resource resource = ctx.getResource();
                if (resource != null) {
                    preparseDone.remove(resource.getLocation());
                }
                beansDeserializer.clearCache();
            }

            private boolean doConfigure(Object item) throws Exception {
                if (item instanceof OutputAwareFromDefinition) {
                    WorkflowDefinition workflow = new WorkflowDefinition();
                    workflow.setInput(((OutputAwareFromDefinition) item).getDelegate());
                    workflow.setOutputs(((OutputAwareFromDefinition) item).getOutputs());

                    ZwangineContextAware.trySetZwangineContext(getWorkflowCollection(), getZwangineContext());
                    getWorkflowCollection().workflow(workflow);
                    return true;
                } else if (item instanceof WorkflowDefinition) {
                    ZwangineContextAware.trySetZwangineContext(getWorkflowCollection(), getZwangineContext());
                    getWorkflowCollection().workflow((WorkflowDefinition) item);
                    return true;
                } else if (item instanceof ZwangineContextCustomizer) {
                    ((ZwangineContextCustomizer) item).configure(getZwangineContext());
                    return true;
                } else if (item instanceof InterceptFromDefinition) {
                    if (!getWorkflowCollection().getWorkflows().isEmpty()) {
                        throw new IllegalArgumentException(
                                "interceptFrom must be defined before any workflows in the WorkflowBuilder");
                    }
                    ZwangineContextAware.trySetZwangineContext(getWorkflowCollection(), getZwangineContext());
                    getWorkflowCollection().getInterceptFroms().add((InterceptFromDefinition) item);
                    return true;
                } else if (item instanceof InterceptDefinition) {
                    if (!getWorkflowCollection().getWorkflows().isEmpty()) {
                        throw new IllegalArgumentException(
                                "intercept must be defined before any workflows in the WorkflowBuilder");
                    }
                    ZwangineContextAware.trySetZwangineContext(getWorkflowCollection(), getZwangineContext());
                    getWorkflowCollection().getIntercepts().add((InterceptDefinition) item);
                    return true;
                } else if (item instanceof InterceptSendToEndpointDefinition) {
                    if (!getWorkflowCollection().getWorkflows().isEmpty()) {
                        throw new IllegalArgumentException(
                                "interceptSendToEndpoint must be defined before any workflows in the WorkflowBuilder");
                    }
                    ZwangineContextAware.trySetZwangineContext(getWorkflowCollection(), getZwangineContext());
                    getWorkflowCollection().getInterceptSendTos().add((InterceptSendToEndpointDefinition) item);
                    return true;
                } else if (item instanceof OnCompletionDefinition) {
                    if (!getWorkflowCollection().getWorkflows().isEmpty()) {
                        throw new IllegalArgumentException(
                                "onCompletion must be defined before any workflows in the WorkflowBuilder");
                    }
                    ZwangineContextAware.trySetZwangineContext(getWorkflowCollection(), getZwangineContext());
                    getWorkflowCollection().getOnCompletions().add((OnCompletionDefinition) item);
                    return true;
                } else if (item instanceof OnExceptionDefinition) {
                    if (!getWorkflowCollection().getWorkflows().isEmpty()) {
                        throw new IllegalArgumentException(
                                "onException must be defined before any workflows in the WorkflowBuilder");
                    }
                    ZwangineContextAware.trySetZwangineContext(getWorkflowCollection(), getZwangineContext());
                    getWorkflowCollection().getOnExceptions().add((OnExceptionDefinition) item);
                    return true;
                } else if (item instanceof ErrorHandlerFactory) {
                    if (!getWorkflowCollection().getWorkflows().isEmpty()) {
                        throw new IllegalArgumentException(
                                "errorHandler must be defined before any workflows in the WorkflowBuilder");
                    }
                    errorHandler((ErrorHandlerFactory) item);
                    return true;
                } else if (item instanceof WorkflowTemplateDefinition) {
                    ZwangineContextAware.trySetZwangineContext(getWorkflowTemplateCollection(), getZwangineContext());
                    getWorkflowTemplateCollection().workflowTemplate((WorkflowTemplateDefinition) item);
                    return true;
                } else if (item instanceof TemplatedWorkflowDefinition) {
                    ZwangineContextAware.trySetZwangineContext(getTemplatedWorkflowCollection(), getZwangineContext());
                    getTemplatedWorkflowCollection().templatedWorkflow((TemplatedWorkflowDefinition) item);
                    return true;
                } else if (item instanceof RestDefinition) {
                    RestDefinition definition = (RestDefinition) item;
                    for (VerbDefinition verb : definition.getVerbs()) {
                        verb.setRest(definition);
                    }
                    ZwangineContextAware.trySetZwangineContext(getRestCollection(), getZwangineContext());
                    getRestCollection().rest(definition);
                    return true;
                } else if (item instanceof RestConfigurationDefinition) {
                    ((RestConfigurationDefinition) item).asRestConfiguration(
                            getZwangineContext(),
                            getZwangineContext().getRestConfiguration());
                    return true;
                }

                return false;
            }

            @Override
            public void configuration() throws Exception {
                setDeserializationContext(root, ctx);

                Object target = preConfigureNode(root, ctx, false);
                if (target == null) {
                    return;
                }

                Iterator<?> it = ObjectHelper.createIterator(target);
                while (it.hasNext()) {
                    target = it.next();
                    if (target instanceof Node && isSequenceNode((Node) target)) {
                        SequenceNode seq = asSequenceNode((Node) target);
                        for (Node node : seq.getValue()) {
                            int idx = -1;
                            if (node.getStartMark().isPresent()) {
                                idx = node.getStartMark().get().getIndex();
                            }
                            if (idx == -1 || !indexes.contains(idx)) {
                                if (node.getNodeType() == NodeType.MAPPING) {
                                    MappingNode mn = asMappingNode(node);
                                    for (NodeTuple nt : mn.getValue()) {
                                        String key = asText(nt.getKeyNode());
                                        // only accept workflow-configuration
                                        if ("workflow-configuration".equals(key) || "workflowConfiguration".equals(key)) {
                                            Object item = ctx.mandatoryResolve(node).construct(node);
                                            boolean accepted = doConfiguration(item);
                                            if (accepted && idx != -1) {
                                                indexes.add(idx);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        doConfiguration(target);
                    }
                }
            }

            private boolean doConfiguration(Object item) {
                if (item instanceof WorkflowConfigurationDefinition) {
                    ZwangineContextAware.trySetZwangineContext(getWorkflowConfigurationCollection(), getZwangineContext());
                    getWorkflowConfigurationCollection().workflowConfiguration((WorkflowConfigurationDefinition) item);
                    return true;
                }
                return false;
            }
        };
    }

    private Object preConfigureNode(Node root, YamlDeserializationContext ctx, boolean preParse) {
        // backwards compatible fixes
        Object target = root;

        // check if the yaml with embedded pipes
        if (Objects.equals(root.getNodeType(), NodeType.MAPPING)) {
            final MappingNode mn = YamlDeserializerSupport.asMappingNode(root);
            // pipe
            boolean pipe = anyTupleMatches(mn.getValue(), "apiVersion", v -> v.startsWith(PIPE_VERSION)) &&
                    anyTupleMatches(mn.getValue(), "kind", "Pipe");
            if (pipe) {
                target = preConfigurePipe(root, ctx, target, preParse);
            }
        }

        // only detect beans during pre-parsing
        if (preParse && Objects.equals(root.getNodeType(), NodeType.SEQUENCE)) {
            final List<Object> list = new ArrayList<>();

            final SequenceNode sn = asSequenceNode(root);
            for (Node node : sn.getValue()) {
                if (Objects.equals(node.getNodeType(), NodeType.MAPPING)) {
                    MappingNode mn = asMappingNode(node);
                    for (NodeTuple nt : mn.getValue()) {
                        String key = asText(nt.getKeyNode());
                        if ("beans".equals(key)) {
                            // inlined beans
                            Node beans = nt.getValueNode();
                            setDeserializationContext(beans, ctx);
                            Object output = beansDeserializer.construct(beans);
                            if (output != null) {
                                list.add(output);
                            }
                        }
                    }
                }
            }
            if (!list.isEmpty()) {
                target = list;
            }
        }
        return target;
    }

    /**
     * Pipe file
     */
    private Object preConfigurePipe(Node root, YamlDeserializationContext ctx, Object target, boolean preParse) {
        // when in pre-parse phase then we only want to gather /metadata/annotations

        List<Object> answer = new ArrayList<>();

        MappingNode ann = asMappingNode(nodeAt(root, "/metadata/annotations"));
        Map<String, Object> params = asMap(ann);

        if (!preParse) {
            // start with a workflow
            final WorkflowDefinition workflow = new WorkflowDefinition();
            String workflowId = asText(nodeAt(root, "/metadata/name"));
            if (workflowId != null) {
                workflow.workflowId(workflowId);
            }

            // Pipe is a bit more complex, so grab the source and sink
            // and map those to Zwangine workflow definitions
            MappingNode source = asMappingNode(nodeAt(root, "/spec/source"));
            MappingNode sink = asMappingNode(nodeAt(root, "/spec/sink"));
            if (source != null) {
                int line = -1;
                if (source.getStartMark().isPresent()) {
                    line = source.getStartMark().get().getLine();
                }

                // source at the beginning (mandatory)
                String uri = extractZwangineEndpointUri(source);
                workflow.from(uri);

                // enrich model with line number
                if (line != -1) {
                    workflow.getInput().setLineNumber(line);
                    if (ctx != null) {
                        workflow.getInput().setLocation(ctx.getResource().getLocation());
                    }
                }

                MappingNode dataTypes = asMappingNode(nodeAt(source, "/dataTypes"));
                if (dataTypes != null) {
                    MappingNode in = asMappingNode(nodeAt(dataTypes, "/in"));
                    if (in != null) {
                        workflow.inputType(extractDataType(in));
                    }

                    MappingNode out = asMappingNode(nodeAt(dataTypes, "/out"));
                    if (out != null) {
                        workflow.transform(new DataType(extractDataType(out)));
                    }
                }

                // steps in the middle (optional)
                Node steps = nodeAt(root, "/spec/steps");
                if (steps != null) {
                    SequenceNode sn = asSequenceNode(steps);
                    for (Node node : sn.getValue()) {
                        MappingNode step = asMappingNode(node);
                        uri = extractZwangineEndpointUri(step);
                        if (uri != null) {
                            line = -1;
                            if (node.getStartMark().isPresent()) {
                                line = node.getStartMark().get().getLine();
                            }

                            ProcessorDefinition<?> out;
                            // if kamelet then use kamelet eip instead of to
                            boolean kamelet = uri.startsWith("kamelet:");
                            if (kamelet) {
                                uri = uri.substring(8);
                                out = new KameletDefinition(uri);
                            } else {
                                out = new ToDefinition(uri);
                            }
                            workflow.addOutput(out);
                            // enrich model with line number
                            if (line != -1) {
                                out.setLineNumber(line);
                                if (ctx != null) {
                                    out.setLocation(ctx.getResource().getLocation());
                                }
                            }
                        }
                    }
                }

                if (sink != null) {
                    dataTypes = asMappingNode(nodeAt(sink, "/dataTypes"));
                    if (dataTypes != null) {
                        MappingNode in = asMappingNode(nodeAt(dataTypes, "/in"));
                        if (in != null) {
                            workflow.transform(new DataType(extractDataType(in)));
                        }

                        MappingNode out = asMappingNode(nodeAt(dataTypes, "/out"));
                        if (out != null) {
                            workflow.outputType(extractDataType(out));
                        }
                    }

                    // sink is at the end (mandatory)
                    line = -1;
                    if (sink.getStartMark().isPresent()) {
                        line = sink.getStartMark().get().getLine();
                    }
                    uri = extractZwangineEndpointUri(sink);
                    ToDefinition to = new ToDefinition(uri);
                    workflow.addOutput(to);

                    // enrich model with line number
                    if (line != -1) {
                        to.setLineNumber(line);
                        if (ctx != null) {
                            to.setLocation(ctx.getResource().getLocation());
                        }
                    }
                }

                // is there any error handler?
                MappingNode errorHandler = asMappingNode(nodeAt(root, "/spec/errorHandler"));
                if (errorHandler != null) {
                    // there are 5 different error handlers, which one is it
                    NodeTuple nt = errorHandler.getValue().get(0);
                    String ehName = asText(nt.getKeyNode());

                    ErrorHandlerFactory ehf = null;
                    if ("sink".equals(ehName)) {
                        // a sink is a dead letter queue
                        DeadLetterChannelDefinition dlcd = new DeadLetterChannelDefinition();
                        MappingNode endpoint = asMappingNode(nodeAt(nt.getValueNode(), "/endpoint"));
                        String dlq = extractZwangineEndpointUri(endpoint);
                        dlcd.setDeadLetterUri(dlq);
                        ehf = dlcd;
                    } else if ("log".equals(ehName)) {
                        // log is the default error handler
                        ehf = new DefaultErrorHandlerDefinition();
                    } else if ("none".equals(ehName)) {
                        workflow.errorHandler(new NoErrorHandlerDefinition());
                    }

                    // some error handlers support additional parameters
                    if (ehf != null) {
                        // properties that are general for all kind of error handlers
                        MappingNode prop = asMappingNode(nodeAt(nt.getValueNode(), "/parameters"));
                        params = asMap(prop);
                        if (params != null) {
                            PropertyBindingSupport.build()
                                    .withIgnoreCase(true)
                                    .withFluentBuilder(true)
                                    .withRemoveParameters(true)
                                    .withZwangineContext(getZwangineContext())
                                    .withTarget(ehf)
                                    .withProperties(params)
                                    .bind();
                        }
                        workflow.errorHandler(ehf);
                    }
                }
            }

            answer.add(workflow);
        }

        return answer;
    }

    /**
     * Extracts the data type transformer name information form nodes dataTypes/in or dataTypes/out. When scheme is set
     * construct the transformer name with a prefix like scheme:format. Otherwise, just use the given format as a data
     * type transformer name.
     *
     * @param  node
     * @return
     */
    private String extractDataType(MappingNode node) {
        String scheme = extractTupleValue(node.getValue(), "scheme");
        String format = extractTupleValue(node.getValue(), "format");
        if (scheme != null) {
            return scheme + ":" + format;
        }

        return format;
    }

    private String extractZwangineEndpointUri(MappingNode node) {
        MappingNode mn = null;
        Node ref = nodeAt(node, "/ref");
        if (ref != null) {
            mn = asMappingNode(ref);
        }

        // extract uri is different if kamelet or not
        boolean kamelet = mn != null && anyTupleMatches(mn.getValue(), "kind", "Kamelet");
        boolean strimzi = !kamelet && mn != null
                && anyTupleMatches(mn.getValue(), "apiVersion", v -> v.startsWith(STRIMZI_VERSION))
                && anyTupleMatches(mn.getValue(), "kind", "KafkaTopic");
        boolean knativeBroker = !kamelet && mn != null
                && anyTupleMatches(mn.getValue(), "apiVersion", v -> v.startsWith(KNATIVE_EVENTING_VERSION))
                && anyTupleMatches(mn.getValue(), "kind", "Broker");
        boolean knativeChannel = !kamelet && !strimzi && mn != null
                && anyTupleMatches(mn.getValue(), "apiVersion", v -> v.startsWith(KNATIVE_MESSAGING_VERSION));
        String uri;
        if (knativeBroker) {
            uri = KNATIVE_EVENT_TYPE;
        } else if (kamelet || strimzi || knativeChannel) {
            uri = extractTupleValue(mn.getValue(), "name");
        } else {
            uri = extractTupleValue(node.getValue(), "uri");
        }

        // properties
        MappingNode prop = asMappingNode(nodeAt(node, "/properties"));
        Map<String, Object> params = asMap(prop);

        if (knativeBroker && params != null && params.containsKey("type")) {
            // Use explicit event type from properties - remove setting from params and set
            // as uri
            uri = params.remove("type").toString();
        }

        if (params != null && !params.isEmpty()) {
            String query = URISupport.createQueryString(params);
            uri = uri + "?" + query;
        }

        if (strimzi) {
            return "kafka:" + uri;
        } else if (knativeBroker) {
            if (uri.contains("?")) {
                uri += "&kind=Broker&name=" + extractTupleValue(mn.getValue(), "name");
            } else {
                uri += "?kind=Broker&name=" + extractTupleValue(mn.getValue(), "name");
            }
            return "knative:event/" + uri;
        } else if (knativeChannel) {
            return "knative:channel/" + uri;
        } else {
            return uri;
        }
    }

    @Override
    public void preParseWorkflow(Resource resource) throws Exception {
        // preparsing is done at early stage, so we have a chance to load additional
        // beans and populate
        // Zwangine registry
        if (preparseDone.getOrDefault(resource.getLocation(), false)) {
            return;
        }

        LOG.trace("Pre-parsing: {}", resource.getLocation());

        if (!resource.exists()) {
            throw new FileNotFoundException("Resource not found: " + resource.getLocation());
        }

        try (InputStream is = resourceInputStream(resource)) {
            LoadSettings local = LoadSettings.builder().setLabel(resource.getLocation()).build();
            YamlDeserializationContext ctx = newYamlDeserializationContext(local, resource);
            StreamReader reader = new StreamReader(local, new YamlUnicodeReader(is));
            Parser parser = new ParserImpl(local, reader);
            Composer composer = new Composer(local, parser);
            try {
                composer.getSingleNode()
                        .map(node -> preParseNode(ctx, node));
            } catch (Exception e) {
                throw new RuntimeZwangineException("Error pre-parsing resource: " + ctx.getResource().getLocation(), e);
            } finally {
                ctx.close();
            }
        }

        preparseDone.put(resource.getLocation(), true);
    }

    private Object preParseNode(final YamlDeserializationContext ctx, final Node root) {
        LOG.trace("Pre-parsing node: {}", root);

        setDeserializationContext(root, ctx);

        Object target = preConfigureNode(root, ctx, true);
        Iterator<?> it = ObjectHelper.createIterator(target);
        while (it.hasNext()) {
            target = it.next();
            if (target instanceof ZwangineContextCustomizer) {
                ZwangineContextCustomizer customizer = (ZwangineContextCustomizer) target;
                customizer.configure(getZwangineContext());
            }
        }

        return null;
    }

}
