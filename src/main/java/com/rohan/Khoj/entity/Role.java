package com.rohan.Khoj.entity;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_PATIENT,
    ROLE_DOCTOR,
    ROLE_CLINIC;

    @Override
    public String getAuthority() {
        return name();
    }
}
