package com.example.apisaturation.controller;

import com.example.apisaturation.dto.TransferRequest;
import com.example.apisaturation.dto.TransferResult;
import com.example.apisaturation.service.locking.LockContentionViolationService;
import com.example.apisaturation.service.locking.LockContentionSolutionService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.WebAsyncTask;

@RestController
@RequestMapping("/api/bottlenecks/lock-contention")
public class LockContentionController {
    private static final long REQUEST_TIMEOUT_MS = 30_000L;
    private final LockContentionViolationService violation;
    private final LockContentionSolutionService solution;
    private final AsyncTaskExecutor executor;

    public LockContentionController(LockContentionViolationService violation, LockContentionSolutionService solution,
            @Qualifier("lockMvcExecutor") AsyncTaskExecutor executor) {
        this.violation = violation;
        this.solution = solution;
        this.executor = executor;
    }

    @PostMapping("/violation")
    public WebAsyncTask<TransferResult> violation(@RequestBody TransferRequest request) {
        return new WebAsyncTask<>(REQUEST_TIMEOUT_MS, executor, () -> violation.assess(request));
    }

    @PostMapping("/solution")
    public WebAsyncTask<TransferResult> solution(@RequestBody TransferRequest request) {
        return new WebAsyncTask<>(REQUEST_TIMEOUT_MS, executor, () -> solution.assess(request));
    }
}
