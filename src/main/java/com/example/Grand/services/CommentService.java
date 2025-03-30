package com.example.Grand.services;

import com.example.Grand.models.Comment;
import com.example.Grand.repositories.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<Comment> getCommentsForProduct(Long productId) {
        return commentRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }
}
