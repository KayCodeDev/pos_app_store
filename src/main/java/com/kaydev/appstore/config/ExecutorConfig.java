package com.kaydev.appstore.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExecutorConfig {

    @Bean(name = "executorService")
    public ExecutorService executorService() {
        return Executors.newCachedThreadPool();
    }
}