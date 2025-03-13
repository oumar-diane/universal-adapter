package org.zenithblox.container.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeploymentStatus {
    private String projectId;
    private String namespace;
    private String env;

    public DeploymentStatus copy() {
        return new DeploymentStatus(projectId, namespace, env);
    }
}
