package com.example.webflux.security;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

public class JwtWebFilter implements WebFilter {
    final JwtProvider jwtProvider;

    public JwtWebFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();

        Mono<Authentication> authentication = jwtProvider.getAuthenticationForJwt(serverHttpRequest);
        return chain.filter(exchange).contextWrite(
            ReactiveSecurityContextHolder.withSecurityContext(authentication.map(SecurityContextImpl::new)));
    }
}
