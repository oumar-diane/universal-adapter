package org.zenithblox.container.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContainerStatusValue {
    public enum Name {

        context,
        inflight,
        memory,
        properties,
        route,
        trace,
        jvm,
        source,
        debug
    }

    private ContainerStatusValue.Name name;
    private String status;

}
