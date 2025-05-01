package com.example.Grand.repositories;

import com.example.Grand.models.Like;
import com.example.Grand.models.Product;
import com.example.Grand.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserIdAndProductId(Long userId, Long productId);
    int countByProductId(Long productId);

    boolean existsByProductAndUser(Product product, User user);
}