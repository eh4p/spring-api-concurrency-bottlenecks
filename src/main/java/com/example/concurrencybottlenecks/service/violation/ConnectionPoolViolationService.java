package com.example.concurrencybottlenecks.service.violation;

import com.example.concurrencybottlenecks.dto.AuthorizationResult;
import com.example.concurrencybottlenecks.repository.AccountRepository;
import com.example.concurrencybottlenecks.service.MockFraudProvider;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/** Intentionally holds a real Hikari connection across a non-database external wait. */
@Service
public class ConnectionPoolViolationService {
    private final AccountRepository accounts;
    private final MockFraudProvider fraudProvider;

    public ConnectionPoolViolationService(AccountRepository accounts, MockFraudProvider fraudProvider) {
        this.accounts = accounts;
        this.fraudProvider = fraudProvider;
    }

    @Transactional(rollbackFor = InterruptedException.class)
    public AuthorizationResult authorize(long accountId) throws InterruptedException {
        var account = accounts.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demo account not found"));
        int score = fraudProvider.check();
        account.authorize(score);
        return new AuthorizationResult(account.getReference(), score, "AUTHORIZED");
    }
}
