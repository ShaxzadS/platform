package com.example.smartrecipe.models;

import jakarta.persistence.*;

import java.util.Arrays;
import java.util.Objects;

@Entity
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "bytes")
    private byte[] bytes;

//    @Column(name = "name")
//    private String name;
//
//    @Column(name = "original_file_name")
//    private String originalFileName;
//
//
//    @Column(name = "content_type")
//    private String contentType;
//
//    @Column(name = "is_preview_image")
//    private boolean isPreviewImage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }


}