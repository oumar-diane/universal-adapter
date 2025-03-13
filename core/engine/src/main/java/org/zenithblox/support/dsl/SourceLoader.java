package org.zenithblox.support.dsl;

import org.zenithblox.spi.Resource;

import java.io.IOException;

/**
 * Loader for loading the source code from {@link Resource} which is the json binary file.
 *
 * Custom {@link SourceLoader} implementations can be plugged into the {@link org.zenithblox.ZwangineContext} by adding
 * to the {@link org.zenithblox.spi.Registry}.
 */
public interface SourceLoader {

    /**
     * Loads the source from the given resource
     *
     * @param  resource    the resource
     * @return             the source code (such as json)
     *
     * @throws IOException is thrown if error loading the source
     */
    String loadResource(Resource resource) throws IOException;

}