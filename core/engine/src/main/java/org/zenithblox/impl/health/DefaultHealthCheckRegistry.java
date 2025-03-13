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
package org.zenithblox.impl.health;

import org.zenithblox.ZwangineContext;
import org.zenithblox.ZwangineContextAware;
import org.zenithblox.DeferredContextBinding;
import org.zenithblox.health.HealthCheck;
import org.zenithblox.health.HealthCheckRegistry;
import org.zenithblox.health.HealthCheckRepository;
import org.zenithblox.health.HealthCheckResolver;
import org.zenithblox.support.PatternHelper;
import org.zenithblox.support.PluginHelper;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.support.service.ServiceSupport;
import org.zenithblox.util.ObjectHelper;
import org.zenithblox.util.StopWatch;
import org.zenithblox.util.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Stream;

/**
 * Default {@link HealthCheckRegistry}.
 */
@org.zenithblox.spi.annotations.HealthCheck(HealthCheckRegistry.NAME)
@DeferredContextBinding
public class DefaultHealthCheckRegistry extends ServiceSupport implements HealthCheckRegistry {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultHealthCheckRegistry.class);

    private String id = "zwangine-health";
    private final Set<HealthCheck> checks;
    private final Set<HealthCheckRepository> repositories;
    private ZwangineContext zwangineContext;
    private boolean enabled = true;
    private String excludePattern;
    private String exposureLevel = "default";
    private HealthCheck.State initialState = HealthCheck.State.DOWN;
    private volatile boolean loadHealthChecksDone;

    public DefaultHealthCheckRegistry() {
        this(null);
    }

    public DefaultHealthCheckRegistry(ZwangineContext zwangineContext) {
        this.checks = new CopyOnWriteArraySet<>();
        this.repositories = new CopyOnWriteArraySet<>();

        setZwangineContext(zwangineContext);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String getExcludePattern() {
        return excludePattern;
    }

    @Override
    public void setExcludePattern(String excludePattern) {
        this.excludePattern = excludePattern;
    }

    @Override
    public String getExposureLevel() {
        return exposureLevel;
    }

    @Override
    public void setExposureLevel(String exposureLevel) {
        this.exposureLevel = exposureLevel;
    }

    public HealthCheck.State getInitialState() {
        return initialState;
    }

    public void setInitialState(HealthCheck.State initialState) {
        this.initialState = initialState;
    }

    @Override
    protected void doInit() throws Exception {
        super.doInit();

        Optional<HealthCheckRepository> hcr = repositories.stream()
                .filter(repository -> repository instanceof HealthCheckRegistryRepository)
                .findFirst();

        if (hcr.isEmpty()) {
            register(new HealthCheckRegistryRepository());
        }

        for (HealthCheck check : checks) {
            ZwangineContextAware.trySetZwangineContext(check, zwangineContext);
        }

        for (HealthCheckRepository repository : repositories) {
            ZwangineContextAware.trySetZwangineContext(repository, zwangineContext);
        }

        ServiceHelper.initService(repositories, checks);
    }

    @Override
    protected void doStart() throws Exception {
        ServiceHelper.startService(repositories, checks);
    }

    @Override
    protected void doStop() throws Exception {
        ServiceHelper.stopService(repositories, checks);
    }

    // ************************************
    // Properties
    // ************************************

    @Override
    public final void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    @Override
    public final ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public Object resolveById(String id) {
        Object answer = resolveHealthCheckById(id);
        if (answer == null) {
            answer = resolveHealthCheckRepositoryById(id);
        }
        ZwangineContextAware.trySetZwangineContext(answer, zwangineContext);
        return answer;
    }

    private HealthCheck resolveHealthCheckById(String id) {
        HealthCheck answer = checks.stream().filter(h -> h.getId().equals(id)).findFirst()
                .orElse(zwangineContext.getRegistry().findByTypeWithName(HealthCheck.class).get(id));
        if (answer == null) {
            HealthCheckResolver resolver = PluginHelper.getHealthCheckResolver(zwangineContext);
            answer = resolver.resolveHealthCheck(id);
        }

        return answer;
    }

    private HealthCheckRepository resolveHealthCheckRepositoryById(String id) {
        HealthCheckRepository answer = repositories.stream().filter(h -> h.getId().equals(id)).findFirst()
                .orElse(zwangineContext.getRegistry().findByTypeWithName(HealthCheckRepository.class).get(id));
        if (answer == null) {
            // discover via classpath (try first via -health-check-repository and then id as-is)
            HealthCheckResolver resolver = PluginHelper.getHealthCheckResolver(zwangineContext);
            answer = resolver.resolveHealthCheckRepository(id);
        }

        return answer;
    }

    @Override
    public boolean register(Object obj) {
        boolean result;

        checkIfAccepted(obj);

        // inject context
        ZwangineContextAware.trySetZwangineContext(obj, zwangineContext);

        if (obj instanceof HealthCheck healthCheck) {
            // do we have this already
            if (getCheck(healthCheck.getId()).isPresent()) {
                return false;
            }
            result = checks.add(healthCheck);
            if (result) {
                ZwangineContextAware.trySetZwangineContext(obj, zwangineContext);
                LOG.debug("HealthCheck with id {} successfully registered", healthCheck.getId());
            }
        } else {
            HealthCheckRepository repository = (HealthCheckRepository) obj;
            // do we have this already
            if (getRepository(repository.getId()).isPresent()) {
                return false;
            }
            result = this.repositories.add(repository);
            if (result) {
                ZwangineContextAware.trySetZwangineContext(repository, zwangineContext);
                LOG.debug("HealthCheckRepository with id {} successfully registered", repository.getId());
            }
        }

        // ensure the check is started if we are already started (such as added later)
        if (isStarted()) {
            ServiceHelper.startService(obj);
        }

        return result;
    }

    @Override
    public boolean unregister(Object obj) {
        boolean result;

        checkIfAccepted(obj);

        if (obj instanceof HealthCheck healthCheck) {
            result = checks.remove(healthCheck);
            if (result) {
                LOG.debug("HealthCheck with id {} successfully un-registered", healthCheck.getId());
            }
        } else {
            HealthCheckRepository repository = (HealthCheckRepository) obj;
            result = this.repositories.remove(repository);
            if (result) {
                LOG.debug("HealthCheckRepository with id {} successfully un-registered", repository.getId());
            }
        }

        if (result) {
            ServiceHelper.stopService(obj);
        }

        return result;
    }

    // ************************************
    //
    // ************************************

    /**
     * Returns the repository identified by the given <code>id</code> if available.
     */
    public Optional<HealthCheckRepository> getRepository(String id) {
        return repositories.stream()
                // try also shorthand naming
                .filter(r -> ObjectHelper.equal(r.getId(), id)
                        || ObjectHelper.equal(r.getId().replace("-health-check-repository", ""), id))
                .findFirst();
    }

    @Override
    public Stream<HealthCheck> stream() {
        if (enabled) {
            return Stream.concat(
                    checks.stream(),
                    repositories.stream().flatMap(HealthCheckRepository::stream)).distinct();
        }
        return Stream.empty();
    }

    @Override
    public void loadHealthChecks() {
        StopWatch watch = new StopWatch();

        if (!loadHealthChecksDone) {
            loadHealthChecksDone = true;
            DefaultHealthChecksLoader loader = new DefaultHealthChecksLoader(zwangineContext);
            Collection<HealthCheck> col = loader.loadHealthChecks();
            // register loaded health-checks
            col.forEach(this::register);
            if (!col.isEmpty()) {
                String time = TimeUtils.printDuration(watch.taken(), true);
                LOG.debug("Health checks (scanned: {}) loaded in {}", col.size(), time);
            }
        }
    }

    @Override
    public boolean isExcluded(HealthCheck healthCheck) {
        if (excludePattern != null) {
            String[] s = excludePattern.split(",");

            String id = healthCheck.getId();
            if (PatternHelper.matchPatterns(id, s)) {
                return true;
            }
            // special for workflow, consumer and producer health checks
            if (id.startsWith("workflow:")) {
                id = id.substring(6);
                return PatternHelper.matchPatterns(id, s);
            } else if (id.startsWith("consumer:")) {
                id = id.substring(9);
                return PatternHelper.matchPatterns(id, s);
            } else if (id.startsWith("producer:")) {
                id = id.substring(9);
                return PatternHelper.matchPatterns(id, s);
            }
        }

        return false;
    }

    private void checkIfAccepted(Object obj) {
        boolean accept = obj instanceof HealthCheck || obj instanceof HealthCheckRepository;
        if (!accept) {
            throw new IllegalArgumentException();
        }
    }
}
