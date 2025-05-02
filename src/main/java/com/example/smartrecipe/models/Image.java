package com.example.smartrecipe.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Arrays;
import java.util.Objects;

@Entity
@Table(name = "images")
@Getter
@Setter // Явно указываем геттеры/сеттеры вместо @Data
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String originalFileName;
    private Long size;
    private String contentType;

    @Column(columnDefinition = "bytea") // Убрали @Lob
    @JdbcTypeCode(SqlTypes.BINARY)     // Явное указание типа
    private byte[] bytes;

    private boolean isPreviewImage;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    // Явно прописываем equals/hashCode без поля bytes
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Image image = (Image) o;
        return isPreviewImage == image.isPreviewImage &&
                Objects.equals(id, image.id) &&
                Objects.equals(name, image.name) &&
                Objects.equals(originalFileName, image.originalFileName) &&
                Objects.equals(size, image.size) &&
                Objects.equals(contentType, image.contentType) &&
                Arrays.equals(bytes, image.bytes) &&
                Objects.equals(product, image.product);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, name, originalFileName, size, contentType, isPreviewImage, product);
        result = 31 * result + Arrays.hashCode(bytes);
        return result;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public boolean isPreviewImage() {
        return isPreviewImage;
    }

    public void setPreviewImage(boolean previewImage) {
        isPreviewImage = previewImage;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}