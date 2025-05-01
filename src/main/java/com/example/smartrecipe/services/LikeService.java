package com.example.Grand.services;

import com.example.Grand.models.Like;
import com.example.Grand.models.Product;
import com.example.Grand.models.User;
import com.example.Grand.repositories.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final NotificationService notificationService; // Добавляем зависимость

    public boolean toggleLike(User user, Product product) {
        Optional<Like> existing = likeRepository.findByUserIdAndProductId(user.getId(), product.getId());
        if (existing.isPresent()) {
            likeRepository.delete(existing.get());
            return false; // Убрали лайк
        } else {
            Like like = new Like();
            like.setUser(user);
            like.setProduct(product);
            likeRepository.save(like);

            // Создаем уведомление, если пользователь лайкает чужой продукт
            if (!user.equals(product.getUser())) {
                notificationService.createLikeNotification(
                        product.getUser(), // Владелец продукта
                        user,              // Кто лайкнул
                        product.getDescription() // Название продукта
                );
            }
            return true; // Добавили лайк
        }
    }

    public int getLikeCount(Product product) {
        return likeRepository.countByProductId(product.getId());
    }

    public boolean isProductLikedByUser(Product product, User user) {
        return likeRepository.existsByProductAndUser(product, user);
    }
}