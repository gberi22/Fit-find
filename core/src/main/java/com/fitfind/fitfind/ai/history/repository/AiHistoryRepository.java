package com.fitfind.fitfind.ai.history.repository;

import com.fitfind.fitfind.ai.history.model.AiHistory;
import com.fitfind.fitfind.client.model.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiHistoryRepository extends JpaRepository<AiHistory, Long> {
    Page<AiHistory> findByClient(Client client, Pageable pageable);
}
