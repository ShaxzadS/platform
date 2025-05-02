package com.example.smartrecipe.services;

import com.example.smartrecipe.models.Subscription;
import com.example.smartrecipe.models.User;
import com.example.smartrecipe.repositories.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final NotificationService notificationService;



    @Autowired
    public SubscriptionService(SubscriptionRepository subscriptionRepository, NotificationService notificationService) {
        this.subscriptionRepository = subscriptionRepository;
        this.notificationService = notificationService;
    }
    public void subscribe(User subscriber, User target) {
        if (!subscriber.equals(target)) { // Не даем подписываться на себя
            Subscription subscription = new Subscription();
            subscription.setSubscriber(subscriber);
            subscription.setTarget(target);
            subscriptionRepository.save(subscription);
            notificationService.createFollowNotification(target, subscriber);
        }
    }
}