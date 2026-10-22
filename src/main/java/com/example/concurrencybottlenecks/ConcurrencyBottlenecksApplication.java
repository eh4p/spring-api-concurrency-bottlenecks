package com.example.concurrencybottlenecks;

import com.example.apisaturation.ApiSaturationApplication;
import com.example.concurrencybottlenecks.entity.Account;
import com.example.concurrencybottlenecks.repository.AccountRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@Import(ApiSaturationApplication.class)
@EntityScan(basePackageClasses = {Account.class, com.example.apisaturation.entity.Account.class})
@EnableJpaRepositories(basePackageClasses = {AccountRepository.class,
        com.example.apisaturation.repository.AccountRepository.class})
public class ConcurrencyBottlenecksApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConcurrencyBottlenecksApplication.class, args);
    }
}
