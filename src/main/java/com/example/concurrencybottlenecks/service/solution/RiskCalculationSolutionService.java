package com.example.concurrencybottlenecks.service.solution;

import com.example.concurrencybottlenecks.dto.RiskResult;
import com.example.concurrencybottlenecks.service.RiskHistory;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class RiskCalculationSolutionService {
    private final RiskHistory history;

    public RiskCalculationSolutionService(RiskHistory history) {
        this.history = history;
    }

    public RiskResult calculate() {
        Map<String, Integer> counts = new HashMap<>();
        long pairs = 0;
        for (String reference : history.references()) {
            int previous = counts.getOrDefault(reference, 0);
            pairs += previous;
            counts.put(reference, previous + 1);
        }
        return new RiskResult(history.references().size(), pairs);
    }
}
