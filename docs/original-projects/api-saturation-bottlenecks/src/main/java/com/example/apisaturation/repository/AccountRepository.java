package com.example.apisaturation.repository;

import com.example.apisaturation.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> { }
