package com.example.concurrencybottlenecks.service;

import com.example.concurrencybottlenecks.dto.AuthorizationResult;
import com.example.concurrencybottlenecks.repository.AccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AccountAuthorizationService {
    private final AccountRepository accounts;

    public AccountAuthorizationService(AccountRepository accounts) {
        this.accounts = accounts;
    }

    @Transactional
    public AuthorizationResult authorize(long accountId, int riskScore) {
        var account = accounts.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demo account not found"));
        account.authorize(riskScore);
        return new AuthorizationResult(account.getReference(), riskScore, "AUTHORIZED");
    }
}
