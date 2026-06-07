package com.fitfind.fitfind.wardrobe.service;

import com.fitfind.fitfind.client.exception.ClientNotFoundException;
import com.fitfind.fitfind.client.model.Client;
import com.fitfind.fitfind.client.repository.ClientRepository;
import com.fitfind.fitfind.wardrobe.model.Look;
import com.fitfind.fitfind.wardrobe.repository.LookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class SavedLookService {
    private final ClientRepository clientRepository;
    private final LookRepository lookRepository;

    @Transactional
    public void save(String email, Long lookId) {
        Client client = client(email);
        Look look = publishedLook(lookId);
        if (look.getClient().getId().equals(client.getId())) {
            throw new ResponseStatusException(CONFLICT, "You cannot save your own look");
        }
        if (!client.getSavedLooks().contains(look)) {
            client.getSavedLooks().add(look);
            clientRepository.save(client);
        }
    }

    @Transactional
    public void unsave(String email, Long lookId) {
        Client client = client(email);
        Look look = publishedLook(lookId);
        if (client.getSavedLooks().remove(look)) {
            clientRepository.save(client);
        }
    }

    private Client client(String email) {
        return clientRepository.findClientByEmail(email)
                .orElseThrow(() -> new ClientNotFoundException("Client not found: " + email));
    }

    private Look publishedLook(Long lookId) {
        return lookRepository.findByIdAndIsPublishedTrue(lookId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }
}
