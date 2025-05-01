package com.example.Grand.controller;

import com.example.Grand.models.User;
import com.example.Grand.services.SubscriptionService;
import com.example.Grand.services.UserServices;
import com.example.Grand.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscriptions")
public class SubscriptionController {
    private final UserServices userServices;
    private final SubscriptionService subscriptionService;
    private final NotificationService notificationService;

    @PostMapping("/subscribe/{userId}")
    public ResponseEntity<?> subscribe(@PathVariable Long userId, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Необходимо авторизоваться");
        }

        Optional<User> subscriberOpt = userServices.getUserByPrincipal(principal);
        Optional<User> targetOpt = userServices.getById(userId);

        if (subscriberOpt.isEmpty() || targetOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Пользователь не найден");
        }

        User subscriber = subscriberOpt.get();
        User target = targetOpt.get();
        subscriptionService.subscribe(subscriber, target);

        // Создаем уведомление о подписке
        notificationService.createFollowNotification(target, subscriber);

        return ResponseEntity.ok("Подписка успешна");
    }
}