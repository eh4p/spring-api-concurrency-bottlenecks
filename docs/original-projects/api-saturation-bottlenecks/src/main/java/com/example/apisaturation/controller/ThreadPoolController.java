package com.example.apisaturation.controller;

import com.example.apisaturation.dto.TransferRequest;
import com.example.apisaturation.dto.TransferResult;
import com.example.apisaturation.service.threadpool.BlockingTransferViolationService;
import com.example.apisaturation.service.threadpool.BlockingTransferSolutionService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.WebAsyncTask;

@RestController
@RequestMapping("/api/bottlenecks/thread-pool")
public class ThreadPoolController {
    private static final long REQUEST_TIMEOUT_MS = 30_000L;
    private final BlockingTransferViolationService violation;
    private final BlockingTransferSolutionService solution;
    private final AsyncTaskExecutor executor;

    public ThreadPoolController(BlockingTransferViolationService violation, BlockingTransferSolutionService solution,
            @Qualifier("virtualMvcExecutor") AsyncTaskExecutor executor) {
        this.violation = violation;
        this.solution = solution;
        this.executor = executor;
    }

    @PostMapping("/violation")
    public TransferResult violation(@RequestBody TransferRequest request) throws InterruptedException {
        return violation.assess(request);
    }

    @PostMapping("/solution")
    public WebAsyncTask<TransferResult> solution(@RequestBody TransferRequest request) {
        return new WebAsyncTask<>(REQUEST_TIMEOUT_MS, executor, () -> solution.assess(request));
    }
}
