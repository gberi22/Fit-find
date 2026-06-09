package com.fitfind.fitfind.wardrobe.repository;

import com.fitfind.fitfind.client.model.Client;
import com.fitfind.fitfind.wardrobe.model.Look;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LookRepository extends JpaRepository<Look, Long> {

    Optional<Look> findByIdAndClient(Long id, Client client);

    Page<Look> findByClient(Client client, Pageable pageable);
}
