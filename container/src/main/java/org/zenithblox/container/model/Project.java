package org.zenithblox.container.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Project {
    
    private String projectId;
    private String name;

    public Project copy() {
        return new Project(projectId, name);
    }


    @Override
    public String toString() {
        return "Project{" +
                "projectId='" + projectId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
