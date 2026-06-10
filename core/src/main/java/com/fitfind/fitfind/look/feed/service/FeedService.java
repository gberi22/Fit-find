package com.fitfind.fitfind.look.feed.service;

import com.fitfind.fitfind.ai.common.model.enums.Gender;
import com.fitfind.fitfind.ai.common.model.enums.Style;
import com.fitfind.fitfind.look.common.model.Look;
import com.fitfind.fitfind.look.common.model.response.LookDetailResponse;
import com.fitfind.fitfind.look.common.model.response.LookSummaryProjection;
import com.fitfind.fitfind.look.common.model.response.LookSummaryResponse;
import com.fitfind.fitfind.look.common.service.LookResponseMapper;
import com.fitfind.fitfind.look.feed.model.request.FeedFiltersRequest;
import com.fitfind.fitfind.look.feed.model.response.FeedResponse;
import com.fitfind.fitfind.look.feed.repository.FeedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeedService {

    private final FeedRepository feedRepository;
    private final FeedLookService feedLookService;
    private final LookResponseMapper lookResponseMapper;

    public FeedResponse list(
            FeedFiltersRequest requestFilters,
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

        Page<LookSummaryProjection> result =
            feedRepository.findPublishedFeed(genderParam, minBudget, maxBudget, stylesParam, pageable);

        List<LookSummaryResponse> looks = result.getContent().stream()
            .map(lookResponseMapper::SummaryProjectionToSummaryResponse)
            .toList();

        return new FeedResponse(looks, result.getTotalElements(), result.getTotalPages());
    }

    @Transactional(readOnly = true)
    public LookDetailResponse lookDetails(Long lookId) {
        Look look = feedLookService.lookById(lookId);
        return lookResponseMapper.lookToDetailResponse(look);
    }
}
