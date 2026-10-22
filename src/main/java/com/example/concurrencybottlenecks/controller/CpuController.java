package com.example.concurrencybottlenecks.controller;

import com.example.concurrencybottlenecks.dto.RiskResult;
import com.example.concurrencybottlenecks.dto.ScenarioResponse;
import com.example.concurrencybottlenecks.service.solution.RiskCalculationSolutionService;
import com.example.concurrencybottlenecks.service.violation.RiskCalculationViolationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.WebAsyncTask;

@RestController
@RequestMapping("/api/v1/concurrency/cpu")
public class CpuController {
    private final RiskCalculationViolationService violation;
    private final RiskCalculationSolutionService solution;
    private final AsyncTaskExecutor executor;

    public CpuController(RiskCalculationViolationService violation, RiskCalculationSolutionService solution,
                         @Qualifier("virtualMvcExecutor") AsyncTaskExecutor executor) {
        this.violation = violation;
        this.solution = solution;
        this.executor = executor;
    }

    @PostMapping("/violation")
    public WebAsyncTask<ScenarioResponse<RiskResult>> violation() {
        long started = System.nanoTime();
        return new WebAsyncTask<>(30_000L, executor,
                () -> ScenarioResponse.measured("CPU", "VIOLATION", started, violation.calculate()));
    }

    @PostMapping("/solution")
    public WebAsyncTask<ScenarioResponse<RiskResult>> solution() {
        long started = System.nanoTime();
        return new WebAsyncTask<>(30_000L, executor,
                () -> ScenarioResponse.measured("CPU", "SOLUTION", started, solution.calculate()));
    }
}
