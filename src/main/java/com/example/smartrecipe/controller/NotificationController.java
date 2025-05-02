package com.example.smartrecipe.controller;

import com.example.smartrecipe.models.Notification;
import com.example.smartrecipe.models.User;
import com.example.smartrecipe.services.NotificationService;
import com.example.smartrecipe.services.UserServices;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {
    private final UserServices userServices;
    private final NotificationService notificationService;


    @Autowired
    public NotificationController(UserServices userServices, NotificationService notificationService) {
        this.userServices = userServices;
        this.notificationService = notificationService;
    }

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