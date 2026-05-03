package com.fitfind.fitfind.registration.controller;

import com.fitfind.fitfind.registration.model.RegisterRequest;
import com.fitfind.fitfind.registration.service.RegisterService;
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
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request){
        registerService.register(request);
        return ResponseEntity.ok().build();
    }
}
