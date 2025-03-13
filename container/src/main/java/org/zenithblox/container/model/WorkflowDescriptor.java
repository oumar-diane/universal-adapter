package org.zenithblox.container.model;

import lombok.Data;

import java.util.UUID;

@Data
public class WorkflowDescriptor {
    private UUID identifier;
    private String name;
}
