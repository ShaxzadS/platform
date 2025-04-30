package com.example.Grand.controller;

import com.example.Grand.models.Notification;
import com.example.Grand.models.User;
import com.example.Grand.services.NotificationService;
import com.example.Grand.services.UserServices;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {
    private final UserServices userServices;
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<?> getNotifications(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Необходимо авторизоваться");
        }

        Optional<User> userOptional = userServices.getUserByPrincipal(principal);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Пользователь не найден");
        }

        User user = userOptional.get();
        List<Notification> notifications = notificationService.getNotificationsForUser(user);
        notificationService.markAllAsRead(user);

        return ResponseEntity.ok(notifications);
    }
}