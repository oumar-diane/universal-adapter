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

import org.zenithblox.builder.WorkflowBuilder;
import org.zenithblox.support.dsl.WorkflowBuilderLoaderSupport;
import org.zenithblox.dsl.yaml.common.YamlDeserializationContext;
import org.zenithblox.dsl.yaml.common.exception.YamlDeserializationException;
import org.zenithblox.dsl.yaml.deserializers.BeansDeserializer;
import org.zenithblox.dsl.yaml.deserializers.CustomResolver;
import org.zenithblox.dsl.yaml.deserializers.ModelDeserializersResolver;
import org.zenithblox.spi.Resource;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.api.YamlUnicodeReader;
import org.snakeyaml.engine.v2.composer.Composer;
import org.snakeyaml.engine.v2.nodes.Node;
import org.snakeyaml.engine.v2.nodes.NodeTuple;
import org.snakeyaml.engine.v2.nodes.NodeType;
import org.snakeyaml.engine.v2.parser.Parser;
import org.snakeyaml.engine.v2.parser.ParserImpl;
import org.snakeyaml.engine.v2.scanner.StreamReader;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static org.zenithblox.dsl.yaml.common.YamlDeserializerSupport.asText;

public abstract class YamlWorkflowsBuilderLoaderSupport extends WorkflowBuilderLoaderSupport {

    // need to use shared bean deserializer
    final BeansDeserializer beansDeserializer = new BeansDeserializer();

    public YamlWorkflowsBuilderLoaderSupport(String extension) {
        super(extension);
    }

    protected YamlDeserializationContext newYamlDeserializationContext(LoadSettings settings, Resource resource) {
        YamlDeserializationContext ctx = new YamlDeserializationContext(settings);

        ctx.setResource(resource);
        ctx.setZwangineContext(getZwangineContext());
        ctx.addResolvers(new CustomResolver(beansDeserializer));
        ctx.addResolvers(new ModelDeserializersResolver());
        return ctx;
    }

    @Override
    public WorkflowBuilder doLoadWorkflowBuilder(Resource resource) throws Exception {
        if (!resource.exists()) {
            throw new FileNotFoundException("Resource not found: " + resource.getLocation());
        }

        try (InputStream is = resourceInputStream(resource)) {
            // need a local settings because we want the label to be the resource we parse so the parser
            // can show parsing errors referring to actual resource file being parsed.
            LoadSettings local = LoadSettings.builder().setLabel(resource.getLocation()).build();
            final YamlDeserializationContext ctx = newYamlDeserializationContext(local, resource);
            final StreamReader reader = new StreamReader(local, new YamlUnicodeReader(is));
            final Parser parser = new ParserImpl(local, reader);
            final Composer composer = new Composer(local, parser);

            return composer.getSingleNode()
                    .map(node -> builder(ctx, node))
                    .orElseThrow(() -> new YamlDeserializationException("Unable to parse resource: " + resource.getLocation()));
        }
    }

    protected abstract WorkflowBuilder builder(YamlDeserializationContext ctx, Node node);

    protected boolean anyTupleMatches(List<NodeTuple> list, String aKey, String aValue) {
        return anyTupleMatches(list, aKey, Predicate.isEqual(aValue));
    }

    protected boolean anyTupleMatches(List<NodeTuple> list, String aKey, Predicate<String> predicate) {
        for (NodeTuple tuple : list) {
            final String key = asText(tuple.getKeyNode());
            final Node val = tuple.getValueNode();
            if (Objects.equals(aKey, key) && NodeType.SCALAR.equals(val.getNodeType())) {
                String value = asText(tuple.getValueNode());
                if (predicate.test(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected String extractTupleValue(List<NodeTuple> list, String aKey) {
        for (NodeTuple tuple : list) {
            final String key = asText(tuple.getKeyNode());
            final Node val = tuple.getValueNode();
            if (Objects.equals(aKey, key) && NodeType.SCALAR.equals(val.getNodeType())) {
                return asText(tuple.getValueNode());
            }
        }
        return null;
    }

}
