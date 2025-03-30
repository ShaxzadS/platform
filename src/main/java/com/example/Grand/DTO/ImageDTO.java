package com.example.Grand.DTO;

import com.example.Grand.models.Image;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor




public class ImageDTO {
    private Long id;
    private String name;
    private String originalFileName;
    private String contentType;
    private boolean isPreviewImage;

    public ImageDTO(Image image) {
        this.id = image.getId();
        this.name = image.getName();
        this.originalFileName = image.getOriginalFileName();
        this.contentType = image.getContentType();
        this.isPreviewImage = image.isPreviewImage();
    }
}
