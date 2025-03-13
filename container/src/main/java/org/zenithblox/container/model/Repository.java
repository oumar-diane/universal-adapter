package org.zenithblox.container.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Repository {
    private String name;
    private String commitId;
    private Long lastCommitTimestamp;
    private List<RepositoryFile> files;
}
