package com.fitfind.fitfind.service;

import com.fitfind.fitfind.model.Client;
import com.fitfind.fitfind.model.RegistrationStatus;
import com.fitfind.fitfind.model.requests.LoginRequest;
import com.fitfind.fitfind.model.requests.RegisterRequest;
import com.fitfind.fitfind.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final ClientRepository clientRep;
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterRequest req) {
        if (clientRep.findClientByEmail(req.email()).isPresent()) {
            throw new RuntimeException("Email taken: " + req.email());
        }

        Client client = Client.builder()
                .withEmail(req.email())
                .withPassword(passwordEncoder.encode(req.password()))
                .withFirstName(req.firstName())
                .withLastName(req.lastName())
                .withStatus(RegistrationStatus.FULL_ACCOUNT)
                .build();

        clientRep.save(client);
    }

    public void login(LoginRequest req) {
        Client client = clientRep.findClientByEmail(req.email())
                .orElseThrow(() -> new RuntimeException("No account found for: " + req.email()));

        if (!passwordEncoder.matches(req.password(), client.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
    }

}
