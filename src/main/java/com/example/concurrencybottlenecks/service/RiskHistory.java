package com.example.concurrencybottlenecks.service;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RiskHistory {
    private volatile List<String> references = List.of();

    public void initialize(List<String> references) {
        this.references = List.copyOf(references);
    }

    public List<String> references() {
        return references;
    }
}
