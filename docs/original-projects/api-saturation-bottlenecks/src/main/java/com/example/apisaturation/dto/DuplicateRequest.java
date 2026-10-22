package com.example.apisaturation.dto;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public record DuplicateRequest(List<String> transactionReferences) {
    public DuplicateRequest {
        // Keep the intentionally quadratic endpoint's workload bounded for a local demo.
        if (transactionReferences == null || transactionReferences.size() > 30_000
                || transactionReferences.stream().anyMatch(reference -> reference == null
                    || reference.isBlank() || reference.length() > 64)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Supply at most 30000 nonblank transaction references of at most 64 characters");
        }
        transactionReferences = List.copyOf(transactionReferences);
    }
}
