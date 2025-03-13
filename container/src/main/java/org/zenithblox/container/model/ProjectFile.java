package org.zenithblox.container.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectFile {
    String name;
    String code;
    String projectId;
    Long lastUpdate;

    public ProjectFile copy() {
        return new ProjectFile(name, code, projectId, lastUpdate);
    }

    @Override
    public String toString() {
        return "ProjectFile{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", projectId='" + projectId + '\'' +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
