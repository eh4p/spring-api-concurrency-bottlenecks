package com.example.concurrencybottlenecks.diagnostics;

import com.example.concurrencybottlenecks.service.SecondaryRiskEvaluationService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class JobQueue implements AutoCloseable {
    private final ThreadPoolExecutor executor;
    private final SecondaryRiskEvaluationService work;
    private final Counter rejected;
    private final Counter failed;
    private final Timer completion;
    private final Timer waiting;
    private final AtomicReference<CompletedJob> lastCompleted = new AtomicReference<>();

    public JobQueue(String implementation, BlockingQueue<Runnable> queue,
                    SecondaryRiskEvaluationService work, MeterRegistry registry) {
        this.work = work;
        this.executor = new ThreadPoolExecutor(2, 2, 0, TimeUnit.MILLISECONDS, queue,
                Thread.ofPlatform().name("risk-" + implementation + "-", 0).factory(),
                new ThreadPoolExecutor.AbortPolicy());
        String[] tags = {"implementation", implementation};
        Gauge.builder("demo.queue.active", executor, ThreadPoolExecutor::getActiveCount).tags(tags).register(registry);
        Gauge.builder("demo.queue.pool.size", executor, ThreadPoolExecutor::getPoolSize).tags(tags).register(registry);
        Gauge.builder("demo.queue.depth", executor, pool -> pool.getQueue().size()).tags(tags).register(registry);
        Gauge.builder("demo.queue.completed", executor, ThreadPoolExecutor::getCompletedTaskCount)
                .tags(tags).register(registry);
        rejected = Counter.builder("demo.queue.rejected").tags(tags).register(registry);
        failed = Counter.builder("demo.queue.failed").tags(tags).register(registry);
        completion = Timer.builder("demo.queue.completion").tags(tags).register(registry);
        waiting = Timer.builder("demo.queue.waiting").tags(tags).register(registry);
    }

    public AcceptedJob submit() {
        String jobId = "JOB-DEMO-" + UUID.randomUUID();
        long submitted = System.nanoTime();
        try {
            executor.execute(() -> {
                long started = System.nanoTime();
                waiting.record(started - submitted, TimeUnit.NANOSECONDS);
                try {
                    long fingerprint = work.evaluate();
                    lastCompleted.set(new CompletedJob(jobId, fingerprint,
                            (started - submitted) / 1_000_000.0,
                            (System.nanoTime() - submitted) / 1_000_000.0));
                } catch (RuntimeException exception) {
                    failed.increment();
                    throw exception;
                } finally {
                    completion.record(System.nanoTime() - submitted, TimeUnit.NANOSECONDS);
                }
            });
        } catch (RejectedExecutionException exception) {
            rejected.increment();
            throw exception;
        }
        return new AcceptedJob(jobId, "ACCEPTED", executor.getQueue().size());
    }

    public Snapshot snapshot() {
        return new Snapshot(executor.getActiveCount(), executor.getPoolSize(), executor.getQueue().size(),
                executor.getCompletedTaskCount(), (long) rejected.count(), (long) failed.count(), lastCompleted.get());
    }

    @Override
    public void close() {
        executor.close();
    }

    public record AcceptedJob(String jobId, String status, int queueDepth) {
    }

    public record CompletedJob(String jobId, long fingerprint, double queueWaitMs, double completionMs) {
    }

    public record Snapshot(int activeWorkers, int poolSize, int queueDepth, long completedTasks,
                           long rejectedTasks, long failedTasks, CompletedJob lastCompleted) {
    }
}
