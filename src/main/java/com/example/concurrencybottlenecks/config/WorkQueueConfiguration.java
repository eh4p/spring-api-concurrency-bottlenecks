package com.example.concurrencybottlenecks.config;

import com.example.concurrencybottlenecks.diagnostics.JobQueue;
import com.example.concurrencybottlenecks.service.SecondaryRiskEvaluationService;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkQueueConfiguration {
    @Bean(destroyMethod = "close")
    public JobQueue violationQueue(SecondaryRiskEvaluationService work, MeterRegistry registry) {
        return new JobQueue("violation", new LinkedBlockingQueue<>(), work, registry);
    }

    @Bean(destroyMethod = "close")
    public JobQueue solutionQueue(SecondaryRiskEvaluationService work, MeterRegistry registry) {
        // Demo configuration only — not production sizing guidance: 2 workers + 8 waiting jobs.
        return new JobQueue("solution", new ArrayBlockingQueue<>(8), work, registry);
    }
}
