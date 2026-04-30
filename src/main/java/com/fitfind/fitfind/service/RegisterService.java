package com.fitfind.fitfind.service;

import com.fitfind.fitfind.model.AuthorityStatus;
import com.fitfind.fitfind.model.Client;
import com.fitfind.fitfind.model.requests.RegisterRequest;
import com.fitfind.fitfind.repository.ClientRepository;
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
