package org.zenithblox.container.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class ServiceConfig {

    private final Environment env;
    @Qualifier("simple-task-executor")
    private final SimpleAsyncTaskExecutor simpleAsyncTaskExecutor;


}
