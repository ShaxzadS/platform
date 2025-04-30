package com.example.Grand.repositories;

import com.example.Grand.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
     List<Product> findByDescriptionContainingIgnoreCase(String description);
}