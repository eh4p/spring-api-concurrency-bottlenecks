package com.example.concurrencybottlenecks.controller;

import java.util.concurrent.RejectedExecutionException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CapacityExceptionHandler {
    @ExceptionHandler(RejectedExecutionException.class)
    public ResponseEntity<CapacityError> capacityExceeded() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .header(HttpHeaders.RETRY_AFTER, "1")
                .body(new CapacityError("CAPACITY_EXCEEDED", "Risk evaluation capacity is temporarily exhausted."));
    }

    public record CapacityError(String code, String message) {
    }
}
