package com.example.concurrencybottlenecks.controller;

import com.example.concurrencybottlenecks.diagnostics.JobQueue;
import com.example.concurrencybottlenecks.service.solution.BoundedQueueSolutionService;
import com.example.concurrencybottlenecks.service.violation.UnboundedQueueViolationService;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/concurrency/work-queue")
public class WorkQueueController {
    private final UnboundedQueueViolationService violation;
    private final BoundedQueueSolutionService solution;

    public WorkQueueController(UnboundedQueueViolationService violation, BoundedQueueSolutionService solution) {
        this.violation = violation;
        this.solution = solution;
    }

    @PostMapping("/violation")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public JobQueue.AcceptedJob violation() {
        return violation.submit();
    }

    @PostMapping("/solution")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public JobQueue.AcceptedJob solution() {
        return solution.submit();
    }

    @GetMapping("/metrics")
    public Map<String, JobQueue.Snapshot> metrics() {
        return Map.of("violation", violation.snapshot(), "solution", solution.snapshot());
    }
}
