package com.example.concurrencybottlenecks.controller;

import com.example.concurrencybottlenecks.dto.ScenarioResponse;
import com.example.concurrencybottlenecks.service.solution.VirtualThreadFraudCheckSolutionService;
import com.example.concurrencybottlenecks.service.violation.BlockingFraudCheckViolationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.WebAsyncTask;

@RestController
@RequestMapping("/api/v1/concurrency/request-threads")
public class RequestThreadController {
    private final BlockingFraudCheckViolationService violation;
    private final VirtualThreadFraudCheckSolutionService solution;
    private final AsyncTaskExecutor executor;

    public RequestThreadController(BlockingFraudCheckViolationService violation,
                                   VirtualThreadFraudCheckSolutionService solution,
                                   @Qualifier("virtualMvcExecutor") AsyncTaskExecutor executor) {
        this.violation = violation;
        this.solution = solution;
        this.executor = executor;
    }

    @GetMapping("/violation")
    public ScenarioResponse<Integer> violation() throws InterruptedException {
        long started = System.nanoTime();
        return ScenarioResponse.measured("REQUEST_THREADS", "VIOLATION", started, violation.check());
    }

    @GetMapping("/solution")
    public WebAsyncTask<ScenarioResponse<Integer>> solution() {
        long started = System.nanoTime();
        return new WebAsyncTask<>(30_000L, executor,
                () -> ScenarioResponse.measured("REQUEST_THREADS", "SOLUTION", started, solution.check()));
    }
}
