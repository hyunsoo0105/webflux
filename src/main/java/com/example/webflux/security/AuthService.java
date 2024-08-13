package com.example.webflux.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.webflux.security.entity.AccountEntity;
import com.example.webflux.security.jpa.AccountJpaRepository;
import com.example.webflux.security.reactive.AccountReactiveRepository;

import reactor.core.publisher.Mono;

@Service
public class AuthService {
    final JwtUtil jwtUtil;
    final PasswordEncoder passwordEncoder;
    final AccountJpaRepository accountJpaRepository;
    final AccountReactiveRepository accountReactiveRepository;

    public AuthService(
        JwtUtil jwtUtil,
        PasswordEncoder passwordEncoder,
        AccountJpaRepository accountJpaRepository,
        AccountReactiveRepository accountReactiveRepository
    ) {
        this.jwtUtil =jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.accountJpaRepository = accountJpaRepository;
        this.accountReactiveRepository = accountReactiveRepository;
    }

    @Transactional(transactionManager = "jpaTransactionManger")
    Mono<AccountEntity> saveAccountJpa(AccountEntity accountEntity) {
        return Mono.just(accountJpaRepository.save(accountEntity));
    }

    @Transactional(transactionManager = "r2dbcTransactionManager")
    Mono<AccountEntity> saveAccountReactive(AccountEntity accountEntity) {
        return accountReactiveRepository.save(accountEntity);
    }

    Mono<AccountEntity> selectAccountJpa(AccountEntity accountEntity) {
        return Mono.just(accountJpaRepository.findById(accountEntity.getEmail()).get());
    }

    Mono<AccountEntity> selectAccountReactive(AccountEntity accountEntity) {
        return accountReactiveRepository.findById(accountEntity.getEmail());
    }

    AccountEntity encodePassword(AccountEntity accountEntity) {
        String encodedPassword = passwordEncoder.encode(accountEntity.getPassword());
        accountEntity.setPassword(encodedPassword);
        return accountEntity;
    }

    boolean matchPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
