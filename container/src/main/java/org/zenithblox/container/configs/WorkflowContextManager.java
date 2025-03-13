package org.zenithblox.container.configs;

import lombok.extern.slf4j.Slf4j;
import org.zenithblox.ZwangineContext;
import org.zenithblox.impl.DefaultZwangineContext;

@Slf4j
public class WorkflowContextManager {

    private static ZwangineContext zwangineContext;

    private WorkflowContextManager() {}

    public static ZwangineContext getContext() throws Exception {
        if(zwangineContext == null) {
            zwangineContext= new DefaultZwangineContext();
            zwangineContext.setTracing(true);
            zwangineContext.start();
        }
        return zwangineContext;
    }
}
