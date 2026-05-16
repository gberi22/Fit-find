package com.fitfind.fitfind.registration.service;

import com.fitfind.fitfind.client.model.AuthorityStatus;
import com.fitfind.fitfind.client.model.Client;
import com.fitfind.fitfind.client.repository.ClientRepository;
import com.fitfind.fitfind.registration.exception.EmailAlreadyExistsException;
import com.fitfind.fitfind.registration.model.RegisterRequest;
import com.fitfind.fitfind.security.auth.service.JwtService;
import com.fitfind.fitfind.security.ratelimit.model.RateLimitType;
import com.fitfind.fitfind.security.ratelimit.service.RateLimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final ClientRepository clientRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RateLimitService rateLimitService;

    public String register(RegisterRequest request) {
        String email = request.email().toLowerCase();
        rateLimitService.enforceRateLimit(email, RateLimitType.CLIENT_LOGIN);

        if (clientRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("An account with this email already exists.");
        }

        Client client = Client.builder()
                .withEmail(email)
                .withPassword(passwordEncoder.encode(request.password()))
                .withFirstName(request.firstName())
                .withLastName(request.lastName())
                .withStatus(AuthorityStatus.USER)
                .build();

        clientRepository.save(client);

        return jwtService.generateToken(client.getEmail(), List.of(client.getStatus().name()));
    }
}
