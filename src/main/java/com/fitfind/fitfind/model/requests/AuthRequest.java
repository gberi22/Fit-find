package com.fitfind.fitfind.model.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record AuthRequest (
    @NotNull @NotBlank(message = "Required") @Email
    String email,

    @NotNull @NotBlank(message = "Required")
    String password
) {}
