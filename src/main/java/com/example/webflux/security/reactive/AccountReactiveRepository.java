package com.example.webflux.security.reactive;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.example.webflux.security.entity.AccountEntity;

import reactor.core.publisher.Mono;


@Repository
public interface AccountReactiveRepository extends ReactiveCrudRepository<AccountEntity, String> {
    Mono<AccountEntity> findByEmail(String email);
}