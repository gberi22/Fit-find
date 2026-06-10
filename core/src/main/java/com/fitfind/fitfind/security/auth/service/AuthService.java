package com.fitfind.fitfind.security.auth.service;

import com.fitfind.fitfind.client.model.Client;
import com.fitfind.fitfind.client.service.ClientService;
import com.fitfind.fitfind.security.auth.model.AuthRequest;
import com.fitfind.fitfind.security.auth.model.AuthResponse;
import com.fitfind.fitfind.security.ratelimit.model.RateLimitType;
import com.fitfind.fitfind.security.ratelimit.service.RateLimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ClientService clientService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RateLimitService rateLimitService;

    public AuthResponse login(AuthRequest request) {
        String email = request.email().toLowerCase();
        rateLimitService.enforceRateLimit(email, RateLimitType.CLIENT_LOGIN);
        Client client = clientService.findClientByEmail(email);

        if (!passwordEncoder.matches(request.password(), client.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        String token = jwtService.generateToken(client.getEmail(), List.of(client.getStatus().name()));
        return new AuthResponse(token);
    }
}
