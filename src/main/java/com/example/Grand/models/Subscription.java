package com.example.Grand.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "subscriptions")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Кто подписывается
    @ManyToOne
    @JoinColumn(name = "subscriber_id")
    private User subscriber;

    // На кого подписывается
    @ManyToOne
    @JoinColumn(name = "target_id")
    private User target;
}

