package com.fitfind.fitfind.look.feed.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitfind.fitfind.ai.common.model.enums.Gender;
import com.fitfind.fitfind.ai.common.model.enums.Style;
import com.fitfind.fitfind.look.feed.model.FeedRequestFilters;
import com.fitfind.fitfind.look.feed.model.FeedResponse;
import com.fitfind.fitfind.look.common.model.LookCardProjection;
import com.fitfind.fitfind.look.common.model.LookCardResponse;
import com.fitfind.fitfind.look.feed.repository.FeedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeedService {
    private static final TypeReference<List<Style>> STYLE_LIST = new TypeReference<>() { };

    private final FeedRepository feedRepository;
    private final ObjectMapper objectMapper;

    public FeedResponse list(
            FeedRequestFilters requestFilters,
            int page,
            int size
    ) {
        Gender gender = requestFilters.gender();
        List<Style> styles = requestFilters.style();
        BigDecimal minBudget = requestFilters.minBudget();
        BigDecimal maxBudget = requestFilters.maxBudget();

        Pageable pageable = PageRequest.of(page, size);
        String genderParam = gender == null ? null : gender.name();
        String[] stylesParam = (styles == null || styles.isEmpty())
                ? null
                : styles.stream().map(Enum::name).toArray(String[]::new);

        Page<LookCardProjection> result =
                feedRepository.findPublishedFeed(genderParam, minBudget, maxBudget, stylesParam, pageable);

        List<LookCardResponse> looks = result.getContent().stream()
                .map(this::toCard)
                .toList();

        return new FeedResponse(looks, result.getTotalElements(), result.getTotalPages());
    }

    private LookCardResponse toCard(LookCardProjection projection) {
        String imageUrl = projection.getImageMimeType() == null
                ? null
                : "/api/public/looks/" + projection.getId() + "/image";

        return new LookCardResponse(
                projection.getId(),
                imageUrl,
                parseStyles(projection.getStyles()),
                Gender.valueOf(projection.getGender()),
                projection.getBudgetMin(),
                projection.getBudgetMax(),
                buildUsername(projection.getFirstName(), projection.getLastName()),
                0.0,
                0L,
                projection.getCreatedAt()
        );
    }

    private List<Style> parseStyles(String raw) {
        if (raw == null || raw.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(raw, STYLE_LIST);
        } catch (Exception e) {
            log.warn("Failed to parse styles json: {}", raw, e);
            return Collections.emptyList();
        }
    }

    private String buildUsername(String firstName, String lastName) {
        return ((firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName)).trim();
    }
}
