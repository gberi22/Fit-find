package com.fitfind.fitfind.look.feed.service;

import com.fitfind.fitfind.client.model.Client;
import com.fitfind.fitfind.client.repository.ClientRepository;
import com.fitfind.fitfind.client.service.ClientService;
import com.fitfind.fitfind.look.common.model.Look;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SavedLookService {
    private final ClientRepository clientRepository;
    private final ClientService clientService;
    private final FeedLookService feedLookService;

    @Transactional
    public void save(String email, Long lookId) {
        Client client = clientService.findClientByEmail(email);
        Look look = feedLookService.lookById(lookId);
        if (look.getClient().getId().equals(client.getId())) {
            throw new IllegalArgumentException("You cannot save your own look.");
        }

        if (!client.getSavedLooks().contains(look)) {
            client.getSavedLooks().add(look);
            clientRepository.save(client);
        }
    }

    @Transactional
    public void unsave(String email, Long lookId) {
        Client client = clientService.findClientByEmail(email);
        Look look = feedLookService.lookById(lookId);
        if (client.getSavedLooks().remove(look)) {
            clientRepository.save(client);
        }
    }
}
