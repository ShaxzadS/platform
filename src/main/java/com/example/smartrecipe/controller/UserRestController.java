package com.example.Grand.controller;

import com.example.Grand.models.User;
import com.example.Grand.services.EmailService;
import com.example.Grand.services.UserServices;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import com.example.Grand.services.JwtTokenService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {

    private final UserServices userServices;
    private final EmailService emailService;
    private final JwtTokenService jwtTokenService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        boolean created = userServices.createUser(user);
        if (!created) {
            return ResponseEntity.badRequest().body("User with email already exists: " + user.getEmail());
        }

        emailService.sendSimpleEmail(
                user.getEmail(),
                "Добро пожаловать в Grand!",
                "Привет, " + user.getName() + "! Спасибо за регистрацию на нашем сайте."
        );

        return ResponseEntity.ok("User registered successfully!");
    }
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
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userServices.list();
        return ResponseEntity.ok(users);
    }
}
