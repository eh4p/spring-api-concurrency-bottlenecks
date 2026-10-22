package com.example.apisaturation.service.locking;

import com.example.apisaturation.dto.TransferRequest;
import com.example.apisaturation.dto.TransferResult;
import com.example.apisaturation.service.SimulatedExternalIo;
import org.springframework.stereotype.Service;

@Service
public class LockContentionSolutionService {
    private final SimulatedExternalIo externalIo;

    public LockContentionSolutionService(SimulatedExternalIo externalIo) {
        this.externalIo = externalIo;
    }

    public TransferResult assess(TransferRequest request) throws InterruptedException {
        int riskScore = externalIo.lookupRisk();
        return TransferResult.assessed(request, riskScore);
    }
}
