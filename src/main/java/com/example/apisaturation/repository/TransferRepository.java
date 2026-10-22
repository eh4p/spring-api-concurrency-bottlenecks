package com.example.apisaturation.repository;

import com.example.apisaturation.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("saturationTransferRepository")
public interface TransferRepository extends JpaRepository<Transfer, Long> { }
