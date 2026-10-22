package com.example.apisaturation.entity;

import com.example.apisaturation.dto.TransferRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;

@Entity
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sourceAccountId;
    private String destinationAccountId;
    @Column(precision = 19, scale = 2)
    private BigDecimal amount;
    private String currency;
    private int riskScore;

    protected Transfer() {
    }

    public Transfer(TransferRequest request, int riskScore) {
        this.sourceAccountId = request.sourceAccountId();
        this.destinationAccountId = request.destinationAccountId();
        this.amount = request.amount();
        this.currency = request.currency();
        this.riskScore = riskScore;
    }
}
