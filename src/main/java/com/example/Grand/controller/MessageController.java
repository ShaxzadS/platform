package com.example.Grand.controller;

import com.example.Grand.models.Message;
import com.example.Grand.models.User;
import com.example.Grand.services.MessageService;
import com.example.Grand.services.UserServices;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {
    private final UserServices userServices;
    private final MessageService messageService;

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getMessages(@RequestParam(required = false) Long withUserId, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Необходимо авторизоваться");
        }

        Optional<User> currentUserOpt = userServices.getUserByPrincipal(principal);
        if (currentUserOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Пользователь не найден");
        }

        User currentUser = currentUserOpt.get();
        if (withUserId == null) {
            List<User> chatUsers = messageService.getChatUsers(currentUser);
            return ResponseEntity.ok(chatUsers);
        } else {
            Optional<User> selectedUserOpt = userServices.getById(withUserId);
            if (selectedUserOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Пользователь не найден");
            }
            User selectedUser = selectedUserOpt.get();
            List<Message> chat = messageService.getChat(currentUser, Optional.of(selectedUser));
            return ResponseEntity.ok(chat);
        }
    }

    @PostMapping("/send")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> sendMessage(@RequestParam Long receiverId, @RequestParam String content, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Необходимо авторизоваться");
        }

        Optional<User> senderOpt = userServices.getUserByPrincipal(principal);
        if (senderOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Пользователь не найден");
        }

        User sender = senderOpt.get();
        Optional<User> receiverOpt = userServices.getById(receiverId);
        if (receiverOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Получатель не найден");
        }

        User receiver = receiverOpt.get();
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.status(400).body("Сообщение не может быть пустым");
        }

        Message message = messageService.sendMessage(sender, receiver, content);
        return ResponseEntity.ok(message);
    }
}