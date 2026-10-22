package com.example.concurrencybottlenecks.service;

import java.time.Duration;
import org.springframework.stereotype.Service;

@Service
public class MockFraudProvider {
    public int check() throws InterruptedException {
        // This mock represents external network I/O, never CPU work.
        Thread.sleep(Duration.ofMillis(250));
        return 12;
    }
}
