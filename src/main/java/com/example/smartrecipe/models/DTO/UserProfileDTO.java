package com.example.smartrecipe.models.DTO;
import java.util.Base64;

public class UserProfileDTO {
    private Long id;
    private String name;
    private String email;
    private String bio;
    private String avatarBase64;
    //<img src="data:image/jpeg;base64,{{ avatarBase64 }}" />

    public UserProfileDTO(Long id, String name, String email, String bio, byte[] avatarData) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.bio = bio;
        this.avatarBase64 = (avatarData != null && avatarData.length > 0)
                ? Base64.getEncoder().encodeToString(avatarData)
                : null;

    }

    public String getAvatarBase64() {
        return avatarBase64;
    }

    public void setAvatarBase64(String avatarBase64) {
        this.avatarBase64 = avatarBase64;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

}