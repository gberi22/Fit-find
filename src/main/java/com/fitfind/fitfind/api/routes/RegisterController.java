package com.fitfind.fitfind.api.routes;

import com.fitfind.fitfind.model.requests.RegisterRequest;
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
public class RegisterController {
    private final AuthService authService;

    @PostMapping(value = "/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request){
        log.info("register start");
        authService.register(request);
        return ResponseEntity.ok().build();
    }
}
