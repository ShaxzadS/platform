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
            // 1. Сначала сохраняем продукт без изображения
            product = productRepository.save(product);

            // 2. Обрабатываем изображение
            if (image != null && !image.isEmpty()) {
                Image imageEntity = new Image();
                // Явно копируем байты
                byte[] bytes = Arrays.copyOf(image.getBytes(), image.getBytes().length);
                imageEntity.setBytes(bytes);

                // Остальные поля
                imageEntity.setContentType(image.getContentType());
                imageEntity.setOriginalFileName(image.getOriginalFilename());
                imageEntity.setSize(image.getSize());
                imageEntity.setName(image.getOriginalFilename());
                imageEntity.setPreviewImage(true);
                imageEntity.setProduct(product);

                // Явно сохраняем изображение
                imageRepository.saveAndFlush(imageEntity);
                product.setImage(imageEntity);
            }

            // 3. Обновляем продукт
            productRepository.saveAndFlush(product);

        } catch (Exception e) {
            log.error("Failed to save product", e);
            throw new RuntimeException("Product save failed", e);
        }
    }


    @Transactional
    public Optional<User> getUserByPrincipal(Principal principal) {
        if (principal == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(userRepository.findByEmailWithProducts(principal.getName()));
    }

    private Image toImageEntity(MultipartFile file) throws IOException {
        Image image = new Image();
        image.setName(file.getOriginalFilename());
        image.setOriginalFileName(file.getOriginalFilename());
        image.setContentType(file.getContentType());
        image.setSize(file.getSize());
        image.setBytes(file.getBytes()); // <-- именно так, а не file.getSize()
        return image;
    }



    private Video toVideoEntity(MultipartFile file) throws IOException {
        Video video = new Video();
        video.setName(file.getOriginalFilename());
        video.setOriginalFileName(file.getOriginalFilename());
        video.setContentType(file.getContentType());
        video.setSize(file.getSize());
        video.setBytes(file.getBytes());
        return video;
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