package com.example.concurrencybottlenecks.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Account {
    @Id
    private Long id;
    private String reference;
    private int lastRiskScore;

    protected Account() {
    }

    public Account(long id, String reference) {
        this.id = id;
        this.reference = reference;
    }

    public String getReference() {
        return reference;
    }

    public void authorize(int riskScore) {
        this.lastRiskScore = riskScore;
    }
}
