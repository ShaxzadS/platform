package com.example.Grand.services;

import com.example.Grand.models.Image;
import com.example.Grand.models.Product;
import com.example.Grand.models.User;
import com.example.Grand.repositories.ProductRepositiry;
import com.example.Grand.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServices {
    private final ProductRepositiry productRepositiry;
    private final UserRepository userRepository;

    public List<Product> listProducts(String title) {
        if (title!=null) return productRepositiry.findByTitle(title);
        return productRepositiry.findAll();
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
        Product productFromDB = productRepositiry.save(product);
        productFromDB.setPreviewImageId(productFromDB.getImages().get(0).getId());
        productRepositiry.save(product);
    }

    public User getUserByPrincipal(Principal principal) {
        if (principal==null) return null;
        return userRepository.findByEmail(principal.getName());
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
        productRepositiry.deleteById(id);
    }

    public Product getProductById(Long id) {
        return productRepositiry.findById(id).orElse(null);
}
}