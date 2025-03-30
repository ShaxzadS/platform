package com.example.Grand.repositories;

import com.example.Grand.models.Product;
import com.example.Grand.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {



     List<Product> findByTitleContainingIgnoreCase(String title);

     @Query("SELECT p FROM Product p JOIN FETCH p.images WHERE p.id = :id")
     Optional<Product> findByIdWithImages(@Param("id") Long id);
}
