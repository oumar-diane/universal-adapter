package org.zenithblox.container.controller.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.zenithblox.ZwangineContext;
import org.zenithblox.container.configs.WorkflowContextManager;
import org.zenithblox.container.controller.WorkflowController;
import org.zenithblox.support.PluginHelper;
import org.zenithblox.support.ResourceHelper;


@Controller
@Slf4j
public class WorkflowControllerImpl implements WorkflowController {

    @Override
    @MessageMapping("/workflow.deploy")
    @SendTo("/topic/workflow.deployment")
    public String  deploy(String code) throws Exception {
        log.info("Deploying workflow: {} ", code);
        ZwangineContext context = WorkflowContextManager.getContext();
        PluginHelper.getWorkflowsLoader(context).loadWorkflows(ResourceHelper.fromString("workflow"+Math.round(Math.random())+".yaml", code.strip()));
        return "Workflow deployed successfully";
    }
}
