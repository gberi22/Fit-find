package com.fitfind.fitfind.client.repository;

import com.fitfind.fitfind.client.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findClientByEmail(String email);

    boolean existsByEmail(String email);
}
