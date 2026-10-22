package com.example.concurrencybottlenecks.service.violation;

import com.example.concurrencybottlenecks.dto.RiskResult;
import com.example.concurrencybottlenecks.service.RiskHistory;
import org.springframework.stereotype.Service;

/** Intentionally scans every pair of historical references: quadratic CPU work. */
@Service
public class RiskCalculationViolationService {
    private final RiskHistory history;

    public RiskCalculationViolationService(RiskHistory history) {
        this.history = history;
    }

    public RiskResult calculate() {
        var references = history.references();
        long pairs = 0;
        for (int i = 0; i < references.size(); i++) {
            for (int j = i + 1; j < references.size(); j++) {
                if (references.get(i).equals(references.get(j))) {
                    pairs++;
                }
            }
        }
        return new RiskResult(references.size(), pairs);
    }
}
