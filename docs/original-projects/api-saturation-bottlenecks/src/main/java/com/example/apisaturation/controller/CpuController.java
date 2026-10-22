package com.example.apisaturation.controller;

import com.example.apisaturation.dto.DuplicateRequest;
import com.example.apisaturation.dto.DuplicateResult;
import com.example.apisaturation.service.cpu.CpuBottleneckViolationService;
import com.example.apisaturation.service.cpu.CpuBottleneckSolutionService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.WebAsyncTask;

@RestController
@RequestMapping("/api/bottlenecks/cpu")
public class CpuController {
    private static final long REQUEST_TIMEOUT_MS = 30_000L;
    private final CpuBottleneckViolationService violation;
    private final CpuBottleneckSolutionService solution;
    private final AsyncTaskExecutor executor;

    public CpuController(CpuBottleneckViolationService violation, CpuBottleneckSolutionService solution,
            @Qualifier("virtualMvcExecutor") AsyncTaskExecutor executor) {
        this.violation = violation;
        this.solution = solution;
        this.executor = executor;
    }

    @PostMapping("/violation")
    public WebAsyncTask<DuplicateResult> violation(@RequestBody DuplicateRequest request) {
        return new WebAsyncTask<>(REQUEST_TIMEOUT_MS, executor, () -> violation.detect(request));
    }

    @PostMapping("/solution")
    public WebAsyncTask<DuplicateResult> solution(@RequestBody DuplicateRequest request) {
        return new WebAsyncTask<>(REQUEST_TIMEOUT_MS, executor, () -> solution.detect(request));
    }
}
