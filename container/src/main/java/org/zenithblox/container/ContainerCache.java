package org.zenithblox.container;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.zenithblox.container.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.zenithblox.container.ContainerConstant.DEV;

@Component
@RequiredArgsConstructor
public class ContainerCache {

    private final Map<String, Project> projects = new ConcurrentHashMap<>();
    private final Map<String, ProjectFile> files = new ConcurrentHashMap<>();
    private final Map<String, ProjectFile> filesCommited = new ConcurrentHashMap<>();

    private final Map<String, DeploymentStatus> deploymentStatuses = new ConcurrentHashMap<>();
    private final Map<String, Boolean> transits = new ConcurrentHashMap<>();
    private final Map<String, ContainerStatus> containerStatuses = new ConcurrentHashMap<>();

    private final SimpMessagingTemplate template;

    private List<Project> getCopyProjects() {
        List<Project> copy = new ArrayList<>(projects.size());
        projects.values().forEach(e -> copy.add(e.copy()));
        return copy;
    }

    private List<ProjectFile> getCopyProjectFiles() {
        List<ProjectFile> copy = new ArrayList<>(files.size());
        files.values().forEach(e -> copy.add(e.copy()));
        return copy;
    }

    private Map<String, ProjectFile> getCopyProjectFilesMap() {
        Map<String, ProjectFile> copy = new ConcurrentHashMap<>(files.size());
        files.forEach((key, value) -> copy.put(key, value.copy()));
        return copy;
    }

    public List<ContainerStatus> getCopyContainerStatuses() {
        List<ContainerStatus> copy = new ArrayList<>(containerStatuses.size());
        containerStatuses.values().forEach(e -> copy.add(e.copy()));
        return copy;
    }



    public List<DeploymentStatus> getCopyDeploymentStatuses() {
        List<DeploymentStatus> copy = new ArrayList<>(deploymentStatuses.size());
        deploymentStatuses.values().forEach(e -> copy.add(e.copy()));
        return copy;
    }

    public List<Project> getProjects() {
        return new ArrayList<>(getCopyProjects());
    }

    public void saveProject(Project project, boolean startup) {
        var key = GroupedKey.create(project.getProjectId(), DEV, project.getProjectId());
        projects.put(key, project);
        if (!startup) {
            template.convertAndSend("/topic/startup", project);
        }
    }

    public List<ProjectFile> getProjectFiles(String projectId) {
        return getCopyProjectFiles().stream().filter(pf -> Objects.equals(pf.getProjectId(), projectId)).toList();
    }

    public Map<String, ProjectFile> getProjectFilesMap(String projectId) {
        return getCopyProjectFilesMap().entrySet().stream()
                .filter(es -> !Objects.isNull(es.getValue()) && Objects.equals(es.getValue().getProjectId(), projectId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public ProjectFile getProjectFile(String projectId, String filename) {
        List<ProjectFile> list = getCopyProjectFiles().stream().filter(pf -> Objects.equals(pf.getProjectId(), projectId) && Objects.equals(pf.getName(), filename)).toList();
        return !list.isEmpty() ? list.get(0) : null;
    }

    public List<ProjectFile> getProjectFilesByName(String filename) {
        return getCopyProjectFiles().stream().filter(pf -> Objects.equals(pf.getName(), filename)).toList();
    }

    public void saveProjectFile(ProjectFile file, boolean commited, boolean startup) {
        files.put(GroupedKey.create(file.getProjectId(), DEV, file.getName()), file);
        if (!startup) {
            template.convertAndSend("/topic/startup", file);
        }
        if (commited) {
            filesCommited.put(GroupedKey.create(file.getProjectId(), DEV, file.getName()), file);
        }
    }

}
