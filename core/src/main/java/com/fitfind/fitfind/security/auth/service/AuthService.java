package com.fitfind.fitfind.security.auth.service;

import com.fitfind.fitfind.client.model.Client;
import com.fitfind.fitfind.security.auth.model.AuthRequest;
import com.fitfind.fitfind.client.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final ClientRepository clientRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public void login(AuthRequest request) {
        Client client = clientRepository.findClientByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("No account found for: " + request.email()));

        if (!passwordEncoder.matches(request.password(), client.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
    }
}
