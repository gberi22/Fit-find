package com.fitfind.fitfind.client.model;

import org.springframework.security.core.GrantedAuthority;

public enum AuthorityStatus implements GrantedAuthority {
    USER,
    ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}
