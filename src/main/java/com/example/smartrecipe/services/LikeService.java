package com.example.smartrecipe.services;

import com.example.smartrecipe.models.Like;
import com.example.smartrecipe.models.Product;
import com.example.smartrecipe.models.User;
import com.example.smartrecipe.repositories.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final NotificationService notificationService; // Добавляем зависимость



    @Autowired
    public LikeService(LikeRepository likeRepository, NotificationService notificationService) {
        this.likeRepository = likeRepository;
        this.notificationService = notificationService;
    }
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