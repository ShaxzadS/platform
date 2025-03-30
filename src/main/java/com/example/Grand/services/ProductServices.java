package com.example.Grand.services;

import com.example.Grand.models.Image;
import com.example.Grand.models.Product;
import com.example.Grand.models.User;
import com.example.Grand.repositories.ProductRepository;
import com.example.Grand.repositories.ProductRepository;
import com.example.Grand.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServices {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    @Transactional
    public List<Product> listProducts(String title) {
        if (title != null && !title.trim().isEmpty()) {
            return productRepository.findByTitleContainingIgnoreCase(title);
        }
        return productRepository.findAll();
    }

    public void saveProduct(Principal principal, Product product, MultipartFile file1, MultipartFile file2, MultipartFile file3) throws IOException {
        product.setUser(getUserByPrincipal(principal));
        Image image1;
        Image image2;
        Image image3;
        if(file1.getSize()!=0){
            image1 = toImageEntity(file1);
            image1.setPreviewImage(true);
            product.addImageToProduct(image1);
        }
        if(file2.getSize()!=0){
            image2 = toImageEntity(file2);
            product.addImageToProduct(image2);
        }
        if(file3.getSize()!=0){
            image3 = toImageEntity(file3);
            product.addImageToProduct(image3);
        }
       log.info("Saving new product. Title:{}; Author: email: {}", product.getTitle(), product.getUser().getEmail());
        Product productFromDB = productRepository.save(product);
        productFromDB.setPreviewImageId(productFromDB.getImages().get(0).getId());
        productRepository.save(product);
    }

    @Transactional
    public User getUserWithProductsByPrincipal(Principal principal) {
        if (principal == null) return null;
        return userRepository.findByEmailWithProducts(principal.getName());
    }



    @Transactional
    public User getUserByPrincipal(Principal principal) {
        if (principal==null) return null;
        return userRepository.findByEmailWithProducts(principal.getName());
    }

    private Image toImageEntity(MultipartFile file) throws IOException{
            Image image = new Image();
            image.setName(file.getName());
            image.setOriginalFileName(file.getOriginalFilename());
            image.setContentType(file.getContentType());
            image.setSize(file.getSize());
            image.setBytes(file.getBytes());
            return image;

        }


    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    @Transactional


    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public Object findAll() {
        return productRepository.findAll();
    }

    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    public Optional<Object> getById(Long productId) {
        return Optional.ofNullable(productRepository.findById(productId).orElse(null));
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }


}