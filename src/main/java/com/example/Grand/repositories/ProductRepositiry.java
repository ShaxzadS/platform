package com.example.Grand.repositories;

import com.example.Grand.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepositiry extends JpaRepository<Product, Long> {
    List<Product> findByTitle(String title);

}
