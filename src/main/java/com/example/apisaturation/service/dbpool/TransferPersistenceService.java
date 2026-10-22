package com.example.apisaturation.service.dbpool;

import com.example.apisaturation.dto.TransferRequest;
import com.example.apisaturation.dto.TransferResult;
import com.example.apisaturation.entity.Transfer;
import com.example.apisaturation.repository.AccountRepository;
import com.example.apisaturation.repository.TransferRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TransferPersistenceService {
    private final AccountRepository accounts;
    private final TransferRepository transfers;

    public TransferPersistenceService(AccountRepository accounts, TransferRepository transfers) {
        this.accounts = accounts;
        this.transfers = transfers;
    }

    // Joins the violation's transaction; starts a short one for the solution.
    // Separate bean ensures Spring's transactional proxy is actually invoked.
    @Transactional
    public TransferResult record(TransferRequest request, int riskScore) {
        var source = accounts.findById(request.sourceAccountId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demo source account not found"));
        var destination = accounts.findById(request.destinationAccountId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demo destination account not found"));
        if (!source.getCurrency().equals(request.currency()) || !destination.getCurrency().equals(request.currency())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Demo accounts must use the requested currency");
        }
        // Append independent audit records; updating one shared balance would add row-lock contention.
        transfers.save(new Transfer(request, riskScore));
        return TransferResult.assessed(request, riskScore);
    }
}
