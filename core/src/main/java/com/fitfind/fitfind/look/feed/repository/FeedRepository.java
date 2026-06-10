package com.fitfind.fitfind.look.feed.repository;

import com.fitfind.fitfind.look.common.model.response.LookSummaryProjection;
import com.fitfind.fitfind.look.common.model.Look;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface FeedRepository extends JpaRepository<Look, Long> {

    @Query(
        value = """
            SELECT l.id AS id, l.image_key AS imageKey,
                   l.published_at AS publishedAt
            FROM looks l
            WHERE l.published_at IS NOT NULL
              AND (CAST(:gender AS text) IS NULL OR l.gender = :gender)
              AND (CAST(:minBudget AS numeric) IS NULL OR l.budget_max IS NULL OR l.budget_max >= :minBudget)
              AND (CAST(:maxBudget AS numeric) IS NULL OR l.budget_min IS NULL OR l.budget_min <= :maxBudget)
              AND (CAST(:styles AS text[]) IS NULL OR jsonb_exists_any(l.styles, CAST(:styles AS text[])))
            ORDER BY l.published_at DESC NULLS LAST, l.id DESC
            """,
        countQuery = """
            SELECT count(*)
            FROM looks l
            WHERE l.published_at IS NOT NULL
              AND (CAST(:gender AS text) IS NULL OR l.gender = :gender)
              AND (CAST(:minBudget AS numeric) IS NULL OR l.budget_max IS NULL OR l.budget_max >= :minBudget)
              AND (CAST(:maxBudget AS numeric) IS NULL OR l.budget_min IS NULL OR l.budget_min <= :maxBudget)
              AND (CAST(:styles AS text[]) IS NULL OR jsonb_exists_any(l.styles, CAST(:styles AS text[])))
            """,
        nativeQuery = true
    )
    Page<LookSummaryProjection> findPublishedFeed(
            @Param("gender") String gender,
            @Param("minBudget") BigDecimal minBudget,
            @Param("maxBudget") BigDecimal maxBudget,
            @Param("styles") String[] styles,
            Pageable pageable
    );
}
