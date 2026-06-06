package com.fitfind.fitfind.feed.repository;

import com.fitfind.fitfind.wardrobe.model.Look;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeedRepository extends JpaRepository<Look, Long> {
    Optional<Look> findLookByIsPublishedTrue();
    Optional<Look> findLookByDeletedAtIsNull();
}
