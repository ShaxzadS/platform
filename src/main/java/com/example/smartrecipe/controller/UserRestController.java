package com.example.smartrecipe.controller;

import com.example.smartrecipe.models.User;
//import com.example.smartrecipe.services.EmailService;
import com.example.smartrecipe.services.UserServices;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.example.smartrecipe.services.JwtTokenService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserServices userServices;
  //  private final EmailService emailService;
    private final JwtTokenService jwtTokenService;


    @Autowired
    public UserRestController(UserServices userServices, JwtTokenService jwtTokenService) {
        this.userServices = userServices;
      //  this.emailService = emailService;
        this.jwtTokenService = jwtTokenService;
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
