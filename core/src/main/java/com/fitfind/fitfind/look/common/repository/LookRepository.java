package com.fitfind.fitfind.look.common.repository;

import com.fitfind.fitfind.client.model.Client;
import com.fitfind.fitfind.look.common.model.Look;
import com.fitfind.fitfind.look.common.model.response.LookSummaryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LookRepository extends JpaRepository<Look, Long> {

    Optional<Look> findByIdAndPublishedAtNotNull(Long id);

    Optional<Look> findByIdAndClient(Long id, Client client);

    Optional<Look> findByImageKey(UUID imageKey);

    List<LookSummaryProjection> findByClientOrderByCreatedAtDesc(Client client);
}
