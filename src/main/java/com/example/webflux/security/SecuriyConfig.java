package com.example.webflux.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecuriyConfig  {
    final JwtProvider jwtProvider;

    public SecuriyConfig(
        JwtProvider jwtProvider
    ) {
        this.jwtProvider = jwtProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        Map<String,PasswordEncoder> encoders = new HashMap<>();
        encoders.put("bcrypt", new BCryptPasswordEncoder());
        encoders.put("pbkdf2@SpringSecurity_v5_8", Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8());
        encoders.put("scrypt@SpringSecurity_v5_8", SCryptPasswordEncoder.defaultsForSpringSecurity_v5_8());
        encoders.put("argon2@SpringSecurity_v5_8", Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8());

        PasswordEncoder passwordEncoder = new DelegatingPasswordEncoder("bcrypt", encoders);
        return passwordEncoder;
    }

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity serverHttpSecurity) throws Exception {
        serverHttpSecurity
            .headers(t -> t.disable())
            .cors(t -> t.disable())
            .csrf(t-> t.disable())
            .authorizeExchange(exchange -> exchange
                .pathMatchers("/favicon.ico", "/resources/**").permitAll()
                .pathMatchers("/test","/test/**").permitAll()
                .pathMatchers(HttpMethod.GET, "/content", "/content/**").permitAll()
                .pathMatchers("/auth/join/**", "/auth/login/**").permitAll()
                .anyExchange().authenticated()
            )
            .addFilterAfter(new JwtWebFilter(jwtProvider), SecurityWebFiltersOrder.AUTHENTICATION)
        ;

        return serverHttpSecurity.build();
    }
}