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
package org.zenithblox.dsl.yaml.common;

import org.snakeyaml.engine.v2.api.ConstructNode;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.constructor.StandardConstructor;
import org.snakeyaml.engine.v2.nodes.MappingNode;
import org.snakeyaml.engine.v2.nodes.Node;
import org.snakeyaml.engine.v2.nodes.NodeType;
import org.snakeyaml.engine.v2.nodes.ScalarNode;
import org.zenithblox.Ordered;
import org.zenithblox.Service;
import org.zenithblox.ZwangineContext;
import org.zenithblox.ZwangineContextAware;
import org.zenithblox.dsl.yaml.common.exception.DuplicateKeyException;
import org.zenithblox.dsl.yaml.common.exception.UnknownNodeIdException;
import org.zenithblox.dsl.yaml.common.exception.YamlDeserializationException;
import org.zenithblox.spi.Resource;
import org.zenithblox.util.ObjectHelper;

import java.util.*;

public class YamlDeserializationContext extends StandardConstructor implements ZwangineContextAware, Service {

    private final Set<YamlDeserializerResolver> resolvers;
    private final Map<String, ConstructNode> constructors;
    private ZwangineContext zwangineContext;
    private Resource resource;

    public YamlDeserializationContext(LoadSettings settings) {
        super(settings);
        this.resolvers = new TreeSet<>(Comparator.comparing(Ordered::getOrder));
        this.constructors = new HashMap<>();
    }

    public void addResolver(YamlDeserializerResolver resolver) {
        this.resolvers.add(resolver);
    }

    public void addResolvers(YamlDeserializerResolver... resolvers) {
        addResolvers(Arrays.asList(resolvers));
    }

    public void addResolvers(Collection<YamlDeserializerResolver> resolvers) {
        this.resolvers.addAll(resolvers);
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
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
    protected Optional<ConstructNode> findConstructorFor(Node node) {
        ConstructNode ctor = resolve(node);
        if (ctor != null) {
            return Optional.of(ctor);
        }

        return super.findConstructorFor(node);
    }

    @Override
    public void start() {
        ObjectHelper.notNull(zwangineContext, "zwangine context");

        this.resolvers.addAll(getZwangineContext().getRegistry().findByType(YamlDeserializerResolver.class));
    }

    @Override
    public void stop() {
        this.constructors.clear();
    }

    // *********************************
    //
    // Construct
    //
    // *********************************

    public Object construct(Node key, Node val) {
        return mandatoryResolve(key).construct(val);
    }

    public <T> T construct(Node key, Node val, Class<T> type) {
        Object result = construct(key, val);
        if (result == null) {
            return null;
        }

        return type.cast(result);
    }

    public <T> T construct(Node node, Class<T> type) {
        ConstructNode constructor = resolve(type);
        if (constructor == null) {
            throw new YamlDeserializationException(node, "Unable to find constructor for node");
        }
        Object result = constructor.construct(node);
        if (result == null) {
            return null;
        }

        return type.cast(result);
    }

    // *********************************
    //
    // Resolve
    //
    // *********************************

    public ConstructNode resolve(Class<?> type) {
        return ZwangineContextAware.trySetZwangineContext(
                (Node n) -> {
                    Node newNode = YamlSupport.setProperty(
                            n,
                            YamlDeserializationContext.class.getName(),
                            YamlDeserializationContext.this);

                    final ConstructNode answer = resolve(n, type.getName());
                    return answer.construct(newNode);
                },
                zwangineContext);
    }

    public ConstructNode mandatoryResolve(Node node) {
        ConstructNode constructor = resolve(node);
        if (constructor == null) {
            throw new YamlDeserializationException(node, "Unable to find constructor for node");
        }

        return constructor;
    }

    public ConstructNode resolve(Node node) {
        if (node.getNodeType() != NodeType.MAPPING) {
            return null;
        }

        MappingNode mn = (MappingNode) node;
        if (mn.getValue().size() > 1) {
            throw new DuplicateKeyException(node, mn.getValue());
        } else if (mn.getValue().size() != 1) {
            return null;
        }

        Node key = mn.getValue().get(0).getKeyNode();
        if (key.getNodeType() != NodeType.SCALAR) {
            return null;
        }

        final String id = ((ScalarNode) key).getValue();
        final ConstructNode answer = resolve(node, id);

        return ZwangineContextAware.trySetZwangineContext(
                (Node n) -> {
                    YamlSupport.setProperty(
                            n,
                            YamlDeserializationContext.class.getName(),
                            YamlDeserializationContext.this);

                    return answer.construct(
                            ((MappingNode) n).getValue().get(0).getValueNode());
                },
                zwangineContext);
    }

    public ConstructNode resolve(Node node, String id) {
        return constructors.computeIfAbsent(id, (String s) -> {
            ConstructNode answer = null;

            for (YamlDeserializerResolver resolver : resolvers) {
                answer = resolver.resolve(id);
                if (answer != null) {
                    break;
                }
            }

            if (answer == null) {
                throw new UnknownNodeIdException(node, id);
            }

            return answer;
        });
    }
}
