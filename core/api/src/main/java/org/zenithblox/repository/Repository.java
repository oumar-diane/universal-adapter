package org.zenithblox.repository;

import org.zenithblox.FailedToInitilizedRepository;

/**
 * a {@link Repository} is the place where all
 *
 * ***/
public interface Repository {

    boolean isInitialized();

    void initialize() throws FailedToInitilizedRepository;



}
