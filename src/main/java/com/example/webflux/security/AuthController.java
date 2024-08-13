package com.example.webflux.security;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.webflux.security.entity.AccountEntity;

import reactor.core.publisher.Mono;

@RestController()
@RequestMapping("/auth")
public class AuthController {
    final AuthService authService;
    final JwtUtil jwtUtil;

    public AuthController(
        AuthService authService,
        JwtUtil jwtUtil
    ) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/join/jpa")
    public Mono<?> joinJpa(@RequestBody AccountEntity accountEntity) {
        return Mono.just(AccountEntity.builder().email(accountEntity.getEmail()).password(accountEntity.getPassword()).isNew(true).build()).map(entity -> authService.encodePassword(entity))
            .flatMap(entity -> authService.saveAccountJpa(entity))
            .map(acount -> CustomUserDetails.builder().accountEntity(acount).build())
            .map(customUserDetails -> jwtUtil.getJwt(customUserDetails))
        ;
    }
    
    @PostMapping("/join/r2dbc")
    public Mono<?> joinReactive(@RequestBody AccountEntity accountEntity) {
        return Mono.just(AccountEntity.builder().email(accountEntity.getEmail()).password(accountEntity.getPassword()).isNew(true).build()).map(entity -> authService.encodePassword(entity))
            .flatMap(entity -> authService.saveAccountReactive(entity))
            .map(acount -> CustomUserDetails.builder().accountEntity(acount).build())
            .map(customUserDetails -> jwtUtil.getJwt(customUserDetails))
        ;
    }

    @PostMapping("/login/jpa")
    public Mono<?> loginJpa(@RequestBody AccountEntity accountEntity) {
        return Mono.just(accountEntity)
            .map(entity -> AccountEntity.builder().email(entity.getEmail()).password(entity.getPassword()).build())
            .flatMap(entity -> authService.selectAccountJpa(entity))
            .filter(account -> authService.matchPassword(accountEntity.getPassword(), account.getPassword())).switchIfEmpty(Mono.error(new AccountNotFoundException()))
            .map(acount -> CustomUserDetails.builder().accountEntity(acount).build())
            .map(customUserDetails -> jwtUtil.getJwt(customUserDetails))
        ;
    }
    
    @PostMapping("/login/r2dbc")
    public Mono<?> loginReactive(@RequestBody AccountEntity accountEntity) {
        return Mono.just(accountEntity)
            .map(entity -> AccountEntity.builder().email(entity.getEmail()).password(entity.getPassword()).build())
            .flatMap(entity -> authService.selectAccountReactive(entity))
            .filter(account -> authService.matchPassword(accountEntity.getPassword(), account.getPassword())).switchIfEmpty(Mono.error(new AccountNotFoundException()))
            .map(acount -> CustomUserDetails.builder().accountEntity(acount).build())
            .map(customUserDetails -> jwtUtil.getJwt(customUserDetails))
        ;
    }
}
