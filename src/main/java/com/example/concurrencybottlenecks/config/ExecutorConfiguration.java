package com.example.concurrencybottlenecks.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExecutorConfiguration {
    @Bean(destroyMethod = "close")
    public ExecutorService virtualIoExecutor() {
        return Executors.newThreadPerTaskExecutor(Thread.ofVirtual().name("demo-virtual-", 0).factory());
    }

    @Bean
    public AsyncTaskExecutor virtualMvcExecutor(@Qualifier("virtualIoExecutor") ExecutorService virtualIoExecutor) {
        return new TaskExecutorAdapter(virtualIoExecutor);
    }
}
