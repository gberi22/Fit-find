package com.fitfind.fitfind.look.feed.repository;

import com.fitfind.fitfind.look.common.model.LookCardProjection;
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
            SELECT l.id AS id, l.gender AS gender, l.budget_min AS budgetMin,
                   l.budget_max AS budgetMax, l.styles AS styles,
                   l.image_mime_type AS imageMimeType,
                   c.first_name AS firstName, c.last_name AS lastName,
                   l.created_at AS createdAt
            FROM looks l
            JOIN client c ON c.id = l.client_id
            WHERE l.is_published = true AND l.deleted_at IS NULL
              AND (CAST(:gender AS text) IS NULL OR l.gender = :gender)
              AND (CAST(:minBudget AS numeric) IS NULL OR l.budget_max IS NULL OR l.budget_max >= :minBudget)
              AND (CAST(:maxBudget AS numeric) IS NULL OR l.budget_min IS NULL OR l.budget_min <= :maxBudget)
              AND (CAST(:styles AS text[]) IS NULL OR jsonb_exists_any(l.styles, CAST(:styles AS text[])))
            ORDER BY l.created_at DESC
            """,
        countQuery = """
            SELECT count(*)
            FROM looks l
            WHERE l.is_published = true AND l.deleted_at IS NULL
              AND (CAST(:gender AS text) IS NULL OR l.gender = :gender)
              AND (CAST(:minBudget AS numeric) IS NULL OR l.budget_max IS NULL OR l.budget_max >= :minBudget)
              AND (CAST(:maxBudget AS numeric) IS NULL OR l.budget_min IS NULL OR l.budget_min <= :maxBudget)
              AND (CAST(:styles AS text[]) IS NULL OR jsonb_exists_any(l.styles, CAST(:styles AS text[])))
            """,
        nativeQuery = true
    )
    Page<LookCardProjection> findPublishedFeed(
            @Param("gender") String gender,
            @Param("minBudget") BigDecimal minBudget,
            @Param("maxBudget") BigDecimal maxBudget,
            @Param("styles") String[] styles,
            Pageable pageable
    );
}
