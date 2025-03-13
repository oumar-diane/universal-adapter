package org.zenithblox.container.hooks;

import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;
import org.zenithblox.ZwangineContext;
import org.zenithblox.container.configs.WorkflowContextManager;

@Component
public class WorkflowContextSmartLifecycle implements SmartLifecycle {

    private boolean isWorkflowContextRunning = false;

    @Override
    public void start() {
        try {
            ZwangineContext context = WorkflowContextManager.getContext();
            isWorkflowContextRunning = true;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        try {
            WorkflowContextManager.getContext().stop();
            isWorkflowContextRunning = false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isRunning() {
        return isWorkflowContextRunning;
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }

}
