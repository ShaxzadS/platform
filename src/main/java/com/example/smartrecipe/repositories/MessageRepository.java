package com.example.Grand.repositories;

import com.example.Grand.models.Message;
import com.example.Grand.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // Исправленный метод с двумя параметрами
    @Query("SELECT m FROM Message m WHERE (m.sender = :sender AND m.receiver = :receiver) OR (m.sender = :receiver AND m.receiver = :sender) ORDER BY m.createdAt ASC")
    List<Message> findBySenderAndReceiverOrReceiverAndSenderOrderByCreatedAtAsc(User sender, User receiver);

    @Query("SELECT DISTINCT m.sender FROM Message m WHERE m.receiver = :receiver")
    List<User> findDistinctSendersByReceiver(User receiver);

    @Query("SELECT DISTINCT m.receiver FROM Message m WHERE m.sender = :sender")
    List<User> findDistinctReceiversBySender(User sender);
}