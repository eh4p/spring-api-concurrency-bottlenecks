package com.example.apisaturation.service.dbpool;

import com.example.apisaturation.dto.TransferRequest;
import com.example.apisaturation.dto.TransferResult;
import com.example.apisaturation.repository.AccountRepository;
import com.example.apisaturation.service.SimulatedExternalIo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/** INTENTIONAL VIOLATION: a checked-out connection spans an unrelated provider wait. */
@Service
public class ConnectionPoolViolationService {
    private final AccountRepository accounts;
    private final TransferPersistenceService persistence;
    private final SimulatedExternalIo externalIo;

    public ConnectionPoolViolationService(AccountRepository accounts,
            TransferPersistenceService persistence, SimulatedExternalIo externalIo) {
        this.accounts = accounts;
        this.persistence = persistence;
        this.externalIo = externalIo;
    }

    @Transactional(rollbackFor = InterruptedException.class)
    public TransferResult process(TransferRequest request) throws InterruptedException {
        // Force real connection acquisition before waiting; an annotation alone does not prove it.
        accounts.findById(request.sourceAccountId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demo source account not found"));
        int score = externalIo.lookupRisk();
        return persistence.record(request, score);
    }
}
