package com.example.apisaturation.service.threadpool;

import com.example.apisaturation.dto.TransferRequest;
import com.example.apisaturation.dto.TransferResult;
import com.example.apisaturation.service.SimulatedExternalIo;
import org.springframework.stereotype.Service;

/** INTENTIONAL VIOLATION: independent blocking calls occupy a servlet worker for 900 ms. */
@Service
public class BlockingTransferViolationService {
    private final SimulatedExternalIo externalIo;

    public BlockingTransferViolationService(SimulatedExternalIo externalIo) {
        this.externalIo = externalIo;
    }

    public TransferResult assess(TransferRequest request) throws InterruptedException {
        externalIo.verifyCustomer();
        externalIo.verifyAccount();
        return TransferResult.assessed(request, externalIo.lookupRisk());
    }
}
