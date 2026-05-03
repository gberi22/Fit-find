package com.fitfind.fitfind.security.auth.controller;

import com.fitfind.fitfind.security.auth.model.AuthRequest;
import com.fitfind.fitfind.security.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/public/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<Void> login(@Valid @RequestBody AuthRequest request){
        authService.login(request);
        return ResponseEntity.ok().build();
    }
}
