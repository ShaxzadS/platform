package com.example.smartrecipe.repositories;

import com.example.smartrecipe.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
     List<Product> findByDescriptionContainingIgnoreCase(String description);
}