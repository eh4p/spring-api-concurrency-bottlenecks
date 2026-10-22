package com.example.apisaturation.service.dbpool;

import com.example.apisaturation.dto.TransferRequest;
import com.example.apisaturation.dto.TransferResult;
import com.example.apisaturation.service.SimulatedExternalIo;
import org.springframework.stereotype.Service;

@Service
public class ConnectionPoolSolutionService {
    private final TransferPersistenceService persistence;
    private final SimulatedExternalIo externalIo;

    public ConnectionPoolSolutionService(TransferPersistenceService persistence, SimulatedExternalIo externalIo) {
        this.persistence = persistence;
        this.externalIo = externalIo;
    }

    public TransferResult process(TransferRequest request) throws InterruptedException {
        int score = externalIo.lookupRisk();
        return persistence.record(request, score);
    }
}
