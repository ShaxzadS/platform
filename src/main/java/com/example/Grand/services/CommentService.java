package com.example.Grand.services;

import com.example.Grand.models.Comment;
import com.example.Grand.models.Product;
import com.example.Grand.models.User;
import com.example.Grand.repositories.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final NotificationService notificationService; // Добавляем зависимость

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