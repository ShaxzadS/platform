package com.example.smartrecipe.services;

import com.example.smartrecipe.models.Image;
import com.example.smartrecipe.models.Product;
import com.example.smartrecipe.models.User;
import com.example.smartrecipe.models.Video;
import com.example.smartrecipe.repositories.ImageRepository;
import com.example.smartrecipe.repositories.ProductRepository;
import com.example.smartrecipe.repositories.UserRepository;
import com.example.smartrecipe.repositories.VideoRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServices {
    private static final Logger log = LoggerFactory.getLogger(ProductServices.class);
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final VideoRepository videoRepository;
    private final UserServices userServices;

    @Autowired
    public ProductServices(ProductRepository productRepository, UserRepository userRepository,
                           ImageRepository imageRepository, VideoRepository videoRepository,
                           UserServices userServices) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
        this.videoRepository = videoRepository;
        this.userServices = userServices;
    }

    @Transactional
    public List<Product> listProducts(String description) {
        if (description != null && !description.trim().isEmpty()) {
            return productRepository.findByDescriptionContainingIgnoreCase(description);
        }
        return productRepository.findAll();
    }
    @Transactional
    public void saveProduct(Principal principal, Product product, MultipartFile image, MultipartFile video) throws IOException {
        try {
            User user = userServices.getUserByPrincipal(principal)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            product.setUser(user);

            // Если есть картинка — создаём её
            if (image != null && !image.isEmpty()) {
                Image imageEntity = toImageEntity(image);
                log.info("Saving image: bytes length={}", imageEntity.getBytes() != null ? imageEntity.getBytes().length : "null");

                product.setImage(imageEntity);  // 👈 сразу сетай в продукт
            }


            if (video != null && !video.isEmpty()) {
                Video videoEntity = toVideoentity(video);
                log.info("Saving video", videoEntity.getBytes() != null ? videoEntity.getBytes().length : "null");

                product.setVideo(videoEntity);
            }


            // Сохраняем продукт ОДИН РАЗ — вместе с картинкой и видео
            productRepository.saveAndFlush(product);
            log.info("Product saved with ID: {}", product.getId());

        } catch (Exception e) {
            log.error("Failed to save product", e);
            throw new RuntimeException("Product save failed: " + e.getMessage(), e);
        }
    }


    private Image toImageEntity(MultipartFile file) throws IOException {
        Image image = new Image();
        image.setBytes(file.getBytes());
        return image;
    }

    private Video toVideoentity(MultipartFile file) throws IOException {
        Video video = new Video();
        video.setBytes(file.getBytes());
        return video;
    }

    @Transactional
    public Optional<User> getUserByPrincipal(Principal principal) {
        if (principal == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(userRepository.findByEmailWithProducts(principal.getName()));
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Transactional
    public Optional<Product> getById(Long id) {
        return productRepository.findById(id);
    }
}