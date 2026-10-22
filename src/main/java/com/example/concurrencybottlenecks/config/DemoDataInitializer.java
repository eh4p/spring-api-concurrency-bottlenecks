package com.example.concurrencybottlenecks.config;

import com.example.concurrencybottlenecks.entity.Account;
import com.example.concurrencybottlenecks.entity.Transfer;
import com.example.concurrencybottlenecks.repository.TransferRepository;
import com.example.concurrencybottlenecks.service.RiskHistory;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DemoDataInitializer implements ApplicationRunner {
    private final EntityManager entityManager;
    private final TransferRepository transfers;
    private final RiskHistory history;

    public DemoDataInitializer(EntityManager entityManager, TransferRepository transfers, RiskHistory history) {
        this.entityManager = entityManager;
        this.transfers = transfers;
        this.history = history;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        for (int i = 1; i <= 100; i++) {
            entityManager.persist(new Account(i, "ACC-DEMO-%03d".formatted(i)));
        }
        Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");
        for (int i = 1; i <= 10_000; i++) {
            entityManager.persist(new Transfer(i, "TRANSFER-DEMO-%04d".formatted((i - 1) % 5000 + 1),
                    (i - 1) % 100 + 1, BigDecimal.valueOf(100 + i % 900), createdAt.plusSeconds(i)));
        }
        entityManager.flush();
        history.initialize(transfers.findAll().stream().map(Transfer::getReference).toList());
    }
}
