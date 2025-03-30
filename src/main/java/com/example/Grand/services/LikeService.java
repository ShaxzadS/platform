package com.example.Grand.services;

import com.example.Grand.models.Like;
import com.example.Grand.models.Product;
import com.example.Grand.models.User;
import com.example.Grand.repositories.LikeRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LikeService {

    private final LikeRepository likeRepository;

    public LikeService(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    public boolean toggleLike(User user, Product product) {
        Optional<Like> existing = likeRepository.findByUserIdAndProductId(user.getId(), product.getId());
        if (existing.isPresent()) {
            likeRepository.delete(existing.get());
            return false; // убрали лайк
        } else {
            Like like = new Like();
            like.setUser(user);
            like.setProduct(product);
            likeRepository.save(like);
            return true; // добавили лайк
        }
    }

    public int getLikeCount(Product product) {
        return likeRepository.countByProductId(product.getId());
    }

    public boolean isProductLikedByUser(Product product, User user) {
        return likeRepository.existsByProductAndUser(product, user);
    }
}
