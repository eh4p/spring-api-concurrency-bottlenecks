package com.example.apisaturation.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;

@Configuration
public class ExecutorConfiguration {
    @Bean(destroyMethod = "close")
    public ExecutorService virtualExecutor() {
        return Executors.newThreadPerTaskExecutor(Thread.ofVirtual().name("demo-virtual-", 0).factory());
    }

    @Bean
    public AsyncTaskExecutor virtualMvcExecutor(@Qualifier("virtualExecutor") ExecutorService executor) {
        return new TaskExecutorAdapter(executor);
    }

    @Bean(destroyMethod = "close")
    public ExecutorService lockExecutor() {
        // Platform workers make monitor contention visible in jcmd Thread.print.
        // Both lock implementations use this same capacity, independent of Tomcat.
        return Executors.newFixedThreadPool(40, Thread.ofPlatform().name("demo-lock-", 0).factory());
    }

    @Bean
    public AsyncTaskExecutor lockMvcExecutor(@Qualifier("lockExecutor") ExecutorService executor) {
        return new TaskExecutorAdapter(executor);
    }
}
