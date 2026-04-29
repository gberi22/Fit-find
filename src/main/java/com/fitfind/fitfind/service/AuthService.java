package com.fitfind.fitfind.service;

import com.fitfind.fitfind.model.Client;
import com.fitfind.fitfind.model.RegistrationStatus;
import com.fitfind.fitfind.model.requests.LoginRequest;
import com.fitfind.fitfind.model.requests.RegisterRequest;
import com.fitfind.fitfind.repository.ClientRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final ClientRepository clientRep;
    private final PasswordEncoder passwordEncoder;

    public AuthService(ClientRepository clientRep, PasswordEncoder encoder){
        this.clientRep = clientRep;
        this.passwordEncoder = encoder;
    }

    public void register(RegisterRequest req) {
        if (clientRep.findClientByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("Email taken: " + req.getEmail());
        }

        Client client = Client.builder()
                .withEmail(req.getEmail())
                .withPassword(passwordEncoder.encode(req.getPassword()))
                .withFirstName(req.getFirstName())
                .withLastName(req.getLastName())
                .withStatus(RegistrationStatus.FULL_ACCOUNT)
                .build();

        clientRep.save(client);
    }

    public void login(LoginRequest req) {
        Client client = clientRep.findClientByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("No account found for: " + req.getEmail()));

        if (!passwordEncoder.matches(req.getPassword(), client.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
    }

}
