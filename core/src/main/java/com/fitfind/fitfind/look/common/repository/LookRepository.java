package com.fitfind.fitfind.look.common.repository;

import com.fitfind.fitfind.look.common.model.Look;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LookRepository extends JpaRepository<Look, Long> {
    Optional<Look> findByIdAndIsPublishedTrue(Long id);
}
