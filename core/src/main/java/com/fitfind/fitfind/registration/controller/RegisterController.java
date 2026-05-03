package com.fitfind.fitfind.registration.controller;

import com.fitfind.fitfind.registration.model.RegisterRequest;
import com.fitfind.fitfind.registration.service.RegisterService;
import com.fitfind.fitfind.security.auth.model.AuthResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/public/register")
@RequiredArgsConstructor
public class RegisterController {
    private final RegisterService registerService;

    @PostMapping
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(new AuthResponse(registerService.register(request)));
    }
}
