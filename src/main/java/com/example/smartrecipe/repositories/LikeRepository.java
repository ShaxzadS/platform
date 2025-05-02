package com.example.smartrecipe.repositories;

import com.example.smartrecipe.models.Like;
import com.example.smartrecipe.models.Product;
import com.example.smartrecipe.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserIdAndProductId(Long userId, Long productId);
    int countByProductId(Long productId);

    boolean existsByProductAndUser(Product product, User user);
}