package com.fitfind.fitfind.look.profile.repository;

import com.fitfind.fitfind.look.common.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
