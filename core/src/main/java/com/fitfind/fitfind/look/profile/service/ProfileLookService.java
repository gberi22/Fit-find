package com.fitfind.fitfind.look.profile.service;

import com.fitfind.fitfind.ai.common.model.Suggestion;
import com.fitfind.fitfind.client.model.Client;
import com.fitfind.fitfind.client.service.ClientService;
import com.fitfind.fitfind.look.common.exception.LookNotFoundException;
import com.fitfind.fitfind.look.common.model.Look;
import com.fitfind.fitfind.look.common.model.LookImage;
import com.fitfind.fitfind.look.common.model.Product;
import com.fitfind.fitfind.look.common.model.response.LookDetailResponse;
import com.fitfind.fitfind.look.common.model.response.LookSummaryResponse;
import com.fitfind.fitfind.look.common.model.response.LooksResponse;
import com.fitfind.fitfind.look.common.repository.LookImageRepository;
import com.fitfind.fitfind.look.common.repository.LookRepository;
import com.fitfind.fitfind.look.common.repository.ProductRepository;
import com.fitfind.fitfind.look.common.service.LookResponseMapper;
import com.fitfind.fitfind.look.profile.model.request.SaveLookRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileLookService {

    private final LookRepository lookRepository;
    private final LookImageRepository lookImageRepository;
    private final ProductRepository productRepository;
    private final ClientService clientService;
    private final LookResponseMapper lookResponseMapper;

    @Transactional
    public void create(String email, SaveLookRequest request) {
        Client client = clientService.findClientByEmail(email);

        List<Product> products = request.suggestions().stream()
            .filter(suggestion -> StringUtils.hasText(suggestion.link()))
            .map(this::findOrCreateProduct)
            .toList();

        byte[] image = Base64.getDecoder().decode(request.imageBase64());

        Look look = lookRepository.save(Look.builder()
            .withClient(client)
            .withGender(request.gender())
            .withSize(request.size())
            .withStyles(request.styles())
            .withBudgetMin(request.budgetMin())
            .withBudgetMax(request.budgetMax())
            .withImageMimeType(request.imageMimeType())
            .withImageKey(UUID.randomUUID())
            .withProducts(products)
            .withPublishedAt(request.published() ? LocalDateTime.now() : null)
            .build()
        );

        lookImageRepository.save(LookImage.builder()
            .withLookId(look.getId())
            .withImage(image)
            .build());
    }

    @Transactional(readOnly = true)
    public LooksResponse list(String email) {
        Client client = clientService.findClientByEmail(email);
        List<LookSummaryResponse> looks = lookRepository.findByClientOrderByCreatedAtDesc(client).stream()
            .map(lookResponseMapper::SummaryProjectionToSummaryResponse)
            .toList();
        return new LooksResponse(looks);
    }

    @Transactional(readOnly = true)
    public LooksResponse savedList(String email) {
        Client client = clientService.findClientByEmail(email);
        List<LookSummaryResponse> looks = client.getSavedLooks().stream()
            .map(lookResponseMapper::SummaryProjectionToSummaryResponse)
            .toList();
        return new LooksResponse(looks);
    }

    @Transactional(readOnly = true)
    public LookDetailResponse get(String email, Long lookId) {
        Look look = findLook(email, lookId);
        return lookResponseMapper.lookToDetailResponse(look);
    }

    @Transactional
    public void setPublished(String email, Long lookId, boolean published) {
        Look look = findLook(email, lookId);
        look.setPublishedAt(published ? LocalDateTime.now() : null);
    }

    @Transactional
    public void delete(String email, Long lookId) {
        Look look = findLook(email, lookId);
        lookRepository.delete(look);
    }

    private Product findOrCreateProduct(Suggestion suggestion) {
        return productRepository.findByUrl(suggestion.link())
            .orElseGet(() -> productRepository.save(Product.builder()
                .withName(suggestion.name())
                .withPrice(suggestion.price())
                .withUrl(suggestion.link())
                .withCategory(suggestion.category())
                .withImageUrl(suggestion.picture())
                .build()));
    }

    private Look findLook(String email, Long lookId) {
        Client client = clientService.findClientByEmail(email);
        return lookRepository.findByIdAndClient(lookId, client)
                .orElseThrow(() -> new LookNotFoundException("Look not found: " + lookId));
    }
}
