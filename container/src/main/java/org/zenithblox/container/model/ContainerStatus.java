package org.zenithblox.container.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContainerStatus {
    private String projectId;
    private String containerName;
    List<ContainerStatus> statuses;
    private String env;

    public ContainerStatus copy() {
        return new ContainerStatus(this.projectId, this.containerName, List.copyOf(this.statuses), this.env);
    }

}
