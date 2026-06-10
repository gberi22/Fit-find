package com.fitfind.fitfind.client.controller;

import com.fitfind.fitfind.client.model.response.ClientNameResponse;
import com.fitfind.fitfind.client.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/full-name")
    public ResponseEntity<ClientNameResponse> userFullName(Authentication authentication) {
        return ResponseEntity.ok().body(clientService.getFullName(authentication.getName()));
    }
}
