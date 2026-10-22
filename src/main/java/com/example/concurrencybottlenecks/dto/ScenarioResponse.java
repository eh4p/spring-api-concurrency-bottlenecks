package com.example.concurrencybottlenecks.dto;

public record ScenarioResponse<T>(String scenario, String implementation, double elapsedMs, T result) {
    public static <T> ScenarioResponse<T> measured(String scenario, String implementation,
                                                  long startedNanos, T result) {
        return new ScenarioResponse<>(scenario, implementation,
                (System.nanoTime() - startedNanos) / 1_000_000.0, result);
    }
}
