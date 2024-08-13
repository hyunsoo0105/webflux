package com.example.webflux.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.webflux.security.entity.AccountEntity;

import lombok.Builder;
import lombok.Getter;

@Builder
public class CustomUserDetails implements UserDetails {
    @Getter
    AccountEntity accountEntity;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");

        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList())
        ;
    }

    @Override
    public String getPassword() {
        return accountEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return accountEntity.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return !accountEntity.getIsDelete();

    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountEntity.getIsBan();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !accountEntity.getIsBan();
    }

    @Override
    public boolean isEnabled() {
        return (accountEntity.getIsBan() == false) && (accountEntity.getIsDelete() == false);
    }
}
