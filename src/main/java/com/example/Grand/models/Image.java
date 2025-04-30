package com.example.Grand.models;

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
}