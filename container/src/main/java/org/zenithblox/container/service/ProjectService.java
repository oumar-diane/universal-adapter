package org.zenithblox.container.service;

import org.springframework.stereotype.Service;
import org.zenithblox.container.model.Project;

import java.util.List;

@Service
public interface ProjectService {
    String runProjectInDeveloperMode(String projectId) throws Exception;
    List<Project> getAllProjects(String type);
    Project create(Project project);
    Project copy(String sourceProjectId, Project project) throws Exception;
    void deploy(Project project) throws Exception;
}
