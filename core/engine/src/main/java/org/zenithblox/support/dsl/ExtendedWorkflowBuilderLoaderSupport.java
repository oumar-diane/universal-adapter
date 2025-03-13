package org.zenithblox.support.dsl;

import org.zenithblox.WorkflowsBuilder;
import org.zenithblox.builder.WorkflowBuilder;
import org.zenithblox.spi.ExtendedWorkflowsBuilderLoader;
import org.zenithblox.spi.Resource;

import java.util.Collection;

/**
 * Base class for {@link org.zenithblox.spi.WorkflowsBuilderLoader} implementations.
 */
public abstract class ExtendedWorkflowBuilderLoaderSupport extends WorkflowBuilderLoaderSupport
        implements ExtendedWorkflowsBuilderLoader {

    protected ExtendedWorkflowBuilderLoaderSupport(String extension) {
        super(extension);
    }

    @Override
    protected WorkflowBuilder doLoadWorkflowBuilder(Resource resource) throws Exception {
        // noop
        return null;
    }

    @Override
    public Collection<WorkflowsBuilder> loadWorkflowsBuilders(Collection<Resource> resources) throws Exception {
        return doLoadWorkflowsBuilders(resources);
    }

    protected abstract Collection<WorkflowsBuilder> doLoadWorkflowsBuilders(Collection<Resource> resources) throws Exception;

}
