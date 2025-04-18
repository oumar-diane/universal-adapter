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
package org.zenithblox.language.csimple;

import org.zenithblox.*;
import org.zenithblox.spi.annotations.Language;
import org.zenithblox.support.TypedLanguageSupport;
import org.zenithblox.support.builder.ExpressionBuilder;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.util.IOHelper;
import org.zenithblox.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Language("csimple")
public class CSimpleLanguage extends TypedLanguageSupport implements StaticService {

    public static final String PRE_COMPILED_FILE = "META-INF/services/org/zentihblox/zwangine/csimple.properties";
    public static final String CONFIG_FILE = "zwangine-csimple.properties";

    private static final Logger LOG = LoggerFactory.getLogger(CSimpleLanguage.class);

    private final Map<String, CSimpleExpression> compiledPredicates;
    private final Map<String, CSimpleExpression> compiledExpressions;

    /**
     * If set, this implementation attempts to compile those expressions at runtime, that are not yet available in
     * {@link #compiledPredicates}; otherwise no compilation attempts will be made at runtime
     */
    private final CompilationSupport compilationSupport;

    public CSimpleLanguage() {
        this.compiledPredicates = new ConcurrentHashMap<>();
        this.compiledExpressions = new ConcurrentHashMap<>();
        this.compilationSupport = new CompilationSupport();
    }

    /**
     * For 100% pre-compiled use cases
     */
    private CSimpleLanguage(Map<String, CSimpleExpression> compiledPredicates,
                            Map<String, CSimpleExpression> compiledExpressions) {
        this.compiledPredicates = compiledPredicates;
        this.compiledExpressions = compiledExpressions;
        this.compilationSupport = null;
    }

    public String getConfigResource() {
        return compilationSupport().configResource;
    }

    public void setConfigResource(String configResource) {
        compilationSupport().configResource = configResource;
    }

    /**
     * Adds an import line
     *
     * @param imports import such as com.foo.MyClass
     */
    public void addImport(String imports) {
        compilationSupport().addImport(imports);
    }

    /**
     * Adds an alias
     *
     * @param key   the key
     * @param value the value
     */
    public void addAliases(String key, String value) {
        compilationSupport().addAliases(key, value);
    }

    @Override
    public void init() {
        if (compilationSupport != null) {
            compilationSupport.init();
        }
    }

    @Override
    public void start() {
        if (compilationSupport != null) {
            ServiceHelper.startService(compilationSupport.compiler);
        }
    }

    @Override
    public void stop() {
        if (compilationSupport != null) {
            ServiceHelper.stopService(compilationSupport.compiler);
        }
    }

    @Override
    public Predicate createPredicate(String expression) {
        if (expression == null) {
            throw new IllegalArgumentException("expression must be specified");
        }
        // text should be single line and trimmed as it can be multi lined
        String text = expression.replace("\n", "");
        text = text.trim();

        return compiledPredicates.computeIfAbsent(text, key -> {
            if (compilationSupport != null) {
                CSimpleExpression exp = compilationSupport.compilePredicate(getZwangineContext(), expression);
                if (exp != null) {
                    exp.init(getZwangineContext());
                    return exp;
                }
            }
            throw new CSimpleException("Cannot find compiled csimple language for predicate: " + expression, expression);
        });
    }

    @Override
    public Expression createExpression(String expression, Object[] properties) {
        Class<?> resultType = property(Class.class, properties, 0, null);
        if (Boolean.class == resultType || boolean.class == resultType) {
            // we want it compiled as a predicate
            return (Expression) createPredicate(expression);
        } else if (resultType == null || resultType == Object.class) {
            // No specific result type has been provided
            return createExpression(expression);
        }
        // A specific result type has been provided
        return ExpressionBuilder.convertToExpression(createExpression(expression), resultType);
    }

    @Override
    public Expression createExpression(String expression) {
        if (expression == null) {
            throw new IllegalArgumentException("expression must be specified");
        }
        // text should be single line and trimmed as it can be multi lined
        String text = expression.replace("\n", "");
        text = text.trim();

        return compiledExpressions.computeIfAbsent(text, key -> {
            if (compilationSupport != null) {
                CSimpleExpression exp = compilationSupport.compileExpression(getZwangineContext(), expression);
                if (exp != null) {
                    exp.init(getZwangineContext());
                    return exp;
                }
            }
            throw new CSimpleException("Cannot find compiled csimple language for expression: " + expression, expression);
        });
    }

    private CompilationSupport compilationSupport() {
        if (compilationSupport == null) {
            throw new IllegalStateException(
                    "Runtime Compilation is not supported with this " + CSimpleLanguage.class.getSimpleName());
        }
        return compilationSupport;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Map<String, CSimpleExpression> compiledPredicates = new LinkedHashMap<>();
        private Map<String, CSimpleExpression> compiledExpressions = new LinkedHashMap<>();

        public CSimpleLanguage build() {
            final Map<String, CSimpleExpression> predicates = compiledPredicates.isEmpty()
                    ? Collections.emptyMap()
                    : new ConcurrentHashMap<>(compiledPredicates);
            this.compiledPredicates = null; // invalidate the builder to prevent leaking the mutable collection
            final Map<String, CSimpleExpression> expressions = compiledExpressions.isEmpty()
                    ? Collections.emptyMap()
                    : new ConcurrentHashMap<>(compiledExpressions);
            this.compiledExpressions = null; // invalidate the builder to prevent leaking the mutable collection
            return new CSimpleLanguage(predicates, expressions);
        }

        public Builder expression(CSimpleExpression expression) {
            (expression.isPredicate() ? compiledPredicates : compiledExpressions).put(expression.getText(), expression);
            return this;
        }
    }

    class CompilationSupport {
        private CSimpleCompiler compiler;
        private String configResource = "zwangine-csimple.properties";
        private final Set<String> imports = new TreeSet<>();
        private final Map<String, String> aliases = new HashMap<>();

        public void addImport(String imports) {
            if (!imports.startsWith("import ")) {
                imports = "import " + imports;
            }
            if (compiler != null) {
                compiler.addImport(imports);
            } else {
                this.imports.add(imports);
            }
        }

        public void init() {
            // load pre compiled first
            loadPreCompiled();

            // load optional configuration file
            loadConfiguration();

            // detect custom compiler (zwangine-csimple-joor)
            ZwangineContext ecc = getZwangineContext();
            Optional<Class<?>> clazz
                    = ecc.getZwangineContextExtension().getBootstrapFactoryFinder().findClass(CSimpleCompiler.FACTORY);
            if (clazz.isPresent()) {
                compiler = (CSimpleCompiler) ecc.getInjector().newInstance(clazz.get(), false);
                if (compiler != null) {
                    LOG.info("Detected zwangine-csimple-joor compiler");
                    imports.forEach(compiler::addImport);
                    aliases.forEach(compiler::addAliases);
                }
                ServiceHelper.initService(compiler);
            }
        }

        public CSimpleExpression compilePredicate(ZwangineContext zwangineContext, String expression) {
            if (compiler != null) {
                return compiler.compilePredicate(zwangineContext, expression);
            }
            return null;
        }

        public CSimpleExpression compileExpression(ZwangineContext zwangineContext, String expression) {
            if (compiler != null) {
                return compiler.compileExpression(zwangineContext, expression);
            }
            return null;
        }

        public void addAliases(String key, String value) {
            if (compiler != null) {
                compiler.addAliases(key, value);
            } else {
                this.aliases.put(key, value);
            }
        }

        private void loadPreCompiled() {
            ZwangineContext ecc = getZwangineContext();
            InputStream is = ecc.getClassResolver().loadResourceAsStream(PRE_COMPILED_FILE);
            if (is != null) {
                try {
                    String text = IOHelper.loadText(is);
                    String[] lines = text.split("\n");
                    for (String fqn : lines) {
                        // skip comments
                        fqn = fqn.trim();
                        if (fqn.startsWith("#") || fqn.isEmpty()) {
                            continue;
                        }
                        // load class
                        Class<CSimpleExpression> clazz
                                = ecc.getClassResolver().resolveMandatoryClass(fqn, CSimpleExpression.class);
                        CSimpleExpression ce = clazz.getConstructor().newInstance();
                        ce.init(getZwangineContext());
                        if (ce.isPredicate()) {
                            compiledPredicates.put(ce.getText(), ce);
                        } else {
                            compiledExpressions.put(ce.getText(), ce);
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeZwangineException("Error initializing csimple language", e);
                } finally {
                    IOHelper.close(is);
                }
                int size = compiledPredicates.size() + compiledExpressions.size();
                if (size > 0) {
                    LOG.info("Loaded and initialized {} csimple expressions from classpath", size);
                }
            }
        }

        private void loadConfiguration() {
            InputStream is;
            final String loaded = load(configResource);
            if (loaded == null) {
                return;
            }

            int counter1 = 0;
            int counter2 = 0;
            String[] lines = loaded.split("\n");
            for (String line : lines) {
                line = line.trim();
                // skip comments
                if (line.startsWith("#")) {
                    continue;
                }
                // imports
                if (line.startsWith("import ")) {
                    imports.add(line);
                    counter1++;
                    continue;
                }
                // aliases as key=value
                String key = StringHelper.before(line, "=");
                String value = StringHelper.after(line, "=");
                if (key != null) {
                    key = key.trim();
                }
                if (value != null) {
                    value = value.trim();
                }
                if (key != null && value != null) {
                    this.aliases.put(key, value);
                    counter2++;
                }
            }
            if (counter1 > 0 || counter2 > 0) {
                LOG.info("Loaded csimple language imports: {} and aliases: {} from configuration: {}", counter1, counter2,
                        configResource);
            }
        }

    }

    private String load(String configResource) {
        InputStream is;
        String loaded;
        is = getZwangineContext().getClassResolver().loadResourceAsStream(CONFIG_FILE);
        try {
            if (is == null) {
                // load from file system
                File file = new File(configResource);
                if (file.exists()) {
                    is = new FileInputStream(file);
                }
            }
            if (is == null) {
                return null;
            }
            loaded = IOHelper.loadText(is);
        } catch (IOException e) {
            throw new RuntimeZwangineException("Cannot load " + CONFIG_FILE + " from classpath");

        }
        IOHelper.close(is);
        return loaded;
    }

}
