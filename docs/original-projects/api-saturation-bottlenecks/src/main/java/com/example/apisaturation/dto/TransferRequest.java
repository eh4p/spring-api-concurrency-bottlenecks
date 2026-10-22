package com.example.apisaturation.dto;

import java.math.BigDecimal;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public record TransferRequest(String sourceAccountId, String destinationAccountId,
                              BigDecimal amount, String currency) {
    public TransferRequest {
        if (sourceAccountId == null || sourceAccountId.isBlank()
                || destinationAccountId == null || destinationAccountId.isBlank()
                || sourceAccountId.equals(destinationAccountId)
                || amount == null || amount.signum() <= 0 || amount.scale() > 2
                || amount.precision() - amount.scale() > 17
                || currency == null || !Set.of("USD", "EUR", "GBP").contains(currency)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Use distinct account IDs, a positive amount (at most 2 decimals / 17 integer digits), and USD, EUR or GBP");
        }
    }
}
