package com.fitfind.fitfind.api.routes;

import com.fitfind.fitfind.model.requests.LoginRequest;
import com.fitfind.fitfind.model.requests.RegisterRequest;
import com.fitfind.fitfind.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping(value = "/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request){
        try {
            logger.info("register start");
            authService.register(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error registering", e);
            throw e;
        }
    }

    @PostMapping(value = "/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest request){
        try {
            logger.info("login start");
            authService.login(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error logging in", e);
            throw e;
        }
    }
}
