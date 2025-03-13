package org.zenithblox.container.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration
public class ExecutorsConfig {

    @Bean(name = "simple-task-executor")
    @Scope("singleton")
    public SimpleAsyncTaskExecutor taskExecutor() {
        var taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setVirtualThreads(true);
        return taskExecutor;
    }
}
