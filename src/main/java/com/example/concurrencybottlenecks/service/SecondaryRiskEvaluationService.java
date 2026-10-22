package com.example.concurrencybottlenecks.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.springframework.stereotype.Service;

@Service
public class SecondaryRiskEvaluationService {
    private final RiskHistory history;

    public SecondaryRiskEvaluationService(RiskHistory history) {
        this.history = history;
    }

    public long evaluate() {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Java requires SHA-256 support", exception);
        }
        long fingerprint = 0;
        // Fixed local batch cost, identical in both queues; no sleeping or database access.
        for (String reference : history.references()) {
            byte[] value = reference.getBytes(StandardCharsets.UTF_8);
            for (int round = 0; round < 100; round++) {
                value = digest.digest(value);
            }
            fingerprint += Byte.toUnsignedInt(value[0]);
        }
        return fingerprint;
    }
}
