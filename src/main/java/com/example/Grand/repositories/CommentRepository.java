package com.example.Grand.repositories;

import com.example.Grand.models.Comment;
import com.example.Grand.models.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByProductIdOrderByCreatedAtDesc(Long productId);
}



