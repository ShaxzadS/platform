package com.example.smartrecipe.services;

import com.example.smartrecipe.models.Notification;
import com.example.smartrecipe.models.User;
import com.example.smartrecipe.models.enums.NotificationType;
import com.example.smartrecipe.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;



    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
    public List<Notification> getNotificationsForUser(User user) {
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user);
    }

    public void markAllAsRead(User user) {
        List<Notification> notifications = notificationRepository.findByRecipientAndIsReadFalse(user);
        notifications.forEach(notification -> notification.setIsRead(true));
        notificationRepository.saveAll(notifications);
    }

    public void createFollowNotification(User recipient, User subscriber) {
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setRelatedUser(subscriber);
        notification.setMessage(subscriber.getName() + " подписался на вас");
        notification.setType(NotificationType.FOLLOW);
        notificationRepository.save(notification);
    }

    public void createLikeNotification(User recipient, User liker, String productTitle) {
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setRelatedUser(liker);
        notification.setMessage(liker.getName() + " лайкнул ваш продукт: " + productTitle);
        notification.setType(NotificationType.LIKE);
        notificationRepository.save(notification);
    }

    public void createCommentNotification(User recipient, User commenter, String productTitle) {
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setRelatedUser(commenter);
        notification.setMessage(commenter.getName() + " прокомментировал ваш продукт: " + productTitle);
        notification.setType(NotificationType.COMMENT);
        notificationRepository.save(notification);
    }

    public void createMessageNotification(User recipient, User sender, String contentPreview) {
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setRelatedUser(sender);
        notification.setMessage(sender.getName() + " отправил вам сообщение: " + contentPreview);
        notification.setType(NotificationType.MESSAGE);
        notificationRepository.save(notification);
    }
}