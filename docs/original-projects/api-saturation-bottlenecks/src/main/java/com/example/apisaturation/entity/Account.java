package com.example.apisaturation.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Account {
    @Id
    private String id;
    private String customerName;
    private String currency;

    protected Account() {
    }

    public String getCurrency() {
        return currency;
    }
}
