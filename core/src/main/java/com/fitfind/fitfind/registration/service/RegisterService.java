package com.fitfind.fitfind.registration.service;

import com.fitfind.fitfind.client.model.AuthorityStatus;
import com.fitfind.fitfind.client.model.Client;
import com.fitfind.fitfind.registration.model.RegisterRequest;
import com.fitfind.fitfind.client.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class RegisterService {
    private final ClientRepository clientRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public void register(RegisterRequest request) {
        Client client = Client.builder()
                .withEmail(request.email().toLowerCase())
                .withPassword(passwordEncoder.encode(request.password()))
                .withFirstName(request.firstName())
                .withLastName(request.lastName())
                .withStatus(AuthorityStatus.USER)
                .build();

        clientRepository.save(client);
    }
}
