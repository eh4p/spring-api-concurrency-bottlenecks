package com.example.apisaturation.service.cpu;

import com.example.apisaturation.dto.DuplicateRequest;
import com.example.apisaturation.dto.DuplicateResult;
import java.util.HashSet;
import org.springframework.stereotype.Service;

@Service
public class CpuBottleneckSolutionService {
    public DuplicateResult detect(DuplicateRequest request) {
        var seen = new HashSet<String>();
        int repeated = 0;
        for (String reference : request.transactionReferences()) {
            if (!seen.add(reference)) {
                repeated++;
            }
        }
        return new DuplicateResult(request.transactionReferences().size(), repeated);
    }
}
