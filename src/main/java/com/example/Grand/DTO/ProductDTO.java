package com.example.Grand.DTO;


import com.example.Grand.models.Image;
import com.example.Grand.models.Product;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"images", "user"}) // или нужные поля

public class ProductDTO {
    private Long id;
    private String title;
    private String description;
    private int price;
    private String city;
    private Long previewImageId;
    private LocalDateTime dateOfCreated;
    private List<ImageDTO> images = new ArrayList<>();

    public ProductDTO(Product product) {
        this.id = product.getId();
        this.title = product.getTitle();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.city = product.getCity();
        this.previewImageId = product.getPreviewImageId();
        this.dateOfCreated = product.getDateOfCreated();

        if (product.getImages() != null) {
            for (Image image : product.getImages()) {
                this.images.add(new ImageDTO(image));
            }
        }
    }
}
