package com.fitfind.fitfind.feed.repository;

import com.fitfind.fitfind.wardrobe.model.Look;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedRepository extends JpaRepository<Look, Long> {
    Page<Look> findLookByIsPublishedTrue(boolean isPublished, Pageable pageable);
}
