package org.zenithblox.container.controller;


import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.zenithblox.container.model.WorkflowDescriptor;

@Controller
public interface WorkflowController {

    @MessageMapping("/workflow.new")
    WorkflowDescriptor newWorkflow(@Payload String name);

}
