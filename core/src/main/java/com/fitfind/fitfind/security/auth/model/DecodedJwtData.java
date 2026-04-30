package com.fitfind.fitfind.security.auth.model;

import java.util.List;

public record DecodedJwtData (
    String username,
    List<String> authorities
) { }
