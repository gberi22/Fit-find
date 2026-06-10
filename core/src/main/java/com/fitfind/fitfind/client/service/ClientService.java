package com.fitfind.fitfind.client.service;

import com.fitfind.fitfind.client.exception.ClientNotFoundException;
import com.fitfind.fitfind.client.model.Client;
import com.fitfind.fitfind.client.model.response.ClientNameResponse;
import com.fitfind.fitfind.client.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientNameResponse getFullName(String email) {
        Client client = findClientByEmail(email);
        return new ClientNameResponse(client.getFirstName() + ' ' + client.getLastName());
    }

    public Client findClientByEmail(String email) {
        return clientRepository.findClientByEmail(email).
                orElseThrow(() -> new ClientNotFoundException("No account found for: " + email));
    }
}
