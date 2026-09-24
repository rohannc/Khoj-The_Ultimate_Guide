package com.rohan.Khoj.common;

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
