package com.fitfind.fitfind.wardrobe.repository;

import com.fitfind.fitfind.wardrobe.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
