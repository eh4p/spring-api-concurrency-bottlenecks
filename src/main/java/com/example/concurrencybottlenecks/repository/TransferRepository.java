package com.example.concurrencybottlenecks.repository;

import com.example.concurrencybottlenecks.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
}
