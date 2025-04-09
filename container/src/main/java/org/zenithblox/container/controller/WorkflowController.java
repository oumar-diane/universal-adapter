package org.zenithblox.container.controller;

import org.springframework.messaging.handler.annotation.Payload;


public interface WorkflowController {

    String deploy(@Payload String code) throws Exception;

}
