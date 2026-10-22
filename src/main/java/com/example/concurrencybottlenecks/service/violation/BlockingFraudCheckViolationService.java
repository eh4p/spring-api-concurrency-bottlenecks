package com.example.concurrencybottlenecks.service.violation;

import com.example.concurrencybottlenecks.service.MockFraudProvider;
import org.springframework.stereotype.Service;

/** Intentionally occupies a scarce platform servlet worker while waiting for external I/O. */
@Service
public class BlockingFraudCheckViolationService {
    private final MockFraudProvider fraudProvider;

    public BlockingFraudCheckViolationService(MockFraudProvider fraudProvider) {
        this.fraudProvider = fraudProvider;
    }

    public int check() throws InterruptedException {
        return fraudProvider.check();
    }
}
