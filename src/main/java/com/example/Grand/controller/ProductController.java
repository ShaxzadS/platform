package com.example.Grand.controller;

import com.example.Grand.DTO.ProductDTO;
import com.example.Grand.models.Comment;
import com.example.Grand.models.Product;
import com.example.Grand.models.User;
import com.example.Grand.services.CommentService;
import com.example.Grand.services.LikeService;
import com.example.Grand.services.UserServices;
import org.springframework.ui.Model;
import com.example.Grand.services.ProductServices;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class ProductController {
    private final ProductServices productServices;
    private final UserServices userServices;
    private final LikeService likeService;
    private final CommentService commentService;


    @GetMapping("/")
    public String products(@RequestParam(name = "title", required = false) String title,
                           Principal principal, Model model) {
        List<Product> products = productServices.listProducts(title);

        Map<String, List<Comment>> productComments = new HashMap<>();
        Set<Long> likedProductIds = new HashSet<>();
        Map<String, Integer> likeCounts = new HashMap<>();

        User user = principal != null ? productServices.getUserByPrincipal(principal) : new User();
        model.addAttribute("user", user);

        for (Product product : products) {
            String productIdStr = String.valueOf(product.getId());

            // Комментарии
            List<Comment> comments = commentService.getCommentsForProduct(product.getId());
            productComments.put(productIdStr, comments);

            // Количество лайков
            likeCounts.put(productIdStr, likeService.getLikeCount(product));

            // Проверка лайков текущего пользователя
            if (principal != null && likeService.isProductLikedByUser(product, user)) {
                likedProductIds.add(product.getId());
            }
        }

        model.addAttribute("products", products);
        model.addAttribute("productComments", productComments);
        model.addAttribute("likedProductIds", likedProductIds);
        model.addAttribute("likeCounts", likeCounts);

        return "products";
    }

    @PostMapping("/logout")
    public String logout() {
        return "redirect:/login";
    }


    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable Long id, Model model, Principal principal) {
        Product product = (Product) productServices.getById(id).orElseThrow();

        model.addAttribute("product", product);
        model.addAttribute("comments", commentService.getCommentsForProduct(id));
        model.addAttribute("likeCount", likeService.getLikeCount(product));

        return "redirect:/";
    }
    @GetMapping("/profile")
    public String profile(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = productServices.getUserWithProductsByPrincipal(principal);
        model.addAttribute("user", user);
        model.addAttribute("products", user.getProducts());
        return "profile";
    }
    @PostMapping("/product/add")
    public String createProduct(@RequestParam("file1") MultipartFile file1,
                                @RequestParam("file2") MultipartFile file2,
                                @RequestParam("file3") MultipartFile file3, Product product, Principal principal) throws IOException {
        productServices.saveProduct(principal, product, file1, file2, file3);
        return "redirect:/";
    }
    @GetMapping("/product/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productServices.deleteProduct(id);
        return "redirect:/";
    }
    @GetMapping("/user/{id}")
    public String userInfo(@PathVariable("id") Long id, Model model) {
        User user = userServices.getUserWithProductsById(id); // <-- правильный вызов
        model.addAttribute("user", user);
        model.addAttribute("products", user.getProducts());
        return "user-info";
    }


    @GetMapping("/products/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {
        Product product = productServices.findById(id);
        model.addAttribute("product", product);
        return "edit-product"; // Название шаблона
    }


    @PostMapping("/products/{productId}/comment")
    public String addComment(@PathVariable Long productId,
                             @RequestParam String content,
                             Principal principal) {
        User user = userServices.getUserByPrincipal(principal); // получаем текущего юзера

        Product product = productServices.getProductById(productId)  // получаем продукт
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setProduct(product);
        comment.setUser(user);

        commentService.save(comment); // сохраняем комментарий
        return "redirect:/product/" + productId;
    }



    @PostMapping("/products/{productId}/like")
    public String likeProduct(@PathVariable Long productId, Principal principal) {
        User user = userServices.getUserByPrincipal(principal);
        Product product = (Product) productServices.getById(productId).orElseThrow();

        likeService.toggleLike(user, product);

        return "redirect:/product/" + productId;
    }
}
