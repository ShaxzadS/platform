package com.example.smartrecipe.controller;

import com.example.smartrecipe.models.Comment;
import com.example.smartrecipe.models.Product;
import com.example.smartrecipe.models.User;
import com.example.smartrecipe.services.CommentService;
import com.example.smartrecipe.services.LikeService;
import com.example.smartrecipe.services.ProductServices;
import com.example.smartrecipe.services.UserServices;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final ProductServices productServices;
    private final UserServices userServices;
    private final CommentService commentService;
    private final LikeService likeService;
    private final ObjectMapper objectMapper;

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get products", description = "Retrieve all products or filter by description")
    public ResponseEntity<List<Product>> getProducts(@RequestParam(required = false) String description) {
        log.info("Fetching products with description filter: {}", description);
        return ResponseEntity.ok(productServices.listProducts(description));
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get product by ID", description = "Retrieve a product by its ID")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        log.info("Fetching product with ID: {}", id);
        return productServices.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Product with ID {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a product", description = "Create a product with description, ingredients, cooking time, calories, one image, and one video")
    public ResponseEntity<?> createProduct(
            @RequestPart("product") String productJson,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestPart(value = "video", required = false) MultipartFile video,
            Principal principal) {

        log.info("Creating new product for user: {}", principal.getName());

        // Проверка JSON
        if (productJson == null || productJson.isBlank()) {
            log.error("Product JSON is empty or null");
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Product JSON is required"));
        }

        // Парсинг JSON
        Product product;
        try {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
            product = objectMapper.readValue(productJson, Product.class);
        } catch (Exception e) {
            log.error("JSON parsing error", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid product JSON format", "details", e.getMessage()));
        }

        // Валидация полей
        List<String> validationErrors = validateProduct(product);
        if (!validationErrors.isEmpty()) {
            log.error("Validation failed: {}", validationErrors);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Validation failed", "details", validationErrors));
        }

        // Валидация файлов
        try {
            validateFile(image, "image", 10_000_000); // 10MB
            validateFile(video, "video", 50_000_000); // 50MB
        } catch (IOException e) {
            log.error("File validation failed", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "File validation failed", "details", e.getMessage()));
        }

        try {
            productServices.saveProduct(principal, product, image, video);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Product saved successfully");
            response.put("productId", product.getId());
            response.put("imageUploaded", image != null && !image.isEmpty());
            response.put("videoUploaded", video != null && !video.isEmpty());

            log.info("Product created successfully with ID: {}", product.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error saving product", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to save product", "details", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete product", description = "Delete a product by its ID")
    @Transactional
    public ResponseEntity<?> deleteProduct(@PathVariable Long id, Principal principal) {
        log.info("Deleting product with ID: {} by user: {}", id, principal.getName());
        try {
            User user = userServices.getUserByPrincipal(principal)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Optional<Product> productOptional = productServices.getById(id);
            if (productOptional.isEmpty()) {
                log.warn("Product with ID {} not found", id);
                return ResponseEntity.notFound().build();
            }
            Product product = productOptional.get();
            if (!product.getUser().getId().equals(user.getId())) {
                log.warn("User {} not authorized to delete product {}", user.getId(), id);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Not authorized to delete this product"));
            }
            productServices.deleteProduct(id);
            log.info("Product with ID {} deleted successfully", id);
            return ResponseEntity.ok(Map.of("message", "Product deleted"));
        } catch (Exception e) {
            log.error("Error deleting product with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete product", "details", e.getMessage()));
        }
    }

    @GetMapping("/{id}/comments")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get comments", description = "Retrieve comments for a product")
    public ResponseEntity<?> getComments(@PathVariable Long id) {
        log.info("Fetching comments for product with ID: {}", id);
        Optional<Product> productOptional = productServices.getById(id);
        if (productOptional.isEmpty()) {
            log.warn("Product with ID {} not found", id);
            return ResponseEntity.notFound().build();
        }
        Product product = productOptional.get();
        return ResponseEntity.ok(product.getComments());
    }

    @PostMapping("/{productId}/comments")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Add comment", description = "Add a comment to a product")
    @Transactional
    public ResponseEntity<?> addComment(@PathVariable Long productId, @RequestBody Map<String, String> request, Principal principal) {
        log.info("Adding comment to product with ID: {} by user: {}", productId, principal.getName());
        String content = request.get("content");
        if (content == null || content.isBlank()) {
            log.error("Comment content is empty");
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Comment content is required"));
        }

        try {
            User user = userServices.getUserByPrincipal(principal)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Optional<Product> productOptional = productServices.getById(productId);
            if (productOptional.isEmpty()) {
                log.warn("Product with ID {} not found", productId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Product not found"));
            }
            Product product = productOptional.get();

            Comment comment = new Comment();
            comment.setContent(content);
            comment.setProduct(product);
            comment.setUser(user);

            commentService.save(comment);
            log.info("Comment added to product with ID: {}", productId);
            return ResponseEntity.ok(Map.of("message", "Comment added"));
        } catch (Exception e) {
            log.error("Error adding comment to product with ID: {}", productId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to add comment", "details", e.getMessage()));
        }
    }

    @PostMapping("/{productId}/like")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Toggle like", description = "Toggle like for a product")
    @Transactional
    public ResponseEntity<?> toggleLike(@PathVariable Long productId, Principal principal) {
        log.info("Toggling like for product with ID: {} by user: {}", productId, principal.getName());
        try {
            User user = userServices.getUserByPrincipal(principal)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Optional<Product> productOptional = productServices.getById(productId);
            if (productOptional.isEmpty()) {
                log.warn("Product with ID {} not found", productId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Product not found"));
            }
            Product product = productOptional.get();

            likeService.toggleLike(user, product);
            log.info("Like toggled for product with ID: {}", productId);
            return ResponseEntity.ok(Map.of("message", "Like toggled"));
        } catch (Exception e) {
            log.error("Error toggling like for product with ID: {}", productId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to toggle like", "details", e.getMessage()));
        }
    }

    private List<String> validateProduct(Product product) {
        List<String> errors = new ArrayList<>();
        if (product.getName() == null || product.getName().isBlank()) {
            errors.add("Name is required");
        }
        if (product.getDescription() == null || product.getDescription().isBlank()) {
            errors.add("Description is required");
        }
        if (product.getIngredients() == null || product.getIngredients().isEmpty()) {
            errors.add("At least one ingredient is required");
        }
        if (product.getIngredients() != null && product.getIngredients().size() > 50) {
            errors.add("Maximum 50 ingredients allowed");
        }
        if (product.getPreparationTime() == null || product.getPreparationTime() <= 0) {
            errors.add("Preparation time must be greater than 0 minutes");
        }
        if (product.getCookingTime() == null || product.getCookingTime() <= 0) {
            errors.add("Cooking time must be greater than 0 minutes");
        }
        if (product.getCalories() == null || product.getCalories() <= 0) {
            errors.add("Calories must be greater than 0");
        }
        if (product.getDifficulty() == null) {
            errors.add("Difficulty level is required");
        }
        if (product.getCategory() == null) {
            errors.add("Category is required");
        }
        return errors;
    }

    private void validateFile(MultipartFile file, String fileType, long maxSize) throws IOException {
        if (file != null && !file.isEmpty()) {
            if (file.getSize() > maxSize) {
                throw new IOException(fileType + " size exceeds maximum limit of " + (maxSize / 1_000_000) + "MB");
            }
            String contentType = file.getContentType();
            if ("image".equals(fileType) && (contentType == null || !contentType.startsWith("image/"))) {
                throw new IOException("Invalid image format");
            }
            if ("video".equals(fileType) && (contentType == null || !contentType.startsWith("video/"))) {
                throw new IOException("Invalid video format");
            }
        }
    }
}