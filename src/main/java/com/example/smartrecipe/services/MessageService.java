package com.example.smartrecipe.services;

import com.example.smartrecipe.models.Message;
import com.example.smartrecipe.models.User;
import com.example.smartrecipe.repositories.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final NotificationService notificationService;



    @Autowired
    public MessageService(MessageRepository messageRepository, NotificationService notificationService) {
        this.messageRepository = messageRepository;
        this.notificationService = notificationService;
    }
    public Message sendMessage(User sender, User receiver, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Сообщение не может быть пустым");
        }

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        Message savedMessage = messageRepository.save(message);

        String contentPreview = content.length() > 50 ? content.substring(0, 50) + "..." : content;
        notificationService.createMessageNotification(receiver, sender, contentPreview);

        return savedMessage;
    }

    public List<User> getChatUsers(User currentUser) {
        List<User> senders = messageRepository.findDistinctSendersByReceiver(currentUser);
        List<User> receivers = messageRepository.findDistinctReceiversBySender(currentUser);
        return Stream.concat(senders.stream(), receivers.stream())
                .distinct()
                .filter(user -> !user.equals(currentUser))
                .toList();
    }

    public List<Message> getChat(User currentUser, Optional<User> selectedUser) {
        if (selectedUser.isEmpty()) {
            return List.of();
        }
        User otherUser = selectedUser.get();
        // Исправлено: передаем только два параметра
        return messageRepository.findBySenderAndReceiverOrReceiverAndSenderOrderByCreatedAtAsc(currentUser, otherUser);
    }
}