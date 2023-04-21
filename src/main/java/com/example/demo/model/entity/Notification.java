package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;
@Getter
@Entity(name = "notifications")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String notification;
    @Column
    private LocalDateTime dateCreated;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
