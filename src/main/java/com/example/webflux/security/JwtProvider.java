package com.example.webflux.security;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.Getter;
import reactor.core.publisher.Mono;

@Component
public class JwtProvider implements AuthenticationProvider {
    final JwtUtil jwtUtil;
    final CustomUserDetailsService customUserDetailsService;
    
    public JwtProvider(
        @Autowired JwtUtil jwtUtil,
        @Autowired CustomUserDetailsService customUserDetailsService
    ) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        CustomUserDetails customUserDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(principal.getUsername());
        return new UsernamePasswordAuthenticationToken(customUserDetails, "password", customUserDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtProvider.class.isAssignableFrom(authentication);
    }
    
    public static enum TokenHeaderType {
        BearerToken(HttpHeaders.AUTHORIZATION),
        JpaHeader("X-J-Auth"),
        ReactiveHeader("X-R-Auth"),
        ;
        
        @Getter
        String name;

        TokenHeaderType(String name) {
            this.name = name;
        }
    }
    
    private String getBearerToken(ServerHttpRequest request){
        // return request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String bearerToken = request.getHeaders().getFirst(TokenHeaderType.BearerToken.getName());
        if(Objects.nonNull(bearerToken) && bearerToken.startsWith("Bearer")){
            return bearerToken.substring(7);
        } else {
            return null;
        }
    }

    private String getCustomToken(ServerHttpRequest request, TokenHeaderType customHeader) {
        return request.getHeaders().getFirst(customHeader.getName());
    }

    public String resolveToken(ServerHttpRequest request){
        String bearerToken = getBearerToken(request);
        String jpaToken = getCustomToken(request, TokenHeaderType.JpaHeader);
        String reactiveToken = getCustomToken(request, TokenHeaderType.ReactiveHeader);

        if(StringUtils.hasText(bearerToken)){
            return bearerToken;
        } else if(StringUtils.hasText(jpaToken)) {
            return jpaToken;
        } else if(StringUtils.hasText(reactiveToken)) {
            return reactiveToken;
        }

        return null;
    }

    public Map.Entry<TokenHeaderType, String> resolveTokenAndTokenType(ServerHttpRequest request){
        String bearerToken = getBearerToken(request);
        String jpaToken = getCustomToken(request, TokenHeaderType.JpaHeader);
        String reactiveToken = getCustomToken(request, TokenHeaderType.ReactiveHeader);

        if(StringUtils.hasText(bearerToken)){
            return new AbstractMap.SimpleEntry<TokenHeaderType, String>(TokenHeaderType.BearerToken, bearerToken);
        } else if(StringUtils.hasText(jpaToken)) {
            return new AbstractMap.SimpleEntry<TokenHeaderType, String>(TokenHeaderType.JpaHeader, jpaToken);
        } else if(StringUtils.hasText(reactiveToken)) {
            return new AbstractMap.SimpleEntry<TokenHeaderType, String>(TokenHeaderType.ReactiveHeader, reactiveToken);
        }

        return null;
    }

    public Mono<Authentication> getAuthenticationForJwt(ServerHttpRequest serverHttpRequest) {
        Map.Entry<TokenHeaderType, String> tokenInfo = resolveTokenAndTokenType(serverHttpRequest);
        if(Objects.isNull(tokenInfo)) {
            return Mono.empty();
        }
        String token = tokenInfo.getValue();
        TokenHeaderType tokenHeaderType = tokenInfo.getKey();

        if(jwtUtil.validateJwt(token)) {
            String userName = jwtUtil.getAudience(token);

            Mono<Authentication> returnVal;
            if(TokenHeaderType.ReactiveHeader.equals(tokenHeaderType)) {
                returnVal = customUserDetailsService.findByUsername(userName).map(customUserDetails -> new UsernamePasswordAuthenticationToken(customUserDetails, "password", customUserDetails.getAuthorities()));
            } else {
                CustomUserDetails customUserDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(userName);
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(customUserDetails, "password", customUserDetails.getAuthorities());
                returnVal = Mono.just(usernamePasswordAuthenticationToken);
            }

            if(jwtUtil.isRefreshToken(token)) {
                if(serverHttpRequest.getPath().toString().contains("/auth/token/refresh")) {
                    return returnVal;
                } else {
                    throw new AccessDeniedException("can not use this token");
                }
            } else {
                return returnVal;
            }
        } else {
            throw new InvalidBearerTokenException("invalid token");
        }
    }
}
