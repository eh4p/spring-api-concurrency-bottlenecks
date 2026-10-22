package com.example.apisaturation.dto;

import java.math.BigDecimal;

public record TransferResult(String sourceAccountId, String destinationAccountId,
                             BigDecimal amount, String currency, int riskScore, String status) {
    public static TransferResult assessed(TransferRequest request, int riskScore) {
        return new TransferResult(request.sourceAccountId(), request.destinationAccountId(),
                request.amount(), request.currency(), riskScore, "DEMO_ASSESSED");
    }
}
