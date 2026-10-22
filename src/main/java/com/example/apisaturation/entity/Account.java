package com.example.apisaturation.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity(name = "SaturationAccount")
@Table(name = "saturation_account")
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
