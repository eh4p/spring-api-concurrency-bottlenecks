package com.example.concurrencybottlenecks.service.solution;

import com.example.concurrencybottlenecks.dto.AuthorizationResult;
import com.example.concurrencybottlenecks.service.AccountAuthorizationService;
import com.example.concurrencybottlenecks.service.MockFraudProvider;
import org.springframework.stereotype.Service;

@Service
public class ShortTransactionSolutionService {
    private final MockFraudProvider fraudProvider;
    private final AccountAuthorizationService authorization;

    public ShortTransactionSolutionService(MockFraudProvider fraudProvider, AccountAuthorizationService authorization) {
        this.fraudProvider = fraudProvider;
        this.authorization = authorization;
    }

    public AuthorizationResult authorize(long accountId) throws InterruptedException {
        int score = fraudProvider.check();
        return authorization.authorize(accountId, score);
    }
}
