package com.fitfind.fitfind.ai.history.service;

import com.fitfind.fitfind.ai.history.model.AiHistory;
import com.fitfind.fitfind.ai.history.model.response.AiHistoryResponse;
import com.fitfind.fitfind.ai.history.repository.AiHistoryRepository;
import com.fitfind.fitfind.ai.model.reqeust.OutfitSuggestionRequest;
import com.fitfind.fitfind.ai.model.response.OutfitSuggestionResponse;
import com.fitfind.fitfind.client.model.Client;
import com.fitfind.fitfind.client.model.ClientNotFoundException;
import com.fitfind.fitfind.client.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiHistoryService {

    private final AiHistoryRepository aiHistoryRepository;
    private final ClientRepository clientRepository;

    public void record(String email, OutfitSuggestionRequest request, OutfitSuggestionResponse response) {
        Client client = clientRepository.findClientByEmail(email)
            .orElseThrow(() -> new ClientNotFoundException("Client not found: " + email));
        aiHistoryRepository.save(AiHistory.builder()
            .withClient(client)
            .withRequest(request)
            .withResponse(response)
            .build()
        );
    }

    public AiHistoryResponse list(String email, int page, int size) {
        Pageable pageable = PageRequest.of(page, size)
            .withSort(Sort.by(Sort.Direction.DESC, "createdAt"));
        Client client = clientRepository.findClientByEmail(email)
            .orElseThrow(() -> new ClientNotFoundException("Client not found: " + email));
        Page<AiHistory> result = aiHistoryRepository.findByClient(client, pageable);
        List<AiHistoryResponse.HistoryItem> items = result.getContent().stream()
            .map(history -> new AiHistoryResponse.HistoryItem(
                history.getId(),
                history.getRequest(),
                history.getResponse(),
                history.getCreatedAt()
            ))
            .toList();
        return new AiHistoryResponse(items, result.getTotalElements(), result.getTotalPages());
    }
}
