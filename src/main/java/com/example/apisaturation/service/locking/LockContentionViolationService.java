package com.example.apisaturation.service.locking;

import com.example.apisaturation.dto.TransferRequest;
import com.example.apisaturation.dto.TransferResult;
import com.example.apisaturation.service.SimulatedExternalIo;
import org.springframework.stereotype.Service;

/** INTENTIONAL VIOLATION: request-local risk state is shared, requiring an over-broad monitor. */
@Service
public class LockContentionViolationService {
    private final SimulatedExternalIo externalIo;
    private int lastRiskScore;

    public LockContentionViolationService(SimulatedExternalIo externalIo) {
        this.externalIo = externalIo;
    }

    public synchronized TransferResult assess(TransferRequest request) throws InterruptedException {
        lastRiskScore = externalIo.lookupRisk();
        return TransferResult.assessed(request, lastRiskScore);
    }
}
