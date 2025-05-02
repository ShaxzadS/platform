package com.example.smartrecipe.controller;

import com.example.smartrecipe.models.User;
import com.example.smartrecipe.services.SubscriptionService;
import com.example.smartrecipe.services.UserServices;
import com.example.smartrecipe.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {
    private final UserServices userServices;
    private final SubscriptionService subscriptionService;
    private final NotificationService notificationService;



    @Autowired
    public SubscriptionController(UserServices userServices, SubscriptionService subscriptionService, NotificationService notificationService) {
        this.userServices = userServices;
        this.subscriptionService = subscriptionService;
        this.notificationService = notificationService;
    }
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