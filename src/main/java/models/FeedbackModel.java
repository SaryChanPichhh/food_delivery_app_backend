package com.group_one.food_delivery_app.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks")
@Data
public class FeedbackModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModel user;

    private String subject;

    @Column(columnDefinition = "TEXT")
    private String message;

    private LocalDateTime createdAt = LocalDateTime.now();
    
    private boolean isRead = false;
}
