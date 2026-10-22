package com.example.apisaturation.service;

import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SimulatedExternalIo {
    private static final Logger log = LoggerFactory.getLogger(SimulatedExternalIo.class);
    private static final Duration PROVIDER_DELAY = Duration.ofMillis(300);

    public boolean verifyCustomer() throws InterruptedException {
        waitForProvider("customer verification");
        return true;
    }

    public boolean verifyAccount() throws InterruptedException {
        waitForProvider("account verification");
        return true;
    }

    public int lookupRisk() throws InterruptedException {
        waitForProvider("risk lookup");
        return 12;
    }

    private void waitForProvider(String operation) throws InterruptedException {
        log.debug("Simulated {} on {}", operation, Thread.currentThread());
        // Deterministic stand-in for network/database waiting, never CPU work.
        // This is the only sleeping component in the application.
        Thread.sleep(PROVIDER_DELAY);
    }
}
