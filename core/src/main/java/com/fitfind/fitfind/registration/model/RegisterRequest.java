package com.fitfind.fitfind.registration.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
        @NotNull @NotBlank String email,
        @NotNull @NotBlank String password,
        @NotNull @NotBlank String firstName,
        @NotNull @NotBlank String lastName
) {}
