package com.fitfind.fitfind.security.auth.controller;

import com.fitfind.fitfind.security.auth.model.AuthRequest;
import com.fitfind.fitfind.security.auth.model.AuthResponse;
import com.fitfind.fitfind.security.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/public/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
