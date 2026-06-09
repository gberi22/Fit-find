package com.fitfind.fitfind.look.common.repository;

import com.fitfind.fitfind.client.model.Client;
import com.fitfind.fitfind.look.common.model.Look;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LookRepository extends JpaRepository<Look, Long> {
    Optional<Look> findByIdAndIsPublishedTrue(Long id);

    Optional<Look> findByIdAndClient(Long id, Client client);

    Page<Look> findByClient(Client client, Pageable pageable);
}
