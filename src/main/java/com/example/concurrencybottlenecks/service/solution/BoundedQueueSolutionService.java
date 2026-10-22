package com.example.concurrencybottlenecks.service.solution;

import com.example.concurrencybottlenecks.diagnostics.JobQueue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class BoundedQueueSolutionService {
    private final JobQueue queue;

    public BoundedQueueSolutionService(@Qualifier("solutionQueue") JobQueue queue) {
        this.queue = queue;
    }

    public JobQueue.AcceptedJob submit() {
        return queue.submit();
    }

    public JobQueue.Snapshot snapshot() {
        return queue.snapshot();
    }
}
