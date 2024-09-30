package com.example.Grand.controller;

import com.example.Grand.models.Product;
import com.example.Grand.models.User;
import org.springframework.ui.Model;
import com.example.Grand.services.ProductServices;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ProductController {
    private final ProductServices productServices;
    @GetMapping("/")
    public String products(@RequestParam(name = "title", required = false) String title, Principal principal, Model model) {
        model.addAttribute("products", productServices.listProducts(title));

        // Проверяем, если principal не null и получаем пользователя
        User user = principal != null ? productServices.getUserByPrincipal(principal) : new User();
        model.addAttribute("user", user);

        return "products";
    }
    @PostMapping("/logout")
    public String logout() {
        return "redirect:/login";
    }

    @GetMapping("/product/{id}")
    public String productInfo(@PathVariable Long id, Model model) {
        Product product = productServices.getProductById(id);
        model.addAttribute("product",product);
        model.addAttribute("images",product.getImages() );
        return "product-info";
    }

    @PostMapping("/product/add")
    public String createProduct(@RequestParam("file1") MultipartFile file1,
                                @RequestParam("file2") MultipartFile file2,
                                @RequestParam("file3") MultipartFile file3, Product product, Principal principal) throws IOException {
        productServices.saveProduct(principal, product, file1, file2, file3);
        return "redirect:/";
    }
    @PostMapping("/product/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productServices.deleteProduct(id);
        return "redirect:/";
    }

    @GetMapping("/user/{user}")
    public String userInfo(@PathVariable ("user") User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("products", user.getProducts());
        return "user-info";
    }
}
