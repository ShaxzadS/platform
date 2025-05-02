package com.example.smartrecipe.controller;

import com.example.smartrecipe.models.Comment;
import com.example.smartrecipe.models.Product;
import com.example.smartrecipe.models.User;
import com.example.smartrecipe.services.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/products")
public class ProductController {
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final ProductServices productServices;
    private final UserServices userServices;
    private final CommentService commentService;
    private final LikeService likeService;



    @Autowired
    public ProductController(ProductServices productServices, UserServices userServices,
                             CommentService commentService, LikeService likeService) {
        this.productServices = productServices;
        this.userServices = userServices;
        this.commentService = commentService;
        this.likeService = likeService;
    }
    @Value("${test.user.id:1}")
    private Long testUserId;

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get products", description = "Retrieve all products or filter by description")
    public ResponseEntity<List<Product>> getProducts(@RequestParam(required = false) String description) {
        return ResponseEntity.ok(productServices.listProducts(description));
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get product by ID", description = "Retrieve a product by its ID")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        return productServices.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a product", description = "Create a product with description, one image, and one video")
    public ResponseEntity<?> createProduct(
            @RequestPart("product") String productJson,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestPart(value = "video", required = false) MultipartFile video) {

        Optional<User> userOptional = userServices.getById(testUserId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Test user not found"));
        }

        // Валидация входных данных
        if (productJson == null || productJson.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Product JSON is required"));
        }

        // Парсинг JSON
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);

        Product product;
        try {
            product = objectMapper.readValue(productJson, Product.class);
        } catch (JsonProcessingException e) {
            log.error("JSON parsing error", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid product JSON format", "details", e.getMessage()));
        }

        // Валидация файлов
        try {
            validateFile(image, "image", 10_000_000); // Макс 10MB для изображения
            validateFile(video, "video", 50_000_000); // Макс 50MB для видео
        } catch (IOException e) {
            log.error("File validation failed", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "File validation failed", "details", e.getMessage()));
        }

        // Сохранение продукта
        try {
            productServices.saveProduct(createPrincipal(userOptional.get()), product, image, video);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Product saved successfully");
            response.put("productId", product.getId());
            response.put("imageUploaded", image != null && !image.isEmpty());
            response.put("videoUploaded", video != null && !video.isEmpty());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error saving product", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to save product", "details", e.getMessage()));
        }
    }

    private Principal createPrincipal(User user) {
        return () -> user.getEmail();
    }

    private void validateFile(MultipartFile file, String fieldName, long maxSize) throws IOException {
        if (file != null && !file.isEmpty()) {
            if (file.getSize() > maxSize) {
                throw new IOException(fieldName + " size exceeds maximum limit of " + maxSize + " bytes");
            }
            if (file.getBytes().length == 0) {
                throw new IOException(fieldName + " is empty");
            }
        }
    }


    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete product", description = "Delete a product by its ID")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productServices.deleteProduct(id);
        return ResponseEntity.ok("Product deleted");
    }

    @GetMapping("/{id}/comments")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get comments", description = "Retrieve comments for a product")
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long id) {
        Optional<Product> productOptional = productServices.getById(id);
        if (productOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Product product = productOptional.get();
        return ResponseEntity.ok(product.getComments());
    }

    @PostMapping("/{productId}/comments")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Add comment", description = "Add a comment to a product")
    public ResponseEntity<?> addComment(@PathVariable Long productId, @RequestBody String content) {
        Optional<User> userOptional = userServices.getById(testUserId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Тестовый пользователь не найден");
        }
        User user = userOptional.get();

        Optional<Product> productOptional = productServices.getById(productId);
        if (productOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Продукт не найден");
        }
        Product product = productOptional.get();

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setProduct(product);
        comment.setUser(user);

        commentService.save(comment);
        return ResponseEntity.ok("Comment added");
    }

    @PostMapping("/{productId}/like")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Toggle like", description = "Toggle like for a product")
    public ResponseEntity<?> toggleLike(@PathVariable Long productId) {

        Optional<User> userOptional = userServices.getById(testUserId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Тестовый пользователь не найден");
        }
        User user = userOptional.get();

        Optional<Product> productOptional = productServices.getById(productId);
        if (productOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Продукт не найден");
        }
        Product product = productOptional.get();

        likeService.toggleLike(user, product);
        return ResponseEntity.ok("Like toggled");
    }
}