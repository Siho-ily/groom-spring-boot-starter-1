package com.study.profile_stack_api.domain.auth.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {
    private final Long memberId;
    private final String username;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Long memberId, String username, Collection<? extends GrantedAuthority> authorities) {
        this.memberId = memberId;
        this.username = username;
        this.authorities = authorities;
    }

    public Long getMemberId() { return memberId; }

    // UserDetails 구현 메서드들...
    @Override public String getUsername() { return username; }
    @Override public String getPassword() { return ""; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}