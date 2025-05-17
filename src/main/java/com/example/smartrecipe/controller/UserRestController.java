package com.example.smartrecipe.controller;

import com.example.smartrecipe.models.DTO.UserDTO;
import com.example.smartrecipe.models.DTO.UserProfileDTO;
import com.example.smartrecipe.models.EditProfileRequest;
import com.example.smartrecipe.models.User;
//import com.example.smartrecipe.services.EmailService;
import com.example.smartrecipe.models.UserDetailsImpl;
import com.example.smartrecipe.repositories.UserRepository;
import com.example.smartrecipe.services.UserServices;
import com.fasterxml.jackson.databind.DeserializationContext;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.example.smartrecipe.services.JwtTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserServices userServices;
  //  private final EmailService emailService;
    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;


    @Autowired
    public UserRestController(UserServices userServices, JwtTokenService jwtTokenService, UserRepository userRepository, ObjectMapper objectMapper) {
        this.userServices = userServices;
      //  this.emailService = emailService;
        this.jwtTokenService = jwtTokenService;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (!user.getPassword().equals(user.getRepeatPassword())) {
            return ResponseEntity.badRequest().body("Passwords do not match");
        }

        boolean created = userServices.createUser(user);
        if (!created) {
            return ResponseEntity.badRequest().body("User with email already exists: " + user.getEmail());
        }

        return ResponseEntity.ok("User registered successfully!");
    }



    @Transactional
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        if (userServices.authenticate(user.getEmail(), user.getPassword())) {
            String token = jwtTokenService.generateToken(user.getEmail());
            return ResponseEntity.ok(Map.of("token", token));
        }
        return ResponseEntity.status(401).build();
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        try {
            User user = userServices.getUserWithProductsById(id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userServices.deleteUser(id);
        return ResponseEntity.ok("User deleted");
    }

    @PostMapping("/{id}/ban")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> banUser(@PathVariable Long id) {
        userServices.banUser(id);
        return ResponseEntity.ok("User ban status toggled");
    }


    @GetMapping("/all")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userServices.list();

        List<UserDTO> dtos = users.stream()
                .map(UserDTO::new)
                .toList();

        return ResponseEntity.ok(dtos);
    }


    @Transactional
    @GetMapping("/profile")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getProfile(Principal principal) {
        Optional<User> userOptional = userServices.getProfile(principal);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }

        User user = userOptional.get();

        // Можно вернуть сам объект User, но лучше сделать DTO,
        // чтобы не отдавать лишние поля (например, пароль, роли и т.д.)
        UserProfileDTO profileDto = new UserProfileDTO(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getBio(),
                user.getAvatarData()
        );

        return ResponseEntity.ok(profileDto);
    }



    @Transactional
    @PutMapping(value = "/profile/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> editProfile(
            Principal principal,
            @RequestPart("profile") String profileJson,
            @RequestPart(value = "avatar", required = false) MultipartFile avatarFile) {

        EditProfileRequest request;
        try {
            request = objectMapper.readValue(profileJson, EditProfileRequest.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid profile JSON");
        }
        Optional<User> userOptional = userServices.findByEmail(principal.getName());
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        User user = userOptional.get();

        if (request.getName() != null && !request.getName().isEmpty()) {
            user.setName(request.getName());
        }
        if (request.getBio() != null && !request.getBio().isEmpty()) {
            user.setBio(request.getBio());
        }

        if (avatarFile != null && !avatarFile.isEmpty()) {
            // Сохраняем файл где-то, например в БД (byte[]) или на диск
            try {
                byte[] avatarBytes = avatarFile.getBytes();
                user.setAvatarData(avatarBytes); // поле типа byte[] в User
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save avatar");
            }
        }

        userServices.save(user);

        return ResponseEntity.ok("Профиль обновлён");
    }


//    @PutMapping("/profile/edit")
//    @SecurityRequirement(name = "bearerAuth")
//    public ResponseEntity<?> editProfile(Principal principal,
//                                         @RequestBody EditProfileRequest request) {
//        Optional<User> userOptional = userServices.findByEmail(principal.getName());
//
//        if (userOptional.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
//        }
//
//        User user = userOptional.get();
//
//        // Обновляем имя, если передано
//        if (request.getName() != null && !request.getName().isEmpty()) {
//            user.setName(request.getName());
//        }
//
//        // Обновляем био, если передано
//        if (request.getBio() != null && !request.getBio().isEmpty()) {
//            user.setBio(request.getBio());
//        }
//
//        // Обновляем аватар, если передан
//        if (request.getAvatarUrl() != null && !request.getAvatarUrl().isEmpty()) {
//            user.setAvatarUrl(request.getAvatarUrl());
//        }
//
//        userServices.save(user);
//        return ResponseEntity.ok("Профиль обновлён");
//    }



}
