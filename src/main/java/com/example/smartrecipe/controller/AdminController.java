package com.example.Grand.controller;

import com.example.Grand.models.User;
import com.example.Grand.models.enums.Role;
import com.example.Grand.services.UserServices;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
public class AdminController {

    private final UserServices userServices;

    // Получить список всех пользователей


    public AdminController(UserServices userServices) {
        this.userServices = userServices;
    }
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/users")

    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userServices.list());
    }

    // Забанить пользователя по ID
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/user/ban/{id}")
    public ResponseEntity<String> banUser(@PathVariable Long id) {
        userServices.banUser(id);
        return ResponseEntity.ok("User banned");
    }

    // Получить одного пользователя + список ролей
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserDetails(@PathVariable("id") Long id) {
        User user = userServices.getById(id)
                .orElse(null);
        if (user == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(Map.of(
                "user", user,
                "roles", Role.values()
        ));
    }

    // Изменить роли пользователя
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/user/edit")
    public ResponseEntity<String> editUserRoles(@RequestParam("userId") Long userId,
                                                @RequestParam Map<String, String> form) {
        User user = userServices.getById(userId).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();

        userServices.changeUserRoles(user, form);
        return ResponseEntity.ok("User roles updated");
    }

    // Удалить пользователя
    @DeleteMapping("/user/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userServices.deleteUser(id);
        return ResponseEntity.ok("User deleted");
    }
}
