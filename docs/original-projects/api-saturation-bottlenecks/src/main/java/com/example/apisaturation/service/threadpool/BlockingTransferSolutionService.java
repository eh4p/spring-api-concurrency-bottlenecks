package com.example.apisaturation.service.threadpool;

import com.example.apisaturation.dto.TransferRequest;
import com.example.apisaturation.dto.TransferResult;
import com.example.apisaturation.service.SimulatedExternalIo;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class BlockingTransferSolutionService {
    private final SimulatedExternalIo externalIo;
    private final ExecutorService executor;

    public BlockingTransferSolutionService(SimulatedExternalIo externalIo,
            @Qualifier("virtualExecutor") ExecutorService executor) {
        this.externalIo = externalIo;
        this.executor = executor;
    }

    public TransferResult assess(TransferRequest request) throws InterruptedException, ExecutionException {
        var customer = executor.submit(externalIo::verifyCustomer);
        var account = executor.submit(externalIo::verifyAccount);
        var risk = executor.submit(externalIo::lookupRisk);
        try {
            customer.get();
            account.get();
            return TransferResult.assessed(request, risk.get());
        } finally {
            // Interrupted/failed request work must not leave provider tasks running.
            customer.cancel(true);
            account.cancel(true);
            risk.cancel(true);
        }
    }
}
