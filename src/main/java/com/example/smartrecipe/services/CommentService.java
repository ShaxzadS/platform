package com.example.smartrecipe.services;

import com.example.smartrecipe.models.Comment;
import com.example.smartrecipe.models.Product;
import com.example.smartrecipe.models.User;
import com.example.smartrecipe.repositories.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class CommentService {

    private final CommentRepository commentRepository;
    private final NotificationService notificationService; // Добавляем зависимость


    @Autowired
    public CommentService(CommentRepository commentRepository, NotificationService notificationService) {
        this.commentRepository = commentRepository;
        this.notificationService = notificationService;
    }
    public List<Comment> getCommentsForProduct(Long productId) {
        return commentRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    public Comment save(Comment comment) {
        Comment savedComment = commentRepository.save(comment);

        // Создаем уведомление, если пользователь комментирует чужой продукт
        User commenter = comment.getUser();
        Product product = comment.getProduct();
        if (commenter != null && product != null && !commenter.equals(product.getUser())) {
            notificationService.createCommentNotification(
                    product.getUser(), // Владелец продукта
                    commenter,         // Кто прокомментировал
                    product.getDescription() // Название продукта
            );
        }
        return savedComment;
    }
}