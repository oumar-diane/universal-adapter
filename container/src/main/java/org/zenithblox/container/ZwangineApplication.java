package org.zenithblox.container;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.zenithblox.ZwangineContext;
import org.zenithblox.builder.WorkflowBuilder;
import org.zenithblox.impl.DefaultZwangineContext;

@SpringBootApplication
@Slf4j
@RequiredArgsConstructor
@EnableAsync
public class ZwangineApplication {


    public static void main(String[] args) {
        SpringApplication.run(ZwangineApplication.class, args);
    }


}
