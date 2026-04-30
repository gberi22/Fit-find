package com.fitfind.fitfind.api.routes;

import com.fitfind.fitfind.model.requests.LoginRequest;
import com.fitfind.fitfind.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class LoginController {
    private final AuthService authService;

    @PostMapping(value = "/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest request){
        log.info("login start");
        authService.login(request);
        return ResponseEntity.ok().build();
    }
}
