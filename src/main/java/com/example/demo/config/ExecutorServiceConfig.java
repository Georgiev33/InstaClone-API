package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorServiceConfig {

    @Bean
    public ExecutorService longPollingExecutorService() {
        int numberOfThreads = 10;
        return Executors.newFixedThreadPool(numberOfThreads);
    }
}