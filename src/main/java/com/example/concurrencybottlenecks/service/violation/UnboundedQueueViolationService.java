package com.example.concurrencybottlenecks.service.violation;

import com.example.concurrencybottlenecks.diagnostics.JobQueue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/** Intentionally accepts work into an unbounded queue, hiding worker overload. */
@Service
public class UnboundedQueueViolationService {
    private final JobQueue queue;

    public UnboundedQueueViolationService(@Qualifier("violationQueue") JobQueue queue) {
        this.queue = queue;
    }

    public JobQueue.AcceptedJob submit() {
        return queue.submit();
    }

    public JobQueue.Snapshot snapshot() {
        return queue.snapshot();
    }
}
