package org.zenithblox.support.dsl;


import org.zenithblox.StartupStep;
import org.zenithblox.WorkflowsBuilder;
import org.zenithblox.ZwangineContextAware;
import org.zenithblox.builder.WorkflowBuilder;
import org.zenithblox.builder.WorkflowBuilderLifecycleStrategy;
import org.zenithblox.spi.CompilePostProcessor;
import org.zenithblox.spi.CompilePreProcessor;
import org.zenithblox.spi.Resource;
import org.zenithblox.spi.StartupStepRecorder;
import org.zenithblox.support.WorkflowsBuilderLoaderSupport;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Base class for {@link org.zenithblox.spi.WorkflowsBuilderLoader} implementations.
 */
public abstract class WorkflowBuilderLoaderSupport extends WorkflowsBuilderLoaderSupport {
    private final String extension;
    private final List<CompilePreProcessor> compilePreProcessors = new ArrayList<>();
    private final List<CompilePostProcessor> compilePostProcessors = new ArrayList<>();
    private StartupStepRecorder recorder;
    private SourceLoader sourceLoader = new DefaultSourceLoader();

    protected WorkflowBuilderLoaderSupport(String extension) {
        this.extension = extension;
    }
    
    @Override
    public String getSupportedExtension() {
        return extension;
    }
    
    @Override
    public boolean isSupportedExtension(String extension) {
        return super.isSupportedExtension(extension);
    }

    /**
     * Gets the registered {@link CompilePreProcessor}.
     */
    public List<CompilePreProcessor> getCompilePreProcessors() {
        return compilePreProcessors;
    }

    /**
     * Add a custom {@link CompilePreProcessor} to handle specific pre-processing before compiling the source into a
     * Java object.
     */
    public void addCompilePreProcessor(CompilePreProcessor preProcessor) {
        this.compilePreProcessors.add(preProcessor);
    }

    /**
     * Gets the registered {@link CompilePostProcessor}.
     */
    public List<CompilePostProcessor> getCompilePostProcessors() {
        return compilePostProcessors;
    }

    /**
     * Add a custom {@link CompilePostProcessor} to handle specific post-processing after compiling the source into a
     * Java object.
     */
    public void addCompilePostProcessor(CompilePostProcessor preProcessor) {
        this.compilePostProcessors.add(preProcessor);
    }

    @Override
    protected void doBuild() throws Exception {
        super.doBuild();

        if (getZwangineContext() != null) {
            this.recorder = getZwangineContext().getZwangineContextExtension().getStartupStepRecorder();
        }
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();

        if (getZwangineContext() != null) {
            // discover optional compile pre-processors to be used
            Set<CompilePreProcessor> pres = getZwangineContext().getRegistry().findByType(CompilePreProcessor.class);
            if (pres != null && !pres.isEmpty()) {
                for (CompilePreProcessor pre : pres) {
                    addCompilePreProcessor(pre);
                }
            }
            // discover optional compile post-processors to be used
            Set<CompilePostProcessor> posts = getZwangineContext().getRegistry().findByType(CompilePostProcessor.class);
            if (posts != null && !posts.isEmpty()) {
                for (CompilePostProcessor post : posts) {
                    addCompilePostProcessor(post);
                }
            }
            // discover a special source loader to be used
            SourceLoader sl = getZwangineContext().getRegistry().findSingleByType(SourceLoader.class);
            if (sl != null) {
                this.sourceLoader = sl;
            }
        }
    }

    @Override
    public WorkflowsBuilder loadWorkflowsBuilder(Resource resource) throws Exception {
        final WorkflowBuilder builder = doLoadWorkflowBuilder(resource);
        if (builder != null) {
            ZwangineContextAware.trySetZwangineContext(builder, getZwangineContext());
            builder.setResource(resource);

            if (recorder != null) {
                StartupStep step = recorder.beginStep(
                        getClass(),
                        resource.getLocation(),
                        "Loading route from: " + resource.getLocation());

                builder.addLifecycleInterceptor(new WorkflowBuilderLifecycleStrategy() {
                    @Override
                    public void afterConfigure(WorkflowBuilder builder) {
                        step.endStep();
                    }
                });
            }
        }

        return builder;
    }

    /**
     * Gets the input stream to the resource
     *
     * @param  resource the resource
     * @return          the input stream
     */
    protected InputStream resourceInputStream(Resource resource) throws IOException {
        // load into memory as we need to skip a specific first-line if present
        String data = sourceLoader.loadResource(resource);
        if (data.isBlank()) {
            throw new IOException("Resource is empty: " + resource.getLocation());
        }
        return new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Loads {@link WorkflowsBuilder} from {@link Resource} from the DSL implementation.
     *
     * @param  resource the resource to be loaded.
     * @return          a {@link WorkflowsBuilder}
     */
    protected abstract WorkflowBuilder doLoadWorkflowBuilder(Resource resource) throws Exception;

}

