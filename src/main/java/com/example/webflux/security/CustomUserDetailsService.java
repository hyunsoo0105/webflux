package com.example.webflux.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.webflux.security.entity.AccountEntity;
import com.example.webflux.security.jpa.AccountJpaRepository;
import com.example.webflux.security.reactive.AccountReactiveRepository;

import reactor.core.publisher.Mono;

@Service
public class CustomUserDetailsService implements UserDetailsService, ReactiveUserDetailsService{
    // 필요한 UserDetailService 상속
    AccountJpaRepository accountJpaRepository;
    AccountReactiveRepository accountReactiveRepository;

    public CustomUserDetailsService(
        @Autowired AccountJpaRepository accountJpaRepository,
        @Autowired AccountReactiveRepository accountReactiveRepository
    ) {
        this.accountJpaRepository=accountJpaRepository;
        this.accountReactiveRepository=accountReactiveRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AccountEntity accountEntity = accountJpaRepository.findByEmail(username);
        return CustomUserDetails.builder().accountEntity(accountEntity).build();
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return accountReactiveRepository.findByEmail(username).map(accountEntity -> CustomUserDetails.builder().accountEntity(accountEntity).build());
    }
}
