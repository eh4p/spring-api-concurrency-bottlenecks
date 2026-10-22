package com.example.concurrencybottlenecks.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
public class Transfer {
    @Id
    private Long id;
    private String reference;
    private Long accountId;
    private BigDecimal amount;
    private Instant createdAt;

    protected Transfer() {
    }

    public Transfer(long id, String reference, long accountId, BigDecimal amount, Instant createdAt) {
        this.id = id;
        this.reference = reference;
        this.accountId = accountId;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public String getReference() {
        return reference;
    }
}
