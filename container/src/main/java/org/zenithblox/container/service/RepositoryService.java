package org.zenithblox.container.service;

import org.springframework.stereotype.Service;
import org.zenithblox.container.model.Repository;

@Service
public interface RepositoryService {
    Repository readProjectFromRepository(String projectId);
}
