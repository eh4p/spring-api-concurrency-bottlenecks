package com.example.concurrencybottlenecks.dto;

public record AuthorizationResult(String accountReference, int riskScore, String status) {
}
