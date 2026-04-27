package com.fitfind.fitfind.api.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitfind.fitfind.model.requests.LoginRequest;
import com.fitfind.fitfind.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Validated
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;
    private final ObjectMapper objectMapper;

    @Autowired
    public AuthController(AuthService authService){
        this.authService = authService;
        this.objectMapper = new ObjectMapper();
    }

    @PostMapping(value = "/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest request){
        try {
            logger.info("login start");
            authService.login(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error logging in", e);
            authService.clearData();
            throw e;
        }
    }
}
