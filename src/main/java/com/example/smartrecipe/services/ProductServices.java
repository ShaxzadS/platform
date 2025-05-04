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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServices {
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
    private static final Logger log = LoggerFactory.getLogger(ProductServices.class);
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
            // 1. Находим пользователя
            User user = userServices.getUserByPrincipal(principal)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            product.setUser(user);

            // 2. Сохраняем продукт без файлов
            product = productRepository.save(product);

            // 3. Обрабатываем изображение
            if (image != null && !image.isEmpty()) {
                Image imageEntity = toImageEntity(image);
                imageEntity.setProduct(product);
                imageRepository.saveAndFlush(imageEntity);
                product.setImage(imageEntity);
            }

            // 4. Обрабатываем видео
            if (video != null && !video.isEmpty()) {
                Video videoEntity = toVideoEntity(video);
                videoEntity.setProduct(product);
                videoRepository.saveAndFlush(videoEntity);
                product.setVideo(videoEntity);
            }

            // 5. Сохраняем продукт уже с файлам
            productRepository.saveAndFlush(product);

        } catch (Exception e) {
            log.error("Failed to save product", e);
            throw new RuntimeException("Product save failed", e);
        }
    }

    private Image toImageEntity(MultipartFile image) throws IOException {
        Image imageEntity = new Image();
        imageEntity.setName(image.getOriginalFilename());
        imageEntity.setOriginalFileName(image.getOriginalFilename());
        imageEntity.setSize(image.getSize());
        imageEntity.setContentType(image.getContentType());
        imageEntity.setBytes(image.getBytes()); // Сохраняем как массив байтов
        return imageEntity;
    }

    private Video toVideoEntity(MultipartFile video) throws IOException {
        Video videoEntity = new Video();
        videoEntity.setName(video.getOriginalFilename());
        videoEntity.setOriginalFileName(video.getOriginalFilename());
        videoEntity.setSize(video.getSize());
        videoEntity.setContentType(video.getContentType());
        videoEntity.setBytes(video.getBytes()); // Сохраняем как массив байтов
        return videoEntity;
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