package com.fitfind.fitfind.look.profile.service;

import com.fitfind.fitfind.client.exception.ClientNotFoundException;
import com.fitfind.fitfind.client.model.Client;
import com.fitfind.fitfind.client.repository.ClientRepository;
import com.fitfind.fitfind.look.common.exception.LookNotFoundException;
import com.fitfind.fitfind.look.common.model.Look;
import com.fitfind.fitfind.look.common.model.Product;
import com.fitfind.fitfind.look.common.model.Store;
import com.fitfind.fitfind.look.common.model.response.LookResponse;
import com.fitfind.fitfind.look.common.model.response.LookSummaryResponse;
import com.fitfind.fitfind.look.common.model.response.LooksPageResponse;
import com.fitfind.fitfind.look.common.model.response.ProductResponse;
import com.fitfind.fitfind.look.profile.repository.LookRepository;
import com.fitfind.fitfind.look.profile.repository.ProductRepository;
import com.fitfind.fitfind.look.profile.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
//TODO: all of that misses image handling logic
public class LookService {

    private final LookRepository lookRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final ClientRepository clientRepository;

    @Transactional
    public Long create(String email) {
        findClient(email);
        throw new UnsupportedOperationException("createLook payload not yet wired");
    }

    @Transactional(readOnly = true)
    public LooksPageResponse list(String email, int page, int size) {
        Client client = findClient(email);
        Pageable pageable = PageRequest.of(page, size)
            .withSort(Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Look> result = lookRepository.findByClient(client, pageable);
        List<LookSummaryResponse> looks = result.getContent().stream()
            .map(this::convertToSummaryResponse)
            .toList();
        return new LooksPageResponse(looks, result.getTotalElements(), result.getTotalPages());
    }

    @Transactional(readOnly = true)
    public LookResponse get(String email, Long lookId) {
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
            look.getGender(),
            look.getSize(),
            look.getStyles(),
            look.getBudgetMin(),
            look.getBudgetMax(),
            look.isPublished(),
            look.getImageMimeType(),
            look.getCreatedAt()
        );
    }

    private LookResponse convertToLookResponse(Look look) {
        List<ProductResponse> products = look.getProducts() == null ? List.of()
            : look.getProducts().stream().map(this::convertToProductResponse).toList();
        return new LookResponse(
            look.getId(),
            look.getGender(),
            look.getSize(),
            look.getStyles(),
            look.getBudgetMin(),
            look.getBudgetMax(),
            look.isPublished(),
            look.getImageMimeType(),
            look.getCreatedAt(),
            products
        );
    }

    private ProductResponse convertToProductResponse(Product product) {
        Store store = product.getStore();
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getPrice(),
            product.getUrl(),
            product.getCategory(),
            store == null ? null : store.getName()
        );
    }
}
