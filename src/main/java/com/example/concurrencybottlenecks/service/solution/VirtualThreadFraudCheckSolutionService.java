package com.example.concurrencybottlenecks.service.solution;

import com.example.concurrencybottlenecks.service.MockFraudProvider;
import org.springframework.stereotype.Service;

@Service
public class VirtualThreadFraudCheckSolutionService {
    private final MockFraudProvider fraudProvider;

    public VirtualThreadFraudCheckSolutionService(MockFraudProvider fraudProvider) {
        this.fraudProvider = fraudProvider;
    }

    public int check() throws InterruptedException {
        return fraudProvider.check();
    }
}
