package com.example.webflux.security;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.example.webflux.security.model.JsonWebToken;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.security.WeakKeyException;
import jakarta.annotation.PostConstruct;
import lombok.Getter;

@Component
public class JwtUtil {
    private final String jwtSecretKey = "256_bits_up_length_set_your_Securect_key";
    private SecretKey key;
    
    @PostConstruct
    void setJwtUtil() throws WeakKeyException, UnsupportedEncodingException {
        this.key = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public static enum TokenType {
        Access("acc"),
        Refresh("ref")
        ;

        @Getter
        String type;

        TokenType(String type) {
            this.type = type;
        }
    }
    
    private String generateJwt(CustomUserDetails customUserDetails, LocalDateTime now, TokenType tokenType) {
        LocalDateTime expiredDateTime;
        if (tokenType.equals(TokenType.Access)) {
            expiredDateTime = now.plusDays(1);
        } else if (tokenType.equals(TokenType.Refresh)) {
            expiredDateTime = now.plusMonths(1);
        } else {
            throw new UnknownError();
        }

        return Jwts.builder()
            .header()
            .add("alg", key.getAlgorithm())
            .add("type","JWT")
            .and()
            .issuer("발생자")
            .subject("jwt 목적")
            .audience().add(customUserDetails.getUsername()).and()
            .id(customUserDetails.getUsername())
            .issuedAt(Date.from(now.toInstant(ZoneOffset.UTC)))
            .expiration(Date.from(expiredDateTime.toInstant(ZoneOffset.UTC)))
            .claim("type", tokenType.getType())
            .signWith(key)
            .b64Url(t->Base64.getUrlEncoder().wrap(t))
            .compact()
        ;
    }

    public JsonWebToken getJwt(CustomUserDetails customUserDetails) {
        LocalDateTime now = LocalDateTime.now();

        return JsonWebToken.builder()
            .accessToken(generateJwt(customUserDetails, now, TokenType.Access))
            .refreshToken(generateJwt(customUserDetails, now, TokenType.Refresh))
            .build()
        ;
    }

    public boolean validateJwt(String token) {
        boolean returnVal = false;
        try {
            Jwts.parser().verifyWith(key).b64Url(t->Base64.getUrlDecoder().wrap(t)).build().parseSignedClaims(token);
            returnVal = true;
        } catch (ExpiredJwtException e) {
        } catch (MalformedJwtException e) {
        } catch (SignatureException e) {
        } catch (SecurityException e) {
        } catch (IllegalArgumentException e) {
        }
        return returnVal;
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parser().verifyWith(key).b64Url(t->Base64.getUrlDecoder().wrap(t)).build().parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public boolean isAccessToken(String token) {
        return parseClaims(token).get("type").equals(TokenType.Access.getType());
    }
    public boolean isRefreshToken(String token) {
        return parseClaims(token).get("type").equals(TokenType.Refresh.getType());
    }

    public String getAudience(String token) {
        return parseClaims(token).getAudience().stream().filter(StringUtils::hasText).findFirst().orElse(null);
    }
}