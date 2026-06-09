package com.fitfind.fitfind.look.profile.service;

import com.fitfind.fitfind.client.exception.ClientNotFoundException;
import com.fitfind.fitfind.client.model.Client;
import com.fitfind.fitfind.client.repository.ClientRepository;
import com.fitfind.fitfind.look.common.exception.LookNotFoundException;
import com.fitfind.fitfind.look.common.model.Look;
import com.fitfind.fitfind.look.common.model.Product;
import com.fitfind.fitfind.look.common.model.response.LookSummaryResponse;
import com.fitfind.fitfind.look.common.repository.LookRepository;
import com.fitfind.fitfind.look.common.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
//TODO: all of that misses image handling logic
public class ProfileLookService {

    private final LookRepository lookRepository;
    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;

    @Transactional
    public Long create(String email) {
        findClient(email);
        throw new UnsupportedOperationException("createLook payload not yet wired");
    }

    @Transactional(readOnly = true)
    public LookSummaryResponse list(String email) {
        Client client = findClient(email);
//        Look result = lookRepository.findByClient(client);
//        List<LookSummaryResponse> looks = result.getContent().stream()
//            .map(this::convertToSummaryResponse)
//            .toList();
        return null;
    }

    @Transactional(readOnly = true)
    public LookDetails get(String email, Long lookId) {
        Look look = findLook(email, lookId);
        return convertToLookResponse(look);
    }

    @Transactional
    public void setPublished(String email, Long lookId, boolean published) {
        Look look = findLook(email, lookId);
        look.setPublished(published);
    }

    @Transactional
    public void delete(String email, Long lookId) {
        Look look = findLook(email, lookId);
        look.setDeletedAt(LocalDateTime.now());
    }

    private Client findClient(String email) {
        return clientRepository.findClientByEmail(email)
            .orElseThrow(() -> new ClientNotFoundException("Client not found: " + email));
    }

    private Look findLook(String email, Long lookId) {
        Client client = findClient(email);
        return lookRepository.findByIdAndClient(lookId, client)
            .orElseThrow(() -> new LookNotFoundException("Look not found: " + lookId));
    }

    private LookSummaryResponse convertToSummaryResponse(Look look) {
        return new LookSummaryResponse(
            look.getId(),
            look.getImageMimeType()
        );
    }
}
