package com.example.concurrencybottlenecks.controller;

import com.example.concurrencybottlenecks.dto.AuthorizationResult;
import com.example.concurrencybottlenecks.dto.ScenarioResponse;
import com.example.concurrencybottlenecks.service.solution.ShortTransactionSolutionService;
import com.example.concurrencybottlenecks.service.violation.ConnectionPoolViolationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.WebAsyncTask;

@RestController
@RequestMapping("/api/v1/concurrency/db-pool")
public class ConnectionPoolController {
    private final ConnectionPoolViolationService violation;
    private final ShortTransactionSolutionService solution;
    private final AsyncTaskExecutor executor;

    public ConnectionPoolController(ConnectionPoolViolationService violation,
                                    ShortTransactionSolutionService solution,
                                    @Qualifier("virtualMvcExecutor") AsyncTaskExecutor executor) {
        this.violation = violation;
        this.solution = solution;
        this.executor = executor;
    }

    @PostMapping("/violation")
    public WebAsyncTask<ScenarioResponse<AuthorizationResult>> violation(
            @RequestParam(defaultValue = "1") long accountId) {
        long started = System.nanoTime();
        return new WebAsyncTask<>(30_000L, executor,
                () -> ScenarioResponse.measured("DATABASE_CONNECTION_POOL", "VIOLATION",
                        started, violation.authorize(accountId)));
    }

    @PostMapping("/solution")
    public WebAsyncTask<ScenarioResponse<AuthorizationResult>> solution(
            @RequestParam(defaultValue = "1") long accountId) {
        long started = System.nanoTime();
        return new WebAsyncTask<>(30_000L, executor,
                () -> ScenarioResponse.measured("DATABASE_CONNECTION_POOL", "SOLUTION",
                        started, solution.authorize(accountId)));
    }
}
