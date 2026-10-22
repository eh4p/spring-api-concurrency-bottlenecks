package com.example.apisaturation.service.cpu;

import com.example.apisaturation.dto.DuplicateRequest;
import com.example.apisaturation.dto.DuplicateResult;
import org.springframework.stereotype.Service;

/** INTENTIONAL VIOLATION: scanning previous references for each entry performs O(n²) work. */
@Service
public class CpuBottleneckViolationService {
    public DuplicateResult detect(DuplicateRequest request) {
        var references = request.transactionReferences();
        int repeated = 0;
        for (int current = 0; current < references.size(); current++) {
            for (int previous = 0; previous < current; previous++) {
                if (references.get(current).equals(references.get(previous))) {
                    repeated++;
                    break;
                }
            }
        }
        return new DuplicateResult(references.size(), repeated);
    }
}
