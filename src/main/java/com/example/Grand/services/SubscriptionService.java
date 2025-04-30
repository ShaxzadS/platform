package com.example.Grand.services;

import com.example.Grand.models.Subscription;
import com.example.Grand.models.User;
import com.example.Grand.repositories.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final NotificationService notificationService;

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