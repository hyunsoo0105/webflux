package com.example.webflux.security.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.webflux.security.entity.AccountEntity;

@Repository
public interface AccountJpaRepository extends JpaRepository<AccountEntity, String> {
    AccountEntity findByEmail(String email);
}