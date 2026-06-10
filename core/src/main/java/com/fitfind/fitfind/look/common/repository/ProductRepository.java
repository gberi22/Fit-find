package com.fitfind.fitfind.look.common.repository;

import com.fitfind.fitfind.look.common.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByUrl(String url);
}
